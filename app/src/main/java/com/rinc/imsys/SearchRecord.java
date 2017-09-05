package com.rinc.imsys;

/**
 * Created by ZhouZhi on 2017/8/30.
 */

public class SearchRecord {
    //用于存储上次的搜索记录，以便在回到搜索界面时自动填充
    public static String id = "";

    public static String type = "";

    public static String band = "";

    public static String original = "";

    public static String position = "";

    public static String yearstart = "";

    public static String yearend = "";

    //用于在关闭滑动菜单栏时判断当前应该点击的菜单项
    public static int lastpage = 1;

    public static void clearRecord() {
        id = "";
        type = "";
        band = "";
        original = "";
        position = "";
        yearstart = "";
        yearend = "";
        lastpage = 1;
    }

    public static void setRecord(String id1, String type1, String band1, String original1,
                                 String position1, String yearstart1, String yearend1) {
        id = id1;
        type = type1;
        band = band1;
        original = original1;
        position = position1;
        yearstart = yearstart1;
        yearend = yearend1;
    }
}
