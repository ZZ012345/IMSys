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
 * Created by ZhouZhi on 2017/9/29.
 */

public class EquipDetailActivity extends BaseActivity {

    private ProgressBar progressBar;

    private LinearLayout wrapperEquipDetail;

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

    private Button inButton;

    private Button outButton;

    private EquipStock equipStock;

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
                Intent intent = new Intent(EquipDetailActivity.this, ModifyEquipDetailActivity.class);
                intent.putExtra("stock", equipStock);
                startActivityForResult(intent, 1);
                break;
            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(EquipDetailActivity.this);
                builder.setTitle("警告");
                builder.setMessage("确定删除该库存品吗？");
                builder.setCancelable(true);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        HttpUtil.deleteEquipDetail(String.valueOf(equipStock.getDatabaseid()), new okhttp3.Callback() {
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(EquipDetailActivity.this);
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
                                LogUtil.d("Delete Equip Detail", "failed");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(EquipDetailActivity.this, "网络连接失败，请重新尝试", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_equipdetail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_equipdetail);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        progressBar = (ProgressBar) findViewById(R.id.progressbar_equipdetail);
        wrapperEquipDetail = (LinearLayout) findViewById(R.id.wrapper_equipdetail);
        textId = (TextView) findViewById(R.id.text_id_equipdetail);
        textType = (TextView) findViewById(R.id.text_type_equipdetail);
        textNum = (TextView) findViewById(R.id.text_num_equipdetail);
        textStorestate = (TextView) findViewById(R.id.text_storestate_equipdetail);
        textMark = (TextView) findViewById(R.id.text_mark_equipdetail);
        textHour = (TextView) findViewById(R.id.text_hour_equipdetail);
        textBand = (TextView) findViewById(R.id.text_band_equipdetail);
        textOriginal = (TextView) findViewById(R.id.text_original_equipdetail);
        textYear = (TextView) findViewById(R.id.text_year_equipdetail);
        textState = (TextView) findViewById(R.id.text_state_equipdetail);
        textPosition = (TextView) findViewById(R.id.text_position_equipdetail);
        textUnit = (TextView) findViewById(R.id.text_unit_equipdetail);
        textDescription = (TextView) findViewById(R.id.text_description_equipdetail);
        inButton = (Button) findViewById(R.id.button_in_equipdetail);
        outButton = (Button) findViewById(R.id.button_out_equipdetail);

        //加载之前已经获取的所有库存品信息，所以不需要再次发送网络请求
        progressBar.setVisibility(View.GONE);
        inButton.setVisibility(View.VISIBLE);
        outButton.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        equipStock = (EquipStock) intent.getSerializableExtra("stock");

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

        inButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EquipDetailActivity.this, EquipInRecActivity.class);
                intent.putExtra("stock", equipStock);
                startActivity(intent);
            }
        });

        outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EquipDetailActivity.this, EquipOutRecActivity.class);
                intent.putExtra("stock", equipStock);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    //修改详细信息成功
                    equipStock = (EquipStock) data.getSerializableExtra("stock");
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
            intent.putExtra("stock", equipStock);
            setResult(SearchRecord.RESULT_DELETE, intent);
            finish();
        } else if (modifiedOrNot) {
            //该库存品被修改
            Intent intent = new Intent();
            intent.putExtra("stock", equipStock);
            setResult(SearchRecord.RESULT_MODIFY, intent);
            finish();
        } else {
            finish();
        }
    }
}
