package com.rinc.imsys;

import android.os.Bundle;
import android.support.annotation.Nullable;
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

import java.text.SimpleDateFormat;
import java.util.Date;

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
        View view = inflater.inflate(R.layout.fragment_equipin, container, false);

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
        toolbar.setTitle("整机入库");

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
                String hourScan = jsonObject.getString("hour");
                String bandScan = jsonObject.getString("band");
                String originalScan = jsonObject.getString("original");
                String yearScan = jsonObject.getString("year");
                String stateScan = jsonObject.getString("state");
                String positionScan = jsonObject.getString("position");
                String unitScan = jsonObject.getString("unit");
                String descriptionScan = jsonObject.getString("description");

                if (stockType.equals("equipment")) {
                    textId.setText(idScan);
                    textType.setText(typeScan);
                    textMark.setText(markScan);
                    textHour.setText(hourScan);
                    textBand.setText(bandScan);
                    textOriginal.setText(originalScan);
                    textYear.setText(yearScan);
                    textState.setText(stateScan);
                    textPosition.setText(positionScan);
                    textUnit.setText(unitScan);
                    textDescription.setText(descriptionScan);
                } else {
                    Toast.makeText(getActivity(), "非整机库二维码！", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "二维码格式错误！", Toast.LENGTH_SHORT).show();
            }
        }

        return view;
    }
}
