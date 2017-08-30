package com.rinc.imsys;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhouzhi on 2017/8/16.
 */

public class ModifyPassFragment extends Fragment {

    private EditText textOldPassword;

    private EditText textPassword1;

    private EditText textPassword2;

    private ProgressBar progressBar;

    private Button submitButton;

    private Button cancelButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_modifypass, container, false);

        textOldPassword = (EditText) view.findViewById(R.id.input_oldpassword_modify);
        textPassword1 = (EditText) view.findViewById(R.id.input_password1_modify);
        textPassword2 = (EditText) view.findViewById(R.id.input_password2_modify);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_modify_pass);
        submitButton = (Button) view.findViewById(R.id.button_submit_modify_pass);
        cancelButton = (Button) view.findViewById(R.id.button_cancel_modify_pass);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("修改密码");

        progressBar.setVisibility(View.GONE);
        submitButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                hideKeyboard(); //隐藏虚拟键盘

                boolean oldpasswordValid = false;
                boolean password1Valid = false;
                boolean password2Valid = false;

                //检查原密码是否为空
                final TextInputLayout wrapperOldPassword = (TextInputLayout) view.findViewById(R.id.wrapper_oldpassword_modify);
                final String oldpassword = textOldPassword.getText().toString();
                LogUtil.d("Modify old password", oldpassword);
                if (oldpassword.length() == 0) {
                    wrapperOldPassword.setError("密码不能为空！");
                } else {
                    wrapperOldPassword.setErrorEnabled(false);
                    oldpasswordValid = true;
                }

                //检查新密码是否合法
                TextInputLayout wrapperPassword1 = (TextInputLayout) view.findViewById(R.id.wrapper_password1_modify);
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
                final TextInputLayout wrapperPassword2 = (TextInputLayout) view.findViewById(R.id.wrapper_password2_modify);
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
                        cancelButton.setVisibility(View.GONE); //隐藏取消按钮
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
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    submitButton.setVisibility(View.VISIBLE);
                                                    cancelButton.setVisibility(View.VISIBLE);
                                                    progressBar.setVisibility(View.GONE);
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                    builder.setTitle("提示");
                                                    builder.setMessage("修改密码成功，请重新登录！");
                                                    builder.setCancelable(false);
                                                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            User.clear();
                                                            HttpUtil.header = null;
                                                            ActivityCollector.finishAll();
                                                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                            getActivity().startActivity(intent);
                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            });
                                        } else {
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
                                        }
                                    } else if (jsonObject.has("old_password")) {
                                        //原密码错误
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                wrapperOldPassword.setError("原密码错误！");
                                                submitButton.setVisibility(View.VISIBLE);
                                                cancelButton.setVisibility(View.VISIBLE);
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        });
                                    } else {
                                        //新密码太简单
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                wrapperPassword2.setError("新密码太简单！");
                                                submitButton.setVisibility(View.VISIBLE);
                                                cancelButton.setVisibility(View.VISIBLE);
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
