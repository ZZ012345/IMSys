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

public class MatInRecActivity extends BaseActivity {

    private ProgressBar progressBar;

    private TextView textNotExist;

    private RecyclerView recyclerView;

    private View pageController;

    private LinearLayout previousPage;

    private LinearLayout nextPage;

    private TextView pageCount;

    private String previousUrl = "";

    private String nextUrl = "";

    private List<MaterialInRecord> mrlist = new ArrayList<>();

    private int allNum;

    private int pageSize;
    
    private MaterialStock materialStock;

    private MaterialInRecordAdapter materialInRecordAdapter;

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
        setContentView(R.layout.activity_matinrec);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_matinrec);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        progressBar = (ProgressBar) findViewById(R.id.progressbar_matinrec);
        textNotExist = (TextView) findViewById(R.id.text_notexist_matinrec);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_matinrec);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MatInRecActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        pageController = LayoutInflater.from(MatInRecActivity.this).inflate(R.layout.page_item, recyclerView, false);
        previousPage = (LinearLayout) pageController.findViewById(R.id.previous_link);
        nextPage = (LinearLayout) pageController.findViewById(R.id.next_link);
        pageCount = (TextView) pageController.findViewById(R.id.page_count);

        progressBar.setVisibility(View.VISIBLE);
        textNotExist.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        Intent intent = getIntent();
        materialStock = (MaterialStock) intent.getSerializableExtra("stock");

        HttpUtil.getMatInRec(String.valueOf(materialStock.getDatabaseid()), new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                LogUtil.d("Get Mat In Rec jsondata", responseData);
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

                        mrlist.clear(); //清空入库记录
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int recordId = jsonObject.getInt("id");
                            String inputDateTime = jsonObject.getString("inputDateTime");
                            String operator = jsonObject.getString("inputOperator");
                            String inputNum = jsonObject.getString("inputNum");
                            String inputDescription = jsonObject.getString("inputDescription");
                            MaterialInRecord materialInRecord = new MaterialInRecord(recordId, inputDateTime, operator, inputNum, inputDescription);
                            mrlist.add(materialInRecord);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                textNotExist.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);

                                materialInRecordAdapter = new MaterialInRecordAdapter(mrlist);
                                recyclerView.setAdapter(materialInRecordAdapter);
                                materialInRecordAdapter.setFooterView(pageController);
                                materialInRecordAdapter.setOnItemClickListener(new MaterialInRecordAdapter.OnItemClickListener() {
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
                LogUtil.d("Get Mat In Rec", "failed");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        textNotExist.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        Toast.makeText(MatInRecActivity.this, "网络连接失败，请重新尝试", Toast.LENGTH_SHORT).show();
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
                        LogUtil.d("Get Mat In Rec Page jsondata", responseData);
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

                                        materialInRecordAdapter.notifyDataSetChanged();

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
                                mrlist.clear(); //清空入库记录
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int recordId = jsonObject.getInt("id");
                                    String inputDateTime = jsonObject.getString("inputDateTime");
                                    String operator = jsonObject.getString("inputOperator");
                                    String inputNum = jsonObject.getString("inputNum");
                                    String inputDescription = jsonObject.getString("inputDescription");
                                    MaterialInRecord materialInRecord = new MaterialInRecord(recordId, inputDateTime, operator, inputNum, inputDescription);
                                    mrlist.add(materialInRecord);
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        textNotExist.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);

                                        materialInRecordAdapter.notifyDataSetChanged();
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
                        LogUtil.d("Get Mat In Rec Page", "failed");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                textNotExist.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                                Toast.makeText(MatInRecActivity.this, "网络连接失败，请重新尝试", Toast.LENGTH_SHORT).show();
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
