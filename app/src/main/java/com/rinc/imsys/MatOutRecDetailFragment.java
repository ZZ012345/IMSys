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
 * Created by zhouzhi on 2017/8/21.
 */

public class MatOutRecDetailFragment extends Fragment {

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

    private TextView textDescription;

    private Button backButton;
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matoutrecdetail, container, false);

        textOutputdatetime = (TextView) view.findViewById(R.id.text_outputdatetime_matoutrecdetail);
        textOperator = (TextView) view.findViewById(R.id.text_operator_matoutrecdetail);
        textOutputnum = (TextView) view.findViewById(R.id.text_outputnum_matoutrecdetail);
        textLeftnum = (TextView) view.findViewById(R.id.text_leftnum_matoutrecdetail);
        textUser = (TextView) view.findViewById(R.id.text_user_matoutrecdetail);
        textId = (TextView) view.findViewById(R.id.text_id_matoutrecdetail);
        textType = (TextView) view.findViewById(R.id.text_type_matoutrecdetail);
        textNum = (TextView) view.findViewById(R.id.text_num_matoutrecdetail);
        textStorestate = (TextView) view.findViewById(R.id.text_storestate_matoutrecdetail);
        textMark = (TextView) view.findViewById(R.id.text_mark_matoutrecdetail);
        textBand = (TextView) view.findViewById(R.id.text_band_matoutrecdetail);
        textOriginal = (TextView) view.findViewById(R.id.text_original_matoutrecdetail);
        textYear = (TextView) view.findViewById(R.id.text_year_matoutrecdetail);
        textState = (TextView) view.findViewById(R.id.text_state_matoutrecdetail);
        textPosition = (TextView) view.findViewById(R.id.text_position_matoutrecdetail);
        textUnit = (TextView) view.findViewById(R.id.text_unit_matoutrecdetail);
        textDescription = (TextView) view.findViewById(R.id.text_description_matoutrecdetail);
        backButton = (Button) view.findViewById(R.id.button_back_matoutrecdetail);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("出库记录详细信息");

        //加载之前已经获取的所有出库记录信息
        final MaterialOutRecord materialOutRecord = (MaterialOutRecord) getArguments().getSerializable("MatOutRecord");
        textOutputdatetime.setText(materialOutRecord.getOutputDateTime());
        textOperator.setText(materialOutRecord.getOperator());
        textOutputnum.setText(materialOutRecord.getOutputNum());
        textLeftnum.setText(materialOutRecord.getLeftNum());
        textUser.setText(materialOutRecord.getUser());
        textId.setText(materialOutRecord.getMaterialStock().getId());
        textType.setText(materialOutRecord.getMaterialStock().getType());
        textNum.setText(materialOutRecord.getMaterialStock().getNum());
        textStorestate.setText(materialOutRecord.getMaterialStock().getStorestate());
        textMark.setText(materialOutRecord.getMaterialStock().getMark());
        textBand.setText(materialOutRecord.getMaterialStock().getBand());
        textOriginal.setText(materialOutRecord.getMaterialStock().getOriginal());
        textYear.setText(materialOutRecord.getMaterialStock().getYear());
        textState.setText(materialOutRecord.getMaterialStock().getState());
        textPosition.setText(materialOutRecord.getMaterialStock().getPosition());
        textUnit.setText(materialOutRecord.getMaterialStock().getUnit());
        textDescription.setText(materialOutRecord.getMaterialStock().getDescription());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                //传递搜索的材料标号
                MatOutRecFragment matOutRecFragment = new MatOutRecFragment();
                Bundle args = new Bundle();
                args.putString("lastSearchId", materialOutRecord.getMaterialStock().getId());
                matOutRecFragment.setArguments(args);
                replaceFragment(matOutRecFragment);
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
