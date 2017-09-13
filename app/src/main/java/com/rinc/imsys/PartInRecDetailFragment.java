package com.rinc.imsys;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by ZhouZhi on 2017/9/12.
 */

public class PartInRecDetailFragment extends Fragment {
    private TextView textInputdatetime;

    private TextView textOperator;

    private TextView textInputnum;

    private TextView textId;

    private TextView textType;

    private TextView textNum;

    private TextView textStorestate;

    private TextView textMark;

    private TextView textBand;

    private TextView textOriginal;

    private TextView textYear;

    private TextView textState;

    private TextView textPosition;

    private TextView textUnit;

    private TextView textName;

    private TextView textCompany;

    private TextView textMachineName;

    private TextView textMachineType;

    private TextView textMachineBand;

    private TextView textCondition;

    private TextView textVulnerability;

    private TextView textDescription;

    private Button backButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_partinrecdetail, container, false);

        textInputdatetime = (TextView) view.findViewById(R.id.text_inputdatetime_partinrecdetail);
        textOperator = (TextView) view.findViewById(R.id.text_operator_partinrecdetail);
        textInputnum = (TextView) view.findViewById(R.id.text_inputnum_partinrecdetail);
        textId = (TextView) view.findViewById(R.id.text_id_partinrecdetail);
        textType = (TextView) view.findViewById(R.id.text_type_partinrecdetail);
        textNum = (TextView) view.findViewById(R.id.text_num_partinrecdetail);
        textStorestate = (TextView) view.findViewById(R.id.text_storestate_partinrecdetail);
        textMark = (TextView) view.findViewById(R.id.text_mark_partinrecdetail);
        textBand = (TextView) view.findViewById(R.id.text_band_partinrecdetail);
        textOriginal = (TextView) view.findViewById(R.id.text_original_partinrecdetail);
        textYear = (TextView) view.findViewById(R.id.text_year_partinrecdetail);
        textState = (TextView) view.findViewById(R.id.text_state_partinrecdetail);
        textPosition = (TextView) view.findViewById(R.id.text_position_partinrecdetail);
        textUnit = (TextView) view.findViewById(R.id.text_unit_partinrecdetail);
        textName = (TextView) view.findViewById(R.id.text_name_partinrecdetail);
        textCompany = (TextView) view.findViewById(R.id.text_company_partinrecdetail);
        textMachineName = (TextView) view.findViewById(R.id.text_machinename_partinrecdetail);
        textMachineType = (TextView) view.findViewById(R.id.text_machinetype_partinrecdetail);
        textMachineBand = (TextView) view.findViewById(R.id.text_machineband_partinrecdetail);
        textCondition = (TextView) view.findViewById(R.id.text_condition_partinrecdetail);
        textVulnerability = (TextView) view.findViewById(R.id.text_vul_partinrecdetail);
        textDescription = (TextView) view.findViewById(R.id.text_description_partinrecdetail);
        backButton = (Button) view.findViewById(R.id.button_back_partinrecdetail);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("入库记录详细信息");

        //加载之前已经获取的入库记录信息
        final PartInRecord partInRecord = (PartInRecord) getArguments().getSerializable("PartInRecord");
        textInputdatetime.setText(partInRecord.getInputDateTime());
        textOperator.setText(partInRecord.getOperator());
        textInputnum.setText(partInRecord.getInputNum());
        textId.setText(partInRecord.getPartStock().getId());
        textType.setText(partInRecord.getPartStock().getType());
        textNum.setText(partInRecord.getPartStock().getNum());
        textStorestate.setText(partInRecord.getPartStock().getStorestate());
        textMark.setText(partInRecord.getPartStock().getMark());
        textBand.setText(partInRecord.getPartStock().getBand());
        textOriginal.setText(partInRecord.getPartStock().getOriginal());
        textYear.setText(partInRecord.getPartStock().getYear());
        textState.setText(partInRecord.getPartStock().getState());
        textPosition.setText(partInRecord.getPartStock().getPosition());
        textUnit.setText(partInRecord.getPartStock().getUnit());
        textName.setText(partInRecord.getPartStock().getName());
        textCompany.setText(partInRecord.getPartStock().getCompany());
        textMachineName.setText(partInRecord.getPartStock().getMachineName());
        textMachineType.setText(partInRecord.getPartStock().getMachineType());
        textMachineBand.setText(partInRecord.getPartStock().getMachineBand());
        textCondition.setText(partInRecord.getPartStock().getCondition());
        textVulnerability.setText(partInRecord.getPartStock().getVulnerability());
        textDescription.setText(partInRecord.getPartStock().getDescription());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                //传递零件信息
                PartInRecFragment partInRecFragment = new PartInRecFragment();
                Bundle args = new Bundle();
                args.putSerializable("stock", partInRecord.getPartStock());
                args.putInt("lastfragment", getArguments().getInt("lastfragment"));
                partInRecFragment.setArguments(args);
                replaceFragment(partInRecFragment);
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
