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
 * Created by zhouzhi on 2017/8/18.
 */

public class MatInRecFragment extends BaseFragment {

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

    private List<MaterialInRecord> mrlist = new ArrayList<>();

    private int allNum;

    private int pageSize;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matinrec, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_matinrec);
        textNotExist = (TextView) view.findViewById(R.id.text_notexist_matinrec);
        backButton = (Button) view.findViewById(R.id.button_back_matinrec);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_matinrec);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        pageController = LayoutInflater.from(getActivity()).inflate(R.layout.pageandback_item, recyclerView, false);
        previousPage = (LinearLayout) pageController.findViewById(R.id.previous_link_pageandback);
        nextPage = (LinearLayout) pageController.findViewById(R.id.next_link_pageandback);
        backButtonBottom = (Button) pageController.findViewById(R.id.back_button_pageandback);
        pageCount = (TextView) pageController.findViewById(R.id.page_count_pageandback);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("材料入库记录");

        progressBar.setVisibility(View.VISIBLE);
        textNotExist.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        final MaterialStock materialStock = (MaterialStock) getArguments().getSerializable("stock");

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

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                textNotExist.setVisibility(View.GONE);
                                backButton.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);

                                MaterialInRecordAdapter materialInRecordAdapter = new MaterialInRecordAdapter(mrlist);
                                recyclerView.setAdapter(materialInRecordAdapter);
                                materialInRecordAdapter.setFooterView(pageController);
                                materialInRecordAdapter.setOnItemClickListener(new MaterialInRecordAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view1, int position) {

                                    }
                                });

                                previousPage.setVisibility(View.GONE);
                                if (nextUrl.length() != 0) {
                                    nextPage.setVisibility(View.VISIBLE);
                                } else {
                                    nextPage.setVisibility(View.GONE);
                                }
                                backButtonBottom.setVisibility(View.VISIBLE);

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
                                mrlist.clear();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        textNotExist.setVisibility(View.GONE);
                                        backButton.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);

                                        MaterialInRecordAdapter materialInRecordAdapter = new MaterialInRecordAdapter(mrlist);
                                        recyclerView.swapAdapter(materialInRecordAdapter, true);
                                        materialInRecordAdapter.setFooterView(pageController);

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

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        textNotExist.setVisibility(View.GONE);
                                        backButton.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);

                                        MaterialInRecordAdapter materialInRecordAdapter = new MaterialInRecordAdapter(mrlist);
                                        recyclerView.swapAdapter(materialInRecordAdapter, true);
                                        materialInRecordAdapter.setFooterView(pageController);
                                        materialInRecordAdapter.setOnItemClickListener(new MaterialInRecordAdapter.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(View view2, int position) {

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
                        LogUtil.d("Get Mat In Rec Page", "failed");
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
                MatDetailFragment matDetailFragment = new MatDetailFragment();
                Bundle args = new Bundle();
                args.putSerializable("stock", materialStock);
                matDetailFragment.setArguments(args);
                replaceFragment(matDetailFragment);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                MatDetailFragment matDetailFragment = new MatDetailFragment();
                Bundle args = new Bundle();
                args.putSerializable("stock", materialStock);
                matDetailFragment.setArguments(args);
                replaceFragment(matDetailFragment);
            }
        });

        return view;
    }
}
