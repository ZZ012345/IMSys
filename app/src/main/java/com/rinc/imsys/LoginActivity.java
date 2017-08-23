package com.rinc.imsys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {

    private Toolbar toolbar;

    private EditText textUsername;

    private EditText textPassword;

    private CheckBox rememberPass;

    private ProgressBar progressBar;

    private Button loginButton;

    private TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        textUsername = (EditText) findViewById(R.id.input_username);
        textPassword = (EditText) findViewById(R.id.input_password);
        rememberPass = (CheckBox) findViewById(R.id.remember_password);
        progressBar = (ProgressBar) findViewById(R.id.progressbar_login);
        loginButton = (Button) findViewById(R.id.button_login);
        registerLink = (TextView) findViewById(R.id.link_register);

        toolbar.setTitle("登录");
        setSupportActionBar(toolbar);

        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE); //隐藏进度条
        }

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemember = pref.getBoolean("remember_password", false);
        if (isRemember) {
            //将用户名和密码置入文本框
            String username = pref.getString("username", "");
            String passwordEncrypted = pref.getString("password", "");
            String passwordUnencrypted = unencryptPassword(passwordEncrypted); //解密
            textUsername.setText(username);
            textPassword.setText(passwordUnencrypted);
            rememberPass.setChecked(true);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(); //隐藏虚拟键盘
                boolean usernameValid = false;
                boolean passwordValid = false;

                //检查用户名是否合法
                TextInputLayout wrapperUsername = (TextInputLayout) findViewById(R.id.wrapper_username);
                final String username = textUsername.getText().toString();
                LogUtil.d("Login username", username);
                if (username.length() == 0) {
                    wrapperUsername.setError("用户名不能为空！");
                } else {
                    wrapperUsername.setErrorEnabled(false);
                    usernameValid = true;
                }

                //检查密码是否合法
                TextInputLayout wrapperPassword = (TextInputLayout) findViewById(R.id.wrapper_password);
                final String password = textPassword.getText().toString();
                LogUtil.d("Login password", password);
                if (password.length() == 0) {
                    wrapperPassword.setError("密码不能为空！");
                } else {
                    wrapperPassword.setErrorEnabled(false);
                    passwordValid = true;
                }

                //登录
                if (usernameValid && passwordValid) {
                    loginButton.setVisibility(View.GONE); //隐藏登录按钮
                    registerLink.setVisibility(View.GONE); //隐藏注册链接
                    progressBar.setVisibility(View.VISIBLE); //显示进度条
                    HttpUtil.login(username, password, new okhttp3.Callback() { //向服务器发送请求
                        @Override
                        public void onResponse(Call call, Response response) throws IOException { //服务器连接成功
                            try {
                                String responseData = response.body().string();
                                LogUtil.d("Login jsondata", responseData);
                                JSONObject jsonObject = new JSONObject(responseData);
                                if (jsonObject.has("key")) { //服务器返回token，登录成功
                                    String keyString = jsonObject.getString("key");
                                    LogUtil.d("Login key", keyString);
                                    HttpUtil.header = keyString; //保存token
                                    User.username = username; //保存用户名

                                    SharedPreferences.Editor editor = pref.edit();
                                    if (rememberPass.isChecked()) {
                                        //在本地存储用户名和密码
                                        editor.putBoolean("remember_password", true);
                                        editor.putString("username", username);
                                        editor.putString("password", encryptPassword(password));
                                    } else {
                                        editor.clear();
                                    }
                                    editor.apply();

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else if (jsonObject.has("non_field_errors")) { //用户名或密码错误
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE); //隐藏进度条
                                            loginButton.setVisibility(View.VISIBLE); //显示登录按钮
                                            registerLink.setVisibility(View.VISIBLE); //显示注册链接
                                            Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    LogUtil.d("Login", "response type error");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call call, IOException e) { //服务器连接失败
                            e.printStackTrace();
                            LogUtil.d("Login", "failed");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE); //隐藏进度条
                                    loginButton.setVisibility(View.VISIBLE); //显示登录按钮
                                    registerLink.setVisibility(View.VISIBLE); //显示注册链接
                                    Toast.makeText(LoginActivity.this, "网络连接失败，请重新尝试", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到注册界面
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private void hideKeyboard() { //隐藏虚拟键盘
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private String encryptPassword(String password) { //对密码进行加密
        StringBuilder st = new StringBuilder("");
        for (int i = 0; i < password.length(); i++) {
            char ch = encryptCharacter(password.charAt(i));
            st = st.append(ch);
        }
        st.insert(1, "2s");
        st.insert(4, "4");
        st.insert(6, "mp");
        st.insert(9, "0");
        st.insert(11, "a1k");
        st.insert(15, "7d");
        st.insert(18, "ia");
        return st.toString();
    }


    private char encryptCharacter(char ch) {
        if (ch > 47 && ch < 58) { //0~9
            ch = (char)((int)ch + 65); //q~z
        } else if (ch > 96 && ch < 113) { //a~p
            ch = (char)((int)ch - 22); //K~Z
        } else if (ch > 112 && ch < 123) { //q~z
            ch = (char)((int)ch - 16); //a~j
        } else if (ch > 64 && ch < 71) { //A~F
            ch = (char)((int)ch + 42); //k~p
        } else if (ch > 70 && ch < 81) { //G~P
            ch = (char)((int)ch - 23); //0~9
        } else if (ch > 80 && ch < 91) { //Q~Z
            ch = (char)((int)ch - 16); //A~J
        }
        return ch;
    }

    private String unencryptPassword(String password) { //对密码进行解密
        StringBuilder st = new StringBuilder(password);
        st.delete(18, 18 + 2);
        st.delete(15, 15 + 2);
        st.delete(11, 11 + 3);
        st.delete(9, 9 + 1);
        st.delete(6, 6 + 2);
        st.delete(4, 4 + 1);
        st.delete(1, 1 + 2);
        StringBuilder st2 = new StringBuilder("");
        for (int i = 0; i < st.length(); i++) {
            char ch = unencryptCharacter(st.charAt(i));
            st2 = st2.append(ch);
        }
        return st2.toString();
    }

    private char unencryptCharacter(char ch) {
        if ((ch - 65) > 47 && (ch - 65) < 58) { //q~z
            ch = (char) ((int) ch - 65); //0~9
        } else if ((ch + 22) > 96 && (ch + 22) < 113) { //K~Z
            ch = (char) ((int) ch + 22); //a~p
        } else if ((ch + 16) > 112 && (ch + 16) < 123) { //a~j
            ch = (char) ((int) ch + 16); //q~z
        } else if ((ch - 42) > 64 && (ch - 42) < 71) { //k~p
            ch = (char) ((int) ch - 42); //A~F
        } else if ((ch + 23) > 70 && (ch + 23) < 81) { //0~9
            ch = (char) ((int) ch + 23); //G~P
        } else if ((ch + 16) > 80 && (ch + 16) < 91) { //A~J
            ch = (char) ((int) ch + 16); //Q~Z
        }
        return ch;
    }
}
