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
 * Created by ZhouZhi on 2017/9/25.
 */

public class MatDetailActivity extends BaseActivity {

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

    private Button inButton;

    private Button outButton;

    private MaterialStock materialStock;

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
                Intent intent = new Intent(MatDetailActivity.this, ModifyMatDetailActivity.class);
                intent.putExtra("stock", materialStock);
                startActivityForResult(intent, 1);
                break;
            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(MatDetailActivity.this);
                builder.setTitle("警告");
                builder.setMessage("确定删除该库存品吗？");
                builder.setCancelable(true);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        HttpUtil.deleteMatDetail(String.valueOf(materialStock.getDatabaseid()), new okhttp3.Callback() {
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(MatDetailActivity.this);
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
                                LogUtil.d("Delete Mat Detail", "failed");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MatDetailActivity.this, "网络连接失败，请重新尝试", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_matdetail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_matdetail);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        progressBar = (ProgressBar) findViewById(R.id.progressbar_matdetail);
        wrapperMatDetail = (LinearLayout) findViewById(R.id.wrapper_matdetail);
        textId = (TextView) findViewById(R.id.text_id_matdetail);
        textType = (TextView) findViewById(R.id.text_type_matdetail);
        textNum = (TextView) findViewById(R.id.text_num_matdetail);
        textStorestate = (TextView) findViewById(R.id.text_storestate_matdetail);
        textMark = (TextView) findViewById(R.id.text_mark_matdetail);
        textBand = (TextView) findViewById(R.id.text_band_matdetail);
        textOriginal = (TextView) findViewById(R.id.text_original_matdetail);
        textYear = (TextView) findViewById(R.id.text_year_matdetail);
        textState = (TextView) findViewById(R.id.text_state_matdetail);
        textPosition = (TextView) findViewById(R.id.text_position_matdetail);
        textUnit = (TextView) findViewById(R.id.text_unit_matdetail);
        textDescription = (TextView) findViewById(R.id.text_description_matdetail);
        inButton = (Button) findViewById(R.id.button_in_matdetail);
        outButton = (Button) findViewById(R.id.button_out_matdetail);

        //加载之前已经获取的所有库存品信息，所以不需要再次发送网络请求
        progressBar.setVisibility(View.GONE);
        inButton.setVisibility(View.VISIBLE);Intent intent = getIntent();
        outButton.setVisibility(View.VISIBLE);

        materialStock = (MaterialStock) intent.getSerializableExtra("stock");

        textId.setText(materialStock.getId());
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

        inButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MatDetailActivity.this, MatInRecActivity.class);
                intent.putExtra("stock", materialStock);
                startActivity(intent);
            }
        });

        outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MatDetailActivity.this, MatOutRecActivity.class);
                intent.putExtra("stock", materialStock);
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
                    materialStock = (MaterialStock) data.getSerializableExtra("stock");
                    textId.setText(materialStock.getId());
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
            intent.putExtra("stock", materialStock);
            setResult(SearchRecord.RESULT_DELETE, intent);
            finish();
        } else if (modifiedOrNot) {
            //该库存品被修改
            Intent intent = new Intent();
            intent.putExtra("stock", materialStock);
            setResult(SearchRecord.RESULT_MODIFY, intent);
            finish();
        } else {
            finish();
        }
    }
}
