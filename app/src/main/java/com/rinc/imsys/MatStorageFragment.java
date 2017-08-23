package com.rinc.imsys;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by zhouzhi on 2017/8/14.
 */

public class MatStorageFragment extends Fragment {

    private ProgressBar progressBar;

    private TextView textNotExist;

    private RecyclerView recyclerView;

    private List<MaterialStock> mlist = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matstorage, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_matstorage);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_matstorage);
        textNotExist = (TextView) view.findViewById(R.id.text_notexist_matstorage);

        progressBar.setVisibility(View.VISIBLE);
        textNotExist.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        HttpUtil.getMatStorage(new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                LogUtil.d("Get Mat Storage response", responseData);
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
                        mlist.clear(); //清空库存列表
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int databaseid = jsonObject.getInt("id");
                            String id = jsonObject.getString("materialID");
                            String type = jsonObject.getString("materialType");
                            String storestate = jsonObject.getString("materialStoreState");
                            String mark = jsonObject.getString("materialMark");
                            String band = jsonObject.getString("materialBand");
                            String original = jsonObject.getString("materialOriginal");
                            String year;
                            if (jsonObject.get("materialYear") == JSONObject.NULL) {
                                year = "";
                            } else {
                                year = jsonObject.getString("materialYear");
                            }
                            String state = jsonObject.getString("materialState");
                            String position = jsonObject.getString("materialPosition");
                            String unit = jsonObject.getString("materialUnit");
                            String description = jsonObject.getString("description");
                            String num = jsonObject.getString("materialNum");
                            MaterialStock materialStock = new MaterialStock(databaseid, id, type, storestate, mark, band,
                                    original, year, state, position, unit, description, num);
                            mlist.add(materialStock);
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                textNotExist.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);

                                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                recyclerView.setLayoutManager(layoutManager);
                                MaterialStockAdapter materialStockAdapter = new MaterialStockAdapter(mlist);
                                recyclerView.setAdapter(materialStockAdapter);
                                materialStockAdapter.setOnItemClickListener(new MaterialStockAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view1, int position) {
                                        //传递对象
                                        MatDetailFragment matDetailFragment = new MatDetailFragment();
                                        MaterialStock materialStock = mlist.get(position);
                                        Bundle args = new Bundle();
                                        args.putSerializable("stock", materialStock);
                                        matDetailFragment.setArguments(args);
                                        replaceFragment(matDetailFragment);
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
                LogUtil.d("Get Mat Storage", "failed");
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

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager =  getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_main, fragment);
        transaction.commit();
    }
}
