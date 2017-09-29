package com.rinc.imsys;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by ZhouZhi on 2017/9/27.
 */

public class PartDetailActivity extends BaseActivity {

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

    private Button inButton;

    private Button outButton;

    private PartStock partStock;

    private boolean modifiedOrNot = false;

    private boolean deleteOrNot = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.modify:
                Intent intent = new Intent(PartDetailActivity.this, ModifyPartDetailActivity.class);
                intent.putExtra("stock", partStock);
                startActivityForResult(intent, 1);
                break;
            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(PartDetailActivity.this);
                builder.setTitle("警告");
                builder.setMessage("确定删除该库存品吗？");
                builder.setCancelable(true);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        HttpUtil.deletePartDetail(String.valueOf(partStock.getDatabaseid()), new okhttp3.Callback() {
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(PartDetailActivity.this);
                                        builder.setTitle("提示");
                                        builder.setMessage("删除成功！");
                                        builder.setCancelable(false);
                                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                deleteOrNot = true;
                                                onBackPressed();
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(PartDetailActivity.this, "网络连接失败，请重新尝试", Toast.LENGTH_SHORT).show();
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
                break;
            default:
        }
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partdetail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_partdetail);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        progressBar = (ProgressBar) findViewById(R.id.progressbar_partdetail);
        wrapperPartDetail = (LinearLayout) findViewById(R.id.wrapper_partdetail);
        textId = (TextView) findViewById(R.id.text_id_partdetail);
        textType = (TextView) findViewById(R.id.text_type_partdetail);
        textNum = (TextView) findViewById(R.id.text_num_partdetail);
        textStorestate = (TextView) findViewById(R.id.text_storestate_partdetail);
        textMark = (TextView) findViewById(R.id.text_mark_partdetail);
        textBand = (TextView) findViewById(R.id.text_band_partdetail);
        textOriginal = (TextView) findViewById(R.id.text_original_partdetail);
        textYear = (TextView) findViewById(R.id.text_year_partdetail);
        textState = (TextView) findViewById(R.id.text_state_partdetail);
        textPosition = (TextView) findViewById(R.id.text_position_partdetail);
        textUnit = (TextView) findViewById(R.id.text_unit_partdetail);
        textName = (TextView) findViewById(R.id.text_name_partdetail);
        textCompany = (TextView) findViewById(R.id.text_company_partdetail);
        textMachineName = (TextView) findViewById(R.id.text_machinename_partdetail);
        textMachineType = (TextView) findViewById(R.id.text_machinetype_partdetail);
        textMachineBand = (TextView) findViewById(R.id.text_machineband_partdetail);
        textCondition = (TextView) findViewById(R.id.text_condition_partdetail);
        textVulnerability = (TextView) findViewById(R.id.text_vul_partdetail);
        textDescription = (TextView) findViewById(R.id.text_description_partdetail);
        inButton = (Button) findViewById(R.id.button_in_partdetail);
        outButton = (Button) findViewById(R.id.button_out_partdetail);

        //加载之前已经获取的所有库存品信息，所以不需要再次发送网络请求
        progressBar.setVisibility(View.GONE);
        inButton.setVisibility(View.VISIBLE);
        outButton.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        partStock = (PartStock) intent.getSerializableExtra("stock");

        textId.setText(partStock.getId());
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

        inButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    //修改详细信息成功
                    partStock = (PartStock) data.getSerializableExtra("stock");
                    textId.setText(partStock.getId());
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
                    modifiedOrNot = true;
                } else if (resultCode == SearchRecord.RESULT_DELETE) {
                    //该库存品被删除
                    deleteOrNot = true;
                    onBackPressed();
                }
                break;
            default:
        }
    }

    @Override
    public void onBackPressed() {
        if (deleteOrNot) {
            //该库存品被删除，优先级大于被修改
            Intent intent = new Intent();
            intent.putExtra("stock", partStock);
            setResult(SearchRecord.RESULT_DELETE, intent);
            finish();
        } else if (modifiedOrNot) {
            //该库存品被修改
            Intent intent = new Intent();
            intent.putExtra("stock", partStock);
            setResult(SearchRecord.RESULT_MODIFY, intent);
            finish();
        } else {
            finish();
        }
    }
}
