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
 * Created by zhouzhi on 2017/8/27.
 */

public class MatSearchResultFragment extends Fragment {

    private RecyclerView recyclerView;

    private Button backButton;

    private List<MaterialStock> mlist = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matsearchresult, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_matsearchresult);
        backButton = (Button) view.findViewById(R.id.button_back_matsearchresult);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("查找结果");

        String responseData = getArguments().getString("MatSearchResult");
        mlist.clear();
        try {
            JSONArray jsonArray = new JSONArray(responseData);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                replaceFragment(new MatSearchFragment());
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
