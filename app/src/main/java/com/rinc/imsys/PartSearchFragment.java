package com.rinc.imsys;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ZhouZhi on 2017/9/7.
 */

public class PartSearchFragment extends Fragment {

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
        View view = inflater.inflate(R.layout.fragment_partsearch, container, false);

        textId = view.findViewById(R.id.input_id_partsearch);
        textType = view.findViewById(R.id.input_type_partsearch);
        textBand = view.findViewById(R.id.input_band_partsearch);
        textOriginal = view.findViewById(R.id.input_original_partsearch);
        textPosition = view.findViewById(R.id.input_position_partsearch);
        textYearstart = view.findViewById(R.id.input_yearstart_partsearch);
        textYearend = view.findViewById(R.id.input_yearend_partsearch);
        progressBar = view.findViewById(R.id.progressbar_partsearch);
        searchButton = view.findViewById(R.id.button_search_partsearch);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("零件查找");

        progressBar.setVisibility(View.GONE);
        searchButton.setVisibility(View.VISIBLE);

        //填充搜索界面
        textId.setText(SearchRecord.id_part);
        textType.setText(SearchRecord.type_part);
        textBand.setText(SearchRecord.band_part);
        textOriginal.setText(SearchRecord.original_part);
        textPosition.setText(SearchRecord.position_part);
        textYearstart.setText(SearchRecord.yearstart_part);
        textYearend.setText(SearchRecord.yearend_part);

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
                    SearchRecord.setPartRecord(id, type, band, original, position, yearstart, yearend);

                    HttpUtil.searchPart(id, type, band, original, position, yearstart, yearend, new okhttp3.Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseData = response.body().string();
                            LogUtil.d("Part Search json", responseData);
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
                                try {
                                    JSONArray jsonArray = new JSONArray(responseData);
                                    if (jsonArray.length() == 0) {
                                        //没有相关记录
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressBar.setVisibility(View.GONE);
                                                searchButton.setVisibility(View.VISIBLE);
                                                Toast.makeText(getActivity(), "没有相关记录", Toast.LENGTH_SHORT).show();
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
                                        PartSearchResultFragment partSearchResultFragment = new PartSearchResultFragment();
                                        Bundle args = new Bundle();
                                        args.putString("PartSearchResult", responseData);
                                        partSearchResultFragment.setArguments(args);
                                        replaceFragment(partSearchResultFragment);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            LogUtil.d("Part Search", "failed");
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

    private void hideKeyboard() { //隐藏虚拟键盘
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager =  getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_main, fragment);
        transaction.commit();
    }
}
