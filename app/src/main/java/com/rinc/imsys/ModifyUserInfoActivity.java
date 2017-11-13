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

public class ModifyUserInfoActivity extends BaseActivity {

    private EditText textTelephone;

    private EditText textCompany;

    private EditText textAddress;

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
        setContentView(R.layout.activity_modifyuserinfo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_modifyuserinfo);
        toolbar.setTitle(getString(R.string.modify_userinfo));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        textTelephone = (EditText) findViewById(R.id.input_tel_modify);
        textCompany = (EditText) findViewById(R.id.input_company_modify);
        textAddress = (EditText) findViewById(R.id.input_address_modify);
        progressBar = (ProgressBar) findViewById(R.id.progressbar_modify);
        submitButton = (Button) findViewById(R.id.button_submit_modify);

        progressBar.setVisibility(View.GONE);
        submitButton.setVisibility(View.VISIBLE);

        //初始化，填入当前个人信息
        textTelephone.setText(User.tel);
        textCompany.setText(User.company);
        textAddress.setText(User.address);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(); //隐藏虚拟键盘

                boolean telephoneValid = false;
                boolean companyValid = false;
                boolean addressValid = false;

                //检查电话是否合法
                TextInputLayout wrapperTel = (TextInputLayout) findViewById(R.id.wrapper_tel_modify);
                final String tel = textTelephone.getText().toString();
                LogUtil.d("Userinfo Modify tel", tel);
                if (tel.length() == 0) {
                    wrapperTel.setError(getString(R.string.phone_not_empty));
                } else if (tel.length() > 20) {
                    wrapperTel.setError(getString(R.string.phone_too_long));
                } else if (RegisterActivity.checkNotAllNum(tel)) {
                    wrapperTel.setError(getString(R.string.phone_all_number));
                } else {
                    wrapperTel.setErrorEnabled(false);
                    telephoneValid = true;
                }

                //检查公司是否合法
                TextInputLayout wrapperCompany = (TextInputLayout) findViewById(R.id.wrapper_company_modify);
                final String company = textCompany.getText().toString();
                LogUtil.d("Userinfo Modify company", company);
                if (company.length() == 0) {
                    wrapperCompany.setError(getString(R.string.company_not_empty));
                } else if (company.length() > 100) {
                    wrapperCompany.setError(getString(R.string.company_too_long));
                } else {
                    wrapperCompany.setErrorEnabled(false);
                    companyValid = true;
                }

                //检查地址是否合法
                TextInputLayout wrapperAddress = (TextInputLayout) findViewById(R.id.wrapper_address_modify);
                final String address = textAddress.getText().toString();
                LogUtil.d("Userinfo Modify address", address);
                if (address.length() == 0) {
                    wrapperAddress.setError(getString(R.string.address_not_empty));
                } else if (address.length() > 100) {
                    wrapperAddress.setError(getString(R.string.address_too_long));
                } else {
                    wrapperAddress.setErrorEnabled(false);
                    addressValid = true;
                }

                if (telephoneValid && companyValid && addressValid) {
                    submitButton.setVisibility(View.GONE); //隐藏提交按钮
                    progressBar.setVisibility(View.VISIBLE); //显示进度条

                    HttpUtil.modifyUserinfo(tel, company, address, new okhttp3.Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                String reponseData = response.body().string();
                                LogUtil.d("Userinfo Modify jsondata", reponseData);
                                JSONObject jsonObject = new JSONObject(reponseData);
                                if (jsonObject.has("detail")) {
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
                                } else {
                                    //修改成功，跳转到个人信息碎片，个人信息会自动刷新
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE);
                                            submitButton.setVisibility(View.VISIBLE);
                                            AlertDialog.Builder builder = new AlertDialog.Builder(ModifyUserInfoActivity.this);
                                            builder.setTitle(getString(R.string.hint));
                                            builder.setMessage(getString(R.string.modify_successful));
                                            builder.setCancelable(false);
                                            builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    User.tel = tel;
                                                    User.company = company;
                                                    User.address = address;
                                                    Intent intent = new Intent();
                                                    intent.putExtra("result", "");
                                                    setResult(RESULT_OK, intent);
                                                    finish();
                                                }
                                            });
                                            builder.show();
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            LogUtil.d("Userinfo Modify", "failed");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE); //隐藏进度条
                                    submitButton.setVisibility(View.VISIBLE); //显示提交按钮
                                    Toast.makeText(ModifyUserInfoActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });
    }
}
