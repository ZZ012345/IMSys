package com.rinc.imsys;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    private ProgressBar progressBar;

    private TextView textNotExist;

    private Button backButton;

    private RecyclerView recyclerView;

    private List<MaterialOutRecord> mrlist = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matoutrec, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_matoutrec);
        textNotExist = (TextView) view.findViewById(R.id.text_notexist_matoutrec);
        backButton = (Button) view.findViewById(R.id.button_back_matoutrec);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_matoutrec);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("材料出库记录");

        progressBar.setVisibility(View.VISIBLE);
        textNotExist.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        final MaterialStock materialStock = (MaterialStock) getArguments().getSerializable("stock");

        HttpUtil.getMatOutRec(materialStock.getId(), new okhttp3.Callback() {
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
                                backButton.setVisibility(View.VISIBLE);
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
                            MaterialOutRecord materialOutRecord = new MaterialOutRecord(recordId, outputDateTime, user, operator, outputNum, leftNum, materialStock, owner);
                            mrlist.add(materialOutRecord);
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                textNotExist.setVisibility(View.GONE);
                                backButton.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);

                                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                recyclerView.setLayoutManager(layoutManager);
                                MaterialOutRecordAdapter materialOutRecordAdapter = new MaterialOutRecordAdapter(mrlist);
                                recyclerView.setAdapter(materialOutRecordAdapter);
                                View footer = LayoutInflater.from(getActivity()).inflate(R.layout.button_item, recyclerView, false);
                                Button backButton2 = footer.findViewById(R.id.back_button_searchresult);
                                backButton2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view1) {
                                        MatDetailFragment matDetailFragment = new MatDetailFragment();
                                        Bundle args = new Bundle();
                                        args.putSerializable("stock", materialStock);
                                        args.putInt("lastfragment", getArguments().getInt("lastfragment"));
                                        matDetailFragment.setArguments(args);
                                        replaceFragment(matDetailFragment);
                                    }
                                });
                                materialOutRecordAdapter.setFooterView(footer);
                                materialOutRecordAdapter.setOnItemClickListener(new MaterialOutRecordAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view1, int position) {
                                        //传递对象
                                        if (position != mrlist.size()) {
                                            MatOutRecDetailFragment matOutRecDetailFragment = new MatOutRecDetailFragment();
                                            Bundle args = new Bundle();
                                            args.putSerializable("MatOutRecord", mrlist.get(position));
                                            args.putInt("lastfragment", getArguments().getInt("lastfragment"));
                                            matOutRecDetailFragment.setArguments(args);
                                            replaceFragment(matOutRecDetailFragment);
                                        }
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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                MatDetailFragment matDetailFragment = new MatDetailFragment();
                Bundle args = new Bundle();
                args.putSerializable("stock", materialStock);
                args.putInt("lastfragment", getArguments().getInt("lastfragment"));
                matDetailFragment.setArguments(args);
                replaceFragment(matDetailFragment);
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
