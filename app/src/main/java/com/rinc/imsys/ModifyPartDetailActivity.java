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
 * Created by ZhouZhi on 2017/9/27.
 */

public class ModifyPartDetailActivity extends BaseActivity {

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

    private EditText textName;

    private EditText textCompany;

    private EditText textMachineName;

    private EditText textMachineType;

    private EditText textMachineBand;

    private EditText textCondition;

    private EditText textVulnerability;

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
        setContentView(R.layout.activity_modifypartdetail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_modifypartdetail);
        toolbar.setTitle(getString(R.string.modify_part_detail));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        textId = (EditText) findViewById(R.id.input_id_partdetail_modify);
        textType = (EditText) findViewById(R.id.input_type_partdetail_modify);
        spinnerStorestate = (Spinner) findViewById(R.id.spinner_state_partdetail_modify);
        textMark = (EditText) findViewById(R.id.input_mark_partdetail_modify);
        textBand = (EditText) findViewById(R.id.input_band_partdetail_modify);
        textOriginal = (EditText) findViewById(R.id.input_original_partdetail_modify);
        textYear = (EditText) findViewById(R.id.input_year_partdetail_modify);
        textState = (EditText) findViewById(R.id.input_state_partdetail_modify);
        textPosition = (EditText) findViewById(R.id.input_position_partdetail_modify);
        textUnit = (EditText) findViewById(R.id.input_unit_partdetail_modify);
        textName = (EditText) findViewById(R.id.input_name_partdetail_modify);
        textCompany = (EditText) findViewById(R.id.input_company_partdetail_modify);
        textMachineName = (EditText) findViewById(R.id.input_machinename_partdetail_modify);
        textMachineType = (EditText) findViewById(R.id.input_machinetype_partdetail_modify);
        textMachineBand = (EditText) findViewById(R.id.input_machineband_partdetail_modify);
        textCondition = (EditText) findViewById(R.id.input_condition_partdetail_modify);
        textVulnerability = (EditText) findViewById(R.id.input_vul_partdetail_modify);
        textDescription = (EditText) findViewById(R.id.input_description_partdetail_modify);
        progressBar = (ProgressBar) findViewById(R.id.progressbar_partdetail_modify);
        submitButton = (Button) findViewById(R.id.button_submit_partdetail_modify);

        progressBar.setVisibility(View.GONE);
        submitButton.setVisibility(View.VISIBLE);

        //初始化，填入当前库存品详细信息
        final Intent intent = getIntent();
        final PartStock partStock = (PartStock) intent.getSerializableExtra("stock");
        textId.setText(partStock.getId());
        textType.setText(partStock.getType());
        String storestate = partStock.getStorestate();
        if (storestate.equals("在用")) {
            spinnerStorestate.setSelection(0);
        } else if (storestate.equals("闲置可用")) {
            spinnerStorestate.setSelection(1);
        } else if(storestate.equals("闲置可租")) {
            spinnerStorestate.setSelection(2);
        } else {
            spinnerStorestate.setSelection(3);
        }
        textMark.setText(partStock.getMark());
        textBand.setText(partStock.getBand());
        textOriginal.setText(partStock.getOriginal());
        textYear.setText(partStock.getYear());
        textState.setText(partStock.getState());
        textPosition.setText(partStock.getPosition());
        textUnit.setText(partStock.getUnit());
        textName.setText(partStock.getName());
        textCompany.setText(partStock.getCompany());
        textMachineName.setText(partStock.getMachineName());
        textMachineType.setText(partStock.getMachineType());
        textMachineBand.setText(partStock.getMachineBand());
        textCondition.setText(partStock.getCondition());
        textVulnerability.setText(partStock.getVulnerability());
        textDescription.setText(partStock.getDescription());

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(); //隐藏虚拟键盘

                boolean idValid = false;
                boolean typeValid = false;
                boolean markValid = false;
                boolean bandValid = false;
                boolean originalValid = false;
                boolean stateValid = false;
                boolean positionValid = false;
                boolean unitValid = false;
                boolean nameValid = false;
                boolean companyValid = false;
                boolean machineNameValid = false;
                boolean machineTypeValid = false;
                boolean machineBandValid = false;
                boolean conditionValid = false;
                boolean vulnerabilityValid = false;

                //检查标号是否合法
                TextInputLayout wrapperId = (TextInputLayout) findViewById(R.id.wrapper_id_partdetail_modify);
                final String id = textId.getText().toString();
                LogUtil.d("Part Detail Modify id", id);
                if (id.length() == 0) {
                    wrapperId.setError(getString(R.string.not_empty));
                } else if (id.length() > 100) {
                    wrapperId.setError(getString(R.string.too_long));
                } else {
                    wrapperId.setErrorEnabled(false);
                    idValid = true;
                }

                //检查类型是否合法
                TextInputLayout wrapperType = (TextInputLayout) findViewById(R.id.wrapper_type_partdetail_modify);
                final String type = textType.getText().toString();
                LogUtil.d("Part Detail Modify type", type);
                if (type.length() > 100) {
                    wrapperType.setError(getString(R.string.too_long));
                } else {
                    wrapperType.setErrorEnabled(false);
                    typeValid = true;
                }

                //检查型号是否合法
                TextInputLayout wrapperMark = (TextInputLayout) findViewById(R.id.wrapper_mark_partdetail_modify);
                final String mark = textMark.getText().toString();
                LogUtil.d("Part Detail Modify mark", mark);
                if (mark.length() > 100) {
                    wrapperMark.setError(getString(R.string.too_long));
                } else {
                    wrapperMark.setErrorEnabled(false);
                    markValid = true;
                }

                //检查品牌是否合法
                TextInputLayout wrapperBand = (TextInputLayout) findViewById(R.id.wrapper_band_partdetail_modify);
                final String band = textBand.getText().toString();
                LogUtil.d("Part Detail Modify band", band);
                if (band.length() > 100) {
                    wrapperBand.setError(getString(R.string.too_long));
                } else {
                    wrapperBand.setErrorEnabled(false);
                    bandValid = true;
                }

                //检查原产地是否合法
                TextInputLayout wrapperOriginal = (TextInputLayout) findViewById(R.id.wrapper_original_partdetail_modify);
                final String original = textOriginal.getText().toString();
                LogUtil.d("Part Detail Modify original", original);
                if (original.length() > 100) {
                    wrapperOriginal.setError(getString(R.string.too_long));
                } else {
                    wrapperOriginal.setErrorEnabled(false);
                    originalValid = true;
                }

                //检查年份是否合法
                final TextInputLayout wrapperYear = (TextInputLayout) findViewById(R.id.wrapper_year_partdetail_modify);
                wrapperYear.setErrorEnabled(false);

                //检查状况是否合法
                TextInputLayout wrapperState = (TextInputLayout) findViewById(R.id.wrapper_state_partdetail_modify);
                final String state = textState.getText().toString();
                LogUtil.d("Part Detail Modify state", state);
                if (state.length() > 100) {
                    wrapperState.setError(getString(R.string.too_long));
                } else {
                    wrapperState.setErrorEnabled(false);
                    stateValid = true;
                }

                //检查位置是否合法
                TextInputLayout wrapperPosition = (TextInputLayout) findViewById(R.id.wrapper_position_partdetail_modify);
                final String position = textPosition.getText().toString();
                LogUtil.d("Part Detail Modify position", position);
                if (position.length() > 100) {
                    wrapperPosition.setError(getString(R.string.too_long));
                } else {
                    wrapperPosition.setErrorEnabled(false);
                    positionValid = true;
                }

                //检查单位原值是否合法
                final TextInputLayout wrapperUnit = (TextInputLayout) findViewById(R.id.wrapper_unit_partdetail_modify);
                final String unit = textUnit.getText().toString();
                LogUtil.d("Part Detail Modify unit", unit);
                wrapperUnit.setErrorEnabled(false);
                unitValid = true;

                //检查名称是否合法
                TextInputLayout wrapperName = (TextInputLayout) findViewById(R.id.wrapper_name_partdetail_modify);
                final String name = textName.getText().toString();
                LogUtil.d("Part Detail Modify name", name);
                if (name.length() > 100) {
                    wrapperName.setError(getString(R.string.too_long));
                } else {
                    wrapperName.setErrorEnabled(false);
                    nameValid = true;
                }

                //检查制造企业是否合法
                TextInputLayout wrapperCompany = (TextInputLayout) findViewById(R.id.wrapper_company_partdetail_modify);
                final String company = textCompany.getText().toString();
                LogUtil.d("Part Detail Modify company", company);
                if (company.length() > 100) {
                    wrapperCompany.setError(getString(R.string.too_long));
                } else {
                    wrapperCompany.setErrorEnabled(false);
                    companyValid = true;
                }

                //检查整机名称是否合法
                TextInputLayout wrapperMachineName = (TextInputLayout) findViewById(R.id.wrapper_machinename_partdetail_modify);
                final String machineName = textMachineName.getText().toString();
                LogUtil.d("Part Detail Modify machineName", machineName);
                if (machineName.length() > 100) {
                    wrapperMachineName.setError(getString(R.string.too_long));
                } else {
                    wrapperMachineName.setErrorEnabled(false);
                    machineNameValid = true;
                }

                //检查整机型号是否合法
                TextInputLayout wrapperMachineType = (TextInputLayout) findViewById(R.id.wrapper_machinetype_partdetail_modify);
                final String machineType = textMachineType.getText().toString();
                LogUtil.d("Part Detail Modify machineType", machineType);
                if (machineType.length() > 100) {
                    wrapperMachineType.setError(getString(R.string.too_long));
                } else {
                    wrapperMachineType.setErrorEnabled(false);
                    machineTypeValid = true;
                }

                //检查整机品牌是否合法
                TextInputLayout wrapperMachineBand = (TextInputLayout) findViewById(R.id.wrapper_machineband_partdetail_modify);
                final String machineBand = textMachineBand.getText().toString();
                LogUtil.d("Part Detail Modify machineBand", machineBand);
                if (machineBand.length() > 100) {
                    wrapperMachineBand.setError(getString(R.string.too_long));
                } else {
                    wrapperMachineBand.setErrorEnabled(false);
                    machineBandValid = true;
                }

                //检查储存条件是否合法
                TextInputLayout wrapperCondition = (TextInputLayout) findViewById(R.id.wrapper_condition_partdetail_modify);
                final String condition = textCondition.getText().toString();
                LogUtil.d("Part Detail Modify condition", condition);
                if (condition.length() > 100) {
                    wrapperCondition.setError(getString(R.string.too_long));
                } else {
                    wrapperCondition.setErrorEnabled(false);
                    conditionValid = true;
                }

                //检查易损性是否合法
                TextInputLayout wrapperVul = (TextInputLayout) findViewById(R.id.wrapper_vul_partdetail_modify);
                final String vulnerability = textVulnerability.getText().toString();
                LogUtil.d("Part Detail Modify vul", vulnerability);
                if (vulnerability.length() > 100) {
                    wrapperVul.setError(getString(R.string.too_long));
                } else {
                    wrapperVul.setErrorEnabled(false);
                    vulnerabilityValid = true;
                }

                if (!(idValid && typeValid && markValid && bandValid && originalValid && stateValid &&
                        positionValid && unitValid && nameValid && companyValid && machineNameValid &&
                        machineTypeValid && machineBandValid && conditionValid && vulnerabilityValid)) {
                    Toast.makeText(ModifyPartDetailActivity.this, getString(R.string.check_and_modify), Toast.LENGTH_SHORT).show();
                }

                if (idValid && typeValid && markValid && bandValid && originalValid && stateValid &&
                        positionValid && unitValid && nameValid && companyValid && machineNameValid &&
                        machineTypeValid && machineBandValid && conditionValid && vulnerabilityValid) {
                    submitButton.setVisibility(View.GONE); //隐藏提交按钮
                    progressBar.setVisibility(View.VISIBLE); //显示进度条

                    final String storestate;
                    int positionSelected = spinnerStorestate.getSelectedItemPosition();
                    if (positionSelected == 0) {
                        storestate = "在用";
                    } else if (positionSelected == 1) {
                        storestate = "闲置可用";
                    } else if (positionSelected == 2) {
                        storestate = "闲置可租";
                    } else {
                        storestate = "闲置可售";
                    }
                    final String year = textYear.getText().toString();
                    final String description = textDescription.getText().toString();

                    HttpUtil.modifyPartDetail(String.valueOf(partStock.getDatabaseid()), id, type, storestate, mark, band, original, year, state, position, unit,
                            name, company, machineName, machineType, machineBand, condition, vulnerability, description, new okhttp3.Callback() {
                                @Override
                                public void onResponse(final Call call, Response response) throws IOException {
                                    try {
                                        String responseData = response.body().string();
                                        LogUtil.d("Part Detail Modify json", responseData);
                                        JSONObject jsonObject = new JSONObject(responseData);
                                        if (jsonObject.has("id")) {
                                            //修改成功
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressBar.setVisibility(View.GONE);
                                                    submitButton.setVisibility(View.VISIBLE);
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(ModifyPartDetailActivity.this);
                                                    builder.setTitle(getString(R.string.hint));
                                                    builder.setMessage(getString(R.string.modify_successful));
                                                    builder.setCancelable(false);
                                                    builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            //回到库存品详细信息碎片
                                                            Intent intent1 = new Intent();
                                                            PartStock partStock1 = new PartStock(partStock.getDatabaseid(), id, type, storestate, mark,
                                                                    band, original, year, state, position, unit, name, company, machineName, machineType,
                                                                    machineBand, condition, vulnerability, description, partStock.getNum());
                                                            intent1.putExtra("stock", partStock1);
                                                            setResult(RESULT_OK, intent1);
                                                            finish();
                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            });
                                        } else if (jsonObject.has("partID")) {
                                            //修改的备件标号与数据库中有重复
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressBar.setVisibility(View.GONE);
                                                    submitButton.setVisibility(View.VISIBLE);
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(ModifyPartDetailActivity.this);
                                                    builder.setTitle(getString(R.string.hint));
                                                    builder.setMessage(getString(R.string.part_id_exist));
                                                    builder.setCancelable(false);
                                                    builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
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
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(ModifyPartDetailActivity.this);
                                                    builder.setTitle(getString(R.string.hint));
                                                    builder.setMessage(getString(R.string.no_related_info_found));
                                                    builder.setCancelable(false);
                                                    builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
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
                                            if (jsonObject.has("partUnit")) {
                                                String unitErr = jsonObject.getString("partUnit");
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

                                            if (jsonObject.has("partYear")) {
                                                //年份格式错误
                                                errorType.add(6);
                                            }

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressBar.setVisibility(View.GONE);
                                                    submitButton.setVisibility(View.VISIBLE);

                                                    if (errorType.size() == 0) {
                                                        //发生未预计到的错误
                                                        Toast.makeText(ModifyPartDetailActivity.this, getString(R.string.modify_failed), Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(ModifyPartDetailActivity.this, getString(R.string.check_and_modify), Toast.LENGTH_SHORT).show();
                                                        if (errorType.contains(1)) {
                                                            wrapperUnit.setError(getString(R.string.not_number));
                                                        }
                                                        if (errorType.contains(2) || errorType.contains(5)) {
                                                            wrapperUnit.setError(getString(R.string.num_too_long_8));
                                                        }
                                                        if (errorType.contains(3)) {
                                                            wrapperUnit.setError(getString(R.string.decimal_6));
                                                        }
                                                        if (errorType.contains(4)) {
                                                            wrapperUnit.setError(getString(R.string.decimal_2));
                                                        }
                                                        if (errorType.contains(6)) {
                                                            wrapperYear.setError(getString(R.string.year_format_error));
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
                                    LogUtil.d("Modify Part Detail", "failed");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE);
                                            submitButton.setVisibility(View.VISIBLE);
                                            Toast.makeText(ModifyPartDetailActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                }
            }
        });
    }
}
