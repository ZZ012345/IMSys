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

/**
 * Created by ZhouZhi on 2017/9/1.
 */

public class EquipStorageFragment extends BaseFragment {

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

    private List<EquipStock> mlist = new ArrayList<>();

    private int allNum;

    private int pageSize;

    private int lastClick = 0;

    private EquipStockAdapter equipStockAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_equipstorage, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_equipstorage);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_equipstorage);
        textNotExist = (TextView) view.findViewById(R.id.text_notexist_equipstorage);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_equipstorage);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        pageController = LayoutInflater.from(getActivity()).inflate(R.layout.page_item, recyclerView, false);
        previousPage = (LinearLayout) pageController.findViewById(R.id.previous_link);
        nextPage = (LinearLayout) pageController.findViewById(R.id.next_link);
        pageCount = (TextView) pageController.findViewById(R.id.page_count);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle(getString(R.string.machine_storage_info));

        progressBar.setVisibility(View.VISIBLE);
        textNotExist.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        HttpUtil.getEquipStorage(new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                LogUtil.d("Get Equip Storage response", responseData);
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

                        getActivity().runOnUiThread(new Runnable() {
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
                                    public void onItemClick(View view1, int position) {
                                        if (position != mlist.size()) {
                                            //传递对象
                                            lastClick = position;
                                            EquipStock equipStock = mlist.get(position);
                                            Intent intent = new Intent(getActivity(), EquipDetailActivity.class);
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
                LogUtil.d("Get Equip Storage", "failed");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        textNotExist.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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
                        LogUtil.d("Get Equip Storage Page response", responseData);
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
                                mlist.clear(); //清空库存列表
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

                                getActivity().runOnUiThread(new Runnable() {
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
                        LogUtil.d("Get Equip Storage Page", "failed");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                textNotExist.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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

                    HttpUtil.getEquipStorage(new okhttp3.Callback() {
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseData = response.body().string();
                            LogUtil.d("Get Equip Storage response", responseData);
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

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE);
                                            textNotExist.setVisibility(View.GONE);
                                            recyclerView.setVisibility(View.VISIBLE);

                                            if (equipStockAdapter == null) {
                                                equipStockAdapter = new EquipStockAdapter(mlist);
                                                recyclerView.setAdapter(equipStockAdapter);
                                                equipStockAdapter.setFooterView(pageController);
                                                equipStockAdapter.setOnItemClickListener(new EquipStockAdapter.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(View view1, int position) {
                                                        if (position != mlist.size()) {
                                                            //传递对象
                                                            lastClick = position;
                                                            EquipStock equipStock = mlist.get(position);
                                                            Intent intent = new Intent(getActivity(), EquipDetailActivity.class);
                                                            intent.putExtra("stock", equipStock);
                                                            startActivityForResult(intent, 1);
                                                        }
                                                    }
                                                });
                                            } else {
                                                equipStockAdapter.notifyDataSetChanged();
                                            }

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
                            LogUtil.d("Get Equip Storage", "failed");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    textNotExist.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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
                            LogUtil.d("Get Equip Storage Page response", responseData);
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
                                    mlist.clear(); //清空库存列表
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

                                    getActivity().runOnUiThread(new Runnable() {
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
                            LogUtil.d("Get Equip Storage Page", "failed");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    textNotExist.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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
