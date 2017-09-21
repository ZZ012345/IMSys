package com.rinc.imsys;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
 * Created by ZhouZhi on 2017/9/8.
 */

public class PartSearchResultFragment extends BaseFragment {

    private ProgressBar progressBar;

    private TextView textNotExist;

    private Button backButton;

    private RecyclerView recyclerView;

    private View pageController;

    private LinearLayout previousPage;

    private LinearLayout nextPage;

    private Button backButtonBottom;

    private TextView pageCount;

    private String previousUrl = "";

    private String nextUrl = "";

    private List<PartStock> mlist = new ArrayList<>();

    private int allNum;

    private int pageSize;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_partsearchresult, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_partsearchresult);
        textNotExist = (TextView) view.findViewById(R.id.text_notexist_partsearchresult);
        backButton = (Button) view.findViewById(R.id.button_back_partsearchresult);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_partsearchresult);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        pageController = LayoutInflater.from(getActivity()).inflate(R.layout.pageandback_item, recyclerView, false);
        previousPage = (LinearLayout) pageController.findViewById(R.id.previous_link_pageandback);
        nextPage = (LinearLayout) pageController.findViewById(R.id.next_link_pageandback);
        backButtonBottom = (Button) pageController.findViewById(R.id.back_button_pageandback);
        pageCount = (TextView) pageController.findViewById(R.id.page_count_pageandback);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("查找结果");

        progressBar.setVisibility(View.VISIBLE);
        textNotExist.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        HttpUtil.searchPart(SearchRecord.id_part, SearchRecord.type_part, SearchRecord.band_part, SearchRecord.original_part,
                SearchRecord.position_part, SearchRecord.yearstart_part, SearchRecord.yearend_part, new okhttp3.Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        LogUtil.d("Part Search Result jsondata", responseData);
                        try {
                            JSONObject jsonAll = new JSONObject(responseData);
                            allNum = jsonAll.getInt("count");
                            JSONArray jsonArray = new JSONArray(jsonAll.getString("results"));
                            if (allNum == 0) {
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
                                if (jsonAll.get("next") != JSONObject.NULL) {
                                    nextUrl = jsonAll.getString("next");
                                }

                                mlist.clear(); //清空查找结果
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

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        textNotExist.setVisibility(View.GONE);
                                        backButton.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);

                                        PartStockAdapter partStockAdapter = new PartStockAdapter(mlist);
                                        recyclerView.setAdapter(partStockAdapter);
                                        partStockAdapter.setFooterView(pageController);
                                        partStockAdapter.setOnItemClickListener(new PartStockAdapter.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(View view1, int position) {
                                                if (position != mlist.size()) {
                                                    //传递对象
                                                    PartDetailFragment partDetailFragment = new PartDetailFragment();
                                                    PartStock partStock = mlist.get(position);
                                                    Bundle args = new Bundle();
                                                    args.putSerializable("stock", partStock);
                                                    partDetailFragment.setArguments(args);
                                                    replaceFragment(partDetailFragment);
                                                }
                                            }
                                        });

                                        previousPage.setVisibility(View.GONE);
                                        if (nextUrl.length() != 0) {
                                            nextPage.setVisibility(View.VISIBLE);
                                        } else {
                                            nextPage.setVisibility(View.GONE);
                                        }
                                        backButtonBottom.setVisibility(View.VISIBLE);

                                        pageSize = mlist.size();
                                        int pageNum = MatStorageFragment.getPageNum(allNum, pageSize);
                                        pageCount.setText("1/" + String.valueOf(pageNum));
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
                        LogUtil.d("Get Part Search Result", "failed");
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

        View.OnClickListener pageListener = new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                progressBar.setVisibility(View.VISIBLE);
                textNotExist.setVisibility(View.GONE);
                backButton.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);

                String url;
                if ((LinearLayout) view1 == previousPage) {
                    url = previousUrl;
                } else {
                    url = nextUrl;
                }

                HttpUtil.simpleGet(url, new okhttp3.Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        LogUtil.d("Part Part Result Page jsondata", responseData);
                        try {
                            JSONObject jsonAll = new JSONObject(responseData);
                            allNum = jsonAll.getInt("count");
                            if (jsonAll.get("previous") != JSONObject.NULL) {
                                previousUrl = jsonAll.getString("previous");
                            } else {
                                previousUrl = "";
                            }
                            if (jsonAll.get("next") != JSONObject.NULL) {
                                nextUrl = jsonAll.getString("next");
                            } else {
                                nextUrl = "";
                            }
                            JSONArray jsonArray = new JSONArray(jsonAll.getString("results"));
                            if (allNum == 0) {
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
                            } else if (jsonArray.length() == 0) {
                                //该页的内容已被删除
                                mlist.clear();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        textNotExist.setVisibility(View.GONE);
                                        backButton.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);

                                        PartStockAdapter partStockAdapter = new PartStockAdapter(mlist);
                                        recyclerView.swapAdapter(partStockAdapter, true);
                                        partStockAdapter.setFooterView(pageController);

                                        if (previousUrl.length() != 0) {
                                            previousPage.setVisibility(View.VISIBLE);
                                        } else {
                                            previousPage.setVisibility(View.GONE);
                                        }
                                        if (nextUrl.length() != 0) {
                                            nextPage.setVisibility(View.VISIBLE);
                                        } else {
                                            nextPage.setVisibility(View.GONE);
                                        }
                                        backButtonBottom.setVisibility(View.VISIBLE);

                                        pageCount.setText("");
                                    }
                                });
                            } else {
                                mlist.clear(); //清空查找结果
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

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        textNotExist.setVisibility(View.GONE);
                                        backButton.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);

                                        PartStockAdapter partStockAdapter = new PartStockAdapter(mlist);
                                        recyclerView.swapAdapter(partStockAdapter, true);
                                        partStockAdapter.setFooterView(pageController);
                                        partStockAdapter.setOnItemClickListener(new PartStockAdapter.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(View view2, int position) {
                                                if (position != mlist.size()) {
                                                    //传递对象
                                                    PartDetailFragment partDetailFragment = new PartDetailFragment();
                                                    PartStock partStock = mlist.get(position);
                                                    Bundle args = new Bundle();
                                                    args.putSerializable("stock", partStock);
                                                    partDetailFragment.setArguments(args);
                                                    replaceFragment(partDetailFragment);
                                                }
                                            }
                                        });

                                        if (previousUrl.length() != 0) {
                                            previousPage.setVisibility(View.VISIBLE);
                                        } else {
                                            previousPage.setVisibility(View.GONE);
                                        }
                                        if (nextUrl.length() != 0) {
                                            nextPage.setVisibility(View.VISIBLE);
                                        } else {
                                            nextPage.setVisibility(View.GONE);
                                        }
                                        backButtonBottom.setVisibility(View.VISIBLE);

                                        pageCount.setText(MatStorageFragment.getPageCountStr(previousUrl, nextUrl, allNum, pageSize));
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
                        LogUtil.d("Get Part Search Result Page", "failed");
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
            }
        };

        previousPage.setOnClickListener(pageListener);
        nextPage.setOnClickListener(pageListener);

        backButtonBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                replaceFragment(new PartSearchFragment());
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                replaceFragment(new PartSearchFragment());
            }
        });

        return view;
    }
}
