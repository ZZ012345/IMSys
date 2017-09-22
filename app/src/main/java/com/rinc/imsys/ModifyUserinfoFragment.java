package com.rinc.imsys;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhouzhi on 2017/8/15.
 */

public class ModifyUserinfoFragment extends BaseFragment {

    private EditText textTelephone;

    private EditText textCompany;

    private EditText textAddress;

    private ProgressBar progressBar;

    private Button submitButton;

    private Button cancelButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_modifyuserinfo, container, false);

        textTelephone = (EditText) view.findViewById(R.id.input_tel_modify);
        textCompany = (EditText) view.findViewById(R.id.input_company_modify);
        textAddress = (EditText) view.findViewById(R.id.input_address_modify);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_modify);
        submitButton = (Button) view.findViewById(R.id.button_submit_modify);
        cancelButton = (Button) view.findViewById(R.id.button_cancel_modify);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("修改个人信息");

        progressBar.setVisibility(View.GONE);
        submitButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);

        //初始化，填入当前个人信息
        textTelephone.setText(User.tel);
        textCompany.setText(User.company);
        textAddress.setText(User.address);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                hideKeyboard(); //隐藏虚拟键盘

                boolean telephoneValid = false;
                boolean companyValid = false;
                boolean addressValid = false;

                //检查电话是否合法
                TextInputLayout wrapperTel = (TextInputLayout) view.findViewById(R.id.wrapper_tel_modify);
                String tel = textTelephone.getText().toString();
                LogUtil.d("Userinfo Modify tel", tel);
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
                TextInputLayout wrapperCompany = (TextInputLayout) view.findViewById(R.id.wrapper_company_modify);
                String company = textCompany.getText().toString();
                LogUtil.d("Userinfo Modify company", company);
                if (company.length() == 0) {
                    wrapperCompany.setError("公司不能为空！");
                } else if (company.length() > 100) {
                    wrapperCompany.setError("公司长度不能超过100个字符！");
                } else {
                    wrapperCompany.setErrorEnabled(false);
                    companyValid = true;
                }

                //检查地址是否合法
                TextInputLayout wrapperAddress = (TextInputLayout) view.findViewById(R.id.wrapper_address_modify);
                String address = textAddress.getText().toString();
                LogUtil.d("Userinfo Modify address", address);
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
                    cancelButton.setVisibility(View.GONE); //隐藏取消按钮
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
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            submitButton.setVisibility(View.VISIBLE);
                                            cancelButton.setVisibility(View.VISIBLE);
                                            progressBar.setVisibility(View.GONE);
                                            /*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                            builder.setTitle("提示");
                                            builder.setMessage("该账号已被强制登出，请重新登录！");
                                            builder.setCancelable(false);
                                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    User.clear();
                                                    HttpUtil.header = null;
                                                    SearchRecord.clearRecord();
                                                    ActivityCollector.finishAll();
                                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                    getActivity().startActivity(intent);
                                                }
                                            });
                                            builder.show();*/
                                            Intent intent = new Intent("com.rinc.imsys.FORCE_OFFLINE");
                                            getActivity().sendBroadcast(intent);
                                        }
                                    });
                                } else {
                                    //修改成功，跳转到个人信息碎片，个人信息会自动刷新
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE);
                                            submitButton.setVisibility(View.VISIBLE);
                                            cancelButton.setVisibility(View.VISIBLE);
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                            builder.setTitle("提示");
                                            builder.setMessage("修改成功！");
                                            builder.setCancelable(false);
                                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    replaceFragment(new UserinfoFragment());
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
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE); //隐藏进度条
                                    submitButton.setVisibility(View.VISIBLE); //显示提交按钮
                                    cancelButton.setVisibility(View.VISIBLE); //显示取消按钮
                                    Toast.makeText(getActivity(), "网络连接失败，请重新尝试", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                replaceFragment(new UserinfoFragment());
            }
        });

        return view;
    }
}
