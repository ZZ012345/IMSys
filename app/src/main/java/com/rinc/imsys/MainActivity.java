package com.rinc.imsys;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends BaseActivity {

    private DrawerLayout drawerLayout;

    private Toolbar toolbar;

    private MenuItem scanItem;

    private NavigationView navView;

    private Menu menu;

    private MenuItem menuMat;

    private MenuItem menuMatsto;

    private MenuItem menuMatin;

    private MenuItem menuMatinrec;

    private MenuItem menuMatout;

    private MenuItem menuMatoutrec;

    private MenuItem menuMatsearch;

    private ImageView imageMat;

    private MenuItem menuPart;

    private MenuItem menuPartsto;

    private MenuItem menuPartin;

    private MenuItem menuPartinrec;

    private MenuItem menuPartout;

    private MenuItem menuPartoutrec;

    private MenuItem menuPartsearch;

    private ImageView imagePart;

    private int lastClickOn = 0; //用于标记上次点击的菜单位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        navView = (NavigationView) findViewById(R.id.nav_view_main);

        toolbar.setTitle("个人信息");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        View header = navView.getHeaderView(0);
        TextView headerUsername = (TextView) header.findViewById(R.id.username_header);
        headerUsername.setText(User.username);

        menu = navView.getMenu();
        menuMat = menu.getItem(1);
        menuMatsto = menu.getItem(2);
        menuMatin = menu.getItem(3);
        menuMatinrec = menu.getItem(4);
        menuMatout = menu.getItem(5);
        menuMatoutrec = menu.getItem(6);
        menuMatsearch = menu.getItem(7);
        View viewMat = menuMat.getActionView();
        imageMat = (ImageView) viewMat.findViewById(R.id.image_menu_mat);

        menuPart = menu.getItem(8);
        menuPartsto = menu.getItem(9);
        menuPartin = menu.getItem(10);
        menuPartinrec = menu.getItem(11);
        menuPartout = menu.getItem(12);
        menuPartoutrec = menu.getItem(13);
        menuPartsearch = menu.getItem(14);
        View viewPart = menuPart.getActionView();
        imagePart = (ImageView) viewPart.findViewById(R.id.image_menu_part);

        //菜单默认收起
        menuMatsto.setVisible(false);
        menuMatin.setVisible(false);
        menuMatinrec.setVisible(false);
        menuMatout.setVisible(false);
        menuMatoutrec.setVisible(false);
        menuMatsearch.setVisible(false);
        imageMat.setImageResource(R.drawable.menu_left);

        menuPartsto.setVisible(false);
        menuPartin.setVisible(false);
        menuPartinrec.setVisible(false);
        menuPartout.setVisible(false);
        menuPartoutrec.setVisible(false);
        menuPartsearch.setVisible(false);
        imagePart.setImageResource(R.drawable.menu_left);

        replaceFragment(new UserinfoFragment()); //加载个人信息碎片

        navView.setCheckedItem(R.id.menu_userinfo);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_userinfo:
                        scanItem.setVisible(false);
                        lastClickOn = 0;
                        replaceFragment(new UserinfoFragment());
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_mat:
                        //根据所有库存品信息菜单是否可见展开或收起菜单
                        setMatMenuVisible(!menuMatsto.isVisible());
                        break;
                    case R.id.menu_matsto:
                        scanItem.setVisible(false);
                        lastClickOn = 1;
                        replaceFragment(new MatStorageFragment());
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_matin:
                        scanItem.setVisible(true);
                        lastClickOn = 2;
                        replaceFragment(new MatInFragment());
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_matin_record:
                        scanItem.setVisible(false);
                        lastClickOn = 3;
                        replaceFragment(new MatInRecFragment());
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_matout:
                        scanItem.setVisible(true);
                        lastClickOn = 4;
                        replaceFragment(new MatOutFragment());
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_matout_record:
                        scanItem.setVisible(false);
                        lastClickOn = 5;
                        replaceFragment(new MatOutRecFragment());
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_matsearch:
                        scanItem.setVisible(false);
                        lastClickOn = 6;
                        replaceFragment(new MatSearchFragment());
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_part:
                        //根据所有库存品信息菜单是否可见展开或收起菜单
                        setPartMenuVisible(!menuPartsto.isVisible());
                        break;
                    case R.id.menu_partsto:
                        scanItem.setVisible(false);
                        lastClickOn = 7;
                        replaceFragment(new PartStorageFragment());
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_partsearch:
                        scanItem.setVisible(false);
                        lastClickOn = 12;
                        replaceFragment(new PartSearchFragment());
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_equisto:
                        scanItem.setVisible(false);
                        //replaceFragment(new EquiStorageFragment());
                        break;
                    case R.id.menu_qrmake:
                        scanItem.setVisible(false);
                        lastClickOn = 19;
                        replaceFragment(new QRMakeFragment());
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_logout:
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                        dialog.setTitle("提示");
                        dialog.setMessage("确定登出吗？");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //登出
                                HttpUtil.logout(new okhttp3.Callback() {
                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        LogUtil.d("Logout", response.body().string());
                                        //清空数据并退回登录界面
                                        User.clear();
                                        HttpUtil.header = null;
                                        SearchRecord.clearRecord();
                                        ActivityCollector.finishAll();
                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        //服务器没有响应，同样清空数据并退回登录界面
                                        User.clear();
                                        HttpUtil.header = null;
                                        SearchRecord.clearRecord();
                                        ActivityCollector.finishAll();
                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }
                        });
                        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        dialog.show();
                        drawerLayout.closeDrawers();
                        break;
                    default:
                }
                return true;
            }
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //当滑动菜单打开时，隐藏虚拟键盘
                hideKeyboard();
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                switch (lastClickOn) {
                    case 0:
                        navView.setCheckedItem(R.id.menu_userinfo);
                        break;
                    case 1:
                        if (!menuMatsto.isVisible()) {
                            setMatMenuVisible(true);
                        }
                        navView.setCheckedItem(R.id.menu_matsto);
                        break;
                    case 2:
                        if (!menuMatsto.isVisible()) {
                            setMatMenuVisible(true);
                        }
                        navView.setCheckedItem(R.id.menu_matin);
                        break;
                    case 3:
                        if (!menuMatsto.isVisible()) {
                            setMatMenuVisible(true);
                        }
                        navView.setCheckedItem(R.id.menu_matin_record);
                        break;
                    case 4:
                        if (!menuMatsto.isVisible()) {
                            setMatMenuVisible(true);
                        }
                        navView.setCheckedItem(R.id.menu_matout);
                        break;
                    case 5:
                        if (!menuMatsto.isVisible()) {
                            setMatMenuVisible(true);
                        }
                        navView.setCheckedItem(R.id.menu_matout_record);
                        break;
                    case 6:
                        if (!menuMatsto.isVisible()) {
                            setMatMenuVisible(true);
                        }
                        navView.setCheckedItem(R.id.menu_matsearch);
                        break;
                    case 7:
                        if (!menuPartsto.isVisible()) {
                            setPartMenuVisible(true);
                        }
                        navView.setCheckedItem(R.id.menu_partsto);
                        break;
                    case 12:
                        if (!menuPartsto.isVisible()) {
                            setPartMenuVisible(true);
                        }
                        navView.setCheckedItem(R.id.menu_partsearch);
                        break;
                    case 19:
                        navView.setCheckedItem(R.id.menu_qrmake);
                        break;
                    default:
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    public void setMatMenuVisible(boolean isVisible) {
        if (isVisible) {
            menuMatsto.setVisible(true);
            menuMatin.setVisible(true);
            menuMatinrec.setVisible(true);
            menuMatout.setVisible(true);
            menuMatoutrec.setVisible(true);
            menuMatsearch.setVisible(true);
            imageMat.setImageResource(R.drawable.menu_down);
        } else {
            menuMatsto.setVisible(false);
            menuMatin.setVisible(false);
            menuMatinrec.setVisible(false);
            menuMatout.setVisible(false);
            menuMatoutrec.setVisible(false);
            menuMatsearch.setVisible(false);
            imageMat.setImageResource(R.drawable.menu_left);
        }
    }

    public void setPartMenuVisible(boolean isVisible) {
        if (isVisible) {
            menuPartsto.setVisible(true);
            menuPartin.setVisible(true);
            menuPartinrec.setVisible(true);
            menuPartout.setVisible(true);
            menuPartoutrec.setVisible(true);
            menuPartsearch.setVisible(true);
            imagePart.setImageResource(R.drawable.menu_down);
        } else {
            menuPartsto.setVisible(false);
            menuPartin.setVisible(false);
            menuPartinrec.setVisible(false);
            menuPartout.setVisible(false);
            menuPartoutrec.setVisible(false);
            menuPartsearch.setVisible(false);
            imagePart.setImageResource(R.drawable.menu_left);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        scanItem = menu.findItem(R.id.scan);
        scanItem.setVisible(false); //默认隐藏扫描二维码按钮
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.scan:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //未授予相机权限
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
                    LogUtil.d("QR scan", "no permission of camera");
                } else {
                    Intent intent = new Intent(this, SimpleScannerActivity.class);
                    startActivityForResult(intent, 1);
                }
                break;
            default:
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    //从二维码扫描界面返回
                    String returnData = data.getStringExtra("data_return");
                    LogUtil.d("QR scan return", returnData);
                    Bundle args = new Bundle();
                    args.putString("info", returnData);

                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_main);
                    if (fragment instanceof MatInFragment) {
                        MatInFragment matInFragment = new MatInFragment();
                        matInFragment.setArguments(args);
                        replaceFragment(matInFragment);
                    } else if (fragment instanceof MatOutFragment) {
                        MatOutFragment matOutFragment = new MatOutFragment();
                        matOutFragment.setArguments(args);
                        replaceFragment(matOutFragment);
                    }
                }
                break;
            default:
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        drawerLayout.closeDrawers();
    }

    private void hideKeyboard() { //隐藏虚拟键盘
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_main, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(this, SimpleScannerActivity.class);
                    startActivityForResult(intent, 1);
                } else {
                    Toast.makeText(this, "请授予相机权限以使用二维码扫描", Toast.LENGTH_SHORT).show();
                }
        }
    }
}
