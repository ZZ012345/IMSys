package com.rinc.imsys;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ZhouZhi on 2017/9/27.
 */

public class FillUserinfoActivity extends BaseActivity {

    private EditText textTelephone;

    private EditText textCompany;

    private EditText textAddress;

    private ProgressBar progressBar;

    private Button submitButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filluserinfo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_filluserinfo);
        toolbar.setTitle(getString(R.string.fill_userinfo));
        setSupportActionBar(toolbar);

        textTelephone = (EditText) findViewById(R.id.input_tel_fill);
        textCompany = (EditText) findViewById(R.id.input_company_fill);
        textAddress = (EditText) findViewById(R.id.input_address_fill);
        progressBar = (ProgressBar) findViewById(R.id.progressbar_fill);
        submitButton = (Button) findViewById(R.id.button_submit_fill);

        progressBar.setVisibility(View.GONE);
        submitButton.setVisibility(View.VISIBLE);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(); //隐藏虚拟键盘
                boolean telephoneValid = false;
                boolean companyValid = false;
                boolean addressValid = false;

                //检查电话是否合法
                TextInputLayout wrapperTel = (TextInputLayout) findViewById(R.id.wrapper_tel_fill);
                String tel = textTelephone.getText().toString();
                LogUtil.d("Userinfo Fill tel", tel);
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
                TextInputLayout wrapperCompany = (TextInputLayout) findViewById(R.id.wrapper_company_fill);
                String company = textCompany.getText().toString();
                LogUtil.d("Userinfo Fill company", company);
                if (company.length() == 0) {
                    wrapperCompany.setError(getString(R.string.company_not_empty));
                } else if (company.length() > 100) {
                    wrapperCompany.setError(getString(R.string.company_too_long));
                } else {
                    wrapperCompany.setErrorEnabled(false);
                    companyValid = true;
                }

                //检查地址是否合法
                TextInputLayout wrapperAddress = (TextInputLayout) findViewById(R.id.wrapper_address_fill);
                String address = textAddress.getText().toString();
                LogUtil.d("Userinfo Fill address", address);
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

                    HttpUtil.fillUserinfo(tel, company, address, new okhttp3.Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                String responseData = response.body().string();
                                LogUtil.d("Userinfo Fill jsondata", responseData);
                                //不管是否已经提交过个人信息，都跳转到个人信息碎片，从而刷新个人信息
                                Intent intent = new Intent();
                                intent.putExtra("result", "");
                                setResult(RESULT_OK, intent);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            LogUtil.d("Userinfo Fill", "failed");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE); //隐藏进度条
                                    submitButton.setVisibility(View.VISIBLE); //显示提交按钮
                                    Toast.makeText(FillUserinfoActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
