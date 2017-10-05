package com.rinc.imsys;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static android.app.Activity.RESULT_OK;

/**
 * Created by zhouzhi on 2017/8/14.
 */

public class MatStorageFragment extends BaseFragment {

    private SwipeRefreshLayout swipeRefreshLayout;

    private ProgressBar progressBar;

    private TextView textNotExist;

    private RecyclerView recyclerView;

    private View pageController;

    private LinearLayout previousPage;

    private LinearLayout nextPage;

    private TextView pageCount;

    private String previousUrl = "";

    private String nextUrl = "";

    private String currentUrl = null;

    private List<MaterialStock> mlist = new ArrayList<>();

    private int allNum;

    private int pageSize;

    private int lastClick = 0;

    private MaterialStockAdapter materialStockAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matstorage, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_matstorage);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_matstorage);
        textNotExist = (TextView) view.findViewById(R.id.text_notexist_matstorage);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_matstorage);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        pageController = LayoutInflater.from(getActivity()).inflate(R.layout.page_item, recyclerView, false);
        previousPage = (LinearLayout) pageController.findViewById(R.id.previous_link);
        nextPage = (LinearLayout) pageController.findViewById(R.id.next_link);
        pageCount = (TextView) pageController.findViewById(R.id.page_count);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("材料库库存品信息");

        progressBar.setVisibility(View.VISIBLE);
        textNotExist.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        HttpUtil.getMatStorage(new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                LogUtil.d("Get Mat Storage response", responseData);
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
                                recyclerView.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        if (jsonAll.get("next") != JSONObject.NULL) {
                            nextUrl = jsonAll.getString("next");
                        }

                        mlist.clear(); //清空库存列表
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

                        getActivity().runOnUiThread(new Runnable() {
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
                                    public void onItemClick(View view1, int position) {
                                        if (position != mlist.size()) {
                                            //传递对象
                                            lastClick = position;
                                            MaterialStock materialStock = mlist.get(position);
                                            Intent intent = new Intent(getActivity(), MatDetailActivity.class);
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
                                int pageNum = getPageNum(allNum, pageSize);
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
                LogUtil.d("Get Mat Storage", "failed");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        textNotExist.setVisibility(View.GONE);
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
                recyclerView.setVisibility(View.GONE);

                String url;
                if ((LinearLayout) view1 == previousPage) {
                    url = previousUrl;
                } else {
                    url = nextUrl;
                }
                currentUrl = url;

                HttpUtil.simpleGet(url, new okhttp3.Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData = response.body().string();
                        LogUtil.d("Get Mat Storage Page response", responseData);
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
                                        recyclerView.setVisibility(View.VISIBLE);

                                        materialStockAdapter.notifyDataSetChanged();

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
                                mlist.clear(); //清空库存列表
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


                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        textNotExist.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);

                                        materialStockAdapter.notifyDataSetChanged();
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

                                        pageCount.setText(getPageCountStr(previousUrl, nextUrl, allNum, pageSize));
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
                        LogUtil.d("Get Mat Storage Page", "failed");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                textNotExist.setVisibility(View.GONE);
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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);

                if (currentUrl == null) {
                    //第一页
                    progressBar.setVisibility(View.VISIBLE);
                    textNotExist.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);

                    HttpUtil.getMatStorage(new okhttp3.Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseData = response.body().string();
                            LogUtil.d("Get Mat Storage response", responseData);
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
                                            recyclerView.setVisibility(View.GONE);
                                        }
                                    });
                                } else {
                                    if (jsonAll.get("next") != JSONObject.NULL) {
                                        nextUrl = jsonAll.getString("next");
                                    }

                                    mlist.clear(); //清空库存列表
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

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE);
                                            textNotExist.setVisibility(View.GONE);
                                            recyclerView.setVisibility(View.VISIBLE);

                                            if (materialStockAdapter == null) {
                                                materialStockAdapter = new MaterialStockAdapter(mlist);
                                                recyclerView.setAdapter(materialStockAdapter);
                                                materialStockAdapter.setFooterView(pageController);
                                                materialStockAdapter.setOnItemClickListener(new MaterialStockAdapter.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(View view1, int position) {
                                                        if (position != mlist.size()) {
                                                            //传递对象
                                                            lastClick = position;
                                                            MaterialStock materialStock = mlist.get(position);
                                                            Intent intent = new Intent(getActivity(), MatDetailActivity.class);
                                                            intent.putExtra("stock", materialStock);
                                                            startActivityForResult(intent, 1);
                                                        }
                                                    }
                                                });
                                            } else {
                                                materialStockAdapter.notifyDataSetChanged();
                                            }

                                            previousPage.setVisibility(View.GONE);
                                            if (nextUrl.length() != 0) {
                                                nextPage.setVisibility(View.VISIBLE);
                                            } else {
                                                nextPage.setVisibility(View.GONE);
                                            }

                                            pageSize = mlist.size();
                                            int pageNum = getPageNum(allNum, pageSize);
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
                            LogUtil.d("Get Mat Storage", "failed");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    textNotExist.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "网络连接失败，请重新尝试", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    textNotExist.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);

                    HttpUtil.simpleGet(currentUrl, new okhttp3.Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseData = response.body().string();
                            LogUtil.d("Get Mat Storage Page response", responseData);
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
                                            recyclerView.setVisibility(View.VISIBLE);

                                            materialStockAdapter.notifyDataSetChanged();

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
                                    mlist.clear(); //清空库存列表
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


                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE);
                                            textNotExist.setVisibility(View.GONE);
                                            recyclerView.setVisibility(View.VISIBLE);

                                            materialStockAdapter.notifyDataSetChanged();
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

                                            pageCount.setText(getPageCountStr(previousUrl, nextUrl, allNum, pageSize));
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
                            LogUtil.d("Get Mat Storage Page", "failed");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    textNotExist.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "网络连接失败，请重新尝试", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == SearchRecord.RESULT_MODIFY) {
                    MaterialStock materialStock = (MaterialStock) data.getSerializableExtra("stock");
                    mlist.remove(lastClick);
                    mlist.add(lastClick, materialStock);
                    materialStockAdapter.notifyItemChanged(lastClick);
                } else if (resultCode == SearchRecord.RESULT_DELETE) {
                    mlist.remove(lastClick);
                    materialStockAdapter.notifyDataSetChanged();
                }
                break;
            default:
        }
    }

    public static int getPageNum(int allNum, int pageSize) {
        if ((allNum % pageSize) == 0) {
            return allNum / pageSize;
        } else {
            return (allNum / pageSize) + 1;
        }
    }

    public static int getOffsetFromUrl(String nextUrl) {
        int index = nextUrl.indexOf("offset");
        int length = nextUrl.length();
        String numStr = nextUrl.substring(index + 7, length);
        int offset = Integer.parseInt(numStr);
        return offset;
    }

    public static String getPageCountStr(String previousUrl, String nextUrl, int allNum, int pageSize) {
        if (previousUrl.length() == 0 && nextUrl.length() == 0) {
            //第一页，且只有一页
            return "1/1";
        } else if (previousUrl.length() == 0 && nextUrl.length() != 0) {
            //第一页，且不止一页
            int pageNum = getPageNum(allNum, pageSize);
            return ("1/" + String.valueOf(pageNum));
        } else if (previousUrl.length() != 0 && nextUrl.length() == 0) {
            //最后一页，且不止一页
            int pageNum = getPageNum(allNum, pageSize);
            return (String.valueOf(pageNum) + "/" + String.valueOf(pageNum));
        } else {
            //中间页
            int pageNum = getPageNum(allNum, pageSize);
            int offset = getOffsetFromUrl(nextUrl);
            int currentPage = offset / pageSize;
            return (String.valueOf(currentPage) + "/" + String.valueOf(pageNum));
        }
    }
}
