package com.rinc.imsys;

import android.content.Context;
import android.content.DialogInterface;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ZhouZhi on 2017/9/11.
 */

public class PartOutFragment extends Fragment {

    private EditText textId;

    private EditText textDateTime;

    private EditText textUser;

    private EditText textOperator;

    private EditText textNum;

    private ProgressBar progressBar;

    private Button submitButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_partout, container, false);

        textId = (EditText) view.findViewById(R.id.input_id_partout);
        textDateTime = (EditText) view.findViewById(R.id.input_date_partout);
        textUser = (EditText) view.findViewById(R.id.input_user_partout);
        textOperator = (EditText) view.findViewById(R.id.input_operator_partout);
        textNum = (EditText) view.findViewById(R.id.input_num_partout);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_partout);
        submitButton = (Button) view.findViewById(R.id.button_submit_partout);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("零件出库");

        progressBar.setVisibility(View.GONE);
        submitButton.setVisibility(View.VISIBLE);

        //将出库时间默认设定为当前时间
        SimpleDateFormat simpleDateFormatYear = new SimpleDateFormat("yyyy");
        String year = simpleDateFormatYear.format(new Date());
        SimpleDateFormat simpleDateFormatMonth = new SimpleDateFormat("MM");
        String month = simpleDateFormatMonth.format(new Date());
        SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("dd");
        String day = simpleDateFormatDay.format(new Date());
        String today = year + "-" + month + "-" + day;
        textDateTime.setText(today);

        if (getArguments() != null) {
            //从扫描二维码界面返回
            String info = getArguments().getString("info");
            try {
                JSONObject jsonObject = new JSONObject(info);
                String stockType = jsonObject.getString("stockType");
                String idScan = jsonObject.getString("id");

                if (stockType.equals("part")) {
                    textId.setText(idScan);
                } else {
                    Toast.makeText(getActivity(), "非零件库二维码！", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "二维码格式错误！", Toast.LENGTH_SHORT).show();
            }
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                hideKeyboard();

                boolean idValid = false;
                boolean datetimeValid = false;
                boolean userValid = false;
                boolean operatorValid = false;
                boolean numValid = false;

                //检查标号是否合法
                TextInputLayout wrapperId = (TextInputLayout) view.findViewById(R.id.wrapper_id_partout);
                String id = textId.getText().toString();
                LogUtil.d("Part out id", id);
                if (id.length() == 0) {
                    wrapperId.setError("不能为空！");
                } else if (id.length() > 100) {
                    wrapperId.setError("长度不能超过100个字符！");
                } else {
                    wrapperId.setErrorEnabled(false);
                    idValid = true;
                }

                //检查出库时间是否合法
                final TextInputLayout wrapperDatetime = (TextInputLayout) view.findViewById(R.id.wrapper_datetime_partout);
                String datetime = textDateTime.getText().toString();
                LogUtil.d("Part out datetime", datetime);
                if (datetime.length() == 0) {
                    wrapperDatetime.setError("不能为空！");
                } else {
                    wrapperDatetime.setErrorEnabled(false);
                    datetimeValid = true;
                }

                //检查去向是否合法
                TextInputLayout wrapperUser = (TextInputLayout) view.findViewById(R.id.wrapper_user_partout);
                String user = textUser.getText().toString();
                LogUtil.d("Part out user", user);
                if (user.length() == 0) {
                    wrapperUser.setError("不能为空！");
                } else if (user.length() > 100) {
                    wrapperUser.setError("长度不能超过100个字符！");
                } else {
                    wrapperUser.setErrorEnabled(false);
                    userValid = true;
                }

                //检查操作人员是否合法
                TextInputLayout wrapperOperator = (TextInputLayout) view.findViewById(R.id.wrapper_operator_partout);
                String operator = textOperator.getText().toString();
                LogUtil.d("Part out operator", operator);
                if (operator.length() == 0) {
                    wrapperOperator.setError("不能为空！");
                } else if (operator.length() > 100) {
                    wrapperOperator.setError("长度不能超过100个字符！");
                } else {
                    wrapperOperator.setErrorEnabled(false);
                    operatorValid = true;
                }

                //检查出库量是否合法
                final TextInputLayout wrapperNum = (TextInputLayout) view.findViewById(R.id.wrapper_num_partout);
                String num = textNum.getText().toString();
                LogUtil.d("Part out num", num);
                if (num.length() == 0) {
                    wrapperNum.setError("不能为空！");
                } else {
                    wrapperNum.setErrorEnabled(false);
                    numValid = true;
                }

                if (idValid && datetimeValid && userValid && operatorValid && numValid) {
                    submitButton.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);

                    HttpUtil.partOut(id, datetime, user, operator, num, new okhttp3.Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                String responseData = response.body().string();
                                LogUtil.d("Part out json", responseData);
                                JSONObject jsonObject = new JSONObject(responseData);
                                if (jsonObject.has("id")) {
                                    //出库成功
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE);
                                            submitButton.setVisibility(View.VISIBLE);
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                            builder.setTitle("提示");
                                            builder.setMessage("出库成功！");
                                            builder.setCancelable(false);
                                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    replaceFragment(new PartOutFragment());
                                                }
                                            });
                                            builder.show();
                                        }
                                    });
                                } else {
                                    //出库失败
                                    final List<Integer> errorType = new ArrayList<Integer>();
                                    if (jsonObject.has("detail")) {
                                        //没有该库存品
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.setVisibility(View.GONE);
                                                submitButton.setVisibility(View.VISIBLE);
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                builder.setTitle("提示");
                                                builder.setMessage("出库失败，该零件不存在！");
                                                builder.setCancelable(false);
                                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                    }
                                                });
                                                builder.show();
                                            }
                                        });
                                    }
                                    if (jsonObject.has("partNum")) {
                                        //库存余量不足
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.setVisibility(View.GONE);
                                                submitButton.setVisibility(View.VISIBLE);
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                builder.setTitle("提示");
                                                builder.setMessage("出库失败，库存余量不足！");
                                                builder.setCancelable(false);
                                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                    }
                                                });
                                                builder.show();
                                            }
                                        });
                                    }
                                    if (jsonObject.has("outputDateTime")) {
                                        //出库时间格式错误
                                        errorType.add(1);
                                    }
                                    if (jsonObject.has("outputNum")) {
                                        String numErr = jsonObject.getString("outputNum");
                                        LogUtil.d("Part out error num", numErr);
                                        if (numErr.equals("[\"A valid integer is required.\"]")) {
                                            //非整数
                                            errorType.add(2);
                                        }
                                        if (numErr.equals("[\"Ensure this value is less than or equal to 2147483647.\"]")) {
                                            //数字过大
                                            errorType.add(3);
                                        }
                                    }

                                    //错误处理
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE);
                                            submitButton.setVisibility(View.VISIBLE);

                                            if (errorType.contains(1)) {
                                                wrapperDatetime.setError("格式错误，正确格式如2000-01-01！");
                                            }
                                            if (errorType.contains(2)) {
                                                wrapperNum.setError("该输入非整数！");
                                            }
                                            if (errorType.contains(3)) {
                                                wrapperNum.setError("数字不能大于2147483647！");
                                            }
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
                            LogUtil.d("Part out", "failed");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE); //隐藏进度条
                                    submitButton.setVisibility(View.VISIBLE); //显示出库按钮
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
