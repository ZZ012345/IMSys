package com.rinc.imsys;

/**
 * Created by zhouzhi on 2017/8/11.
 */

public class User {

    public static String username;

    public static String company;

    public static String tel;

    public static String address;

    public static String email;

    public User() {

    }

    public static void clear() { //清空用户信息
        username = null;
        company = null;
        tel = null;
        address = null;
        email = null;
    }
}
