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

    //http://118.89.20.122:8888/"
    public static final String serverAddr = "http://101.132.104.112:8080/";

    public static String header;

    private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void simpleGet(String url, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Token " + header)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //用户api
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

    //材料库api
    public static void materialIn(String id, String type, String storestate, String mark, String band, String original,
                                  String year, String state, String position, String unit, String description, String datetime,
                                  String operator, String num, String inputDescription, okhttp3.Callback callback) {
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
            if (unit.length() != 0) {
                jsonMaterial.put("materialUnit", unit);
            } else {
                jsonMaterial.put("materialUnit", JSONObject.NULL);
            }
            jsonMaterial.put("description", description);
            jsonAll.put("inputMaterial", jsonMaterial);
            jsonAll.put("inputDateTime", datetime);
            jsonAll.put("inputOperator", operator);
            jsonAll.put("inputNum", num);
            jsonAll.put("inputDescription", inputDescription);
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

    public static void modifyMatDetail(String databaseId, String id, String type, String storestate, String mark,
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
            if (unit.length() != 0) {
                jsonMaterial.put("materialUnit", unit);
            } else {
                jsonMaterial.put("materialUnit", JSONObject.NULL);
            }
            jsonMaterial.put("description", description);
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JSON, jsonMaterial.toString());
        Request request = new Request.Builder()
                .url(serverAddr + "api/material/" + databaseId + "/")
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
                .url(serverAddr + "api/material/inputlist?id=" + id)
                .addHeader("Authorization", "Token " + header)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void materialOut(String id, String datetime, String user, String operator, String num, String outputDescription, okhttp3.Callback callback) {
        JSONObject jsonMaterial = new JSONObject();
        JSONObject jsonAll = new JSONObject();
        try {
            jsonMaterial.put("materialID", id);
            jsonAll.put("outputMaterial", jsonMaterial);
            jsonAll.put("outputDateTime", datetime);
            jsonAll.put("materialUser", user);
            jsonAll.put("outputOperator", operator);
            jsonAll.put("outputNum", num);
            jsonAll.put("outputDescription", outputDescription);
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
                .url(serverAddr + "api/material/outputlist?id=" + id)
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

    //备件库api
    public static void partIn(String id, String type, String storestate, String mark, String band, String original,
                              String year, String state, String position, String unit, String name, String company,
                              String machineName, String machineType, String machineBand, String condition, String vulnerability,
                              String description, String datetime, String operator, String num, String inputDescription, okhttp3.Callback callback) {
        JSONObject jsonPart = new JSONObject();
        JSONObject jsonAll = new JSONObject();
        try {
            jsonPart.put("partID", id);
            jsonPart.put("partType", type);
            jsonPart.put("partStoreState", storestate);
            jsonPart.put("partMark", mark);
            jsonPart.put("partBand", band);
            jsonPart.put("partOriginal", original);
            if (year.length() != 0) {
                jsonPart.put("partYear", year);
            } else {
                jsonPart.put("partYear", JSONObject.NULL);
            }
            jsonPart.put("partState", state);
            jsonPart.put("partPosition", position);
            if (unit.length() != 0) {
                jsonPart.put("partUnit", unit);
            } else {
                jsonPart.put("partUnit", JSONObject.NULL);
            }
            jsonPart.put("partName", name);
            jsonPart.put("partCompany", company);
            jsonPart.put("partMachineName", machineName);
            jsonPart.put("partMachineType", machineType);
            jsonPart.put("partMachineBand", machineBand);
            jsonPart.put("partCondition", condition);
            jsonPart.put("partVulnerability", vulnerability);
            jsonPart.put("description", description);
            jsonAll.put("inputPart", jsonPart);
            jsonAll.put("inputDateTime", datetime);
            jsonAll.put("inputOperator", operator);
            jsonAll.put("inputNum", num);
            jsonAll.put("inputDescription", inputDescription);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.d("Part in send", jsonAll.toString());
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JSON, jsonAll.toString());
        Request request = new Request.Builder()
                .url(serverAddr + "api/part_input/")
                .addHeader("Authorization", "Token " + header)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void getPartStorage(okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(serverAddr + "api/part/")
                .addHeader("Authorization", "Token " + header)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void modifyPartDetail(String databaseId, String id, String type, String storestate, String mark,
                                        String band, String original, String year, String state, String position,
                                        String unit, String name, String company, String machineName, String machineType,
                                        String machineBand, String condition, String vulnerability, String description, okhttp3.Callback callback) {
        JSONObject jsonPart = new JSONObject();
        try {
            jsonPart.put("partID", id);
            jsonPart.put("partType", type);
            jsonPart.put("partStoreState", storestate);
            jsonPart.put("partMark", mark);
            jsonPart.put("partBand", band);
            jsonPart.put("partOriginal", original);
            if (year.length() != 0) {
                jsonPart.put("partYear", year);
            } else {
                jsonPart.put("partYear", JSONObject.NULL);
            }
            jsonPart.put("partState", state);
            jsonPart.put("partPosition", position);
            if (unit.length() != 0) {
                jsonPart.put("partUnit", unit);
            } else {
                jsonPart.put("partUnit", JSONObject.NULL);
            }
            jsonPart.put("partName", name);
            jsonPart.put("partCompany", company);
            jsonPart.put("partMachineName", machineName);
            jsonPart.put("partMachineType", machineType);
            jsonPart.put("partMachineBand", machineBand);
            jsonPart.put("partCondition", condition);
            jsonPart.put("partVulnerability", vulnerability);
            jsonPart.put("description", description);
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JSON, jsonPart.toString());
        Request request = new Request.Builder()
                .url(serverAddr + "api/part/" + databaseId + "/")
                .addHeader("Authorization", "Token " + header)
                .put(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void deletePartDetail(String id, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(serverAddr + "api/part/" + id + "/")
                .addHeader("Authorization", "Token " + header)
                .delete()
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void getPartInRec(String id, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(serverAddr + "api/part/inputlist?id=" + id)
                .addHeader("Authorization", "Token " + header)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void partOut(String id, String datetime, String user, String operator, String num, String outputDescription, okhttp3.Callback callback) {
        JSONObject jsonPart = new JSONObject();
        JSONObject jsonAll = new JSONObject();
        try {
            jsonPart.put("partID", id);
            jsonAll.put("outputPart", jsonPart);
            jsonAll.put("outputDateTime", datetime);
            jsonAll.put("partUser", user);
            jsonAll.put("outputOperator", operator);
            jsonAll.put("outputNum", num);
            jsonAll.put("outputDescription", outputDescription);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.d("Part out send", jsonAll.toString());
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JSON, jsonAll.toString());
        Request request = new Request.Builder()
                .url(serverAddr + "api/part_output/")
                .addHeader("Authorization", "Token " + header)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void getPartOutRec(String id, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(serverAddr + "api/part/outputlist?id=" + id)
                .addHeader("Authorization", "Token " + header)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void searchPart(String id, String type, String band, String original, String position,
                                 String yearstart, String yearend, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        String url = serverAddr + "api/part/search?" + "partID=" + id + "&partState=" + type +
                "&partName=" + band + "&partMark=" + original + "&partPosition=" + position +
                "&partYearstart=" + yearstart + "&partYearend=" + yearend;
        LogUtil.d("Part Search url", url);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Token " + header)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //整机库api
    public static void equipIn(String id, String type, String storestate, String mark, String hour, String band, String original,
                                  String year, String state, String position, String unit, String description, String datetime,
                                  String operator, String num, String inputDescription, okhttp3.Callback callback) {
        JSONObject jsonMaterial = new JSONObject();
        JSONObject jsonAll = new JSONObject();
        try {
            jsonMaterial.put("equipmentID", id);
            jsonMaterial.put("equipmentType", type);
            jsonMaterial.put("equipmentStoreState", storestate);
            jsonMaterial.put("equipmentMark", mark);
            if (hour.length() != 0) {
                jsonMaterial.put("equipmentHour", hour);
            } else {
                jsonMaterial.put("equipmentHour", JSONObject.NULL);
            }
            jsonMaterial.put("equipmentBand", band);
            jsonMaterial.put("equipmentOriginal", original);
            if (year.length() != 0) {
                jsonMaterial.put("equipmentYear", year);
            } else {
                jsonMaterial.put("equipmentYear", JSONObject.NULL);
            }
            jsonMaterial.put("equipmentState", state);
            jsonMaterial.put("equipmentPosition", position);
            if (unit.length() != 0) {
                jsonMaterial.put("equipmentUnit", unit);
            } else {
                jsonMaterial.put("equipmentUnit", JSONObject.NULL);
            }
            jsonMaterial.put("description", description);
            jsonAll.put("inputEquipment", jsonMaterial);
            jsonAll.put("inputDateTime", datetime);
            jsonAll.put("inputOperator", operator);
            jsonAll.put("inputNum", num);
            jsonAll.put("inputDescription", inputDescription);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.d("Equip in send", jsonAll.toString());
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JSON, jsonAll.toString());
        Request request = new Request.Builder()
                .url(serverAddr + "api/equipment_input/")
                .addHeader("Authorization", "Token " + header)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void getEquipStorage(okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(serverAddr + "api/equipment/")
                .addHeader("Authorization", "Token " + header)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void modifyEquipDetail(String databaseId, String id, String type, String storestate, String mark,
                                       String hour, String band, String original, String year, String state,
                                       String position, String unit, String description, okhttp3.Callback callback) {
        JSONObject jsonEquip = new JSONObject();
        try {
            jsonEquip.put("equipmentID", id);
            jsonEquip.put("equipmentType", type);
            jsonEquip.put("equipmentStoreState", storestate);
            jsonEquip.put("equipmentMark", mark);
            if (hour.length() != 0) {
                jsonEquip.put("equipmentHour", hour);
            } else {
                jsonEquip.put("equipmentHour", JSONObject.NULL);
            }
            jsonEquip.put("equipmentBand", band);
            jsonEquip.put("equipmentOriginal", original);
            if (year.length() != 0) {
                jsonEquip.put("equipmentYear", year);
            } else {
                jsonEquip.put("equipmentYear", JSONObject.NULL);
            }
            jsonEquip.put("equipmentState", state);
            jsonEquip.put("equipmentPosition", position);
            if (unit.length() != 0) {
                jsonEquip.put("equipmentUnit", unit);
            } else {
                jsonEquip.put("equipmentUnit", JSONObject.NULL);
            }
            jsonEquip.put("description", description);
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JSON, jsonEquip.toString());
        Request request = new Request.Builder()
                .url(serverAddr + "api/equipment/" + databaseId + "/")
                .addHeader("Authorization", "Token " + header)
                .put(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void deleteEquipDetail(String id, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(serverAddr + "api/equipment/" + id + "/")
                .addHeader("Authorization", "Token " + header)
                .delete()
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void getEquipInRec(String id, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(serverAddr + "api/equipment/inputlist?id=" + id)
                .addHeader("Authorization", "Token " + header)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void equipOut(String id, String datetime, String user, String operator, String num, String outputDescription, okhttp3.Callback callback) {
        JSONObject jsonEquip = new JSONObject();
        JSONObject jsonAll = new JSONObject();
        try {
            jsonEquip.put("equipmentID", id);
            jsonAll.put("outputEquipment", jsonEquip);
            jsonAll.put("outputDateTime", datetime);
            jsonAll.put("equipmentUser", user);
            jsonAll.put("outputOperator", operator);
            jsonAll.put("outputNum", num);
            jsonAll.put("outputDescription", outputDescription);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.d("Equip out send", jsonAll.toString());
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(JSON, jsonAll.toString());
        Request request = new Request.Builder()
                .url(serverAddr + "api/equipment_output/")
                .addHeader("Authorization", "Token " + header)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void getEquipOutRec(String id, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(serverAddr + "api/equipment/outputlist?id=" + id)
                .addHeader("Authorization", "Token " + header)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void searchEquip(String id, String type, String band, String original, String position,
                                 String yearstart, String yearend, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        String url = serverAddr + "api/equipment/search?" + "equipmentID=" + id + "&equipmentType=" + type +
                "&equipmentBand=" + band + "&equipmentOriginal=" + original + "&equipmentPosition=" + position +
                "&equipmentYearstart=" + yearstart + "&equipmentYearend=" + yearend;
        LogUtil.d("Equip Search url", url);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Token " + header)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
