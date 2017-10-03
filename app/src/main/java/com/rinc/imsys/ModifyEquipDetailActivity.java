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
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ZhouZhi on 2017/9/29.
 */

public class ModifyEquipDetailActivity extends BaseActivity {

    private EditText textId;

    private EditText textType;

    private Spinner spinnerStorestate;

    private EditText textMark;

    private EditText textBand;

    private EditText textOriginal;

    private EditText textYear;

    private EditText textHour;

    private EditText textState;

    private EditText textPosition;

    private EditText textUnit;

    private EditText textDescription;

    private ProgressBar progressBar;

    private Button submitButton;
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifyequipdetail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_modifyequipdetail);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        textId = (EditText) findViewById(R.id.input_id_equipdetail_modify);
        textType = (EditText) findViewById(R.id.input_type_equipdetail_modify);
        spinnerStorestate = (Spinner) findViewById(R.id.spinner_state_equipdetail_modify);
        textMark = (EditText) findViewById(R.id.input_mark_equipdetail_modify);
        textHour = (EditText) findViewById(R.id.input_hour_equipdetail_modify);
        textBand = (EditText) findViewById(R.id.input_band_equipdetail_modify);
        textOriginal = (EditText) findViewById(R.id.input_original_equipdetail_modify);
        textYear = (EditText) findViewById(R.id.input_year_equipdetail_modify);
        textState = (EditText) findViewById(R.id.input_state_equipdetail_modify);
        textPosition = (EditText) findViewById(R.id.input_position_equipdetail_modify);
        textUnit = (EditText) findViewById(R.id.input_unit_equipdetail_modify);
        textDescription = (EditText) findViewById(R.id.input_description_equipdetail_modify);
        progressBar = (ProgressBar) findViewById(R.id.progressbar_equipdetail_modify);
        submitButton = (Button) findViewById(R.id.button_submit_equipdetail_modify);

        progressBar.setVisibility(View.GONE);
        submitButton.setVisibility(View.VISIBLE);

        //初始化，填入当前库存品详细信息
        final Intent intent = getIntent();
        final EquipStock equipStock = (EquipStock) intent.getSerializableExtra("stock");
        textId.setText(equipStock.getId());
        textType.setText(equipStock.getType());
        String storestate = equipStock.getStorestate();
        if (storestate.equals("在用")) {
            spinnerStorestate.setSelection(0);
        } else if (storestate.equals("闲置可用")) {
            spinnerStorestate.setSelection(1);
        } else if(storestate.equals("闲置可租")) {
            spinnerStorestate.setSelection(2);
        } else {
            spinnerStorestate.setSelection(3);
        }
        textMark.setText(equipStock.getMark());
        textHour.setText(equipStock.getHour());
        textBand.setText(equipStock.getBand());
        textOriginal.setText(equipStock.getOriginal());
        textYear.setText(equipStock.getYear());
        textState.setText(equipStock.getState());
        textPosition.setText(equipStock.getPosition());
        textUnit.setText(equipStock.getUnit());
        textDescription.setText(equipStock.getDescription());

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(); //隐藏虚拟键盘

                boolean idValid = false;
                boolean typeValid = false;
                boolean markValid = false;
                boolean hourValid = false;
                boolean bandValid = false;
                boolean originalValid = false;
                boolean stateValid = false;
                boolean positionValid = false;
                boolean unitValid = false;

                //检查标号是否合法
                TextInputLayout wrapperId = (TextInputLayout) findViewById(R.id.wrapper_id_equipdetail_modify);
                final String id = textId.getText().toString();
                LogUtil.d("Equip Detail Modify id", id);
                if (id.length() == 0) {
                    wrapperId.setError("不能为空！");
                } else if (id.length() > 100) {
                    wrapperId.setError("长度不能超过100个字符！");
                } else {
                    wrapperId.setErrorEnabled(false);
                    idValid = true;
                }

                //检查类型是否合法
                TextInputLayout wrapperType = (TextInputLayout) findViewById(R.id.wrapper_type_equipdetail_modify);
                final String type = textType.getText().toString();
                LogUtil.d("Equip Detail Modify type", type);
                if (type.length() > 100) {
                    wrapperType.setError("长度不能超过100个字符！");
                } else {
                    wrapperType.setErrorEnabled(false);
                    typeValid = true;
                }

                //检查型号是否合法
                TextInputLayout wrapperMark = (TextInputLayout) findViewById(R.id.wrapper_mark_equipdetail_modify);
                final String mark = textMark.getText().toString();
                LogUtil.d("Equip Detail Modify mark", mark);
                if (mark.length() > 100) {
                    wrapperMark.setError("长度不能超过100个字符！");
                } else {
                    wrapperMark.setErrorEnabled(false);
                    markValid = true;
                }

                //检查工作小时是否合法
                final TextInputLayout wrapperHour = (TextInputLayout) findViewById(R.id.wrapper_hour_equipdetail_modify);
                final String hour = textHour.getText().toString();
                LogUtil.d("Equip in hour", hour);
                wrapperHour.setErrorEnabled(false);
                hourValid = true;

                //检查品牌是否合法
                TextInputLayout wrapperBand = (TextInputLayout) findViewById(R.id.wrapper_band_equipdetail_modify);
                final String band = textBand.getText().toString();
                LogUtil.d("Equip Detail Modify band", band);
                if (band.length() > 100) {
                    wrapperBand.setError("长度不能超过100个字符！");
                } else {
                    wrapperBand.setErrorEnabled(false);
                    bandValid = true;
                }

                //检查原产地是否合法
                TextInputLayout wrapperOriginal = (TextInputLayout) findViewById(R.id.wrapper_original_equipdetail_modify);
                final String original = textOriginal.getText().toString();
                LogUtil.d("Equip Detail Modify original", original);
                if (original.length() > 100) {
                    wrapperOriginal.setError("长度不能超过100个字符！");
                } else {
                    wrapperOriginal.setErrorEnabled(false);
                    originalValid = true;
                }

                //检查年份是否合法
                final TextInputLayout wrapperYear = (TextInputLayout) findViewById(R.id.wrapper_year_equipdetail_modify);
                wrapperYear.setErrorEnabled(false);

                //检查状况是否合法
                TextInputLayout wrapperState = (TextInputLayout) findViewById(R.id.wrapper_state_equipdetail_modify);
                final String state = textState.getText().toString();
                LogUtil.d("Equip Detail Modify state", state);
                if (state.length() > 100) {
                    wrapperState.setError("长度不能超过100个字符！");
                } else {
                    wrapperState.setErrorEnabled(false);
                    stateValid = true;
                }

                //检查位置是否合法
                TextInputLayout wrapperPosition = (TextInputLayout) findViewById(R.id.wrapper_position_equipdetail_modify);
                final String position = textPosition.getText().toString();
                LogUtil.d("Equip Detail Modify position", position);
                if (position.length() > 100) {
                    wrapperPosition.setError("长度不能超过100个字符！");
                } else {
                    wrapperPosition.setErrorEnabled(false);
                    positionValid = true;
                }

                //检查单位原值是否合法
                final TextInputLayout wrapperUnit = (TextInputLayout) findViewById(R.id.wrapper_unit_equipdetail_modify);
                final String unit = textUnit.getText().toString();
                LogUtil.d("Equip Detail Modify unit", unit);
                wrapperUnit.setErrorEnabled(false);
                unitValid = true;

                if (!(idValid && typeValid && markValid && hourValid && bandValid && originalValid &&
                        stateValid && positionValid && unitValid)) {
                    Toast.makeText(ModifyEquipDetailActivity.this, "有字段填写错误，请检查并修改", Toast.LENGTH_SHORT).show();
                }

                if (idValid && typeValid && markValid && hourValid && bandValid && originalValid &&
                        stateValid && positionValid && unitValid) {
                    submitButton.setVisibility(View.GONE); //隐藏提交按钮
                    progressBar.setVisibility(View.VISIBLE); //显示进度条

                    final String storestate = (String) spinnerStorestate.getSelectedItem();
                    final String year = textYear.getText().toString();
                    final String description = textDescription.getText().toString();

                    HttpUtil.modifyEquipDetail(String.valueOf(equipStock.getDatabaseid()), id, type, storestate, mark, hour, band, original, year, state,
                            position, unit, description, new okhttp3.Callback() {
                                @Override
                                public void onResponse(final Call call, Response response) throws IOException {
                                    try {
                                        String responseData = response.body().string();
                                        LogUtil.d("Equip Detail Modify json", responseData);
                                        JSONObject jsonObject = new JSONObject(responseData);
                                        if (jsonObject.has("id")) {
                                            //修改成功
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressBar.setVisibility(View.GONE);
                                                    submitButton.setVisibility(View.VISIBLE);
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(ModifyEquipDetailActivity.this);
                                                    builder.setTitle("提示");
                                                    builder.setMessage("修改成功！");
                                                    builder.setCancelable(false);
                                                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            //回到库存品详细信息碎片
                                                            Intent intent1 = new Intent();
                                                            EquipStock equipStock1 = new EquipStock(equipStock.getDatabaseid(), id, type,
                                                                    storestate, mark, hour, band, original, year, state, position, unit, description, equipStock.getNum());
                                                            intent1.putExtra("stock", equipStock1);
                                                            setResult(RESULT_OK, intent1);
                                                            finish();
                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            });
                                        } else if (jsonObject.has("equipmentID")) {
                                            //修改的整机标号与数据库中有重复
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressBar.setVisibility(View.GONE);
                                                    submitButton.setVisibility(View.VISIBLE);
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(ModifyEquipDetailActivity.this);
                                                    builder.setTitle("提示");
                                                    builder.setMessage("修改的整机标号与数据库中有重复！");
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
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressBar.setVisibility(View.GONE);
                                                    submitButton.setVisibility(View.VISIBLE);
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(ModifyEquipDetailActivity.this);
                                                    builder.setTitle("提示");
                                                    builder.setMessage("没有找到相关记录！");
                                                    builder.setCancelable(false);
                                                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            Intent intent1 = new Intent();
                                                            intent1.putExtra("stock", "");
                                                            setResult(SearchRecord.RESULT_DELETE, intent1);
                                                            finish();
                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            });
                                        } else {
                                            final List<Integer> errorType = new ArrayList<Integer>();
                                            if (jsonObject.has("equipmentUnit")) {
                                                String unitErr = jsonObject.getString("equipmentUnit");
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

                                            if (jsonObject.has("equipmentYear")) {
                                                //年份格式错误
                                                errorType.add(6);
                                            }
                                            if (jsonObject.has("equipmentHour")) {
                                                String hourErr = jsonObject.getString("equipmentHour");
                                                if (hourErr.equals("[\"A valid integer is required.\"]")) {
                                                    //非整数
                                                    errorType.add(7);
                                                }
                                                if (hourErr.equals("[\"Ensure this value is less than or equal to 2147483647.\"]")) {
                                                    //数字过大
                                                    errorType.add(8);
                                                }
                                            }

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressBar.setVisibility(View.GONE);
                                                    submitButton.setVisibility(View.VISIBLE);

                                                    if (errorType.size() == 0) {
                                                        //发生未预计到的错误
                                                        Toast.makeText(ModifyEquipDetailActivity.this, "修改失败，请重新尝试", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(ModifyEquipDetailActivity.this, "有字段填写错误，请检查并修改", Toast.LENGTH_SHORT).show();
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
                                                        if (errorType.contains(7)) {
                                                            wrapperHour.setError("该输入非整数！");
                                                        }
                                                        if (errorType.contains(8)) {
                                                            wrapperHour.setError("数字不能大于2147483647！");
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
                                    LogUtil.d("Modify Equip Detail", "failed");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE);
                                            submitButton.setVisibility(View.VISIBLE);
                                            Toast.makeText(ModifyEquipDetailActivity.this, "网络连接失败，请重新尝试", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                }
            }
        });
    }
}
