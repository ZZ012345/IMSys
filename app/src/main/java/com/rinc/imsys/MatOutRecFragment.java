package com.rinc.imsys;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class MatOutRecFragment extends Fragment {

    private EditText searchId;

    private ImageView searchImage;

    private ProgressBar progressBar;

    private TextView textNotExist;

    private RecyclerView recyclerView;

    private List<MaterialOutRecord> mrlist = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matoutrec, container, false);

        searchId = (EditText) view.findViewById(R.id.input_matoutrec);
        searchImage = (ImageView) view.findViewById(R.id.search_matoutrec);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_matoutrec);
        textNotExist = (TextView) view.findViewById(R.id.text_notexist_matoutrec);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_matoutrec);

        searchId.setVisibility(View.VISIBLE);
        searchImage.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        textNotExist.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();

                String textSeach = searchId.getText().toString();
                if (textSeach.length() != 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    textNotExist.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);

                    HttpUtil.getMatOutRec(textSeach, new okhttp3.Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseData = response.body().string();
                            LogUtil.d("Get Mat Out Rec jsondata", responseData);
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
                                    mrlist.clear(); //清空出库记录
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        int recordId = jsonObject.getInt("id");
                                        String outputDateTime = jsonObject.getString("outputDateTime");
                                        String operator = jsonObject.getString("outputOperator");
                                        String outputNum = jsonObject.getString("outputNum");
                                        String leftNum = jsonObject.getString("leftNum");
                                        String user = jsonObject.getString("materialUser");
                                        JSONObject jsonMaterial = jsonObject.getJSONObject("outputMaterial");
                                        String materialYear;
                                        if (jsonMaterial.get("materialYear") == JSONObject.NULL) {
                                            materialYear = "";
                                        } else {
                                            materialYear = jsonMaterial.getString("materialYear");
                                        }
                                        MaterialStock materialStock = new MaterialStock(jsonMaterial.getInt("id"), jsonMaterial.getString("materialID"),
                                                jsonMaterial.getString("materialType"), jsonMaterial.getString("materialStoreState"),
                                                jsonMaterial.getString("materialMark"), jsonMaterial.getString("materialBand"),
                                                jsonMaterial.getString("materialOriginal"), materialYear,
                                                jsonMaterial.getString("materialState"), jsonMaterial.getString("materialPosition"),
                                                jsonMaterial.getString("materialUnit"), jsonMaterial.getString("description"),
                                                jsonMaterial.getString("materialNum"));
                                        int owner = jsonMaterial.getInt("owner");
                                        MaterialOutRecord materialOutRecord = new MaterialOutRecord(recordId, outputDateTime, user, operator, outputNum, leftNum, materialStock, owner);
                                        mrlist.add(materialOutRecord);
                                    }

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE);
                                            textNotExist.setVisibility(View.GONE);
                                            recyclerView.setVisibility(View.VISIBLE);

                                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                            recyclerView.setLayoutManager(layoutManager);
                                            MaterialOutRecordAdapter materialOutRecordAdapter = new MaterialOutRecordAdapter(mrlist);
                                            recyclerView.setAdapter(materialOutRecordAdapter);
                                            materialOutRecordAdapter.setOnItemClickListener(new MaterialOutRecordAdapter.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(View view2, int position) {
                                                    //传递对象
                                                    MatOutRecDetailFragment matOutRecDetailFragment = new MatOutRecDetailFragment();
                                                    MaterialOutRecord materialOutRecord = mrlist.get(position);
                                                    Bundle args = new Bundle();
                                                    args.putSerializable("MatOutRecord", materialOutRecord);
                                                    matOutRecDetailFragment.setArguments(args);
                                                    replaceFragment(matOutRecDetailFragment);
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
                            LogUtil.d("Get Mat Out Rec", "failed");
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
            //从出库记录详细信息返回，从新加载之前的查询界面
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
