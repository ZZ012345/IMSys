package com.rinc.imsys;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
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
 * Created by ZhouZhi on 2017/9/29.
 */

public class PartSearchResultActivity extends BaseActivity {

    private ProgressBar progressBar;

    private TextView textNotExist;

    private RecyclerView recyclerView;

    private View pageController;

    private LinearLayout previousPage;

    private LinearLayout nextPage;

    private TextView pageCount;

    private String previousUrl = "";

    private String nextUrl = "";

    private List<PartStock> mlist = new ArrayList<>();

    private int allNum;

    private int pageSize;

    private int lastClick = 0;

    private PartStockAdapter partStockAdapter;
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partsearchresult);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_partsearchresult);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        progressBar = (ProgressBar) findViewById(R.id.progressbar_partsearchresult);
        textNotExist = (TextView) findViewById(R.id.text_notexist_partsearchresult);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_partsearchresult);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PartSearchResultActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        pageController = LayoutInflater.from(PartSearchResultActivity.this).inflate(R.layout.page_item, recyclerView, false);
        previousPage = (LinearLayout) pageController.findViewById(R.id.previous_link);
        nextPage = (LinearLayout) pageController.findViewById(R.id.next_link);
        pageCount = (TextView) pageController.findViewById(R.id.page_count);

        progressBar.setVisibility(View.VISIBLE);
        textNotExist.setVisibility(View.GONE);
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        textNotExist.setVisibility(View.VISIBLE);
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

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        textNotExist.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);

                                        partStockAdapter = new PartStockAdapter(mlist);
                                        recyclerView.setAdapter(partStockAdapter);
                                        partStockAdapter.setFooterView(pageController);
                                        partStockAdapter.setOnItemClickListener(new PartStockAdapter.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(View view, int position) {
                                                if (position != mlist.size()) {
                                                    //传递对象
                                                    lastClick = position;
                                                    PartStock partStock = mlist.get(position);
                                                    Intent intent = new Intent(PartSearchResultActivity.this, PartDetailActivity.class);
                                                    intent.putExtra("stock", partStock);
                                                    startActivityForResult(intent, 1);
                                                }
                                            }
                                        });

                                        previousPage.setVisibility(View.GONE);
                                        if (nextUrl.length() != 0) {
                                            nextPage.setVisibility(View.VISIBLE);
                                        } else {
                                            nextPage.setVisibility(View.GONE);
                                        }

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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                textNotExist.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                                Toast.makeText(PartSearchResultActivity.this, "网络连接失败，请重新尝试", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

        View.OnClickListener pageListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                textNotExist.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);

                String url;
                if ((LinearLayout) view == previousPage) {
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        textNotExist.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);
                                    }
                                });
                            } else if (jsonArray.length() == 0) {
                                //该页的内容已被删除
                                mlist.clear();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        textNotExist.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);

                                        partStockAdapter.notifyDataSetChanged();

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

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        textNotExist.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);

                                        partStockAdapter.notifyDataSetChanged();
                                        recyclerView.scrollToPosition(0);

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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                textNotExist.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                                Toast.makeText(PartSearchResultActivity.this, "网络连接失败，请重新尝试", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        };

        previousPage.setOnClickListener(pageListener);
        nextPage.setOnClickListener(pageListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == SearchRecord.RESULT_MODIFY) {
                    PartStock partStock = (PartStock) data.getSerializableExtra("stock");
                    mlist.remove(lastClick);
                    mlist.add(lastClick, partStock);
                    partStockAdapter.notifyItemChanged(lastClick);
                } else if (resultCode == SearchRecord.RESULT_DELETE) {
                    mlist.remove(lastClick);
                    partStockAdapter.notifyDataSetChanged();
                }
                break;
            default:
        }
    }
}
