package com.rinc.imsys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by ZhouZhi on 2017/9/3.
 */

public class QRCodeActivity extends BaseActivity {

    ImageView imageView;

    Button button;

    Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        imageView = (ImageView) findViewById(R.id.image_qrcode);
        button = (Button) findViewById(R.id.button_qrcode);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_qrcode);
        toolbar.setTitle(getString(R.string.qrcode_result));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");
        LogUtil.d("QR Code info", info);
        bitmap = encodeAsBitmap(info);
        imageView.setImageBitmap(bitmap);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //检查是否有写入文件的权限
                if (ContextCompat.checkSelfPermission(QRCodeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //未授予权限
                    ActivityCompat.requestPermissions(QRCodeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    LogUtil.d("QR Code", "no permission of write external storage");
                } else {
                    saveImage();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Bitmap encodeAsBitmap(String str) {
        Bitmap bitmap = null;
        BitMatrix result;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            result = multiFormatWriter.encode(new String(str.getBytes("UTF-8"), "ISO-8859-1"), BarcodeFormat.QR_CODE, 800, 800);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private void saveImage() {
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "BlueCatFiles";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            boolean isSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();
            if (isSuccess) {
                Toast.makeText(QRCodeActivity.this, getString(R.string.save_successful), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(QRCodeActivity.this, getString(R.string.save_failed), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveImage();
                } else {
                    Toast.makeText(QRCodeActivity.this, getString(R.string.save_failed_and_need_permission), Toast.LENGTH_LONG).show();
                }
        }
    }
}
