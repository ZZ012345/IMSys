package com.rinc.imsys;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhouzhi on 2017/8/14.
 */

public class UserinfoFragment extends Fragment {

    private ProgressBar progressBar;

    private LinearLayout wrapperInfo;

    private Button modifyInfoButton;

    private Button modifyPassButton;

    private TextView textUsername;

    private TextView textEmail;

    private TextView textTelephone;

    private TextView textCompany;

    private TextView textAddress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userinfo, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_userinfo);
        wrapperInfo = (LinearLayout) view.findViewById(R.id.wrapper_userinfo);
        modifyInfoButton = (Button) view.findViewById(R.id.button_modify_info);
        modifyPassButton = (Button) view.findViewById(R.id.button_modify_pass);
        textUsername = (TextView) view.findViewById(R.id.text_username_info);
        textEmail = (TextView)  view.findViewById(R.id.text_email_info);
        textTelephone = (TextView) view.findViewById(R.id.text_telephone_info);
        textCompany = (TextView) view.findViewById(R.id.text_company_info);
        textAddress = (TextView) view.findViewById(R.id.text_address_info);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("个人信息");

        progressBar.setVisibility(View.VISIBLE); //显示进度条
        wrapperInfo.setVisibility(View.GONE); //隐藏个人信息
        modifyInfoButton.setVisibility(View.GONE); //隐藏修改个人信息按钮
        modifyPassButton.setVisibility(View.GONE); //隐藏修改密码按钮

        HttpUtil.getUserinfo(new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseData = response.body().string();
                    LogUtil.d("Userinfo Get response data", responseData);
                    JSONObject jsonObject = new JSONObject(responseData);
                    if (jsonObject.has("detail")) { //获取个人信息失败
                        String detailMess = jsonObject.getString("detail");
                        if (detailMess.equals("Not found.")) {
                            //用户还没有填写详细个人信息，跳转到填写个人信息碎片
                            replaceFragment(new FillUserinfoFragment());
                        } else if (detailMess.equals("Authentication credentials were not provided.")) {
                            //用户未登录，一般是在另一个设备上登录的该账号修改了密码引起的
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    modifyInfoButton.setVisibility(View.VISIBLE);
                                    modifyPassButton.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle("提示");
                                    builder.setMessage("该账号已被强制登出，请重新登录！");
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
                        }
                    } else { //获取个人信息成功
                        String telephoneString = jsonObject.getString("tel");
                        String companyString = jsonObject.getString("company");
                        String addressString = jsonObject.getString("address");
                        String userString = jsonObject.getString("user");
                        JSONObject jsonObject1 = new JSONObject(userString);
                        String emailString = jsonObject1.getString("email");
                        //保存个人信息
                        User.tel = telephoneString;
                        User.company = companyString;
                        User.address = addressString;
                        User.email = emailString;

                        //显示个人信息
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE); //隐藏进度条
                                wrapperInfo.setVisibility(View.VISIBLE); //显示个人信息
                                modifyInfoButton.setVisibility(View.VISIBLE); //显示修改个人信息按钮
                                modifyPassButton.setVisibility(View.VISIBLE); //显示修改密码按钮

                                textUsername.setText(User.username);
                                textEmail.setText(User.email);
                                textTelephone.setText(User.tel);
                                textCompany.setText(User.company);
                                textAddress.setText(User.address);
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
                LogUtil.d("Userinfo Get", "failed");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE); //隐藏进度条
                        wrapperInfo.setVisibility(View.VISIBLE); //显示个人信息
                        modifyInfoButton.setVisibility(View.VISIBLE); //显示修改个人信息按钮
                        modifyPassButton.setVisibility(View.VISIBLE); //显示修改密码按钮
                        Toast.makeText(getActivity(), "网络连接失败，请重新尝试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        modifyInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                replaceFragment(new ModifyUserinfoFragment());
            }
        });

        modifyPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                replaceFragment(new ModifyPassFragment());
            }
        });

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager =  getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_main, fragment);
        transaction.commit();
    }
}
