package com.rinc.imsys;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by ZhouZhi on 2017/11/6.
 */

public class SettingsFragment extends BaseFragment {

    private List<String> languageList = new ArrayList<String>();

    private Spinner spinner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        spinner = view.findViewById(R.id.spinner_settings);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle(getString(R.string.settings));

        //初始化下拉列表
        languageList.add("简体中文");
        languageList.add("English");
        languageList.add("Español");
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinnertext_lan_item, languageList);
        adapter.setDropDownViewResource(R.layout.spinnerdropdown_lan_item);
        spinner.setAdapter(adapter);
        //选中当前语言
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String language = pref.getString("language", "");
        cancelSwitch(language);
        //添加监听
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    //简体中文
                    LogUtil.d("Language Setting", "choose Simplified Chinese");
                    if (!language.equals("Simplified Chinese")) {
                        //当前语言是英语
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle(getString(R.string.hint));
                        dialog.setMessage(getString(R.string.switch_language));
                        dialog.setCancelable(false);
                        dialog.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switchLanguage("Simplified Chinese");
                            }
                        });
                        dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cancelSwitch(language);
                            }
                        });
                        dialog.show();
                    }
                } else if (i == 1) {
                    //英语
                    LogUtil.d("Language Setting", "choose English");
                    if (!language.equals("English")) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle(getString(R.string.hint));
                        dialog.setMessage(getString(R.string.switch_language));
                        dialog.setCancelable(false);
                        dialog.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switchLanguage("English");
                            }
                        });
                        dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cancelSwitch(language);
                            }
                        });
                        dialog.show();
                    }
                } else if (i == 2) {
                    //西班牙语
                    LogUtil.d("Language Setting", "choose Spanish");
                    if (!language.equals("Spanish")) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle(getString(R.string.hint));
                        dialog.setMessage(getString(R.string.switch_language));
                        dialog.setCancelable(false);
                        dialog.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switchLanguage("Spanish");
                            }
                        });
                        dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cancelSwitch(language);
                            }
                        });
                        dialog.show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    private void switchLanguage(String language) {
        //将语言设置持久化
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
        editor.putString("language", language);
        editor.apply();

        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LogUtil.d("Language Setting", "Android Version >= 7.0");
            if (language.equals("Simplified Chinese")) {
                configuration.setLocale(Locale.SIMPLIFIED_CHINESE);
            } else if (language.equals("English")) {
                configuration.setLocale(Locale.ENGLISH);
            } else if (language.equals("Spanish")) {
                configuration.setLocale(new Locale("es"));
            }
        } else {
            LogUtil.d("Language Setting", "Android Version < 7.0");
            if (language.equals("Simplified Chinese")) {
                configuration.locale = Locale.SIMPLIFIED_CHINESE;
            } else if (language.equals("English")) {
                configuration.locale = Locale.ENGLISH;
            } else if (language.equals("Spanish")) {
                configuration.locale = new Locale("es");
            }
        }
        resources.updateConfiguration(configuration, displayMetrics); //更新语言
        //重启应用
        User.clear();
        HttpUtil.header = null;
        SearchRecord.clearRecord();
        ActivityCollector.finishAll();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    private void cancelSwitch(String language) {
        if (language.equals("Simplified Chinese")) {
            spinner.setSelection(0);
        } else if (language.equals("English")) {
            spinner.setSelection(1);
        } else {
            spinner.setSelection(2);
        }
    }
}
