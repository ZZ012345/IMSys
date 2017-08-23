package com.rinc.imsys;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;

/**
 * Created by zhouzhi on 2017/8/11.
 */

public class HttpUtil {

    public static final String serverAddr = "http://118.89.20.122:8888/";

    public static String header;

    private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void login(String username, String password, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url(serverAddr + "rest-auth/login/")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void register(String username, String email, String password1, String password2, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("username", username)
                .add("email", email)
                .add("password1", password1)
                .add("password2", password2)
                .build();
        Request request = new Request.Builder()
                .url(serverAddr + "rest-auth/registration/")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void logout(okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(serverAddr + "rest-auth/logout/")
                .addHeader("Authorization", "Token " + header)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void getUserinfo(okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(serverAddr + "api/users_display/")
                .addHeader("Authorization", "Token " + header)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void fillUserinfo(String tel, String company, String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("tel", tel)
                .add("company", company)
                .add("address", address)
                .build();
        Request request = new Request.Builder()
                .url(serverAddr + "api/create_users_info/")
                .addHeader("Authorization", "Token " + header)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void modifyUserinfo(String tel, String company, String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("tel", tel)
                .add("company", company)
                .add("address", address)
                .build();
        Request request = new Request.Builder()
                .url(serverAddr + "api/users_display/")
                .addHeader("Authorization", "Token " + header)
                .put(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void modifyPassword(String oldpassword, String password1, String password2, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("old_password", oldpassword)
                .add("new_password1", password1)
                .add("new_password2", password2)
                .build();
        Request request = new Request.Builder()
                .url(serverAddr + "rest-auth/password/change/")
                .addHeader("Authorization", "Token " + header)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void materialIn(String id, String type, String storestate, String mark, String band, String original,
                                  String year, String state, String position, String unit, String description, String datetime,
                                  String operator, String num, okhttp3.Callback callback) {
        JSONObject jsonMaterial = new JSONObject();
        JSONObject jsonAll = new JSONObject();
        try {
            jsonMaterial.put("materialID", id);
            jsonMaterial.put("materialType", type);
            jsonMaterial.put("materialStoreState", storestate);
            jsonMaterial.put("materialMark", mark);
            jsonMaterial.put("materialBand", band);
            jsonMaterial.put("materialOriginal", original);
            if (year.length() != 0) {
                jsonMaterial.put("materialYear", year);
            } else {
                jsonMaterial.put("materialYear", JSONObject.NULL);
            }
            jsonMaterial.put("materialState", state);
            jsonMaterial.put("materialPosition", position);
            jsonMaterial.put("materialUnit", unit);
            jsonMaterial.put("description", description);
            jsonAll.put("inputMaterial", jsonMaterial);
            jsonAll.put("inputDateTime", datetime);
            jsonAll.put("inputOperator", operator);
            jsonAll.put("inputNum", num);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.d("Mat in send", jsonAll.toString());
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JSON, jsonAll.toString());
        Request request = new Request.Builder()
                .url(serverAddr + "api/material_input/")
                .addHeader("Authorization", "Token " + header)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void getMatStorage(okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(serverAddr + "api/material/")
                .addHeader("Authorization", "Token " + header)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void modifyMatDetail(String Oldid, String id, String type, String storestate, String mark,
                                       String band, String original, String year, String state,
                                       String position, String unit, String description, okhttp3.Callback callback) {
        JSONObject jsonMaterial = new JSONObject();
        try {
            jsonMaterial.put("materialID", id);
            jsonMaterial.put("materialType", type);
            jsonMaterial.put("materialStoreState", storestate);
            jsonMaterial.put("materialMark", mark);
            jsonMaterial.put("materialBand", band);
            jsonMaterial.put("materialOriginal", original);
            if (year.length() != 0) {
                jsonMaterial.put("materialYear", year);
            } else {
                jsonMaterial.put("materialYear", JSONObject.NULL);
            }
            jsonMaterial.put("materialState", state);
            jsonMaterial.put("materialPosition", position);
            jsonMaterial.put("materialUnit", unit);
            jsonMaterial.put("description", description);
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JSON, jsonMaterial.toString());
        Request request = new Request.Builder()
                .url(serverAddr + "api/material/" + Oldid + "/")
                .addHeader("Authorization", "Token " + header)
                .put(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void deleteMatDetail(String id, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(serverAddr + "api/material/" + id + "/")
                .addHeader("Authorization", "Token " + header)
                .delete()
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void getMatInRec(String id, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(serverAddr + "api/material/inputlist?materialID=" + id)
                .addHeader("Authorization", "Token " + header)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void materialOut(String id, String datetime, String user, String operator, String num, okhttp3.Callback callback) {
        JSONObject jsonMaterial = new JSONObject();
        JSONObject jsonAll = new JSONObject();
        try {
            jsonMaterial.put("materialID", id);
            jsonAll.put("outputMaterial", jsonMaterial);
            jsonAll.put("outputDateTime", datetime);
            jsonAll.put("materialUser", user);
            jsonAll.put("outputOperator", operator);
            jsonAll.put("outputNum", num);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.d("Mat out send", jsonAll.toString());
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JSON, jsonAll.toString());
        Request request = new Request.Builder()
                .url(serverAddr + "api/material_output/")
                .addHeader("Authorization", "Token " + header)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void getMatOutRec(String id, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(serverAddr + "api/material/outputlist?materialID=" + id)
                .addHeader("Authorization", "Token " + header)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void searchMat(String id, String type, String band, String original, String position,
                                 String yearstart, String yearend, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        String url = serverAddr + "api/material/search?" + "materialID=" + id + "&materialType=" + type +
                "&materialBand=" + band + "&materialOriginal=" + original + "&materialPosition=" + position +
                "&materialYearstart=" + yearstart + "&materialYearend=" + yearend;
        LogUtil.d("Mat Search url", url);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Token " + header)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
