package com.rinc.imsys;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ZhouZhi on 2017/9/22.
 */

public class EquipDetailFragment extends BaseFragment {

    private ProgressBar progressBar;

    private LinearLayout wrapperMatDetail;

    private TextView textId;

    private TextView textType;

    private TextView textNum;

    private TextView textStorestate;

    private TextView textMark;
    
    private TextView textHour;

    private TextView textBand;

    private TextView textOriginal;

    private TextView textYear;

    private TextView textState;

    private TextView textPosition;

    private TextView textUnit;

    private TextView textDescription;

    private Button backButton;

    private Button modifyButton;

    private Button deleteButton;

    private Button inButton;

    private Button outButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_equipdetail, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_equipdetail);
        wrapperMatDetail = (LinearLayout) view.findViewById(R.id.wrapper_equipdetail);
        textId = (TextView) view.findViewById(R.id.text_id_equipdetail);
        textType = (TextView) view.findViewById(R.id.text_type_equipdetail);
        textNum = (TextView) view.findViewById(R.id.text_num_equipdetail);
        textStorestate = (TextView) view.findViewById(R.id.text_storestate_equipdetail);
        textMark = (TextView) view.findViewById(R.id.text_mark_equipdetail);
        textHour = (TextView) view.findViewById(R.id.text_hour_equipdetail);
        textBand = (TextView) view.findViewById(R.id.text_band_equipdetail);
        textOriginal = (TextView) view.findViewById(R.id.text_original_equipdetail);
        textYear = (TextView) view.findViewById(R.id.text_year_equipdetail);
        textState = (TextView) view.findViewById(R.id.text_state_equipdetail);
        textPosition = (TextView) view.findViewById(R.id.text_position_equipdetail);
        textUnit = (TextView) view.findViewById(R.id.text_unit_equipdetail);
        textDescription = (TextView) view.findViewById(R.id.text_description_equipdetail);
        backButton = (Button) view.findViewById(R.id.button_back_equipdetail);
        modifyButton = (Button) view.findViewById(R.id.button_modify_equipdetail);
        deleteButton = (Button) view.findViewById(R.id.button_delete_equipdetail);
        inButton = (Button) view.findViewById(R.id.button_in_equipdetail);
        outButton = (Button) view.findViewById(R.id.button_out_equipdetail);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("整机详细信息");

        //加载之前已经获取的所有库存品信息，所以不需要再次发送网络请求
        progressBar.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);
        modifyButton.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);
        inButton.setVisibility(View.VISIBLE);
        outButton.setVisibility(View.VISIBLE);

        final EquipStock equipStock = (EquipStock) getArguments().getSerializable("stock");

        textId.setText(equipStock.getId());
        textType.setText(equipStock.getType());
        textNum.setText(equipStock.getNum());
        textStorestate.setText(equipStock.getStorestate());
        textMark.setText(equipStock.getMark());
        textHour.setText(equipStock.getHour());
        textBand.setText(equipStock.getBand());
        textOriginal.setText(equipStock.getOriginal());
        textYear.setText(equipStock.getYear());
        textState.setText(equipStock.getState());
        textPosition.setText(equipStock.getPosition());
        textUnit.setText(equipStock.getUnit());
        textDescription.setText(equipStock.getDescription());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                if (SearchRecord.lastFrag == SearchRecord.FRAGLABEL_STORAGE) {
                    //由所有库存品信息碎片跳转而来
                    replaceFragment(new EquipStorageFragment());
                } else if (SearchRecord.lastFrag == SearchRecord.FRAGLABEL_SEARCH) {
                    //由查找结果碎片跳转而来
                    replaceFragment(new EquipSearchResultFragment());
                }
            }
        });

        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                //传递对象
                ModifyEquipDetailFragment modifyEquipDetailFragment = new ModifyEquipDetailFragment();
                Bundle args = new Bundle();
                args.putSerializable("stock", equipStock);
                modifyEquipDetailFragment.setArguments(args);
                replaceFragment(modifyEquipDetailFragment);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("警告");
                builder.setMessage("确定删除该库存品吗？");
                builder.setCancelable(true);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        HttpUtil.deleteEquipDetail(String.valueOf(equipStock.getDatabaseid()), new okhttp3.Callback() {
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setTitle("提示");
                                        builder.setMessage("删除成功！");
                                        builder.setCancelable(false);
                                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if (SearchRecord.lastFrag == SearchRecord.FRAGLABEL_STORAGE) {
                                                    //由所有库存品信息碎片跳转而来
                                                    replaceFragment(new EquipStorageFragment());
                                                } else if (SearchRecord.lastFrag == SearchRecord.FRAGLABEL_SEARCH) {
                                                    //由查找结果碎片跳转而来
                                                    replaceFragment(new EquipSearchResultFragment());
                                                }
                                            }
                                        });
                                        builder.show();
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                                LogUtil.d("Delete Equip Detail", "failed");
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "网络连接失败，请重新尝试", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        });

        inButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                //传递对象
                EquipInRecFragment equipInRecFragment = new EquipInRecFragment();
                Bundle args = new Bundle();
                args.putSerializable("stock", equipStock);
                equipInRecFragment.setArguments(args);
                replaceFragment(equipInRecFragment);
            }
        });

        outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                //传递对象
                EquipOutRecFragment equipOutRecFragment = new EquipOutRecFragment();
                Bundle args = new Bundle();
                args.putSerializable("stock", equipStock);
                equipOutRecFragment.setArguments(args);
                replaceFragment(equipOutRecFragment);
            }
        });

        return view;
    }
}
