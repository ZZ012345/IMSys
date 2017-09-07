package com.rinc.imsys;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
 * Created by ZhouZhi on 2017/9/7.
 */

public class PartDetailFragment extends Fragment {

    private ProgressBar progressBar;

    private LinearLayout wrapperPartDetail;

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

    private TextView textName;

    private TextView textCompany;

    private TextView textMachineName;

    private TextView textMachineType;

    private TextView textMachineBand;

    private TextView textCondition;

    private TextView textVulnerability;

    private TextView textDescription;

    private Button backButton;

    private Button modifyButton;

    private Button deleteButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_partdetail, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressbar_partdetail);
        wrapperPartDetail = (LinearLayout) view.findViewById(R.id.wrapper_partdetail);
        textId = (TextView) view.findViewById(R.id.text_id_partdetail);
        textType = (TextView) view.findViewById(R.id.text_type_partdetail);
        textNum = (TextView) view.findViewById(R.id.text_num_partdetail);
        textStorestate = (TextView) view.findViewById(R.id.text_storestate_partdetail);
        textMark = (TextView) view.findViewById(R.id.text_mark_partdetail);
        textBand = (TextView) view.findViewById(R.id.text_band_partdetail);
        textOriginal = (TextView) view.findViewById(R.id.text_original_partdetail);
        textYear = (TextView) view.findViewById(R.id.text_year_partdetail);
        textState = (TextView) view.findViewById(R.id.text_state_partdetail);
        textPosition = (TextView) view.findViewById(R.id.text_position_partdetail);
        textUnit = (TextView) view.findViewById(R.id.text_unit_partdetail);
        textName = (TextView) view.findViewById(R.id.text_name_partdetail);
        textCompany = (TextView) view.findViewById(R.id.text_company_partdetail);
        textMachineName = (TextView) view.findViewById(R.id.text_machinename_partdetail);
        textMachineType = (TextView) view.findViewById(R.id.text_machinetype_partdetail);
        textMachineBand = (TextView) view.findViewById(R.id.text_machineband_partdetail);
        textCondition = (TextView) view.findViewById(R.id.text_condition_partdetail);
        textVulnerability = (TextView) view.findViewById(R.id.text_vul_partdetail);
        textDescription = (TextView) view.findViewById(R.id.text_description_partdetail);
        backButton = (Button) view.findViewById(R.id.button_back_partdetail);
        modifyButton = (Button) view.findViewById(R.id.button_modify_partdetail);
        deleteButton = (Button) view.findViewById(R.id.button_delete_partdetail);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("零件详细信息");

        //加载之前已经获取的所有库存品信息，所以不需要再次发送网络请求
        progressBar.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);
        modifyButton.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);

        final PartStock partStock = (PartStock) getArguments().getSerializable("stock");

        textId.setText(String.valueOf(partStock.getId()));
        textType.setText(partStock.getType());
        textNum.setText(partStock.getNum());
        textStorestate.setText(partStock.getStorestate());
        textMark.setText(partStock.getMark());
        textBand.setText(partStock.getBand());
        textOriginal.setText(partStock.getOriginal());
        textYear.setText(partStock.getYear());
        textState.setText(partStock.getState());
        textPosition.setText(partStock.getPosition());
        textUnit.setText(partStock.getUnit());
        textName.setText(partStock.getName());
        textCompany.setText(partStock.getCompany());
        textMachineName.setText(partStock.getMachineName());
        textMachineType.setText(partStock.getMachineType());
        textMachineBand.setText(partStock.getMachineBand());
        textCondition.setText(partStock.getCondition());
        textVulnerability.setText(partStock.getVulnerability());
        textDescription.setText(partStock.getDescription());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                int lastfragment = getArguments().getInt("lastfragment");
                if (lastfragment == 1) {
                    //由所有库存品信息碎片跳转而来
                    replaceFragment(new PartStorageFragment());
                } else if (lastfragment == 2) {
                    //由查找结果碎片跳转而来

                }
            }
        });

        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                //传递对象
                ModifyPartDetailFragment modifyPartDetailFragment = new ModifyPartDetailFragment();
                Bundle args = new Bundle();
                args.putSerializable("stock", partStock);
                args.putInt("lastfragment", getArguments().getInt("lastfragment"));
                modifyPartDetailFragment.setArguments(args);
                replaceFragment(modifyPartDetailFragment);
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
                        HttpUtil.deletePartDetail(partStock.getId(), new okhttp3.Callback() {
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
                                                int lastfragment = getArguments().getInt("lastfragment");
                                                if (lastfragment == 1) {
                                                    //由所有库存品信息碎片跳转而来
                                                    replaceFragment(new PartStorageFragment());
                                                } else if (lastfragment == 2) {
                                                    //由查找结果碎片跳转而来

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
                                LogUtil.d("Delete Part Detail", "failed");
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

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager =  getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_main, fragment);
        transaction.commit();
    }
}
