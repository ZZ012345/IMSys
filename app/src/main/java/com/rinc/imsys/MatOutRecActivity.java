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

public class MatOutRecActivity extends BaseActivity {

    private ProgressBar progressBar;

    private TextView textNotExist;

    private RecyclerView recyclerView;

    private View pageController;

    private LinearLayout previousPage;

    private LinearLayout nextPage;

    private TextView pageCount;

    private String previousUrl = "";

    private String nextUrl = "";

    private List<MaterialOutRecord> mrlist = new ArrayList<>();

    private int allNum;

    private int pageSize;

    private MaterialStock materialStock;

    private MaterialOutRecordAdapter materialOutRecordAdapter;
    
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
        setContentView(R.layout.activity_matoutrec);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_matoutrec);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        progressBar = (ProgressBar) findViewById(R.id.progressbar_matoutrec);
        textNotExist = (TextView) findViewById(R.id.text_notexist_matoutrec);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_matoutrec);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MatOutRecActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        pageController = LayoutInflater.from(MatOutRecActivity.this).inflate(R.layout.page_item, recyclerView, false);
        previousPage = (LinearLayout) pageController.findViewById(R.id.previous_link);
        nextPage = (LinearLayout) pageController.findViewById(R.id.next_link);
        pageCount = (TextView) pageController.findViewById(R.id.page_count);

        progressBar.setVisibility(View.VISIBLE);
        textNotExist.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        Intent intent = getIntent();
        materialStock = (MaterialStock) intent.getSerializableExtra("stock");

        HttpUtil.getMatOutRec(String.valueOf(materialStock.getDatabaseid()), new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                LogUtil.d("Get Mat Out Rec jsondata", responseData);
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

                        mrlist.clear(); //清空出库记录
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int recordId = jsonObject.getInt("id");
                            String outputDateTime = jsonObject.getString("outputDateTime");
                            String user = jsonObject.getString("materialUser");
                            String operator = jsonObject.getString("outputOperator");
                            String outputNum = jsonObject.getString("outputNum");
                            String leftNum = jsonObject.getString("leftNum");
                            String description = jsonObject.getString("outputDescription");
                            MaterialOutRecord materialOutRecord = new MaterialOutRecord(recordId, outputDateTime, user, operator, outputNum, leftNum, description);
                            mrlist.add(materialOutRecord);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                textNotExist.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);

                                materialOutRecordAdapter = new MaterialOutRecordAdapter(mrlist);
                                recyclerView.setAdapter(materialOutRecordAdapter);
                                materialOutRecordAdapter.setFooterView(pageController);
                                materialOutRecordAdapter.setOnItemClickListener(new MaterialOutRecordAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {

                                    }
                                });

                                previousPage.setVisibility(View.GONE);
                                if (nextUrl.length() != 0) {
                                    nextPage.setVisibility(View.VISIBLE);
                                } else {
                                    nextPage.setVisibility(View.GONE);
                                }

                                pageSize = mrlist.size();
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
                LogUtil.d("Get Mat Out Rec", "failed");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        textNotExist.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        Toast.makeText(MatOutRecActivity.this, "网络连接失败，请重新尝试", Toast.LENGTH_SHORT).show();
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
                        LogUtil.d("Get Mat Out Rec Page jsondata", responseData);
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
                                mrlist.clear();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        textNotExist.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);

                                        materialOutRecordAdapter.notifyDataSetChanged();

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
                                mrlist.clear(); //清空出库记录
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int recordId = jsonObject.getInt("id");
                                    String outputDateTime = jsonObject.getString("outputDateTime");
                                    String user = jsonObject.getString("materialUser");
                                    String operator = jsonObject.getString("outputOperator");
                                    String outputNum = jsonObject.getString("outputNum");
                                    String leftNum = jsonObject.getString("leftNum");
                                    String description = jsonObject.getString("outputDescription");
                                    MaterialOutRecord materialOutRecord = new MaterialOutRecord(recordId, outputDateTime, user, operator, outputNum, leftNum, description);
                                    mrlist.add(materialOutRecord);
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        textNotExist.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);

                                        materialOutRecordAdapter.notifyDataSetChanged();
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
                        LogUtil.d("Get Mat Out Rec Page", "failed");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                textNotExist.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                                Toast.makeText(MatOutRecActivity.this, "网络连接失败，请重新尝试", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        };

        previousPage.setOnClickListener(pageListener);
        nextPage.setOnClickListener(pageListener);
    }
}
