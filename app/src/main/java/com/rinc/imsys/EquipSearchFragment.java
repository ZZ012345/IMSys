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
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ZhouZhi on 2017/9/23.
 */

public class EquipSearchFragment extends BaseFragment {

    private EditText textId;

    private EditText textType;

    private EditText textBand;

    private EditText textOriginal;

    private EditText textPosition;

    private EditText textYearstart;

    private EditText textYearend;

    private ProgressBar progressBar;

    private Button searchButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_equipsearch, container, false);

        textId = view.findViewById(R.id.input_id_equipsearch);
        textType = view.findViewById(R.id.input_type_equipsearch);
        textBand = view.findViewById(R.id.input_band_equipsearch);
        textOriginal = view.findViewById(R.id.input_original_equipsearch);
        textPosition = view.findViewById(R.id.input_position_equipsearch);
        textYearstart = view.findViewById(R.id.input_yearstart_equipsearch);
        textYearend = view.findViewById(R.id.input_yearend_equipsearch);
        progressBar = view.findViewById(R.id.progressbar_equipsearch);
        searchButton = view.findViewById(R.id.button_search_equipsearch);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("整机查找");

        progressBar.setVisibility(View.GONE);
        searchButton.setVisibility(View.VISIBLE);

        //填充搜索界面
        textId.setText(SearchRecord.id_equip);
        textType.setText(SearchRecord.type_equip);
        textBand.setText(SearchRecord.band_equip);
        textOriginal.setText(SearchRecord.original_equip);
        textPosition.setText(SearchRecord.position_equip);
        textYearstart.setText(SearchRecord.yearstart_equip);
        textYearend.setText(SearchRecord.yearend_equip);

        SearchRecord.lastFrag = SearchRecord.FRAGLABEL_SEARCH;

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                hideKeyboard();

                String id = textId.getText().toString();
                String type = textType.getText().toString();
                String band = textBand.getText().toString();
                String original = textOriginal.getText().toString();
                String position = textPosition.getText().toString();
                String yearstart = textYearstart.getText().toString();
                String yearend = textYearend.getText().toString();

                if (id.length() == 0 && type.length() == 0 && band.length() == 0 && original.length() == 0 &&
                        position.length() == 0 && yearstart.length() == 0 && yearend.length() == 0) {
                    Toast.makeText(getActivity(), "查找条件不能为空！", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    searchButton.setVisibility(View.GONE);

                    //保存搜索记录
                    SearchRecord.setEquipRecord(id, type, band, original, position, yearstart, yearend);

                    HttpUtil.searchEquip(id, type, band, original, position, yearstart, yearend, new okhttp3.Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseData = response.body().string();
                            LogUtil.d("Equip Search json", responseData);
                            if (responseData.equals("{\"detail\":\"Invalid time format!\"}")) {
                                //时间格式错误
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        searchButton.setVisibility(View.VISIBLE);
                                        Toast.makeText(getActivity(), "时间格式错误，正确格式如2000-01-01！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        searchButton.setVisibility(View.VISIBLE);
                                    }
                                });
                                EquipSearchResultFragment equipSearchResultFragment = new EquipSearchResultFragment();
                                replaceFragment(equipSearchResultFragment);
                            }
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            LogUtil.d("Equip Search", "failed");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    searchButton.setVisibility(View.VISIBLE);
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
