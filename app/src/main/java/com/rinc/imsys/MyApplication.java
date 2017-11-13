package com.rinc.imsys;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * Created by zhouzhi on 2017/8/10.
 */

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        //语言设置
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LogUtil.d("Language Set", "Android Version >= 7.0");
            locale = configuration.getLocales().get(0);
        } else {
            LogUtil.d("Language Set", "Android Version < 7.0");
            locale = configuration.locale;
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String language = pref.getString("language", "");
        if (language.length() == 0) {
            //第一次打开应用
            LogUtil.d("Language Set", "First Launch");
            if (locale.toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                //如果系统默认语言为简体中文，则设置为简体中文
                language = "Simplified Chinese";
            } else {
                //如果系统默认语言非简体中文，则设置为英语
                language = "English";
            }
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("language", language); //将语言设置持久化
            editor.apply();
        }
        if (language.equals("Simplified Chinese")) {
            //应用语言设置为简体中文
            LogUtil.d("Language Set", "to Simplified Chinese");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                configuration.setLocale(Locale.SIMPLIFIED_CHINESE);
            } else {
                locale = Locale.SIMPLIFIED_CHINESE;
            }
        } else if (language.equals("English")) {
            //应用语言设置为英语
            LogUtil.d("Language Set", "to English");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                configuration.setLocale(Locale.ENGLISH);
            } else {
                locale = Locale.ENGLISH;
            }
        }
        resources.updateConfiguration(configuration, displayMetrics); //更新语言
    }

    public static Context getContext() {
        return context;
    }
}
