package com.rinc.imsys;

import android.content.DialogInterface;
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
import android.widget.Spinner;
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
 * Created by ZhouZhi on 2017/9/10.
 */

public class PartInFragment extends BaseFragment {

    private Spinner stateSpinner;

    private Button submitButton;

    private EditText textId;

    private EditText textType;

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

    private EditText textDateTime;

    private EditText textOperator;

    private EditText textNum;

    private EditText textInputDescription;

    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_partin, container, false);

        stateSpinner = (Spinner) view.findViewById(R.id.spinner_state_partin);
        submitButton = (Button) view.findViewById(R.id.button_submit_partin);
        textId = (EditText) view.findViewById(R.id.input_id_partin);
        textType = (EditText) view.findViewById(R.id.input_type_partin);
        textMark = (EditText) view.findViewById(R.id.input_mark_partin);
        textBand = (EditText) view.findViewById(R.id.input_band_partin);
        textOriginal = (EditText) view.findViewById(R.id.input_original_partin);
        textYear = (EditText) view.findViewById(R.id.input_year_partin);
        textState = (EditText) view.findViewById(R.id.input_state_partin);
        textPosition = (EditText) view.findViewById(R.id.input_position_partin);
        textUnit = (EditText) view.findViewById(R.id.input_unit_partin);
        textName = (EditText) view.findViewById(R.id.input_name_partin);
        textCompany = (EditText) view.findViewById(R.id.input_company_partin);
        textMachineName = (EditText) view.findViewById(R.id.input_machinename_partin);
        textMachineType = (EditText) view.findViewById(R.id.input_machinetype_partin);
        textMachineBand = (EditText) view.findViewById(R.id.input_machineband_partin);
        textCondition = (EditText) view.findViewById(R.id.input_condition_partin);
        textVulnerability = (EditText) view.findViewById(R.id.input_vul_partin);
        textDescription = (EditText) view.findViewById(R.id.input_description_partin);
        textDateTime = (EditText) view.findViewById(R.id.input_date_partin);
        textOperator = (EditText) view.findViewById(R.id.input_operator_partin);
        textNum = (EditText) view.findViewById(R.id.input_num_partin);
        textInputDescription = (EditText) view.findViewById(R.id.input_inputdescription_partin);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_partin);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("备件入库");

        progressBar.setVisibility(View.GONE);
        submitButton.setVisibility(View.VISIBLE);

        //将入库时间默认设定为当前时间
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
                String typeScan = jsonObject.getString("type");
                String markScan = jsonObject.getString("mark");
                String bandScan = jsonObject.getString("band");
                String originalScan = jsonObject.getString("original");
                String yearScan = jsonObject.getString("year");
                String stateScan = jsonObject.getString("state");
                String positionScan = jsonObject.getString("position");
                String unitScan = jsonObject.getString("unit");
                String nameScan = jsonObject.getString("name");
                String companyScan = jsonObject.getString("company");
                String machineNameScan = jsonObject.getString("machineName");
                String machineTypeScan = jsonObject.getString("machineType");
                String machineBandScan = jsonObject.getString("machineBand");
                String conditionScan = jsonObject.getString("condition");
                String vulnerabilityScan = jsonObject.getString("vulnerability");
                String descriptionScan = jsonObject.getString("description");

                if (stockType.equals("part")) {
                    textId.setText(idScan);
                    textType.setText(typeScan);
                    textMark.setText(markScan);
                    textBand.setText(bandScan);
                    textOriginal.setText(originalScan);
                    textYear.setText(yearScan);
                    textState.setText(stateScan);
                    textPosition.setText(positionScan);
                    textUnit.setText(unitScan);
                    textName.setText(nameScan);
                    textCompany.setText(companyScan);
                    textMachineName.setText(machineNameScan);
                    textMachineType.setText(machineTypeScan);
                    textMachineBand.setText(machineBandScan);
                    textCondition.setText(conditionScan);
                    textVulnerability.setText(vulnerabilityScan);
                    textDescription.setText(descriptionScan);
                } else {
                    Toast.makeText(getActivity(), "非备件库二维码！", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "二维码格式错误！", Toast.LENGTH_SHORT).show();
            }
        }

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
                boolean nameValid = false;
                boolean companyValid = false;
                boolean machineNameValid = false;
                boolean machineTypeValid = false;
                boolean machineBandValid = false;
                boolean conditionValid = false;
                boolean vulnerabilityValid = false;
                boolean datetimeValid = false;
                boolean operatorValid = false;
                boolean numValid = false;

                //检查标号是否合法
                TextInputLayout wrapperId = (TextInputLayout) view.findViewById(R.id.wrapper_id_partin);
                String id = textId.getText().toString();
                LogUtil.d("Part in id", id);
                if (id.length() == 0) {
                    wrapperId.setError("不能为空！");
                } else if (id.length() > 100) {
                    wrapperId.setError("长度不能超过100个字符！");
                } else {
                    wrapperId.setErrorEnabled(false);
                    idValid = true;
                }

                //检查类型是否合法
                TextInputLayout wrapperType = (TextInputLayout) view.findViewById(R.id.wrapper_type_partin);
                String type = textType.getText().toString();
                LogUtil.d("Part in type", type);
                if (type.length() > 100) {
                    wrapperType.setError("长度不能超过100个字符！");
                } else {
                    wrapperType.setErrorEnabled(false);
                    typeValid = true;
                }

                //检查型号是否合法
                TextInputLayout wrapperMark = (TextInputLayout) view.findViewById(R.id.wrapper_mark_partin);
                String mark = textMark.getText().toString();
                LogUtil.d("Part in mark", mark);
                if (mark.length() > 100) {
                    wrapperMark.setError("长度不能超过100个字符！");
                } else {
                    wrapperMark.setErrorEnabled(false);
                    markValid = true;
                }

                //检查品牌是否合法
                TextInputLayout wrapperBand = (TextInputLayout) view.findViewById(R.id.wrapper_band_partin);
                String band = textBand.getText().toString();
                LogUtil.d("Part in band", band);
                if (band.length() > 100) {
                    wrapperBand.setError("长度不能超过100个字符！");
                } else {
                    wrapperBand.setErrorEnabled(false);
                    bandValid = true;
                }

                //检查原产地是否合法
                TextInputLayout wrapperOriginal = (TextInputLayout) view.findViewById(R.id.wrapper_original_partin);
                String original = textOriginal.getText().toString();
                LogUtil.d("Part in original", original);
                if (original.length() > 100) {
                    wrapperOriginal.setError("长度不能超过100个字符！");
                } else {
                    wrapperOriginal.setErrorEnabled(false);
                    originalValid = true;
                }

                //检查年份是否合法
                final TextInputLayout wrapperYear = (TextInputLayout) view.findViewById(R.id.wrapper_year_partin);
                wrapperYear.setErrorEnabled(false);

                //检查状态是否合法
                TextInputLayout wrapperState = (TextInputLayout) view.findViewById(R.id.wrapper_state_partin);
                String state = textState.getText().toString();
                LogUtil.d("Part in state", state);
                if (state.length() > 100) {
                    wrapperState.setError("长度不能超过100个字符！");
                } else {
                    wrapperState.setErrorEnabled(false);
                    stateValid = true;
                }

                //检查位置是否合法
                TextInputLayout wrapperPosition = (TextInputLayout) view.findViewById(R.id.wrapper_position_partin);
                String position = textPosition.getText().toString();
                LogUtil.d("Part in position", position);
                if (position.length() > 100) {
                    wrapperPosition.setError("长度不能超过100个字符！");
                } else {
                    wrapperPosition.setErrorEnabled(false);
                    positionValid = true;
                }

                //检查单位原值是否合法
                final TextInputLayout wrapperUnit = (TextInputLayout) view.findViewById(R.id.wrapper_unit_partin);
                final String unit = textUnit.getText().toString();
                LogUtil.d("Part in unit", unit);
                wrapperUnit.setErrorEnabled(false);
                unitValid = true;

                //检查名称是否合法
                TextInputLayout wrapperName = (TextInputLayout) view.findViewById(R.id.wrapper_name_partin);
                String name = textName.getText().toString();
                LogUtil.d("Part in name", name);
                if (name.length() > 100) {
                    wrapperName.setError("长度不能超过100个字符！");
                } else {
                    wrapperName.setErrorEnabled(false);
                    nameValid = true;
                }

                //检查制造企业是否合法
                TextInputLayout wrapperCompany = (TextInputLayout) view.findViewById(R.id.wrapper_company_partin);
                String company = textCompany.getText().toString();
                LogUtil.d("Part in company", company);
                if (company.length() > 100) {
                    wrapperCompany.setError("长度不能超过100个字符！");
                } else {
                    wrapperCompany.setErrorEnabled(false);
                    companyValid = true;
                }

                //检查整机名称是否合法
                TextInputLayout wrapperMachineName = (TextInputLayout) view.findViewById(R.id.wrapper_machinename_partin);
                String machineName = textMachineName.getText().toString();
                LogUtil.d("Part in machineName", machineName);
                if (machineName.length() > 100) {
                    wrapperMachineName.setError("长度不能超过100个字符！");
                } else {
                    wrapperMachineName.setErrorEnabled(false);
                    machineNameValid = true;
                }

                //检查整机型号是否合法
                TextInputLayout wrapperMachineType = (TextInputLayout) view.findViewById(R.id.wrapper_machinetype_partin);
                String machineType = textMachineType.getText().toString();
                LogUtil.d("Part in machineType", machineType);
                if (machineType.length() > 100) {
                    wrapperMachineType.setError("长度不能超过100个字符！");
                } else {
                    wrapperMachineType.setErrorEnabled(false);
                    machineTypeValid = true;
                }

                //检查整机品牌是否合法
                TextInputLayout wrapperMachineBand = (TextInputLayout) view.findViewById(R.id.wrapper_machineband_partin);
                String machineBand = textMachineBand.getText().toString();
                LogUtil.d("Part in machineBand", machineBand);
                if (machineBand.length() > 100) {
                    wrapperMachineBand.setError("长度不能超过100个字符！");
                } else {
                    wrapperMachineBand.setErrorEnabled(false);
                    machineBandValid = true;
                }

                //检查储存条件是否合法
                TextInputLayout wrapperCondition = (TextInputLayout) view.findViewById(R.id.wrapper_condition_partin);
                String condition = textCondition.getText().toString();
                LogUtil.d("Part in condition", condition);
                if (condition.length() > 100) {
                    wrapperCondition.setError("长度不能超过100个字符！");
                } else {
                    wrapperCondition.setErrorEnabled(false);
                    conditionValid = true;
                }

                //检查易损性是否合法
                TextInputLayout wrapperVul = (TextInputLayout) view.findViewById(R.id.wrapper_vul_partin);
                String vulnerability = textVulnerability.getText().toString();
                LogUtil.d("Part in vulnerability", vulnerability);
                if (vulnerability.length() > 100) {
                    wrapperVul.setError("长度不能超过100个字符！");
                } else {
                    wrapperVul.setErrorEnabled(false);
                    vulnerabilityValid = true;
                }

                //检查入库时间是否合法
                final TextInputLayout wrapperDatetime = (TextInputLayout) view.findViewById(R.id.wrapper_datetime_partin);
                String datetime = textDateTime.getText().toString();
                LogUtil.d("Part in datetime", datetime);
                if (datetime.length() == 0) {
                    wrapperDatetime.setError("不能为空！");
                } else {
                    wrapperDatetime.setErrorEnabled(false);
                    datetimeValid = true;
                }

                //检查操作人员是否合法
                TextInputLayout wrapperOperator = (TextInputLayout) view.findViewById(R.id.wrapper_operator_partin);
                String operator = textOperator.getText().toString();
                LogUtil.d("Part in operator", operator);
                if (operator.length() == 0) {
                    wrapperOperator.setError("不能为空！");
                } else if (operator.length() > 100) {
                    wrapperOperator.setError("长度不能超过100个字符！");
                } else {
                    wrapperOperator.setErrorEnabled(false);
                    operatorValid = true;
                }

                //检查入库量是否合法
                final TextInputLayout wrapperNum = (TextInputLayout) view.findViewById(R.id.wrapper_num_partin);
                String num = textNum.getText().toString();
                LogUtil.d("Part in num", num);
                if (num.length() == 0) {
                    wrapperNum.setError("不能为空！");
                } else {
                    wrapperNum.setErrorEnabled(false);
                    numValid = true;
                }

                if (!(idValid && typeValid && markValid && bandValid && originalValid && stateValid &&
                        positionValid && unitValid && nameValid && companyValid && machineNameValid &&
                        machineTypeValid && machineBandValid && conditionValid && vulnerabilityValid &&
                        datetimeValid && operatorValid && numValid)) {
                    Toast.makeText(getActivity(), "有字段填写错误，请检查并修改", Toast.LENGTH_SHORT).show();
                }

                if (idValid && typeValid && markValid && bandValid && originalValid && stateValid &&
                        positionValid && unitValid && nameValid && companyValid && machineNameValid &&
                        machineTypeValid && machineBandValid && conditionValid && vulnerabilityValid &&
                        datetimeValid && operatorValid && numValid) {
                    submitButton.setVisibility(View.GONE); //隐藏入库按钮
                    progressBar.setVisibility(View.VISIBLE); //显示进度条

                    String storestate = (String) stateSpinner.getSelectedItem();
                    String year = textYear.getText().toString();
                    String description = textDescription.getText().toString();
                    String inputDescription = textInputDescription.getText().toString();

                    HttpUtil.partIn(id, type, storestate, mark, band, original, year, state,
                            position, unit, name, company, machineName, machineType, machineBand,
                            condition, vulnerability, description, datetime, operator, num, inputDescription, new okhttp3.Callback() {
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    try {
                                        String responseData = response.body().string();
                                        LogUtil.d("Part in json", responseData);
                                        JSONObject jsonObject = new JSONObject(responseData);
                                        if (jsonObject.has("id")) {
                                            //入库成功
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressBar.setVisibility(View.GONE);
                                                    submitButton.setVisibility(View.VISIBLE);
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                    builder.setTitle("提示");
                                                    builder.setMessage("入库成功！");
                                                    builder.setCancelable(false);
                                                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            replaceFragment(new PartInFragment());
                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            });
                                        } else {
                                            //入库失败
                                            final List<Integer> errorType = new ArrayList<Integer>();
                                            if (jsonObject.has("inputPart")) {
                                                String partErr = jsonObject.getString("inputPart");
                                                LogUtil.d("Part in error inputPart", partErr);
                                                JSONObject jsonPartErr = new JSONObject(partErr);
                                                if (jsonPartErr.has("partYear")) {
                                                    //年份格式错误
                                                    errorType.add(1);
                                                }
                                                if (jsonPartErr.has("partUnit")) {
                                                    String unitErr = jsonPartErr.getString("partUnit");
                                                    if (unitErr.equals("[\"A valid number is required.\"]")) {
                                                        //非数字
                                                        errorType.add(2);
                                                    }
                                                    if (unitErr.equals("[\"Ensure that there are no more than 8 digits in total.\"]")) {
                                                        //数字不能超过8位
                                                        errorType.add(3);
                                                    }
                                                    if (unitErr.equals("[\"Ensure that there are no more than 6 digits before the decimal point.\"]")) {
                                                        //小数点前不能超过6位
                                                        errorType.add(4);
                                                    }
                                                    if (unitErr.equals("[\"Ensure that there are no more than 2 decimal places.\"]")) {
                                                        //小数点后不能超过2位
                                                        errorType.add(5);
                                                    }
                                                    if (unitErr.equals("[\"String value too large.\"]")) {
                                                        //字符串值过大，按数字不能超过8位处理
                                                        errorType.add(6);
                                                    }
                                                }
                                            }
                                            if (jsonObject.has("inputDateTime")) {
                                                //入库时间格式错误
                                                errorType.add(7);
                                            }
                                            if (jsonObject.has("inputNum")) {
                                                String numErr = jsonObject.getString("inputNum");
                                                LogUtil.d("Part in error num", numErr);
                                                if (numErr.equals("[\"A valid integer is required.\"]")) {
                                                    //非整数
                                                    errorType.add(8);
                                                }
                                                if (numErr.equals("[\"Ensure this value is less than or equal to 2147483647.\"]")) {
                                                    //数字过大
                                                    errorType.add(9);
                                                }
                                            }

                                            //错误处理
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressBar.setVisibility(View.GONE);
                                                    submitButton.setVisibility(View.VISIBLE);

                                                    if (errorType.size() == 0) {
                                                        //发生未预计到的错误
                                                        Toast.makeText(getActivity(), "入库失败，请修改后重新尝试", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getActivity(), "有字段填写错误，请检查并修改", Toast.LENGTH_SHORT).show();
                                                        if (errorType.contains(1)) {
                                                            wrapperYear.setError("格式错误，正确格式如2000-01-01！");
                                                        }
                                                        if (errorType.contains(2)) {
                                                            wrapperUnit.setError("该输入非数字！");
                                                        }
                                                        if (errorType.contains(3) || errorType.contains(6)) {
                                                            wrapperUnit.setError("数字不能超过8位！");
                                                        }
                                                        if (errorType.contains(4)) {
                                                            wrapperUnit.setError("小数点前不能超过6位！");
                                                        }
                                                        if (errorType.contains(5)) {
                                                            wrapperUnit.setError("小数点后不能超过2位！");
                                                        }
                                                        if (errorType.contains(7)) {
                                                            wrapperDatetime.setError("格式错误，正确格式如2000-01-01!");
                                                        }
                                                        if (errorType.contains(8)) {
                                                            wrapperNum.setError("该输入非整数！");
                                                        }
                                                        if (errorType.contains(9)) {
                                                            wrapperNum.setError("数字不能大于2147483647！");
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
                                    LogUtil.d("Part in", "failed");
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE); //隐藏进度条
                                            submitButton.setVisibility(View.VISIBLE); //显示入库按钮
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
}
