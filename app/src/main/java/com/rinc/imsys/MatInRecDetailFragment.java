package com.rinc.imsys;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by zhouzhi on 2017/8/20.
 */

public class MatInRecDetailFragment extends Fragment {

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

    private TextView textDescription;

    private Button backButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matinrecdetail, container, false);

        textInputdatetime = (TextView) view.findViewById(R.id.text_inputdatetime_matinrecdetail);
        textOperator = (TextView) view.findViewById(R.id.text_operator_matinrecdetail);
        textInputnum = (TextView) view.findViewById(R.id.text_inputnum_matinrecdetail);
        textId = (TextView) view.findViewById(R.id.text_id_matinrecdetail);
        textType = (TextView) view.findViewById(R.id.text_type_matinrecdetail);
        textNum = (TextView) view.findViewById(R.id.text_num_matinrecdetail);
        textStorestate = (TextView) view.findViewById(R.id.text_storestate_matinrecdetail);
        textMark = (TextView) view.findViewById(R.id.text_mark_matinrecdetail);
        textBand = (TextView) view.findViewById(R.id.text_band_matinrecdetail);
        textOriginal = (TextView) view.findViewById(R.id.text_original_matinrecdetail);
        textYear = (TextView) view.findViewById(R.id.text_year_matinrecdetail);
        textState = (TextView) view.findViewById(R.id.text_state_matinrecdetail);
        textPosition = (TextView) view.findViewById(R.id.text_position_matinrecdetail);
        textUnit = (TextView) view.findViewById(R.id.text_unit_matinrecdetail);
        textDescription = (TextView) view.findViewById(R.id.text_description_matinrecdetail);
        backButton = (Button) view.findViewById(R.id.button_back_matinrecdetail);

        //加载之前已经获取的所有入库记录信息
        final MaterialInRecord materialInRecord = (MaterialInRecord) getArguments().getSerializable("MatInRecord");
        textInputdatetime.setText(materialInRecord.getInputDateTime());
        textOperator.setText(materialInRecord.getOperator());
        textInputnum.setText(materialInRecord.getInputNum());
        textId.setText(materialInRecord.getMaterialStock().getId());
        textType.setText(materialInRecord.getMaterialStock().getType());
        textNum.setText(materialInRecord.getMaterialStock().getNum());
        textStorestate.setText(materialInRecord.getMaterialStock().getStorestate());
        textMark.setText(materialInRecord.getMaterialStock().getMark());
        textBand.setText(materialInRecord.getMaterialStock().getBand());
        textOriginal.setText(materialInRecord.getMaterialStock().getOriginal());
        textYear.setText(materialInRecord.getMaterialStock().getYear());
        textState.setText(materialInRecord.getMaterialStock().getState());
        textPosition.setText(materialInRecord.getMaterialStock().getPosition());
        textUnit.setText(materialInRecord.getMaterialStock().getUnit());
        textDescription.setText(materialInRecord.getMaterialStock().getDescription());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                //传递搜索的材料标号
                MatInRecFragment matInRecFragment = new MatInRecFragment();
                Bundle args = new Bundle();
                args.putString("lastSearchId", materialInRecord.getMaterialStock().getId());
                matInRecFragment.setArguments(args);
                replaceFragment(matInRecFragment);
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
