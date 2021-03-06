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
 * Created by ZhouZhi on 2017/9/13.
 */

public class EquipInFragment extends BaseFragment {

    private Spinner stateSpinner;

    private Button submitButton;

    private EditText textId;

    private EditText textType;

    private EditText textMark;
    
    private EditText textHour;

    private EditText textBand;

    private EditText textOriginal;

    private EditText textYear;

    private EditText textState;

    private EditText textPosition;

    private EditText textUnit;

    private EditText textDescription;

    private EditText textDateTime;

    private EditText textOperator;

    private EditText textNum;

    private EditText textInputDescription;

    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_equipin, container, false);

        stateSpinner = (Spinner) view.findViewById(R.id.spinner_state_equipin);
        submitButton = (Button) view.findViewById(R.id.button_submit_equipin);
        textId = (EditText) view.findViewById(R.id.input_id_equipin);
        textType = (EditText) view.findViewById(R.id.input_type_equipin);
        textMark = (EditText) view.findViewById(R.id.input_mark_equipin);
        textHour = (EditText) view.findViewById(R.id.input_hour_equipin);
        textBand = (EditText) view.findViewById(R.id.input_band_equipin);
        textOriginal = (EditText) view.findViewById(R.id.input_original_equipin);
        textYear = (EditText) view.findViewById(R.id.input_year_equipin);
        textState = (EditText) view.findViewById(R.id.input_state_equipin);
        textPosition = (EditText) view.findViewById(R.id.input_position_equipin);
        textUnit = (EditText) view.findViewById(R.id.input_unit_equipin);
        textDescription = (EditText) view.findViewById(R.id.input_description_equipin);
        textDateTime = (EditText) view.findViewById(R.id.input_date_equipin);
        textOperator = (EditText) view.findViewById(R.id.input_operator_equipin);
        textNum = (EditText) view.findViewById(R.id.input_num_equipin);
        textInputDescription = (EditText) view.findViewById(R.id.input_inputdescription_equipin);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_equipin);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle(getString(R.string.machine_in));

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

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
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
                boolean datetimeValid = false;
                boolean operatorValid = false;
                boolean numValid = false;

                //检查标号是否合法
                TextInputLayout wrapperId = (TextInputLayout) view.findViewById(R.id.wrapper_id_equipin);
                String id = textId.getText().toString();
                LogUtil.d("Equip in id", id);
                if (id.length() == 0) {
                    wrapperId.setError(getString(R.string.not_empty));
                } else if (id.length() > 100) {
                    wrapperId.setError(getString(R.string.too_long));
                } else {
                    wrapperId.setErrorEnabled(false);
                    idValid = true;
                }

                //检查类型是否合法
                TextInputLayout wrapperType = (TextInputLayout) view.findViewById(R.id.wrapper_type_equipin);
                String type = textType.getText().toString();
                LogUtil.d("Equip in type", type);
                if (type.length() > 100) {
                    wrapperType.setError(getString(R.string.too_long));
                } else {
                    wrapperType.setErrorEnabled(false);
                    typeValid = true;
                }

                //检查型号是否合法
                TextInputLayout wrapperMark = (TextInputLayout) view.findViewById(R.id.wrapper_mark_equipin);
                String mark = textMark.getText().toString();
                LogUtil.d("Equip in mark", mark);
                if (mark.length() > 100) {
                    wrapperMark.setError(getString(R.string.too_long));
                } else {
                    wrapperMark.setErrorEnabled(false);
                    markValid = true;
                }

                //检查工作小时是否合法
                final TextInputLayout wrapperHour = (TextInputLayout) view.findViewById(R.id.wrapper_hour_equipin);
                String hour = textHour.getText().toString();
                LogUtil.d("Equip in hour", hour);
                wrapperHour.setErrorEnabled(false);
                hourValid = true;

                //检查品牌是否合法
                TextInputLayout wrapperBand = (TextInputLayout) view.findViewById(R.id.wrapper_band_equipin);
                String band = textBand.getText().toString();
                LogUtil.d("Equip in band", band);
                if (band.length() > 100) {
                    wrapperBand.setError(getString(R.string.too_long));
                } else {
                    wrapperBand.setErrorEnabled(false);
                    bandValid = true;
                }

                //检查原产地是否合法
                TextInputLayout wrapperOriginal = (TextInputLayout) view.findViewById(R.id.wrapper_original_equipin);
                String original = textOriginal.getText().toString();
                LogUtil.d("Equip in original", original);
                if (original.length() > 100) {
                    wrapperOriginal.setError(getString(R.string.too_long));
                } else {
                    wrapperOriginal.setErrorEnabled(false);
                    originalValid = true;
                }

                //检查年份是否合法
                final TextInputLayout wrapperYear = (TextInputLayout) view.findViewById(R.id.wrapper_year_equipin);
                wrapperYear.setErrorEnabled(false);

                //检查状况是否合法
                TextInputLayout wrapperState = (TextInputLayout) view.findViewById(R.id.wrapper_state_equipin);
                String state = textState.getText().toString();
                LogUtil.d("Equip in state", state);
                if (state.length() > 100) {
                    wrapperState.setError(getString(R.string.too_long));
                } else {
                    wrapperState.setErrorEnabled(false);
                    stateValid = true;
                }

                //检查位置是否合法
                TextInputLayout wrapperPosition = (TextInputLayout) view.findViewById(R.id.wrapper_position_equipin);
                String position = textPosition.getText().toString();
                LogUtil.d("Equip in position", position);
                if (position.length() > 100) {
                    wrapperPosition.setError(getString(R.string.too_long));
                } else {
                    wrapperPosition.setErrorEnabled(false);
                    positionValid = true;
                }

                //检查单位原值是否合法
                final TextInputLayout wrapperUnit = (TextInputLayout) view.findViewById(R.id.wrapper_unit_equipin);
                String unit = textUnit.getText().toString();
                LogUtil.d("Equip in unit", unit);
                wrapperUnit.setErrorEnabled(false);
                unitValid = true;

                //检查入库时间是否合法
                final TextInputLayout wrapperDatetime = (TextInputLayout) view.findViewById(R.id.wrapper_datetime_equipin);
                String datetime = textDateTime.getText().toString();
                LogUtil.d("Equip in datetime", datetime);
                if (datetime.length() == 0) {
                    wrapperDatetime.setError(getString(R.string.not_empty));
                } else {
                    wrapperDatetime.setErrorEnabled(false);
                    datetimeValid = true;
                }

                //检查操作人员是否合法
                TextInputLayout wrapperOperator = (TextInputLayout) view.findViewById(R.id.wrapper_operator_equipin);
                String operator = textOperator.getText().toString();
                LogUtil.d("Equip in operator", operator);
                if (operator.length() == 0) {
                    wrapperOperator.setError(getString(R.string.not_empty));
                } else if (operator.length() > 100) {
                    wrapperOperator.setError(getString(R.string.too_long));
                } else {
                    wrapperOperator.setErrorEnabled(false);
                    operatorValid = true;
                }

                //检查入库量是否合法
                final TextInputLayout wrapperNum = (TextInputLayout) view.findViewById(R.id.wrapper_num_equipin);
                String num = textNum.getText().toString();
                LogUtil.d("Equip in num", num);
                if (num.length() == 0) {
                    wrapperNum.setError(getString(R.string.not_empty));
                } else {
                    wrapperNum.setErrorEnabled(false);
                    numValid = true;
                }

                if (!(idValid && typeValid && markValid && hourValid && bandValid && originalValid && stateValid &&
                        positionValid && unitValid && datetimeValid && operatorValid && numValid)) {
                    Toast.makeText(getActivity(), getString(R.string.check_and_modify), Toast.LENGTH_SHORT).show();
                }

                if (idValid && typeValid && markValid && hourValid && bandValid && originalValid && stateValid &&
                        positionValid && unitValid && datetimeValid && operatorValid && numValid) {
                    submitButton.setVisibility(View.GONE); //隐藏入库按钮
                    progressBar.setVisibility(View.VISIBLE); //显示进度条

                    String storestate;
                    int positionSelected = stateSpinner.getSelectedItemPosition();
                    if (positionSelected == 0) {
                        storestate = "在用";
                    } else if (positionSelected == 1) {
                        storestate = "闲置可用";
                    } else if (positionSelected == 2) {
                        storestate = "闲置可租";
                    } else {
                        storestate = "闲置可售";
                    }
                    String year = textYear.getText().toString();
                    String description = textDescription.getText().toString();
                    String inputDescription = textInputDescription.getText().toString();

                    HttpUtil.equipIn(id, type, storestate, mark, hour, band, original, year, state,
                            position, unit, description, datetime, operator, num, inputDescription, new okhttp3.Callback() {
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    try {
                                        String responseData = response.body().string();
                                        LogUtil.d("Equip in json", responseData);
                                        JSONObject jsonObject = new JSONObject(responseData);
                                        if (jsonObject.has("id")) {
                                            //入库成功
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressBar.setVisibility(View.GONE);
                                                    submitButton.setVisibility(View.VISIBLE);
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                    builder.setTitle(getString(R.string.hint));
                                                    builder.setMessage(getString(R.string.stock_in_successful));
                                                    builder.setCancelable(false);
                                                    builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            //清空入库信息，只保留入库时间和操作人员
                                                            textId.setText("");
                                                            textType.setText("");
                                                            stateSpinner.setSelection(0);
                                                            textMark.setText("");
                                                            textHour.setText("");
                                                            textBand.setText("");
                                                            textOriginal.setText("");
                                                            textYear.setText("");
                                                            textState.setText("");
                                                            textPosition.setText("");
                                                            textUnit.setText("");
                                                            textDescription.setText("");
                                                            textNum.setText("");
                                                            textInputDescription.setText("");
                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            });
                                        } else {
                                            //入库失败
                                            final List<Integer> errorType = new ArrayList<Integer>();
                                            if (jsonObject.has("inputEquipment")) {
                                                String equipErr = jsonObject.getString("inputEquipment");
                                                LogUtil.d("Equip in error inputEquipment", equipErr);
                                                JSONObject jsonEquipErr = new JSONObject(equipErr);
                                                if (jsonEquipErr.has("equipmentYear")) {
                                                    //年份格式错误
                                                    errorType.add(1);
                                                }
                                                if (jsonEquipErr.has("equipmentUnit")) {
                                                    String unitErr = jsonEquipErr.getString("equipmentUnit");
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
                                                if (jsonEquipErr.has("equipmentHour")) {
                                                    String hourErr = jsonEquipErr.getString("equipmentHour");
                                                    if (hourErr.equals("[\"A valid integer is required.\"]")) {
                                                        //非整数
                                                        errorType.add(7);
                                                    }
                                                    if (hourErr.equals("[\"Ensure this value is less than or equal to 2147483647.\"]")) {
                                                        //数字过大
                                                        errorType.add(8);
                                                    }
                                                }
                                            }
                                            if (jsonObject.has("inputDateTime")) {
                                                //入库时间格式错误
                                                errorType.add(9);
                                            }
                                            if (jsonObject.has("inputNum")) {
                                                String numErr = jsonObject.getString("inputNum");
                                                LogUtil.d("Equip in error num", numErr);
                                                if (numErr.equals("[\"A valid integer is required.\"]")) {
                                                    //非整数
                                                    errorType.add(10);
                                                }
                                                if (numErr.equals("[\"Ensure this value is less than or equal to 2147483647.\"]")) {
                                                    //数字过大
                                                    errorType.add(11);
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
                                                        Toast.makeText(getActivity(), getString(R.string.stock_in_failed), Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getActivity(), getString(R.string.check_and_modify), Toast.LENGTH_SHORT).show();
                                                        if (errorType.contains(1)) {
                                                            wrapperYear.setError(getString(R.string.year_format_error));
                                                        }
                                                        if (errorType.contains(2)) {
                                                            wrapperUnit.setError(getString(R.string.not_number));
                                                        }
                                                        if (errorType.contains(3) || errorType.contains(6)) {
                                                            wrapperUnit.setError(getString(R.string.num_too_long_8));
                                                        }
                                                        if (errorType.contains(4)) {
                                                            wrapperUnit.setError(getString(R.string.decimal_6));
                                                        }
                                                        if (errorType.contains(5)) {
                                                            wrapperUnit.setError(getString(R.string.decimal_2));
                                                        }
                                                        if (errorType.contains(7)) {
                                                            wrapperHour.setError(getString(R.string.not_integer));
                                                        }
                                                        if (errorType.contains(8)) {
                                                            wrapperHour.setError(getString(R.string.large_than_num));
                                                        }
                                                        if (errorType.contains(9)) {
                                                            wrapperDatetime.setError(getString(R.string.year_format_error));
                                                        }
                                                        if (errorType.contains(10)) {
                                                            wrapperNum.setError(getString(R.string.not_integer));
                                                        }
                                                        if (errorType.contains(11)) {
                                                            wrapperNum.setError(getString(R.string.large_than_num));
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
                                    LogUtil.d("Equip in", "failed");
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE); //隐藏进度条
                                            submitButton.setVisibility(View.VISIBLE); //显示入库按钮
                                            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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
