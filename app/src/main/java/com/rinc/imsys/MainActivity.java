package com.rinc.imsys;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends BaseActivity {

    private DrawerLayout drawerLayout;

    private Toolbar toolbar;

    private NavigationView navView;

    private int lastClickOn = 0; //用于标记登出前滑动菜单栏点击的位置

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

        replaceFragment(new UserinfoFragment()); //加载个人信息碎片

        navView.setCheckedItem(R.id.menu_userinfo);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_userinfo:
                        replaceFragment(new UserinfoFragment());
                        lastClickOn = 0;
                        break;
                    case R.id.menu_matsto:
                        replaceFragment(new MatStorageFragment());
                        lastClickOn = 1;
                        break;
                    case R.id.menu_matin:
                        replaceFragment(new MatInFragment());
                        lastClickOn = 2;
                        break;
                    case R.id.menu_matin_record:
                        replaceFragment(new MatInRecFragment());
                        lastClickOn = 3;
                        break;
                    case R.id.menu_matout:
                        replaceFragment(new MatOutFragment());
                        lastClickOn = 4;
                        break;
                    case R.id.menu_matout_record:
                        replaceFragment(new MatOutRecFragment());
                        lastClickOn = 5;
                        break;
                    case R.id.menu_matsearch:
                        replaceFragment(new MatSearchFragment());
                        lastClickOn = 6;
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
                                        ActivityCollector.finishAll();
                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        //服务器没有响应，同样清空数据并退回登录界面
                                        User.clear();
                                        HttpUtil.header = null;
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
                                switch (lastClickOn) {
                                    case 0:
                                        navView.setCheckedItem(R.id.menu_userinfo);
                                        break;
                                    case 1:
                                        navView.setCheckedItem(R.id.menu_matsto);
                                        break;
                                    case 2:
                                        navView.setCheckedItem(R.id.menu_matin);
                                        break;
                                    case 3:
                                        navView.setCheckedItem(R.id.menu_matin_record);
                                        break;
                                    case 4:
                                        navView.setCheckedItem(R.id.menu_matout);
                                        break;
                                    case 5:
                                        navView.setCheckedItem(R.id.menu_matout_record);
                                        break;
                                    case 6:
                                        navView.setCheckedItem(R.id.menu_matsearch);
                                        break;
                                    default:
                                }
                            }
                        });
                        dialog.show();
                        break;
                    default:
                }
                drawerLayout.closeDrawers();
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

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
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
        transaction.commit();
    }
}
