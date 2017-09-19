package com.rinc.imsys;

/**
 * Created by ZhouZhi on 2017/8/30.
 */

public class SearchRecord {
    //该类用于存储上次的搜索记录，以便在回到搜索界面时自动填充
    public static String id_mat = "";

    public static String type_mat = "";

    public static String band_mat = "";

    public static String original_mat = "";

    public static String position_mat = "";

    public static String yearstart_mat = "";

    public static String yearend_mat = "";

    public static String id_part = "";

    public static String type_part = "";

    public static String band_part = "";

    public static String original_part = "";

    public static String position_part = "";

    public static String yearstart_part = "";

    public static String yearend_part = "";

    public static final int FRAGLABEL_STORAGE = 1;

    public static final int FRAGLABEL_SEARCH = 2;

    public static int lastFrag = FRAGLABEL_STORAGE;

    public static void clearRecord() {
        id_mat = "";
        type_mat = "";
        band_mat = "";
        original_mat = "";
        position_mat = "";
        yearstart_mat = "";
        yearend_mat = "";

        id_part = "";
        type_part = "";
        band_part = "";
        original_part = "";
        position_part = "";
        yearstart_part = "";
        yearend_part = "";

        lastFrag = FRAGLABEL_STORAGE;
    }

    public static void setMatRecord(String id, String type, String band, String original,
                                 String position, String yearstart, String yearend) {
        id_mat = id;
        type_mat = type;
        band_mat = band;
        original_mat = original;
        position_mat = position;
        yearstart_mat = yearstart;
        yearend_mat = yearend;
    }

    public static void setPartRecord(String id, String type, String band, String original,
                                    String position, String yearstart, String yearend) {
        id_part = id;
        type_part = type;
        band_part = band;
        original_part = original;
        position_part = position;
        yearstart_part = yearstart;
        yearend_part = yearend;
    }
}
