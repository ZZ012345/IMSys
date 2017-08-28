package com.rinc.imsys;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhouzhi on 2017/8/14.
 */

public class FillUserinfoFragment extends Fragment {

    private EditText textTelephone;

    private EditText textCompany;

    private EditText textAddress;

    private ProgressBar progressBar;

    private Button submitButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_filluserinfo, container, false);

        textTelephone = (EditText) view.findViewById(R.id.input_tel_fill);
        textCompany = (EditText) view.findViewById(R.id.input_company_fill);
        textAddress = (EditText) view.findViewById(R.id.input_address_fill);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_fill);
        submitButton = (Button) view.findViewById(R.id.button_submit_fill);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("填写个人信息");

        progressBar.setVisibility(View.GONE);
        submitButton.setVisibility(View.VISIBLE);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            //注意，这里的参数view1指按钮，view指上面定义的碎片布局，如果这里写成view，那么下面在调用
            //view.findViewById时会从按钮中去找，而不是碎片布局，会造成空指针错误
            public void onClick(View view1) {
                hideKeyboard(); //隐藏虚拟键盘
                boolean telephoneValid = false;
                boolean companyValid = false;
                boolean addressValid = false;

                //检查电话是否合法
                TextInputLayout wrapperTel = (TextInputLayout) view.findViewById(R.id.wrapper_tel_fill);
                String tel = textTelephone.getText().toString();
                LogUtil.d("Userinfo Fill tel", tel);
                if (tel.length() == 0) {
                    wrapperTel.setError("电话不能为空！");
                } else if (tel.length() > 20) {
                    wrapperTel.setError("电话长度不能超过20个字符！");
                } else if (RegisterActivity.checkNotAllNum(tel)) {
                    wrapperTel.setError("电话不能包含非数字字符！");
                } else {
                    wrapperTel.setErrorEnabled(false);
                    telephoneValid = true;
                }

                //检查公司是否合法
                TextInputLayout wrapperCompany = (TextInputLayout) view.findViewById(R.id.wrapper_company_fill);
                String company = textCompany.getText().toString();
                LogUtil.d("Userinfo Fill company", company);
                if (company.length() == 0) {
                    wrapperCompany.setError("公司不能为空！");
                } else if (company.length() > 100) {
                    wrapperCompany.setError("公司长度不能超过100个字符！");
                } else {
                    wrapperCompany.setErrorEnabled(false);
                    companyValid = true;
                }

                //检查地址是否合法
                TextInputLayout wrapperAddress = (TextInputLayout) view.findViewById(R.id.wrapper_address_fill);
                String address = textAddress.getText().toString();
                LogUtil.d("Userinfo Fill address", address);
                if (address.length() == 0) {
                    wrapperAddress.setError("地址不能为空！");
                } else if (address.length() > 100) {
                    wrapperAddress.setError("地址长度不能超过100个字符！");
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
                                replaceFragment(new UserinfoFragment());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            LogUtil.d("Userinfo Fill", "failed");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE); //隐藏进度条
                                    submitButton.setVisibility(View.VISIBLE); //显示提交按钮
                                    Toast.makeText(getActivity(), "网络连接失败，请重新尝试", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });

        return view;
    }

    private void hideKeyboard() { //隐藏虚拟键盘
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager =  getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_main, fragment);
        transaction.commit();
    }
}
