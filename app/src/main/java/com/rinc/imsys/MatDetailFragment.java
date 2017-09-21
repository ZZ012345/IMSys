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
 * Created by zhouzhi on 2017/8/18.
 */

public class MatDetailFragment extends BaseFragment {

    private ProgressBar progressBar;

    private LinearLayout wrapperMatDetail;

    private TextView textId;

    private TextView textType;

    private TextView textNum;

    private TextView textStorestate;

    private TextView textMark;

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
        View view = inflater.inflate(R.layout.fragment_matdetail, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_matdetail);
        wrapperMatDetail = (LinearLayout) view.findViewById(R.id.wrapper_matdetail);
        textId = (TextView) view.findViewById(R.id.text_id_matdetail);
        textType = (TextView) view.findViewById(R.id.text_type_matdetail);
        textNum = (TextView) view.findViewById(R.id.text_num_matdetail);
        textStorestate = (TextView) view.findViewById(R.id.text_storestate_matdetail);
        textMark = (TextView) view.findViewById(R.id.text_mark_matdetail);
        textBand = (TextView) view.findViewById(R.id.text_band_matdetail);
        textOriginal = (TextView) view.findViewById(R.id.text_original_matdetail);
        textYear = (TextView) view.findViewById(R.id.text_year_matdetail);
        textState = (TextView) view.findViewById(R.id.text_state_matdetail);
        textPosition = (TextView) view.findViewById(R.id.text_position_matdetail);
        textUnit = (TextView) view.findViewById(R.id.text_unit_matdetail);
        textDescription = (TextView) view.findViewById(R.id.text_description_matdetail);
        backButton = (Button) view.findViewById(R.id.button_back_matdetail);
        modifyButton = (Button) view.findViewById(R.id.button_modify_matdetail);
        deleteButton = (Button) view.findViewById(R.id.button_delete_matdetail);
        inButton = (Button) view.findViewById(R.id.button_in_matdetail);
        outButton = (Button) view.findViewById(R.id.button_out_matdetail);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("材料详细信息");

        //加载之前已经获取的所有库存品信息，所以不需要再次发送网络请求
        progressBar.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);
        modifyButton.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);
        inButton.setVisibility(View.VISIBLE);
        outButton.setVisibility(View.VISIBLE);

        final MaterialStock materialStock = (MaterialStock) getArguments().getSerializable("stock");

        textId.setText(String.valueOf(materialStock.getId()));
        textType.setText(materialStock.getType());
        textNum.setText(materialStock.getNum());
        textStorestate.setText(materialStock.getStorestate());
        textMark.setText(materialStock.getMark());
        textBand.setText(materialStock.getBand());
        textOriginal.setText(materialStock.getOriginal());
        textYear.setText(materialStock.getYear());
        textState.setText(materialStock.getState());
        textPosition.setText(materialStock.getPosition());
        textUnit.setText(materialStock.getUnit());
        textDescription.setText(materialStock.getDescription());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                if (SearchRecord.lastFrag == SearchRecord.FRAGLABEL_STORAGE) {
                    //由所有库存品信息碎片跳转而来
                    replaceFragment(new MatStorageFragment());
                } else if (SearchRecord.lastFrag == SearchRecord.FRAGLABEL_SEARCH) {
                    //由查找结果碎片跳转而来
                    replaceFragment(new MatSearchResultFragment());
                }
            }
        });

        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                //传递对象
                ModifyMatDetailFragment modifyMatDetailFragment = new ModifyMatDetailFragment();
                Bundle args = new Bundle();
                args.putSerializable("stock", materialStock);
                modifyMatDetailFragment.setArguments(args);
                replaceFragment(modifyMatDetailFragment);
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
                        HttpUtil.deleteMatDetail(String.valueOf(materialStock.getDatabaseid()), new okhttp3.Callback() {
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
                                                    replaceFragment(new MatStorageFragment());
                                                } else if (SearchRecord.lastFrag == SearchRecord.FRAGLABEL_SEARCH) {
                                                    //由查找结果碎片跳转而来
                                                    replaceFragment(new MatSearchResultFragment());
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
                                LogUtil.d("Delete Mat Detail", "failed");
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
                MatInRecFragment matInRecFragment = new MatInRecFragment();
                Bundle args = new Bundle();
                args.putSerializable("stock", materialStock);
                matInRecFragment.setArguments(args);
                replaceFragment(matInRecFragment);
            }
        });

        outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                //传递对象
                MatOutRecFragment matOutRecFragment = new MatOutRecFragment();
                Bundle args = new Bundle();
                args.putSerializable("stock", materialStock);
                matOutRecFragment.setArguments(args);
                replaceFragment(matOutRecFragment);
            }
        });

        return view;
    }
}
