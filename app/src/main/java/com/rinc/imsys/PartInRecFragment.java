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
 * Created by ZhouZhi on 2017/9/11.
 */

public class PartInRecFragment extends Fragment {

    private ProgressBar progressBar;

    private TextView textNotExist;

    private Button backButton;

    private RecyclerView recyclerView;

    private List<PartInRecord> prlist = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_partinrec, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_partinrec);
        textNotExist = (TextView) view.findViewById(R.id.text_notexist_partinrec);
        backButton = (Button) view.findViewById(R.id.button_back_partinrec);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_partinrec);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("零件入库记录");

        progressBar.setVisibility(View.VISIBLE);
        textNotExist.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        final PartStock partStock = (PartStock) getArguments().getSerializable("stock");

        HttpUtil.getPartInRec(partStock.getId(), new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String responseData = response.body().string();
                LogUtil.d("Get Part In Rec jsondata", responseData);
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
                        prlist.clear(); //清空入库记录
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int recordId = jsonObject.getInt("id");
                            String inputDateTime = jsonObject.getString("inputDateTime");
                            String operator = jsonObject.getString("inputOperator");
                            String inputNum = jsonObject.getString("inputNum");
                            JSONObject jsonPart = jsonObject.getJSONObject("inputPart");
                            String partYear;
                            if (jsonPart.get("partYear") == JSONObject.NULL) {
                                partYear = "";
                            } else {
                                partYear = jsonPart.getString("partYear");
                            }
                            String partUnit;
                            if (jsonPart.get("partUnit") == JSONObject.NULL) {
                                partUnit = "";
                            } else {
                                partUnit = jsonPart.getString("partUnit");
                            }
                            PartStock partStock = new PartStock(jsonPart.getInt("id"), jsonPart.getString("partID"),
                                    jsonPart.getString("partType"), jsonPart.getString("partStoreState"),
                                    jsonPart.getString("partMark"), jsonPart.getString("partBand"),
                                    jsonPart.getString("partOriginal"), partYear, jsonPart.getString("partState"), jsonPart.getString("partPosition"),
                                    partUnit, jsonPart.getString("partName"), jsonPart.getString("partCompany"), jsonPart.getString("partMachineName"),
                                    jsonPart.getString("partMachineType"), jsonPart.getString("partMachineBand"), jsonPart.getString("partCondition"),
                                    jsonPart.getString("partVulnerability"), jsonPart.getString("description"), jsonPart.getString("partNum"));
                            int owner = jsonPart.getInt("owner");
                            PartInRecord partInRecord = new PartInRecord(recordId, inputDateTime, operator, inputNum, partStock, owner);
                            prlist.add(partInRecord);
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
                                PartInRecordAdapter partInRecordAdapter = new PartInRecordAdapter(prlist);
                                recyclerView.setAdapter(partInRecordAdapter);
                                View footer = LayoutInflater.from(getActivity()).inflate(R.layout.button_item, recyclerView, false);
                                Button backButton2 = footer.findViewById(R.id.back_button_searchresult);
                                backButton2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view1) {
                                        PartDetailFragment partDetailFragment = new PartDetailFragment();
                                        Bundle args = new Bundle();
                                        args.putSerializable("stock", partStock);
                                        args.putInt("lastfragment", getArguments().getInt("lastfragment"));
                                        partDetailFragment.setArguments(args);
                                        replaceFragment(partDetailFragment);
                                    }
                                });
                                partInRecordAdapter.setFooterView(footer);
                                partInRecordAdapter.setOnItemClickListener(new PartInRecordAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view1, int position) {
                                        //传递对象
                                        if (position != prlist.size()) {
                                            PartInRecDetailFragment partInRecDetailFragment = new PartInRecDetailFragment();
                                            Bundle args = new Bundle();
                                            args.putSerializable("PartInRecord", prlist.get(position));
                                            args.putInt("lastfragment", getArguments().getInt("lastfragment"));
                                            partInRecDetailFragment.setArguments(args);
                                            replaceFragment(partInRecDetailFragment);
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
                LogUtil.d("Get Part In Rec", "failed");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        textNotExist.setVisibility(View.GONE);
                        backButton.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "网络连接失败，请重新尝试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                PartDetailFragment partDetailFragment = new PartDetailFragment();
                Bundle args = new Bundle();
                args.putSerializable("stock", partStock);
                args.putInt("lastfragment", getArguments().getInt("lastfragment"));
                partDetailFragment.setArguments(args);
                replaceFragment(partDetailFragment);
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
