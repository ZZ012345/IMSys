package com.rinc.imsys;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhouzhi on 2017/8/18.
 */

public class ModifyMatDetailFragment extends BaseFragment {

    private EditText textId;

    private EditText textType;

    private Spinner spinnerStorestate;

    private EditText textMark;

    private EditText textBand;

    private EditText textOriginal;

    private EditText textYear;

    private EditText textState;

    private EditText textPosition;

    private EditText textUnit;

    private EditText textDescription;

    private ProgressBar progressBar;

    private Button submitButton;

    private Button cancelButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_modifymatdetail, container, false);

        textId = (EditText) view.findViewById(R.id.input_id_matdetail_modify);
        textType = (EditText) view.findViewById(R.id.input_type_matdetail_modify);
        spinnerStorestate = (Spinner) view.findViewById(R.id.spinner_state_matdetail_modify);
        textMark = (EditText) view.findViewById(R.id.input_mark_matdetail_modify);
        textBand = (EditText) view.findViewById(R.id.input_band_matdetail_modify);
        textOriginal = (EditText) view.findViewById(R.id.input_original_matdetail_modify);
        textYear = (EditText) view.findViewById(R.id.input_year_matdetail_modify);
        textState = (EditText) view.findViewById(R.id.input_state_matdetail_modify);
        textPosition = (EditText) view.findViewById(R.id.input_position_matdetail_modify);
        textUnit = (EditText) view.findViewById(R.id.input_unit_matdetail_modify);
        textDescription = (EditText) view.findViewById(R.id.input_description_matdetail_modify);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_matdetail_modify);
        submitButton = (Button) view.findViewById(R.id.button_submit_matdetail_modify);
        cancelButton = (Button) view.findViewById(R.id.button_cancel_matdetail_modify);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("修改材料详细信息");

        progressBar.setVisibility(View.GONE);
        submitButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);

        //初始化，填入当前库存品详细信息
        final MaterialStock materialStock = (MaterialStock) getArguments().getSerializable("stock");
        textId.setText(String.valueOf(materialStock.getId()));
        textType.setText(materialStock.getType());
        String storestate = materialStock.getStorestate();
        if (storestate.equals("在用")) {
            spinnerStorestate.setSelection(0);
        } else if (storestate.equals("闲置可用")) {
            spinnerStorestate.setSelection(1);
        } else if(storestate.equals("闲置可租")) {
            spinnerStorestate.setSelection(2);
        } else {
            spinnerStorestate.setSelection(3);
        }
        textMark.setText(materialStock.getMark());
        textBand.setText(materialStock.getBand());
        textOriginal.setText(materialStock.getOriginal());
        textYear.setText(materialStock.getYear());
        textState.setText(materialStock.getState());
        textPosition.setText(materialStock.getPosition());
        textUnit.setText(materialStock.getUnit());
        textDescription.setText(materialStock.getDescription());

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                hideKeyboard(); //隐藏虚拟键盘

                boolean idValid = false;
                boolean typeValid = false;
                boolean markValid = false;
                boolean bandValid = false;
                boolean originalValid = false;
                boolean stateValid = false;
                boolean positionValid = false;
                boolean unitValid = false;

                //检查标号是否合法
                TextInputLayout wrapperId = (TextInputLayout) view.findViewById(R.id.wrapper_id_matdetail_modify);
                final String id = textId.getText().toString();
                LogUtil.d("Mat Detail Modify id", id);
                if (id.length() == 0) {
                    wrapperId.setError("不能为空！");
                } else if (id.length() > 100) {
                    wrapperId.setError("长度不能超过100个字符！");
                } else {
                    wrapperId.setErrorEnabled(false);
                    idValid = true;
                }

                //检查类型是否合法
                TextInputLayout wrapperType = (TextInputLayout) view.findViewById(R.id.wrapper_type_matdetail_modify);
                final String type = textType.getText().toString();
                LogUtil.d("Mat Detail Modify type", type);
                if (type.length() > 100) {
                    wrapperType.setError("长度不能超过100个字符！");
                } else {
                    wrapperType.setErrorEnabled(false);
                    typeValid = true;
                }

                //检查型号是否合法
                TextInputLayout wrapperMark = (TextInputLayout) view.findViewById(R.id.wrapper_mark_matdetail_modify);
                final String mark = textMark.getText().toString();
                LogUtil.d("Mat Detail Modify mark", mark);
                if (mark.length() > 100) {
                    wrapperMark.setError("长度不能超过100个字符！");
                } else {
                    wrapperMark.setErrorEnabled(false);
                    markValid = true;
                }

                //检查品牌是否合法
                TextInputLayout wrapperBand = (TextInputLayout) view.findViewById(R.id.wrapper_band_matdetail_modify);
                final String band = textBand.getText().toString();
                LogUtil.d("Mat Detail Modify band", band);
                if (band.length() > 100) {
                    wrapperBand.setError("长度不能超过100个字符！");
                } else {
                    wrapperBand.setErrorEnabled(false);
                    bandValid = true;
                }

                //检查原产地是否合法
                TextInputLayout wrapperOriginal = (TextInputLayout) view.findViewById(R.id.wrapper_original_matdetail_modify);
                final String original = textOriginal.getText().toString();
                LogUtil.d("Mat Detail Modify original", original);
                if (original.length() > 100) {
                    wrapperOriginal.setError("长度不能超过100个字符！");
                } else {
                    wrapperOriginal.setErrorEnabled(false);
                    originalValid = true;
                }

                //检查年份是否合法
                final TextInputLayout wrapperYear = (TextInputLayout) view.findViewById(R.id.wrapper_year_matdetail_modify);
                wrapperYear.setErrorEnabled(false);

                //检查状态是否合法
                TextInputLayout wrapperState = (TextInputLayout) view.findViewById(R.id.wrapper_state_matdetail_modify);
                final String state = textState.getText().toString();
                LogUtil.d("Mat Detail Modify state", state);
                if (state.length() > 100) {
                    wrapperState.setError("长度不能超过100个字符！");
                } else {
                    wrapperState.setErrorEnabled(false);
                    stateValid = true;
                }

                //检查位置是否合法
                TextInputLayout wrapperPosition = (TextInputLayout) view.findViewById(R.id.wrapper_position_matdetail_modify);
                final String position = textPosition.getText().toString();
                LogUtil.d("Mat Detail Modify position", position);
                if (position.length() > 100) {
                    wrapperPosition.setError("长度不能超过100个字符！");
                } else {
                    wrapperPosition.setErrorEnabled(false);
                    positionValid = true;
                }

                //检查单位原值是否合法
                final TextInputLayout wrapperUnit = (TextInputLayout) view.findViewById(R.id.wrapper_unit_matdetail_modify);
                final String unit = textUnit.getText().toString();
                LogUtil.d("Mat Detail Modify unit", unit);
                /*if (unit.length() == 0) {
                    wrapperUnit.setError("不能为空！");
                } else {
                    wrapperUnit.setErrorEnabled(false);
                    unitValid = true;
                }*/
                wrapperUnit.setErrorEnabled(false);
                unitValid = true;

                if (!(idValid && typeValid && markValid && bandValid && originalValid &&
                        stateValid && positionValid && unitValid)) {
                    Toast.makeText(getActivity(), "有字段填写错误，请检查并修改", Toast.LENGTH_SHORT).show();
                }

                if (idValid && typeValid && markValid && bandValid && originalValid &&
                        stateValid && positionValid && unitValid) {
                    submitButton.setVisibility(View.GONE); //隐藏提交按钮
                    cancelButton.setVisibility(View.GONE); //隐藏取消按钮
                    progressBar.setVisibility(View.VISIBLE); //显示进度条

                    final String storestate = (String) spinnerStorestate.getSelectedItem();
                    final String year = textYear.getText().toString();
                    final String description = textDescription.getText().toString();

                    HttpUtil.modifyMatDetail(String.valueOf(materialStock.getDatabaseid()), id, type, storestate, mark, band, original, year, state,
                            position, unit, description, new okhttp3.Callback() {
                                @Override
                                public void onResponse(final Call call, Response response) throws IOException {
                                    try {
                                        String responseData = response.body().string();
                                        LogUtil.d("Mat Detail Modify json", responseData);
                                        JSONObject jsonObject = new JSONObject(responseData);
                                        if (jsonObject.has("id")) {
                                            //修改成功
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
                                                            //回到库存品详细信息碎片
                                                            MatDetailFragment matDetailFragment = new MatDetailFragment();
                                                            MaterialStock materialStock1 = new MaterialStock(materialStock.getDatabaseid(), id, type,
                                                                    storestate, mark, band, original, year, state, position, unit, description, materialStock.getNum());
                                                            Bundle args = new Bundle();
                                                            args.putSerializable("stock", materialStock1);
                                                            matDetailFragment.setArguments(args);
                                                            replaceFragment(matDetailFragment);
                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            });
                                        } else if (jsonObject.has("materialID")) {
                                            //修改的材料标号与数据库中有重复
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressBar.setVisibility(View.GONE);
                                                    submitButton.setVisibility(View.VISIBLE);
                                                    cancelButton.setVisibility(View.VISIBLE);
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                    builder.setTitle("提示");
                                                    builder.setMessage("修改的材料标号与数据库中有重复！");
                                                    builder.setCancelable(false);
                                                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {

                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            });
                                        } else if (jsonObject.has("detail")) {
                                            //没有找到相关记录，说明该库存品已被删除或标号已被修改
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressBar.setVisibility(View.GONE);
                                                    submitButton.setVisibility(View.VISIBLE);
                                                    cancelButton.setVisibility(View.VISIBLE);
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                    builder.setTitle("提示");
                                                    builder.setMessage("没有找到相关记录！");
                                                    builder.setCancelable(false);
                                                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            if (SearchRecord.lastFrag == SearchRecord.FRAGLABEL_STORAGE) {
                                                                //由所有库存品信息碎片跳转而来
                                                                replaceFragment(new MatStorageFragment());
                                                            } else if (SearchRecord.lastFrag == SearchRecord.FRAGLABEL_SEARCH) {
                                                                //由查找结果碎片跳转而来
                                                                replaceFragment(new MatSearchResultFragment());
                                                            }
                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            });
                                        } else {
                                            final List<Integer> errorType = new ArrayList<Integer>();
                                            if (jsonObject.has("materialUnit")) {
                                                String unitErr = jsonObject.getString("materialUnit");
                                                if (unitErr.equals("[\"A valid number is required.\"]")) {
                                                    //非数字
                                                    errorType.add(1);
                                                }
                                                if (unitErr.equals("[\"Ensure that there are no more than 8 digits in total.\"]")) {
                                                    //数字不能超过8位
                                                    errorType.add(2);
                                                }
                                                if (unitErr.equals("[\"Ensure that there are no more than 6 digits before the decimal point.\"]")) {
                                                    //小数点前不能超过6位
                                                    errorType.add(3);
                                                }
                                                if (unitErr.equals("[\"Ensure that there are no more than 2 decimal places.\"]")) {
                                                    //小数点后不能超过2位
                                                    errorType.add(4);
                                                }
                                                if (unitErr.equals("[\"String value too large.\"]")) {
                                                    //字符串值过大，按数字不能超过8位处理
                                                    errorType.add(5);
                                                }
                                            }

                                            if (jsonObject.has("materialYear")) {
                                                //年份格式错误
                                                errorType.add(6);
                                            }

                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressBar.setVisibility(View.GONE);
                                                    submitButton.setVisibility(View.VISIBLE);
                                                    cancelButton.setVisibility(View.VISIBLE);

                                                    if (errorType.size() == 0) {
                                                        //发生未预计到的错误
                                                        Toast.makeText(getActivity(), "修改失败，请重新尝试", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getActivity(), "有字段填写错误，请检查并修改", Toast.LENGTH_SHORT).show();
                                                        if (errorType.contains(1)) {
                                                            wrapperUnit.setError("该输入非数字！");
                                                        }
                                                        if (errorType.contains(2) || errorType.contains(5)) {
                                                            wrapperUnit.setError("数字不能超过8位！");
                                                        }
                                                        if (errorType.contains(3)) {
                                                            wrapperUnit.setError("小数点前不能超过6位！");
                                                        }
                                                        if (errorType.contains(4)) {
                                                            wrapperUnit.setError("小数点后不能超过2位！");
                                                        }
                                                        if (errorType.contains(6)) {
                                                            wrapperYear.setError("格式错误，正确格式如2000-01-01！");
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
                                public void onFailure(Call call, IOException e) {
                                    e.printStackTrace();
                                    LogUtil.d("Modify Mat Detail", "failed");
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE);
                                            submitButton.setVisibility(View.VISIBLE);
                                            cancelButton.setVisibility(View.VISIBLE);
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
            public void onClick(View view) {
                //传递对象
                MatDetailFragment matDetailFragment = new MatDetailFragment();
                Bundle args = new Bundle();
                args.putSerializable("stock", materialStock);
                matDetailFragment.setArguments(args);
                replaceFragment(matDetailFragment);
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
}
