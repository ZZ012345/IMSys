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

public class PartOutRecDetailFragment extends Fragment {

    private TextView textOutputdatetime;

    private TextView textOperator;

    private TextView textOutputnum;

    private TextView textLeftnum;

    private TextView textUser;

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
        View view = inflater.inflate(R.layout.fragment_partoutrecdetail, container, false);

        textOutputdatetime = (TextView) view.findViewById(R.id.text_outputdatetime_partoutrecdetail);
        textOperator = (TextView) view.findViewById(R.id.text_operator_partoutrecdetail);
        textOutputnum = (TextView) view.findViewById(R.id.text_outputnum_partoutrecdetail);
        textLeftnum = (TextView) view.findViewById(R.id.text_leftnum_partoutrecdetail);
        textUser = (TextView) view.findViewById(R.id.text_user_partoutrecdetail);
        textId = (TextView) view.findViewById(R.id.text_id_partoutrecdetail);
        textType = (TextView) view.findViewById(R.id.text_type_partoutrecdetail);
        textNum = (TextView) view.findViewById(R.id.text_num_partoutrecdetail);
        textStorestate = (TextView) view.findViewById(R.id.text_storestate_partoutrecdetail);
        textMark = (TextView) view.findViewById(R.id.text_mark_partoutrecdetail);
        textBand = (TextView) view.findViewById(R.id.text_band_partoutrecdetail);
        textOriginal = (TextView) view.findViewById(R.id.text_original_partoutrecdetail);
        textYear = (TextView) view.findViewById(R.id.text_year_partoutrecdetail);
        textState = (TextView) view.findViewById(R.id.text_state_partoutrecdetail);
        textPosition = (TextView) view.findViewById(R.id.text_position_partoutrecdetail);
        textUnit = (TextView) view.findViewById(R.id.text_unit_partoutrecdetail);
        textName = (TextView) view.findViewById(R.id.text_name_partoutrecdetail);
        textCompany = (TextView) view.findViewById(R.id.text_company_partoutrecdetail);
        textMachineName = (TextView) view.findViewById(R.id.text_machinename_partoutrecdetail);
        textMachineType = (TextView) view.findViewById(R.id.text_machinetype_partoutrecdetail);
        textMachineBand = (TextView) view.findViewById(R.id.text_machineband_partoutrecdetail);
        textCondition = (TextView) view.findViewById(R.id.text_condition_partoutrecdetail);
        textVulnerability = (TextView) view.findViewById(R.id.text_vul_partoutrecdetail);
        textDescription = (TextView) view.findViewById(R.id.text_description_partoutrecdetail);
        backButton = (Button) view.findViewById(R.id.button_back_partoutrecdetail);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("出库记录详细信息");

        //加载之前已经获取的出库记录信息
        final PartOutRecord partOutRecord = (PartOutRecord) getArguments().getSerializable("PartOutRecord");
        textOutputdatetime.setText(partOutRecord.getOutputDateTime());
        textOperator.setText(partOutRecord.getOperator());
        textOutputnum.setText(partOutRecord.getOutputNum());
        textLeftnum.setText(partOutRecord.getLeftNum());
        textUser.setText(partOutRecord.getUser());
        textId.setText(partOutRecord.getPartStock().getId());
        textType.setText(partOutRecord.getPartStock().getType());
        textNum.setText(partOutRecord.getPartStock().getNum());
        textStorestate.setText(partOutRecord.getPartStock().getStorestate());
        textMark.setText(partOutRecord.getPartStock().getMark());
        textBand.setText(partOutRecord.getPartStock().getBand());
        textOriginal.setText(partOutRecord.getPartStock().getOriginal());
        textYear.setText(partOutRecord.getPartStock().getYear());
        textState.setText(partOutRecord.getPartStock().getState());
        textPosition.setText(partOutRecord.getPartStock().getPosition());
        textUnit.setText(partOutRecord.getPartStock().getUnit());
        textName.setText(partOutRecord.getPartStock().getName());
        textCompany.setText(partOutRecord.getPartStock().getCompany());
        textMachineName.setText(partOutRecord.getPartStock().getMachineName());
        textMachineType.setText(partOutRecord.getPartStock().getMachineType());
        textMachineBand.setText(partOutRecord.getPartStock().getMachineBand());
        textCondition.setText(partOutRecord.getPartStock().getCondition());
        textVulnerability.setText(partOutRecord.getPartStock().getVulnerability());
        textDescription.setText(partOutRecord.getPartStock().getDescription());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                //传递零件信息
                PartOutRecFragment partOutRecFragment = new PartOutRecFragment();
                Bundle args = new Bundle();
                args.putSerializable("stock", partOutRecord.getPartStock());
                args.putInt("lastfragment", getArguments().getInt("lastfragment"));
                partOutRecFragment.setArguments(args);
                replaceFragment(partOutRecFragment);
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
