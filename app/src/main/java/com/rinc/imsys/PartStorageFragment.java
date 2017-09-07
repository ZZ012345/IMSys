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
 * Created by ZhouZhi on 2017/9/1.
 */

public class PartStorageFragment extends Fragment {

    private ProgressBar progressBar;

    private TextView textNotExist;

    private RecyclerView recyclerView;

    private List<PartStock> mlist = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_partstorage, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_partstorage);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_partstorage);
        textNotExist = (TextView) view.findViewById(R.id.text_notexist_partstorage);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("零件库库存品信息");

        progressBar.setVisibility(View.VISIBLE);
        textNotExist.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        HttpUtil.getPartStorage(new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                LogUtil.d("Get Part Storage response", responseData);
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
                            String id = jsonObject.getString("partID");
                            String type = jsonObject.getString("partType");
                            String storestate = jsonObject.getString("partStoreState");
                            String mark = jsonObject.getString("partMark");
                            String band = jsonObject.getString("partBand");
                            String original = jsonObject.getString("partOriginal");
                            String year;
                            if (jsonObject.get("partYear") == JSONObject.NULL) {
                                year = "";
                            } else {
                                year = jsonObject.getString("partYear");
                            }
                            String state = jsonObject.getString("partState");
                            String position = jsonObject.getString("partPosition");
                            String unit;
                            if (jsonObject.get("partUnit") == JSONObject.NULL) {
                                unit = "";
                            } else {
                                unit = jsonObject.getString("partUnit");
                            }
                            String name = jsonObject.getString("partName");
                            String company = jsonObject.getString("partCompany");
                            String machineName = jsonObject.getString("partMachineName");
                            String machineType = jsonObject.getString("partMachineType");
                            String machineBand = jsonObject.getString("partMachineBand");
                            String condition = jsonObject.getString("partCondition");
                            String vulnerability = jsonObject.getString("partVulnerability");
                            String description = jsonObject.getString("description");
                            String num = jsonObject.getString("partNum");
                            PartStock partStock = new PartStock(databaseid, id, type, storestate, mark, band,
                                    original, year, state, position, unit, name, company, machineName, machineType,
                                    machineBand, condition,vulnerability, description, num);
                            mlist.add(partStock);
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                textNotExist.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);

                                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                recyclerView.setLayoutManager(layoutManager);
                                PartStockAdapter partStockAdapter = new PartStockAdapter(mlist);
                                recyclerView.setAdapter(partStockAdapter);
                                partStockAdapter.setOnItemClickListener(new PartStockAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view1, int position) {
                                        //传递对象
                                        PartDetailFragment partDetailFragment = new PartDetailFragment();
                                        PartStock partStock = mlist.get(position);
                                        Bundle args = new Bundle();
                                        args.putSerializable("stock", partStock);
                                        args.putInt("lastfragment", 1);
                                        partDetailFragment.setArguments(args);
                                        replaceFragment(partDetailFragment);
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
                LogUtil.d("Get Part Storage", "failed");
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
