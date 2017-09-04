package com.rinc.imsys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhouZhi on 2017/8/30.
 */

public class QRMakeFragment extends Fragment {

    private List<String> stockList = new ArrayList<String>();

    private Spinner spinner;

    private TextInputLayout wrapperId;

    private EditText textId;

    private TextInputLayout wrapperType;

    private EditText textType;

    private TextInputLayout wrapperMark;

    private EditText textMark;

    private TextInputLayout wrapperHour;

    private EditText textHour;

    private TextInputLayout wrapperBand;

    private EditText textBand;

    private TextInputLayout wrapperOriginal;

    private EditText textOriginal;

    private TextInputLayout wrapperYear;

    private EditText textYear;

    private TextInputLayout wrapperState;

    private EditText textState;

    private TextInputLayout wrapperPosition;

    private EditText textPosition;

    private TextInputLayout wrapperUnit;

    private EditText textUnit;

    private TextInputLayout wrapperName;

    private EditText textName;

    private TextInputLayout wrapperCompany;

    private EditText textCompany;

    private TextInputLayout wrapperMachineName;

    private EditText textMachineName;

    private TextInputLayout wrapperMachineType;

    private EditText textMachineType;

    private TextInputLayout wrapperMachineBand;

    private EditText textMachineBand;

    private TextInputLayout wrapperCondition;

    private EditText textCondition;

    private TextInputLayout wrapperVulnerability;

    private EditText textVulnerability;

    private TextInputLayout wrapperDescription;

    private EditText textDescription;

    private Button button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qrmake, container, false);

        spinner = view.findViewById(R.id.spinner_qrmake);
        wrapperId = (TextInputLayout) view.findViewById(R.id.wrapper_id_qrmake);
        textId = (EditText) view.findViewById(R.id.input_id_qrmake);
        wrapperType = (TextInputLayout) view.findViewById(R.id.wrapper_type_qrmake);
        textType = (EditText) view.findViewById(R.id.input_type_qrmake);
        wrapperMark = (TextInputLayout) view.findViewById(R.id.wrapper_mark_qrmake);
        textMark = (EditText) view.findViewById(R.id.input_mark_qrmake);
        wrapperHour = (TextInputLayout) view.findViewById(R.id.wrapper_hour_qrmake);
        textHour = (EditText) view.findViewById(R.id.input_hour_qrmake);
        wrapperBand = (TextInputLayout) view.findViewById(R.id.wrapper_band_qrmake);
        textBand = (EditText) view.findViewById(R.id.input_band_qrmake);
        wrapperOriginal = (TextInputLayout) view.findViewById(R.id.wrapper_original_qrmake);
        textOriginal = (EditText) view.findViewById(R.id.input_original_qrmake);
        wrapperYear = (TextInputLayout) view.findViewById(R.id.wrapper_year_qrmake);
        textYear = (EditText) view.findViewById(R.id.input_year_qrmake);
        wrapperState = (TextInputLayout) view.findViewById(R.id.wrapper_state_qrmake);
        textState = (EditText) view.findViewById(R.id.input_state_qrmake);
        wrapperPosition = (TextInputLayout) view.findViewById(R.id.wrapper_position_qrmake);
        textPosition = (EditText) view.findViewById(R.id.input_position_qrmake);
        wrapperUnit = (TextInputLayout) view.findViewById(R.id.wrapper_unit_qrmake);
        textUnit = (EditText) view.findViewById(R.id.input_unit_qrmake);
        wrapperName = (TextInputLayout) view.findViewById(R.id.wrapper_name_qrmake);
        textName = (EditText) view.findViewById(R.id.input_name_qrmake);
        wrapperCompany = (TextInputLayout) view.findViewById(R.id.wrapper_company_qrmake);
        textCompany = (EditText) view.findViewById(R.id.input_company_qrmake);
        wrapperMachineName = (TextInputLayout) view.findViewById(R.id.wrapper_machinename_qrmake);
        textMachineName = (EditText) view.findViewById(R.id.input_machinename_qrmake);
        wrapperMachineType = (TextInputLayout) view.findViewById(R.id.wrapper_machinetype_qrmake);
        textMachineType = (EditText) view.findViewById(R.id.input_machinetype_qrmake);
        wrapperMachineBand = (TextInputLayout) view.findViewById(R.id.wrapper_machineband_qrmake);
        textMachineBand = (EditText) view.findViewById(R.id.input_machineband_qrmake);
        wrapperCondition = (TextInputLayout) view.findViewById(R.id.wrapper_condition_qrmake);
        textCondition = (EditText) view.findViewById(R.id.input_condition_qrmake);
        wrapperVulnerability = (TextInputLayout) view.findViewById(R.id.wrapper_vul_qrmake);
        textVulnerability = (EditText) view.findViewById(R.id.input_vul_qrmake);
        wrapperDescription = (TextInputLayout) view.findViewById(R.id.wrapper_description_qrmake);
        textDescription = (EditText) view.findViewById(R.id.input_description_qrmake);
        button = (Button) view.findViewById(R.id.button_qrmake);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("生成二维码");

        //初始化下拉列表
        stockList.add("材料库");
        stockList.add("零件库");
        stockList.add("设备库");
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinnertext_item, stockList);
        adapter.setDropDownViewResource(R.layout.spinnerdropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view1, int i, long l) {
                if (i == 0) {
                    //材料库
                    wrapperHour.setVisibility(View.GONE);
                    wrapperName.setVisibility(View.GONE);
                    wrapperCompany.setVisibility(View.GONE);
                    wrapperMachineName.setVisibility(View.GONE);
                    wrapperMachineType.setVisibility(View.GONE);
                    wrapperMachineBand.setVisibility(View.GONE);
                    wrapperCondition.setVisibility(View.GONE);
                    wrapperVulnerability.setVisibility(View.GONE);
                } else if (i == 1) {
                    //零件库
                    wrapperHour.setVisibility(View.GONE);
                    wrapperName.setVisibility(View.VISIBLE);
                    wrapperCompany.setVisibility(View.VISIBLE);
                    wrapperMachineName.setVisibility(View.VISIBLE);
                    wrapperMachineType.setVisibility(View.VISIBLE);
                    wrapperMachineBand.setVisibility(View.VISIBLE);
                    wrapperCondition.setVisibility(View.VISIBLE);
                    wrapperVulnerability.setVisibility(View.VISIBLE);
                } else {
                    //设备库
                    wrapperHour.setVisibility(View.VISIBLE);
                    wrapperName.setVisibility(View.GONE);
                    wrapperCompany.setVisibility(View.GONE);
                    wrapperMachineName.setVisibility(View.GONE);
                    wrapperMachineType.setVisibility(View.GONE);
                    wrapperMachineBand.setVisibility(View.GONE);
                    wrapperCondition.setVisibility(View.GONE);
                    wrapperVulnerability.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                hideKeyboard();

                boolean idValid = false;
                boolean typeValid = false;
                boolean markValid = false;
                boolean bandValid = false;
                boolean originalValid = false;
                boolean stateValid = false;
                boolean positionValid = false;

                //检查标号是否合法
                String id = textId.getText().toString();
                LogUtil.d("QR Make id", id);
                if (id.length() == 0) {
                    wrapperId.setError("不能为空！");
                } else if (id.length() > 100) {
                    wrapperId.setError("长度不能超过100个字符！");
                } else {
                    wrapperId.setErrorEnabled(false);
                    idValid = true;
                }

                //检查类型是否合法
                String type = textType.getText().toString();
                LogUtil.d("QR Make type", type);
                if (type.length() > 100) {
                    wrapperType.setError("长度不能超过100个字符！");
                } else {
                    wrapperType.setErrorEnabled(false);
                    typeValid = true;
                }

                //检查型号是否合法
                String mark = textMark.getText().toString();
                LogUtil.d("QR Make mark", mark);
                if (mark.length() > 100) {
                    wrapperMark.setError("长度不能超过100个字符！");
                } else {
                    wrapperMark.setErrorEnabled(false);
                    markValid = true;
                }

                //检查品牌是否合法
                String band = textBand.getText().toString();
                LogUtil.d("QR Make band", band);
                if (band.length() > 100) {
                    wrapperBand.setError("长度不能超过100个字符！");
                } else {
                    wrapperBand.setErrorEnabled(false);
                    bandValid = true;
                }

                //检查原产地是否合法
                String original = textOriginal.getText().toString();
                LogUtil.d("QR Make original", original);
                if (original.length() > 100) {
                    wrapperOriginal.setError("长度不能超过100个字符！");
                } else {
                    wrapperOriginal.setErrorEnabled(false);
                    originalValid = true;
                }

                //检查状态是否合法
                String state = textState.getText().toString();
                LogUtil.d("QR Make state", state);
                if (state.length() > 100) {
                    wrapperState.setError("长度不能超过100个字符！");
                } else {
                    wrapperState.setErrorEnabled(false);
                    stateValid = true;
                }

                //检查位置是否合法
                String position = textPosition.getText().toString();
                LogUtil.d("QR Make position", position);
                if (position.length() > 100) {
                    wrapperPosition.setError("长度不能超过100个字符！");
                } else {
                    wrapperPosition.setErrorEnabled(false);
                    positionValid = true;
                }

                String year = textYear.getText().toString();
                String unit = textUnit.getText().toString();
                String description = textDescription.getText().toString();

                if (idValid && typeValid && markValid && bandValid && originalValid && stateValid && positionValid) {
                    int stockType = spinner.getSelectedItemPosition();
                    if (stockType == 0) {
                        //材料库
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("stockType", "material");
                            jsonObject.put("id", id);
                            jsonObject.put("type", type);
                            jsonObject.put("mark", mark);
                            jsonObject.put("hour", "");
                            jsonObject.put("band", band);
                            jsonObject.put("original" , original);
                            jsonObject.put("year", year);
                            jsonObject.put("state", state);
                            jsonObject.put("position", position);
                            jsonObject.put("unit", unit);
                            jsonObject.put("name", "");
                            jsonObject.put("company", "");
                            jsonObject.put("machineName", "");
                            jsonObject.put("machineType", "");
                            jsonObject.put("machineBand", "");
                            jsonObject.put("condition", "");
                            jsonObject.put("vulnerability", "");
                            jsonObject.put("description", description);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String infoStr = jsonObject.toString();
                        Intent intent = new Intent(getActivity(), QRCodeActivity.class);
                        intent.putExtra("info", infoStr);
                        getActivity().startActivity(intent);
                    } else if (stockType == 1) {
                        //零件库

                    } else {
                        //设备库
                    }
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
}
