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

public class EquipSearchResultActivity extends BaseActivity {

    private ProgressBar progressBar;

    private TextView textNotExist;

    private RecyclerView recyclerView;

    private View pageController;

    private LinearLayout previousPage;

    private LinearLayout nextPage;

    private TextView pageCount;

    private String previousUrl = "";

    private String nextUrl = "";

    private List<EquipStock> mlist = new ArrayList<>();

    private int allNum;

    private int pageSize;

    private int lastClick = 0;

    private EquipStockAdapter equipStockAdapter;

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
        setContentView(R.layout.activity_equipsearchresult);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_equipsearchresult);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        progressBar = (ProgressBar) findViewById(R.id.progressbar_equipsearchresult);
        textNotExist = (TextView) findViewById(R.id.text_notexist_equipsearchresult);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_equipsearchresult);
        LinearLayoutManager layoutManager = new LinearLayoutManager(EquipSearchResultActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        pageController = LayoutInflater.from(EquipSearchResultActivity.this).inflate(R.layout.page_item, recyclerView, false);
        previousPage = (LinearLayout) pageController.findViewById(R.id.previous_link);
        nextPage = (LinearLayout) pageController.findViewById(R.id.next_link);
        pageCount = (TextView) pageController.findViewById(R.id.page_count);

        progressBar.setVisibility(View.VISIBLE);
        textNotExist.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        HttpUtil.searchEquip(SearchRecord.id_equip, SearchRecord.type_equip, SearchRecord.band_equip, SearchRecord.original_equip,
                SearchRecord.position_equip, SearchRecord.yearstart_equip, SearchRecord.yearend_equip, new okhttp3.Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        LogUtil.d("Equip Search Result jsondata", responseData);
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
                                    String id = jsonObject.getString("equipmentID");
                                    String type = jsonObject.getString("equipmentType");
                                    String storestate = jsonObject.getString("equipmentStoreState");
                                    String mark = jsonObject.getString("equipmentMark");
                                    String hour;
                                    if (jsonObject.get("equipmentHour") == JSONObject.NULL) {
                                        hour = "";
                                    } else {
                                        hour = jsonObject.getString("equipmentHour");
                                    }
                                    String band = jsonObject.getString("equipmentBand");
                                    String original = jsonObject.getString("equipmentOriginal");
                                    String year;
                                    if (jsonObject.get("equipmentYear") == JSONObject.NULL) {
                                        year = "";
                                    } else {
                                        year = jsonObject.getString("equipmentYear");
                                    }
                                    String state = jsonObject.getString("equipmentState");
                                    String position = jsonObject.getString("equipmentPosition");
                                    String unit;
                                    if (jsonObject.get("equipmentUnit") == JSONObject.NULL) {
                                        unit = "";
                                    } else {
                                        unit = jsonObject.getString("equipmentUnit");
                                    }
                                    String description = jsonObject.getString("description");
                                    String num = jsonObject.getString("equipmentNum");
                                    EquipStock equipStock = new EquipStock(databaseid, id, type, storestate, mark, hour, band,
                                            original, year, state, position, unit, description, num);
                                    mlist.add(equipStock);
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        textNotExist.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);

                                        equipStockAdapter = new EquipStockAdapter(mlist);
                                        recyclerView.setAdapter(equipStockAdapter);
                                        equipStockAdapter.setFooterView(pageController);
                                        equipStockAdapter.setOnItemClickListener(new EquipStockAdapter.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(View view, int position) {
                                                if (position != mlist.size()) {
                                                    //传递对象
                                                    lastClick = position;
                                                    EquipStock equipStock = mlist.get(position);
                                                    Intent intent = new Intent(EquipSearchResultActivity.this, EquipDetailActivity.class);
                                                    intent.putExtra("stock", equipStock);
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
                        LogUtil.d("Get Equip Search Result", "failed");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                textNotExist.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                                Toast.makeText(EquipSearchResultActivity.this, "网络连接失败，请重新尝试", Toast.LENGTH_SHORT).show();
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
                        LogUtil.d("Equip Search Result Page jsondata", responseData);
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

                                        equipStockAdapter.notifyDataSetChanged();

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
                                    String id = jsonObject.getString("equipmentID");
                                    String type = jsonObject.getString("equipmentType");
                                    String storestate = jsonObject.getString("equipmentStoreState");
                                    String mark = jsonObject.getString("equipmentMark");
                                    String hour;
                                    if (jsonObject.get("equipmentHour") == JSONObject.NULL) {
                                        hour = "";
                                    } else {
                                        hour = jsonObject.getString("equipmentHour");
                                    }
                                    String band = jsonObject.getString("equipmentBand");
                                    String original = jsonObject.getString("equipmentOriginal");
                                    String year;
                                    if (jsonObject.get("equipmentYear") == JSONObject.NULL) {
                                        year = "";
                                    } else {
                                        year = jsonObject.getString("equipmentYear");
                                    }
                                    String state = jsonObject.getString("equipmentState");
                                    String position = jsonObject.getString("equipmentPosition");
                                    String unit;
                                    if (jsonObject.get("equipmentUnit") == JSONObject.NULL) {
                                        unit = "";
                                    } else {
                                        unit = jsonObject.getString("equipmentUnit");
                                    }
                                    String description = jsonObject.getString("description");
                                    String num = jsonObject.getString("equipmentNum");
                                    EquipStock equipStock = new EquipStock(databaseid, id, type, storestate, mark, hour, band,
                                            original, year, state, position, unit, description, num);
                                    mlist.add(equipStock);
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        textNotExist.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);

                                        equipStockAdapter.notifyDataSetChanged();
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
                        LogUtil.d("Get Equip Search Result Page", "failed");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                textNotExist.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                                Toast.makeText(EquipSearchResultActivity.this, "网络连接失败，请重新尝试", Toast.LENGTH_SHORT).show();
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
                    EquipStock equipStock = (EquipStock) data.getSerializableExtra("stock");
                    mlist.remove(lastClick);
                    mlist.add(lastClick, equipStock);
                    equipStockAdapter.notifyItemChanged(lastClick);
                } else if (resultCode == SearchRecord.RESULT_DELETE) {
                    mlist.remove(lastClick);
                    equipStockAdapter.notifyDataSetChanged();
                }
                break;
            default:
        }
    }
}
