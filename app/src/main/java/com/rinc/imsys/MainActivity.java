package com.rinc.imsys;

import android.Manifest;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

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

    private MenuItem menuMatout;

    private MenuItem menuMatsearch;

    private ImageView imageMat;

    private MenuItem menuPart;

    private MenuItem menuPartsto;

    private MenuItem menuPartin;

    private MenuItem menuPartout;

    private MenuItem menuPartsearch;

    private ImageView imagePart;

    private MenuItem menuEquip;

    private MenuItem menuEquipsto;

    private MenuItem menuEquipin;

    private MenuItem menuEquipout;

    private MenuItem menuEquipsearch;

    private ImageView imageEquip;

    private int lastClickOn = 0; //用于标记上次点击的菜单位置

    private Fragment currentFrag;

    private UserinfoFragment userinfoFragment = new UserinfoFragment();

    private PartStorageFragment partStorageFragment = new PartStorageFragment();

    private PartInFragment partInFragment = new PartInFragment();

    private PartOutFragment partOutFragment = new PartOutFragment();

    private PartSearchFragment partSearchFragment = new PartSearchFragment();

    private EquipStorageFragment equipStorageFragment = new EquipStorageFragment();

    private EquipInFragment equipInFragment = new EquipInFragment();

    private EquipOutFragment equipOutFragment = new EquipOutFragment();

    private EquipSearchFragment equipSearchFragment = new EquipSearchFragment();

    private MatStorageFragment matStorageFragment = new MatStorageFragment();

    private MatInFragment matInFragment = new MatInFragment();

    private MatOutFragment matOutFragment = new MatOutFragment();

    private MatSearchFragment matSearchFragment = new MatSearchFragment();

    private QRMakeFragment qrMakeFragment = new QRMakeFragment();

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

        menuMat = menu.getItem(11);
        menuMatsto = menu.getItem(12);
        menuMatin = menu.getItem(13);
        menuMatout = menu.getItem(14);
        menuMatsearch = menu.getItem(15);
        View viewMat = menuMat.getActionView();
        imageMat = (ImageView) viewMat.findViewById(R.id.image_menu_mat);

        menuPart = menu.getItem(1);
        menuPartsto = menu.getItem(2);
        menuPartin = menu.getItem(3);
        menuPartout = menu.getItem(4);
        menuPartsearch = menu.getItem(5);
        View viewPart = menuPart.getActionView();
        imagePart = (ImageView) viewPart.findViewById(R.id.image_menu_part);

        menuEquip = menu.getItem(6);
        menuEquipsto = menu.getItem(7);
        menuEquipin = menu.getItem(8);
        menuEquipout = menu.getItem(9);
        menuEquipsearch = menu.getItem(10);
        View viewEquip = menuEquip.getActionView();
        imageEquip = (ImageView) viewEquip.findViewById(R.id.image_menu_equip);

        //菜单默认收起
        setMatMenuVisible(false);
        setPartMenuVisible(false);
        setEquipMenuVisible(false);

        replaceFragmentWithoutRefresh(userinfoFragment); //加载个人信息碎片

        navView.setCheckedItem(R.id.menu_userinfo);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_userinfo:
                        scanItem.setVisible(false);
                        lastClickOn = 0;
                        toolbar.setTitle("个人信息");
                        replaceFragmentWithoutRefresh(userinfoFragment);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_mat:
                        //根据库存品信息菜单是否可见展开或收起菜单
                        setMatMenuVisible(!menuMatsto.isVisible());
                        break;
                    case R.id.menu_matsto:
                        scanItem.setVisible(false);
                        lastClickOn = 1;
                        toolbar.setTitle("材料库库存品信息");
                        replaceFragmentWithoutRefresh(matStorageFragment);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_matin:
                        scanItem.setVisible(true);
                        lastClickOn = 2;
                        toolbar.setTitle("材料入库");
                        replaceFragmentWithoutRefresh(matInFragment);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_matout:
                        scanItem.setVisible(true);
                        lastClickOn = 3;
                        toolbar.setTitle("材料出库");
                        replaceFragmentWithoutRefresh(matOutFragment);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_matsearch:
                        scanItem.setVisible(false);
                        lastClickOn = 4;
                        toolbar.setTitle("材料查找");
                        replaceFragmentWithoutRefresh(matSearchFragment);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_part:
                        //根据库存品信息菜单是否可见展开或收起菜单
                        setPartMenuVisible(!menuPartsto.isVisible());
                        break;
                    case R.id.menu_partsto:
                        scanItem.setVisible(false);
                        lastClickOn = 5;
                        toolbar.setTitle("备件库库存品信息");
                        replaceFragmentWithoutRefresh(partStorageFragment);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_partin:
                        scanItem.setVisible(true);
                        lastClickOn = 6;
                        toolbar.setTitle("备件入库");
                        replaceFragmentWithoutRefresh(partInFragment);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_partout:
                        scanItem.setVisible(true);
                        lastClickOn = 7;
                        toolbar.setTitle("备件出库");
                        replaceFragmentWithoutRefresh(partOutFragment);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_partsearch:
                        scanItem.setVisible(false);
                        lastClickOn = 8;
                        toolbar.setTitle("备件查找");
                        replaceFragmentWithoutRefresh(partSearchFragment);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_equip:
                        //根据库存品信息菜单是否可见展开或收起菜单
                        setEquipMenuVisible(!menuEquipsto.isVisible());
                        break;
                    case R.id.menu_equipsto:
                        scanItem.setVisible(false);
                        lastClickOn = 9;
                        toolbar.setTitle("整机库库存品信息");
                        replaceFragmentWithoutRefresh(equipStorageFragment);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_equipin:
                        scanItem.setVisible(true);
                        lastClickOn = 10;
                        toolbar.setTitle("整机入库");
                        replaceFragmentWithoutRefresh(equipInFragment);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_equipout:
                        scanItem.setVisible(true);
                        lastClickOn = 11;
                        toolbar.setTitle("整机出库");
                        replaceFragmentWithoutRefresh(equipOutFragment);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_equipsearch:
                        scanItem.setVisible(false);
                        lastClickOn = 12;
                        toolbar.setTitle("整机查找");
                        replaceFragmentWithoutRefresh(equipSearchFragment);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.menu_qrmake:
                        scanItem.setVisible(false);
                        lastClickOn = 13;
                        toolbar.setTitle("生成二维码");
                        replaceFragmentWithoutRefresh(qrMakeFragment);
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
                        navView.setCheckedItem(R.id.menu_matout);
                        break;
                    case 4:
                        if (!menuMatsto.isVisible()) {
                            setMatMenuVisible(true);
                        }
                        navView.setCheckedItem(R.id.menu_matsearch);
                        break;
                    case 5:
                        if (!menuPartsto.isVisible()) {
                            setPartMenuVisible(true);
                        }
                        navView.setCheckedItem(R.id.menu_partsto);
                        break;
                    case 6:
                        if (!menuPartsto.isVisible()) {
                            setPartMenuVisible(true);
                        }
                        navView.setCheckedItem(R.id.menu_partin);
                        break;
                    case 7:
                        if (!menuPartsto.isVisible()) {
                            setPartMenuVisible(true);
                        }
                        navView.setCheckedItem(R.id.menu_partout);
                        break;
                    case 8:
                        if (!menuPartsto.isVisible()) {
                            setPartMenuVisible(true);
                        }
                        navView.setCheckedItem(R.id.menu_partsearch);
                        break;
                    case 9:
                        if (!menuEquipsto.isVisible()) {
                            setEquipMenuVisible(true);
                        }
                        navView.setCheckedItem(R.id.menu_equipsto);
                        break;
                    case 10:
                        if (!menuEquipsto.isVisible()) {
                            setEquipMenuVisible(true);
                        }
                        navView.setCheckedItem(R.id.menu_equipin);
                        break;
                    case 11:
                        if (!menuEquipsto.isVisible()) {
                            setEquipMenuVisible(true);
                        }
                        navView.setCheckedItem(R.id.menu_equipout);
                        break;
                    case 12:
                        if (!menuEquipsto.isVisible()) {
                            setEquipMenuVisible(true);
                        }
                        navView.setCheckedItem(R.id.menu_equipsearch);
                        break;
                    case 13:
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
            menuMatout.setVisible(true);
            menuMatsearch.setVisible(true);
            imageMat.setImageResource(R.drawable.menu_down);
        } else {
            menuMatsto.setVisible(false);
            menuMatin.setVisible(false);
            menuMatout.setVisible(false);
            menuMatsearch.setVisible(false);
            imageMat.setImageResource(R.drawable.menu_left);
        }
    }

    public void setPartMenuVisible(boolean isVisible) {
        if (isVisible) {
            menuPartsto.setVisible(true);
            menuPartin.setVisible(true);
            menuPartout.setVisible(true);
            menuPartsearch.setVisible(true);
            imagePart.setImageResource(R.drawable.menu_down);
        } else {
            menuPartsto.setVisible(false);
            menuPartin.setVisible(false);
            menuPartout.setVisible(false);
            menuPartsearch.setVisible(false);
            imagePart.setImageResource(R.drawable.menu_left);
        }
    }

    public void setEquipMenuVisible(boolean isVisible) {
        if (isVisible) {
            menuEquipsto.setVisible(true);
            menuEquipin.setVisible(true);
            menuEquipout.setVisible(true);
            menuEquipsearch.setVisible(true);
            imageEquip.setImageResource(R.drawable.menu_down);
        } else {
            menuEquipsto.setVisible(false);
            menuEquipin.setVisible(false);
            menuEquipout.setVisible(false);
            menuEquipsearch.setVisible(false);
            imageEquip.setImageResource(R.drawable.menu_left);
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
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    //从二维码扫描界面返回
                    String returnData = data.getStringExtra("data_return");
                    LogUtil.d("QR scan return", returnData);
                    Bundle args = new Bundle();
                    args.putString("info", returnData);

                    Fragment fragment = getCurrentFragment();
                    if (fragment == null) {
                        LogUtil.d("QR current fragment", "null");
                    } else {
                        LogUtil.d("QR current fragment", fragment.getClass().getSimpleName());
                    }
                    if (fragment instanceof MatInFragment) {
                        //材料入库
                        EditText textId = (EditText) findViewById(R.id.input_id_matin);
                        EditText textType = (EditText) findViewById(R.id.input_type_matin);
                        EditText textMark = (EditText) findViewById(R.id.input_mark_matin);
                        EditText textBand = (EditText) findViewById(R.id.input_band_matin);
                        EditText textOriginal = (EditText) findViewById(R.id.input_original_matin);
                        EditText textYear = (EditText) findViewById(R.id.input_year_matin);
                        EditText textState = (EditText) findViewById(R.id.input_state_matin);
                        EditText textPosition = (EditText) findViewById(R.id.input_position_matin);
                        EditText textUnit = (EditText) findViewById(R.id.input_unit_matin);
                        EditText textDescription = (EditText) findViewById(R.id.input_description_matin);
                        try {
                            JSONObject jsonObject = new JSONObject(returnData);
                            String stockType = jsonObject.getString("stockType");
                            String idScan = jsonObject.getString("id");
                            String typeScan = jsonObject.getString("type");
                            String markScan = jsonObject.getString("mark");
                            String bandScan = jsonObject.getString("band");
                            String originalScan = jsonObject.getString("original");
                            String yearScan = jsonObject.getString("year");
                            String stateScan = jsonObject.getString("state");
                            String positionScan = jsonObject.getString("position");
                            String unitScan = jsonObject.getString("unit");
                            String descriptionScan = jsonObject.getString("description");

                            if (stockType.equals("material")) {
                                textId.setText(idScan);
                                textType.setText(typeScan);
                                textMark.setText(markScan);
                                textBand.setText(bandScan);
                                textOriginal.setText(originalScan);
                                textYear.setText(yearScan);
                                textState.setText(stateScan);
                                textPosition.setText(positionScan);
                                textUnit.setText(unitScan);
                                textDescription.setText(descriptionScan);
                            } else {
                                Toast.makeText(MainActivity.this, "非材料库二维码！", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "二维码格式错误！", Toast.LENGTH_SHORT).show();
                        }
                    } else if (fragment instanceof MatOutFragment) {
                        //材料出库
                        EditText textId = (EditText) findViewById(R.id.input_id_matout);
                        try {
                            JSONObject jsonObject = new JSONObject(returnData);
                            String stockType = jsonObject.getString("stockType");
                            String idScan = jsonObject.getString("id");

                            if (stockType.equals("material")) {
                                textId.setText(idScan);
                            } else {
                                Toast.makeText(MainActivity.this, "非材料库二维码！", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "二维码格式错误！", Toast.LENGTH_SHORT).show();
                        }
                    } else if (fragment instanceof PartInFragment) {
                        //备件入库
                        EditText textId = (EditText) findViewById(R.id.input_id_partin);
                        EditText textType = (EditText) findViewById(R.id.input_type_partin);
                        EditText textMark = (EditText) findViewById(R.id.input_mark_partin);
                        EditText textBand = (EditText) findViewById(R.id.input_band_partin);
                        EditText textOriginal = (EditText) findViewById(R.id.input_original_partin);
                        EditText textYear = (EditText) findViewById(R.id.input_year_partin);
                        EditText textState = (EditText) findViewById(R.id.input_state_partin);
                        EditText textPosition = (EditText) findViewById(R.id.input_position_partin);
                        EditText textUnit = (EditText) findViewById(R.id.input_unit_partin);
                        EditText textName = (EditText) findViewById(R.id.input_name_partin);
                        EditText textCompany = (EditText) findViewById(R.id.input_company_partin);
                        EditText textMachineName = (EditText) findViewById(R.id.input_machinename_partin);
                        EditText textMachineType = (EditText) findViewById(R.id.input_machinetype_partin);
                        EditText textMachineBand = (EditText) findViewById(R.id.input_machineband_partin);
                        EditText textCondition = (EditText) findViewById(R.id.input_condition_partin);
                        EditText textVulnerability = (EditText) findViewById(R.id.input_vul_partin);
                        EditText textDescription = (EditText) findViewById(R.id.input_description_partin);
                        try {
                            JSONObject jsonObject = new JSONObject(returnData);
                            String stockType = jsonObject.getString("stockType");
                            String idScan = jsonObject.getString("id");
                            String typeScan = jsonObject.getString("type");
                            String markScan = jsonObject.getString("mark");
                            String bandScan = jsonObject.getString("band");
                            String originalScan = jsonObject.getString("original");
                            String yearScan = jsonObject.getString("year");
                            String stateScan = jsonObject.getString("state");
                            String positionScan = jsonObject.getString("position");
                            String unitScan = jsonObject.getString("unit");
                            String nameScan = jsonObject.getString("name");
                            String companyScan = jsonObject.getString("company");
                            String machineNameScan = jsonObject.getString("machineName");
                            String machineTypeScan = jsonObject.getString("machineType");
                            String machineBandScan = jsonObject.getString("machineBand");
                            String conditionScan = jsonObject.getString("condition");
                            String vulnerabilityScan = jsonObject.getString("vulnerability");
                            String descriptionScan = jsonObject.getString("description");

                            if (stockType.equals("part")) {
                                textId.setText(idScan);
                                textType.setText(typeScan);
                                textMark.setText(markScan);
                                textBand.setText(bandScan);
                                textOriginal.setText(originalScan);
                                textYear.setText(yearScan);
                                textState.setText(stateScan);
                                textPosition.setText(positionScan);
                                textUnit.setText(unitScan);
                                textName.setText(nameScan);
                                textCompany.setText(companyScan);
                                textMachineName.setText(machineNameScan);
                                textMachineType.setText(machineTypeScan);
                                textMachineBand.setText(machineBandScan);
                                textCondition.setText(conditionScan);
                                textVulnerability.setText(vulnerabilityScan);
                                textDescription.setText(descriptionScan);
                            } else {
                                Toast.makeText(MainActivity.this, "非备件库二维码！", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "二维码格式错误！", Toast.LENGTH_SHORT).show();
                        }
                    } else if (fragment instanceof PartOutFragment) {
                        //备件出库
                        EditText textId = (EditText) findViewById(R.id.input_id_partout);
                        try {
                            JSONObject jsonObject = new JSONObject(returnData);
                            String stockType = jsonObject.getString("stockType");
                            String idScan = jsonObject.getString("id");

                            if (stockType.equals("part")) {
                                textId.setText(idScan);
                            } else {
                                Toast.makeText(MainActivity.this, "非备件库二维码！", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "二维码格式错误！", Toast.LENGTH_SHORT).show();
                        }
                    } else if (fragment instanceof EquipInFragment) {
                        //整机入库
                        EditText textId = (EditText) findViewById(R.id.input_id_equipin);
                        EditText textType = (EditText) findViewById(R.id.input_type_equipin);
                        EditText textMark = (EditText) findViewById(R.id.input_mark_equipin);
                        EditText textHour = (EditText) findViewById(R.id.input_hour_equipin);
                        EditText textBand = (EditText) findViewById(R.id.input_band_equipin);
                        EditText textOriginal = (EditText) findViewById(R.id.input_original_equipin);
                        EditText textYear = (EditText) findViewById(R.id.input_year_equipin);
                        EditText textState = (EditText) findViewById(R.id.input_state_equipin);
                        EditText textPosition = (EditText) findViewById(R.id.input_position_equipin);
                        EditText textUnit = (EditText) findViewById(R.id.input_unit_equipin);
                        EditText textDescription = (EditText) findViewById(R.id.input_description_equipin);
                        try {
                            JSONObject jsonObject = new JSONObject(returnData);
                            String stockType = jsonObject.getString("stockType");
                            String idScan = jsonObject.getString("id");
                            String typeScan = jsonObject.getString("type");
                            String markScan = jsonObject.getString("mark");
                            String hourScan = jsonObject.getString("hour");
                            String bandScan = jsonObject.getString("band");
                            String originalScan = jsonObject.getString("original");
                            String yearScan = jsonObject.getString("year");
                            String stateScan = jsonObject.getString("state");
                            String positionScan = jsonObject.getString("position");
                            String unitScan = jsonObject.getString("unit");
                            String descriptionScan = jsonObject.getString("description");

                            if (stockType.equals("equipment")) {
                                textId.setText(idScan);
                                textType.setText(typeScan);
                                textMark.setText(markScan);
                                textHour.setText(hourScan);
                                textBand.setText(bandScan);
                                textOriginal.setText(originalScan);
                                textYear.setText(yearScan);
                                textState.setText(stateScan);
                                textPosition.setText(positionScan);
                                textUnit.setText(unitScan);
                                textDescription.setText(descriptionScan);
                            } else {
                                Toast.makeText(MainActivity.this, "非整机库二维码！", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "二维码格式错误！", Toast.LENGTH_SHORT).show();
                        }
                    } else if (fragment instanceof EquipOutFragment) {
                        //整机出库
                        EditText textId = (EditText) findViewById(R.id.input_id_equipout);
                        try {
                            JSONObject jsonObject = new JSONObject(returnData);
                            String stockType = jsonObject.getString("stockType");
                            String idScan = jsonObject.getString("id");

                            if (stockType.equals("equipment")) {
                                textId.setText(idScan);
                            } else {
                                Toast.makeText(MainActivity.this, "非整机库二维码！", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "二维码格式错误！", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            default:
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        }
    }

    private void replaceFragmentWithoutRefresh(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (currentFrag == null) {
            transaction.add(R.id.frame_main, fragment);
            transaction.commit();
            currentFrag = fragment;
        } else if (currentFrag != fragment) {
            if (!fragment.isAdded()) {
                transaction.hide(currentFrag).add(R.id.frame_main, fragment);
                transaction.commit();
            } else {
                transaction.hide(currentFrag).show(fragment);
                transaction.commit();
            }
            currentFrag = fragment;
        }
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

    private Fragment getCurrentFragment() {
        if (userinfoFragment != null && userinfoFragment.isVisible()) {
            return userinfoFragment;
        } else if (partStorageFragment != null && partStorageFragment.isVisible()) {
            return partStorageFragment;
        } else if (partInFragment != null && partInFragment.isVisible()) {
            return partInFragment;
        } else if (partOutFragment != null && partOutFragment.isVisible()) {
            return partOutFragment;
        } else if (partSearchFragment != null && partSearchFragment.isVisible()) {
            return partSearchFragment;
        } else if (equipStorageFragment != null && equipStorageFragment.isVisible()) {
            return equipStorageFragment;
        } else if (equipInFragment != null && equipInFragment.isVisible()) {
            return equipInFragment;
        } else if (equipOutFragment != null && equipOutFragment.isVisible()) {
            return equipOutFragment;
        } else if (equipSearchFragment != null && equipSearchFragment.isVisible()) {
            return equipSearchFragment;
        } else if (matStorageFragment != null && matStorageFragment.isVisible()) {
            return matStorageFragment;
        } else if (matInFragment != null && matInFragment.isVisible()) {
            return matInFragment;
        } else if (matOutFragment != null && matOutFragment.isVisible()) {
            return matOutFragment;
        } else if (matSearchFragment != null && matSearchFragment.isVisible()) {
            return matSearchFragment;
        } else if (qrMakeFragment != null && qrMakeFragment.isVisible()) {
            return qrMakeFragment;
        } else {
            return null;
        }
    }
}
