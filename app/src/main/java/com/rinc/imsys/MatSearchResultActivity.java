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
 * Created by ZhouZhi on 2017/9/26.
 */

public class MatSearchResultActivity extends BaseActivity {

    private ProgressBar progressBar;

    private TextView textNotExist;

    private RecyclerView recyclerView;

    private View pageController;

    private LinearLayout previousPage;

    private LinearLayout nextPage;
    
    private TextView pageCount;

    private String previousUrl = "";

    private String nextUrl = "";

    private List<MaterialStock> mlist = new ArrayList<>();

    private int allNum;

    private int pageSize;

    private int lastClick = 0;

    private MaterialStockAdapter materialStockAdapter;
    
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
        setContentView(R.layout.activity_matsearchresult);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_matsearchresult);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        progressBar = (ProgressBar) findViewById(R.id.progressbar_matsearchresult);
        textNotExist = (TextView) findViewById(R.id.text_notexist_matsearchresult);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_matsearchresult);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MatSearchResultActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        pageController = LayoutInflater.from(MatSearchResultActivity.this).inflate(R.layout.page_item, recyclerView, false);
        previousPage = (LinearLayout) pageController.findViewById(R.id.previous_link);
        nextPage = (LinearLayout) pageController.findViewById(R.id.next_link);
        pageCount = (TextView) pageController.findViewById(R.id.page_count);

        progressBar.setVisibility(View.VISIBLE);
        textNotExist.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        HttpUtil.searchMat(SearchRecord.id_mat, SearchRecord.type_mat, SearchRecord.band_mat, SearchRecord.original_mat,
                SearchRecord.position_mat, SearchRecord.yearstart_mat, SearchRecord.yearend_mat, new okhttp3.Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        LogUtil.d("Mat Search Result jsondata", responseData);
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
                                    String unit;
                                    if (jsonObject.get("materialUnit") == JSONObject.NULL) {
                                        unit = "";
                                    } else {
                                        unit = jsonObject.getString("materialUnit");
                                    }
                                    String description = jsonObject.getString("description");
                                    String num = jsonObject.getString("materialNum");
                                    MaterialStock materialStock = new MaterialStock(databaseid, id, type, storestate, mark, band,
                                            original, year, state, position, unit, description, num);
                                    mlist.add(materialStock);
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        textNotExist.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);

                                        materialStockAdapter = new MaterialStockAdapter(mlist);
                                        recyclerView.setAdapter(materialStockAdapter);
                                        materialStockAdapter.setFooterView(pageController);
                                        materialStockAdapter.setOnItemClickListener(new MaterialStockAdapter.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(View view, int position) {
                                                if (position != mlist.size()) {
                                                    //传递对象
                                                    lastClick = position;
                                                    MaterialStock materialStock = mlist.get(position);
                                                    Intent intent = new Intent(MatSearchResultActivity.this, MatDetailActivity.class);
                                                    intent.putExtra("stock", materialStock);
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
                        LogUtil.d("Get Mat Search Result", "failed");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                textNotExist.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                                Toast.makeText(MatSearchResultActivity.this, "网络连接失败，请重新尝试", Toast.LENGTH_SHORT).show();
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
                        LogUtil.d("Mat Search Result Page jsondata", responseData);
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

                                        materialStockAdapter = new MaterialStockAdapter(mlist);
                                        recyclerView.swapAdapter(materialStockAdapter, true);
                                        materialStockAdapter.setFooterView(pageController);

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
                                    String unit;
                                    if (jsonObject.get("materialUnit") == JSONObject.NULL) {
                                        unit = "";
                                    } else {
                                        unit = jsonObject.getString("materialUnit");
                                    }
                                    String description = jsonObject.getString("description");
                                    String num = jsonObject.getString("materialNum");
                                    MaterialStock materialStock = new MaterialStock(databaseid, id, type, storestate, mark, band,
                                            original, year, state, position, unit, description, num);
                                    mlist.add(materialStock);
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        textNotExist.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);

                                        materialStockAdapter = new MaterialStockAdapter(mlist);
                                        recyclerView.swapAdapter(materialStockAdapter, true);
                                        materialStockAdapter.setFooterView(pageController);
                                        materialStockAdapter.setOnItemClickListener(new MaterialStockAdapter.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(View view1, int position) {
                                                if (position != mlist.size()) {
                                                    //传递对象
                                                    lastClick = position;
                                                    MaterialStock materialStock = mlist.get(position);
                                                    Intent intent = new Intent(MatSearchResultActivity.this, MatDetailActivity.class);
                                                    intent.putExtra("stock", materialStock);
                                                    startActivityForResult(intent, 1);
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
                        LogUtil.d("Get Mat Search Result Page", "failed");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                textNotExist.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                                Toast.makeText(MatSearchResultActivity.this, "网络连接失败，请重新尝试", Toast.LENGTH_SHORT).show();
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
                    MaterialStock materialStock = (MaterialStock) data.getSerializableExtra("stock");
                    mlist.remove(lastClick);
                    mlist.add(lastClick, materialStock);
                    materialStockAdapter = new MaterialStockAdapter(mlist);
                    recyclerView.swapAdapter(materialStockAdapter, true);
                    materialStockAdapter.setFooterView(pageController);
                    materialStockAdapter.setOnItemClickListener(new MaterialStockAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            if (position != mlist.size()) {
                                //传递对象
                                lastClick = position;
                                MaterialStock materialStock = mlist.get(position);
                                Intent intent = new Intent(MatSearchResultActivity.this, MatDetailActivity.class);
                                intent.putExtra("stock", materialStock);
                                startActivityForResult(intent, 1);
                            }
                        }
                    });
                } else if (resultCode == SearchRecord.RESULT_DELETE) {
                    mlist.remove(lastClick);
                    materialStockAdapter = new MaterialStockAdapter(mlist);
                    recyclerView.swapAdapter(materialStockAdapter, true);
                    materialStockAdapter.setFooterView(pageController);
                    materialStockAdapter.setOnItemClickListener(new MaterialStockAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            if (position != mlist.size()) {
                                //传递对象
                                lastClick = position;
                                MaterialStock materialStock = mlist.get(position);
                                Intent intent = new Intent(MatSearchResultActivity.this, MatDetailActivity.class);
                                intent.putExtra("stock", materialStock);
                                startActivityForResult(intent, 1);
                            }
                        }
                    });
                }
                break;
            default:
        }
    }
}
