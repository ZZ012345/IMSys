package com.rinc.imsys;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity {

    private Toolbar toolbar;

    private EditText textUsername;

    private EditText textEmail;

    private EditText textPassword1;

    private EditText textPassword2;

    private ProgressBar progressBar;

    private Button registerButton;

    private TextView loginLink;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = (Toolbar) findViewById(R.id.toolbar_register);
        textUsername = (EditText) findViewById(R.id.input_username_register);
        textEmail = (EditText) findViewById(R.id.input_email_register);
        textPassword1 = (EditText) findViewById(R.id.input_password1_register);
        textPassword2 = (EditText) findViewById(R.id.input_password2_register);
        progressBar = (ProgressBar) findViewById(R.id.progressbar_register);
        registerButton = (Button) findViewById(R.id.button_register);
        loginLink = (TextView) findViewById(R.id.link_login);

        toolbar.setTitle(getString(R.string.sign_up));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(); //隐藏虚拟键盘
                boolean usernameValid = false;
                boolean emailValid = false;
                boolean password1Valid = false;
                boolean password2Valid = false;

                //检查用户名是否合法
                final TextInputLayout wrapperUsername = (TextInputLayout) findViewById(R.id.wrapper_username_register);
                final String username = textUsername.getText().toString();
                LogUtil.d("Register username", username);
                if (username.length() == 0) {
                    wrapperUsername.setError(getString(R.string.username_not_empty));
                } else if (username.length() > 150) {
                    wrapperUsername.setError(getString(R.string.username_too_long));
                } else if (!checkUsername(username)) {
                    wrapperUsername.setError(getString(R.string.username_format_error));
                } else {
                    wrapperUsername.setErrorEnabled(false);
                    usernameValid = true;
                }

                //检查邮箱是否合法
                final TextInputLayout wrapperEmail = (TextInputLayout) findViewById(R.id.wrapper_email_register);
                final String email = textEmail.getText().toString();
                LogUtil.d("Register email", email);
                if (email.length() == 0) {
                    wrapperEmail.setError(getString(R.string.email_not_empty));
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    wrapperEmail.setError(getString(R.string.email_format_error));
                } else {
                    wrapperEmail.setErrorEnabled(false);
                    emailValid = true;
                }

                //检查密码是否合法
                TextInputLayout wrapperPassword1 = (TextInputLayout) findViewById(R.id.wrapper_password1_register);
                String password1 = textPassword1.getText().toString();
                LogUtil.d("Register password1", password1);
                if (password1.length() == 0) {
                    wrapperPassword1.setError(getString(R.string.password_not_empty));
                } else if (password1.length() < 8) {
                    wrapperPassword1.setError(getString(R.string.password_too_short));
                } else if (!checkNotAllNum(password1)) {
                    wrapperPassword1.setError(getString(R.string.password_all_number));
                } else {
                    wrapperPassword1.setErrorEnabled(false);
                    password1Valid = true;
                }

                //检查确认密码是否合法
                final TextInputLayout wrapperPassword2 = (TextInputLayout) findViewById(R.id.wrapper_password2_register);
                String password2 = textPassword2.getText().toString();
                LogUtil.d("Register password2", password2);
                if (password2.length() == 0) {
                    wrapperPassword2.setError(getString(R.string.password_not_empty));
                } else if (password2.length() < 8) {
                    wrapperPassword2.setError(getString(R.string.password_too_short));
                } else if (!checkNotAllNum(password2)) {
                    wrapperPassword2.setError(getString(R.string.password_all_number));
                } else {
                    wrapperPassword2.setErrorEnabled(false);
                    password2Valid = true;
                }

                if (usernameValid && emailValid && password1Valid && password2Valid) {
                    if (!password1.equals(password2)) {
                        wrapperPassword2.setError(getString(R.string.password_different));
                    } else {
                        registerButton.setVisibility(View.GONE); //隐藏注册按钮
                        loginLink.setVisibility(View.GONE); //隐藏登录链接
                        progressBar.setVisibility(View.VISIBLE); //显示进度条

                        //注册
                        HttpUtil.register(username, email, password1, password2, new okhttp3.Callback() { //向服务器发送请求
                            @Override
                            public void onResponse(Call call, Response response) throws IOException { //服务器连接成功
                                try {
                                    String responseData = response.body().string();
                                    LogUtil.d("Register jsondata", responseData);
                                    JSONObject jsonObject = new JSONObject(responseData);
                                    if (jsonObject.has("key")) { //服务器返回token，注册成功
                                        String keyString = jsonObject.getString("key");
                                        LogUtil.d("Register key", keyString);
                                        HttpUtil.header = keyString; //保存token
                                        User.username = username; //保存用户名
                                        //此时用户已经登录，跳转到主页
                                        ActivityCollector.finishAll();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    } else { //注册失败
                                        final List<Integer> errorType = new ArrayList<Integer>();
                                        if (jsonObject.has("password1")) {
                                            String passwordError = jsonObject.getString("password1");
                                            if (passwordError.equals("[\"This password is too common.\"]")) {
                                                errorType.add(1);
                                            }
                                        }
                                        if (jsonObject.has("email")) {
                                            String emailError = jsonObject.getString("email");
                                            if (emailError.equals("[\"Enter a valid email address.\"]")) {
                                                errorType.add(2);
                                            } else if (emailError.equals("[\"A user is already registered with this e-mail address.\"]")) {
                                                errorType.add(3);
                                            }
                                        }
                                        if (jsonObject.has("username")) {
                                            String usernameError = jsonObject.getString("username");
                                            if (usernameError.equals("[\"A user with that username already exists.\"]")) {
                                                errorType.add(4);
                                            }
                                        }

                                        runOnUiThread(new Runnable() { //显示错误信息
                                            @Override
                                            public void run() {
                                                progressBar.setVisibility(View.GONE); //隐藏进度条
                                                registerButton.setVisibility(View.VISIBLE); //显示注册按钮
                                                loginLink.setVisibility(View.VISIBLE); //显示登录链接
                                                if (errorType.size() == 0) { //发生未预计到的错误
                                                    Toast.makeText(RegisterActivity.this, getString(R.string.sign_up_fail), Toast.LENGTH_SHORT).show();
                                                } else {
                                                    if (errorType.contains(1)) {
                                                        wrapperPassword2.setError(getString(R.string.password_too_simple));
                                                    }
                                                    if (errorType.contains(2)) {
                                                        wrapperEmail.setError(getString(R.string.email_format_error));
                                                    }
                                                    if (errorType.contains(3)) {
                                                        wrapperEmail.setError(getString(R.string.email_been_registered));
                                                    }
                                                    if (errorType.contains(4)) {
                                                        wrapperUsername.setError(getString(R.string.username_been_registered));
                                                    }
                                                }
                                            }
                                        });
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call call, IOException e) { //服务器连接失败
                                e.printStackTrace();
                                LogUtil.d("Register", "failed");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE); //隐藏进度条
                                        registerButton.setVisibility(View.VISIBLE); //显示注册按钮
                                        loginLink.setVisibility(View.VISIBLE); //显示登录链接
                                        Toast.makeText(RegisterActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到登录界面
                finish();
            }
        });
    }

    private boolean checkUsername(String username) { //检查用户名是否合法
        for (int i = 0; i < username.length(); i++) {
            char ch = username.charAt(i);
            if (!((ch > 47 && ch < 58) || (ch > 64 && ch < 91) || (ch > 96 && ch < 123) ||
                    ch == '@' || ch == '.' || ch == '+' || ch == '-' || ch == '_')) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkNotAllNum(String s) { //检查字符串是否由纯数字组成
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (!(ch > 47 && ch < 58)) { //包含非数字字符
                return true;
            }
        }
        return false;
    }
}
