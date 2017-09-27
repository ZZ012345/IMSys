package com.rinc.imsys;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ZhouZhi on 2017/9/26.
 */

public class ModifyPassActivity extends BaseActivity {

    private EditText textOldPassword;

    private EditText textPassword1;

    private EditText textPassword2;

    private ProgressBar progressBar;

    private Button submitButton;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifypass);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_modifypass);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        textOldPassword = (EditText) findViewById(R.id.input_oldpassword_modify);
        textPassword1 = (EditText) findViewById(R.id.input_password1_modify);
        textPassword2 = (EditText) findViewById(R.id.input_password2_modify);
        progressBar = (ProgressBar) findViewById(R.id.progressbar_modify_pass);
        submitButton = (Button) findViewById(R.id.button_submit_modify_pass);

        progressBar.setVisibility(View.GONE);
        submitButton.setVisibility(View.VISIBLE);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(); //隐藏虚拟键盘

                boolean oldpasswordValid = false;
                boolean password1Valid = false;
                boolean password2Valid = false;

                //检查原密码是否为空
                final TextInputLayout wrapperOldPassword = (TextInputLayout) findViewById(R.id.wrapper_oldpassword_modify);
                final String oldpassword = textOldPassword.getText().toString();
                LogUtil.d("Modify old password", oldpassword);
                if (oldpassword.length() == 0) {
                    wrapperOldPassword.setError("密码不能为空！");
                } else {
                    wrapperOldPassword.setErrorEnabled(false);
                    oldpasswordValid = true;
                }

                //检查新密码是否合法
                TextInputLayout wrapperPassword1 = (TextInputLayout) findViewById(R.id.wrapper_password1_modify);
                String password1 = textPassword1.getText().toString();
                LogUtil.d("Modify password1", password1);
                if (password1.length() == 0) {
                    wrapperPassword1.setError("密码不能为空！");
                } else if (password1.length() < 8) {
                    wrapperPassword1.setError("密码至少需要包含8个字符！");
                } else if (!RegisterActivity.checkNotAllNum(password1)) {
                    wrapperPassword1.setError("密码必须包含非数字字符！");
                } else {
                    wrapperPassword1.setErrorEnabled(false);
                    password1Valid = true;
                }

                //检查确认新密码是否合法
                final TextInputLayout wrapperPassword2 = (TextInputLayout) findViewById(R.id.wrapper_password2_modify);
                String password2 = textPassword2.getText().toString();
                LogUtil.d("Modify password2", password2);
                if (password2.length() == 0) {
                    wrapperPassword2.setError("密码不能为空！");
                } else if (password2.length() < 8) {
                    wrapperPassword2.setError("密码至少需要包含8个字符！");
                } else if (!RegisterActivity.checkNotAllNum(password2)) {
                    wrapperPassword2.setError("密码必须包含非数字字符！");
                } else {
                    wrapperPassword2.setErrorEnabled(false);
                    password2Valid = true;
                }

                if (oldpasswordValid && password1Valid && password2Valid) {
                    if (!password1.equals(password2)) {
                        wrapperPassword2.setError("前后密码不一致！");
                    } else {
                        submitButton.setVisibility(View.GONE); //隐藏提交按钮
                        progressBar.setVisibility(View.VISIBLE); //显示进度条

                        HttpUtil.modifyPassword(oldpassword, password1, password2, new okhttp3.Callback() {
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try {
                                    String responseData = response.body().string();
                                    LogUtil.d("Modify password jsondata", responseData);
                                    JSONObject jsonObject = new JSONObject(responseData);
                                    if (jsonObject.has("detail")) {
                                        String detailString = jsonObject.getString("detail");
                                        if (detailString.equals("New password has been saved.")) {
                                            //修改成功
                                            LogUtil.d("Modify password", "successful");
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    submitButton.setVisibility(View.VISIBLE);
                                                    progressBar.setVisibility(View.GONE);
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(ModifyPassActivity.this);
                                                    builder.setTitle("提示");
                                                    builder.setMessage("修改密码成功，请重新登录！");
                                                    builder.setCancelable(false);
                                                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            User.clear();
                                                            HttpUtil.header = null;
                                                            ActivityCollector.finishAll();
                                                            Intent intent = new Intent(ModifyPassActivity.this, LoginActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            });
                                        } else {
                                            //用户未登录，一般是在另一个设备上登录的该账号修改了密码引起的
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    submitButton.setVisibility(View.VISIBLE);
                                                    progressBar.setVisibility(View.GONE);
                                                    Intent intent = new Intent("com.rinc.imsys.FORCE_OFFLINE");
                                                    sendBroadcast(intent);
                                                }
                                            });
                                        }
                                    } else if (jsonObject.has("old_password")) {
                                        //原密码错误
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                wrapperOldPassword.setError("原密码错误！");
                                                submitButton.setVisibility(View.VISIBLE);
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        });
                                    } else {
                                        //新密码太简单
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                wrapperPassword2.setError("新密码太简单！");
                                                submitButton.setVisibility(View.VISIBLE);
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        });
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(final Call call, IOException e) {
                                e.printStackTrace();
                                LogUtil.d("Modify password", "failed");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE); //隐藏进度条
                                        submitButton.setVisibility(View.VISIBLE); //显示提交按钮
                                        Toast.makeText(ModifyPassActivity.this, "网络连接失败，请重新尝试", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
    }
}
