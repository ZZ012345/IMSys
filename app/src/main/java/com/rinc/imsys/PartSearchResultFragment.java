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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhouZhi on 2017/9/8.
 */

public class PartSearchResultFragment extends Fragment {

    private RecyclerView recyclerView;

    private List<PartStock> mlist = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_partsearchresult, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_partsearchresult);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("查找结果");

        String responseData = getArguments().getString("PartSearchResult");
        mlist.clear();
        try {
            JSONArray jsonArray = new JSONArray(responseData);
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
                        machineBand, condition, vulnerability, description, num);
                mlist.add(partStock);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        EnhancedPartStockAdapter enhancedPartStockAdapter = new EnhancedPartStockAdapter(mlist);
        recyclerView.setAdapter(enhancedPartStockAdapter);
        View footer = LayoutInflater.from(getActivity()).inflate(R.layout.button_item, recyclerView, false);
        Button backButton = footer.findViewById(R.id.back_button_searchresult);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                replaceFragment(new PartSearchFragment());
            }
        });
        enhancedPartStockAdapter.setFooterView(footer);
        enhancedPartStockAdapter.setOnItemClickListener(new EnhancedPartStockAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view1, int position) {
                if (position != mlist.size()) {
                    //非返回键，传递对象
                    PartDetailFragment partDetailFragment = new PartDetailFragment();
                    PartStock partStock = mlist.get(position);
                    Bundle args = new Bundle();
                    args.putSerializable("stock", partStock);
                    args.putInt("lastfragment", 2);
                    partDetailFragment.setArguments(args);
                    replaceFragment(partDetailFragment);
                }
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
