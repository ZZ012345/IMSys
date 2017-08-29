package com.rinc.imsys;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhouzhi on 2017/8/18.
 */

public class MatInRecFragment extends Fragment {

    private EditText searchId;

    private ImageView searchImage;

    private ProgressBar progressBar;

    private TextView textNotExist;

    private RecyclerView recyclerView;

    private List<MaterialInRecord> mrlist = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matinrec, container, false);

        searchId = (EditText) view.findViewById(R.id.input_matinrec);
        searchImage = (ImageView) view.findViewById(R.id.search_matinrec);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_matinrec);
        textNotExist = (TextView) view.findViewById(R.id.text_notexist_matinrec);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_matinrec);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("材料入库记录查询");

        searchId.setVisibility(View.VISIBLE);
        searchImage.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        textNotExist.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                hideKeyboard();

                String textSeach = searchId.getText().toString();
                if (textSeach.length() != 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    textNotExist.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);

                    HttpUtil.getMatInRec(textSeach, new okhttp3.Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseData = response.body().string();
                            LogUtil.d("Get Mat In Rec jsondata", responseData);
                            try {
                                JSONArray jsonArray = new JSONArray(responseData);
                                if (jsonArray.length() == 0) {
                                    //没有相关信息
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE);
                                            textNotExist.setVisibility(View.VISIBLE);
                                            recyclerView.setVisibility(View.GONE);
                                        }
                                    });
                                } else {
                                    mrlist.clear(); //清空入库记录
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        int recordId = jsonObject.getInt("id");
                                        String inputDateTime = jsonObject.getString("inputDateTime");
                                        String operator = jsonObject.getString("inputOperator");
                                        String inputNum = jsonObject.getString("inputNum");
                                        JSONObject jsonMaterial = jsonObject.getJSONObject("inputMaterial");
                                        String materialYear;
                                        if (jsonMaterial.get("materialYear") == JSONObject.NULL) {
                                            materialYear = "";
                                        } else {
                                            materialYear = jsonMaterial.getString("materialYear");
                                        }
                                        String materialUnit;
                                        if (jsonMaterial.get("materialUnit") == JSONObject.NULL) {
                                            materialUnit = "";
                                        } else {
                                            materialUnit = jsonMaterial.getString("materialUnit");
                                        }
                                        MaterialStock materialStock = new MaterialStock(jsonMaterial.getInt("id"), jsonMaterial.getString("materialID"),
                                                jsonMaterial.getString("materialType"), jsonMaterial.getString("materialStoreState"),
                                                jsonMaterial.getString("materialMark"), jsonMaterial.getString("materialBand"),
                                                jsonMaterial.getString("materialOriginal"), materialYear,
                                                jsonMaterial.getString("materialState"), jsonMaterial.getString("materialPosition"),
                                                materialUnit, jsonMaterial.getString("description"),
                                                jsonMaterial.getString("materialNum"));
                                        int owner = jsonMaterial.getInt("owner");
                                        MaterialInRecord materialInRecord = new MaterialInRecord(recordId, inputDateTime, operator, inputNum, materialStock, owner);
                                        mrlist.add(materialInRecord);
                                    }

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE);
                                            textNotExist.setVisibility(View.GONE);
                                            recyclerView.setVisibility(View.VISIBLE);

                                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                            recyclerView.setLayoutManager(layoutManager);
                                            MaterialInRecordAdapter materialInRecordAdapter = new MaterialInRecordAdapter(mrlist);
                                            recyclerView.setAdapter(materialInRecordAdapter);
                                            materialInRecordAdapter.setOnItemClickListener(new MaterialInRecordAdapter.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(View view2, int position) {
                                                    //传递对象
                                                    MatInRecDetailFragment matInRecDetailFragment = new MatInRecDetailFragment();
                                                    MaterialInRecord materialInRecord = mrlist.get(position);
                                                    Bundle args = new Bundle();
                                                    args.putSerializable("MatInRecord", materialInRecord);
                                                    matInRecDetailFragment.setArguments(args);
                                                    replaceFragment(matInRecDetailFragment);
                                                }
                                            });
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
                            LogUtil.d("Get Mat In Rec", "failed");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    textNotExist.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "网络连接失败，请重新尝试", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });

        searchId.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    //当用户按下虚拟键盘的enter键时，执行搜索
                    searchImage.performClick();
                    return true;
                }
                return false;
            }
        });

        if (getArguments() != null) {
            //从入库记录详细信息返回，从新加载之前的查询界面
            searchId.setText(getArguments().getString("lastSearchId"));
            searchImage.performClick();
        }

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
