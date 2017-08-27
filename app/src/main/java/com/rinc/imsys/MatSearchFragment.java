package com.rinc.imsys;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
 * Created by zhouzhi on 2017/8/22.
 */

public class MatSearchFragment extends Fragment {

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
        View view = inflater.inflate(R.layout.fragment_matsearch, container, false);

        textId = view.findViewById(R.id.input_id_matsearch);
        textType = view.findViewById(R.id.input_type_matsearch);
        textBand = view.findViewById(R.id.input_band_matsearch);
        textOriginal = view.findViewById(R.id.input_original_matsearch);
        textPosition = view.findViewById(R.id.input_position_matsearch);
        textYearstart = view.findViewById(R.id.input_yearstart_matsearch);
        textYearend = view.findViewById(R.id.input_yearend_matsearch);
        progressBar = view.findViewById(R.id.progressbar_matsearch);
        searchButton = view.findViewById(R.id.button_search_matsearch);

        progressBar.setVisibility(View.GONE);
        searchButton.setVisibility(View.VISIBLE);

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
                    Toast.makeText(getActivity(), "搜索条件不能为空！", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    searchButton.setVisibility(View.GONE);

                    HttpUtil.searchMat(id, type, band, original, position, yearstart, yearend, new okhttp3.Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseData = response.body().string();
                            LogUtil.d("Mat Search json", responseData);
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
                                        MatSearchResultFragment matSearchResultFragment = new MatSearchResultFragment();
                                        Bundle args = new Bundle();
                                        args.putString("MatSearchResult", responseData);
                                        matSearchResultFragment.setArguments(args);
                                        replaceFragment(matSearchResultFragment);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            LogUtil.d("Mat Search", "failed");
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
