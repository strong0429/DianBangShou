package com.xingdhl.www.storehelper.webservice;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.xingdhl.www.storehelper.ObjectDefine.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Strong on 17/11/8.
 *
 */

public class WebServiceAPIs {
    private static final String URL_HOST = "http://192.168.0.105:8000/xing/";

    private static final String URL_TOKEN = URL_HOST + "token/";
    private static final String URL_USER_LOGIN = URL_HOST + "login/";
    private static final String URL_USER_REGISTER = URL_HOST + "register/";
    private static final String URL_SMS_CODE = URL_HOST + "sms/";
    private static final String URL_STORES = URL_HOST + "stores/";  //店铺创建、查询、更新等；
    private static final String URL_SELLS_SUMMARY = URL_HOST + "stores/%d/sales/sum/";
    private static final String URL_PURCHASE = URL_HOST + "stores/%d/purchases/";
    private static final String URL_STOCKS = URL_HOST + "stores/%d/stocks/";
    private static final String URL_GOODS_CATEGORIES = URL_HOST + "goods/categories";
    private static final String URL_GOODS = URL_HOST + "goods/";

    public static final String URL_GET_APP = URL_HOST + "file/app/download";
    public static final String URL_POST_FILE_IMG = URL_HOST + "file/img/upload";
    public static final String URL_GET_FILE_IMG = URL_HOST + "file/img/download";

    private static final String URL_USER = URL_HOST + "users";
    private static final String URL_USER_CLERKS = URL_HOST + "users/clerks";
    private static final String URL_UPDATE_CLERK = URL_HOST + "users/clerks/%d";
    private static final String URL_GET_USERINFO = URL_HOST + "users/info/";
    private static final String URL_UPDATE_PASSWORD = URL_HOST + "users/password";
    private static final String URL_GET_SHOP_TYPE = URL_HOST + "stores/category";
    private static final String URL_GET_SHOPS = URL_HOST + "store_default/list/";
    private static final String URL_SUPPLIER = URL_HOST + "stores/supplier";
    private static final String URL_GET_PURCHASE = URL_HOST + "stores/purchase/%d";
    private static final String URL_ADD_SELL = URL_HOST + "sales";
    private static final String URL_GET_SELLS_DAYSUM = URL_HOST + "sales/days_sum";
    private static final String URL_GET_SELLS_DETAIL = URL_HOST + "sales/detail";
    private static final String URL_STORE_STORAGE = URL_HOST + "stores/%d/storage/%s";
    private static final String URL_GET_GOODS_BARCODE = URL_HOST + "goods/%s";

    private static final String KEY_ERR_MSG = "err_msg";

    private static final String KEY_ERR_CODE = "err_code";

    public static final int HTTP_CONTINUE = 1;

    public static final int HTTP_OVER_RIGHT = -200;
    public static final int HTTP_HAD_EXIST = -201;
    public static final int HTTP_NO_EXIST = -202;
    public static final int HTTP_DATA_INVALID = -203;
    public static final int HTTP_FILE_ERROR = -204;
    public static final int HTTP_UPLOAD_FAIL = -205;

    public static final int MSG_GET_TOKEN = -1000;
    public static final int MSG_USER_LOGIN = -1001;
    public static final int MSG_GET_CATEGORY = -1002;
    public static final int MSG_GET_USERINFO = -1003;
    public static final int MSG_SMS_CODE = -1004;
    public static final int MSG_USER_REGISTER = -1005;
    public static final int MSG_RESET_PWD = -1006;
    public static final int MSG_GET_SHOPS = -1007;
    public static final int MSG_STORE_CREATE = -1008;
    public static final int MSG_MODIFY_PWD = -1009;
    public static final int MSG_ADD_SUPPLIER = -1010;
    public static final int MSG_GET_SUPPLIER = -1011;
    public static final int MSG_DEL_SUPPLIER = -1012;
    public static final int MSG_UPDATE_SUPPLIER = -1013;
    public static final int MSG_GET_GOODS = -1014;
    public static final int MSG_ADD_PURCHASE = -1015;
    public static final int MSG_ADD_GOODS = -1016;
    public static final int MSG_GET_STOCK = -1017;
    public static final int MSG_ADD_SELL = -1018;
    public static final int MSG_ADD_STORAGE = -1020;
    public static final int MSG_UPDATE_STORAGE = -1021;
    public static final int MSG_GET_SELL_SUM = -1022;
    public static final int MSG_GET_SELL_SUM_BY_DAY = -1023;
    public static final int MSG_GET_APP_VER = -1024;
    public static final int MSG_UPLOAD_FILE = -1025;
    public static final int MSG_DOWNLOAD_FILE = -1026;
    public static final int MSG_GET_CLERKS_INFO = -1027;
    public static final int MSG_REGISTER_CLERK = -1028;
    public static final int MSG_UPDATE_CLERK = -1029;
    public static final int MSG_DELETE_CLERK = -1030;
    public static final int MSG_GET_SELL_DETAIL = -1031;
    public static final int MSG_UPDATE_USER = -1032;
    public static final int MSG_STORE_UPDATE = -1033;
    public static final int MSG_UPLOAD_SELL = -1034;
    public static final int MSG_GET_PURCHASE = -1035;

    private WebServiceAPIs() {
    }

    private static int transHttpCode(int code){
        return code;
    }

    private static int getErrorMessage(Bundle data, JSONObject jsonObject) throws JSONException {
        data.putString("field", jsonObject.getString("field"));
        data.putString("message", jsonObject.getString("message"));
        return jsonObject.getInt("code");
    }

    public static void getToken(final HttpHandler httpHandler, final String appName){
        HttpRunnable httpRunnable = new HttpRunnable(URL_TOKEN) {
            @Override
            public void run() {
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("name", appName);
                byte[] urlData = httpRequest(dataMap, null);

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_GET_TOKEN;
                msg.arg1 = getHttpCode();

                JSONObject jsonObject;
                Bundle data = new Bundle();
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    if(msg.arg1 == HTTP_OK) {
                        data.putString("token", jsonObject.getString("token"));
                        jsonObject = jsonObject.getJSONObject("app_version");
                        msg.arg2 = jsonObject.getInt("ver_code");
                        data.putString("ver_txt", jsonObject.getString("ver_txt"));
                        data.putString("date_pub", jsonObject.getString("date_pub"));
                        data.putString("detail", jsonObject.getString("detail"));
                    } else {
                        msg.arg1 = getErrorMessage(data, jsonObject);
                    }
                }catch (JSONException je){
                    Log.d(GCV.D_TAG, "clerkRegister()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                    data.putString("field", "no_field_errors");
                    data.putString("message", "数据解析错误。");
                }
                msg.setData(data);
                httpHandler.sendMessage(msg);
            }
        };
        new Thread(httpRunnable).start();
    }

    public static void userLogin(final HttpHandler httpHandler) {
        final User user = User.getUser(null);

        HttpRunnable httpRunnable = new HttpRunnable(URL_USER_LOGIN) {
            @Override
            public void run() {
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("username", user.getUserName());
                dataMap.put("password", user.getPassword());
                byte[] returnData = httpRequest(dataMap, user.getToken());

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_USER_LOGIN;
                msg.arg1 = getHttpCode();

                JSONObject jsonObject;
                Bundle data = new Bundle();
                String jsonString = new String(returnData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    if(msg.arg1 == HTTP_OK) {
                        user.setToken(jsonObject.getString("token"));
                        jsonObject = jsonObject.getJSONObject("user");
                        user.setUser(jsonObject);

                        user.getStores().clear();
                        JSONArray jsonArray = jsonObject.getJSONArray("stores");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Store store = new Store(jsonArray.getJSONObject(i));
                            user.addStore(store);
                        }
                        //用户组、权限；
                    }else{
                        msg.arg1 = getErrorMessage(data, jsonObject);
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "UserLogin()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                    data.putString("field", "no_field_errors");
                    data.putString("message", "数据解析错误。");
                }
                msg.setData(data);
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_USER_LOGIN);
        new Thread(httpRunnable).start();
    }

    public static void getSmsCode(final HttpHandler httpHandler, final String phoneNum, final Boolean existed) {
        final User user = User.getUser(null);

        HttpRunnable httpRunnable = new HttpRunnable(URL_SMS_CODE) {
            @Override
            public void run() {
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("mobile", phoneNum);
                dataMap.put("existed", existed.toString());
                byte[] urlData = httpRequest(dataMap, user.getToken());

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_SMS_CODE;
                msg.arg1 = getHttpCode();

                JSONObject jsonObject;
                Bundle data = new Bundle();
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    if(msg.arg1 == HTTP_OK){
                        user.setToken(jsonObject.getString("token"));
                    }else{
                        msg.arg1 = getErrorMessage(data, jsonObject);
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "getSmsCode()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                    data.putString("field", "no_field_errors");
                    data.putString("message", "数据解析错误。");
                }
                msg.setData(data);
                httpHandler.sendMessage(msg);
            }
        };
        new Thread(httpRunnable).start();
    }

    public static void userRegister(final HttpHandler httpHandler, final String smsCode) {
        final User user = User.getUser(null);

        HttpRunnable httpRunnable = new HttpRunnable(URL_USER_REGISTER) {
            @Override
            public void run() {
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("username", user.getUserName());
                dataMap.put("mobile", user.getMobile());
                dataMap.put("password", user.getPassword());
                dataMap.put("sms_code", smsCode);
                byte[] urlData = httpRequest(dataMap, user.getToken());

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_USER_REGISTER;
                msg.arg1 = getHttpCode();

                JSONObject jsonObject;
                Bundle data = new Bundle();
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    if(msg.arg1 != HTTP_OK){
                        msg.arg1 = getErrorMessage(data, jsonObject);
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "userRegister()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                    data.putString("field", "no_field_errors");
                    data.putString("message", "数据解析错误。");
                }
                msg.setData(data);
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_USER_REGISTER);
        new Thread(httpRunnable).start();
    }

    public static void createStore(final HttpHandler httpHandler, final Store store) {
        HttpRunnable httpRunnable = new HttpRunnable(URL_STORES) {
            @Override
            public void run() {
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("name", store.getName());
                dataMap.put("phone", store.getPhone());
                dataMap.put("addr_detail", store.getAddress());
                dataMap.put("addr_street", store.getStreet());
                dataMap.put("addr_city", store.getCity());
                dataMap.put("addr_district", store.getDistrict());
                dataMap.put("addr_province", store.getProvince());
                dataMap.put("paycode_wec", store.getWxCode());
                dataMap.put("paycode_ali", store.getAliCode());

                byte[] urlData = httpRequest(dataMap, User.getUser(null).getToken());

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_STORE_CREATE;
                msg.arg1 = getHttpCode();

                JSONObject jsonObject;
                Bundle data = new Bundle();
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    if(msg.arg1 == HTTP_OK) {
                        store.setId(jsonObject);
                        store.setRegDate(jsonObject);
                    }
                    else{
                        msg.arg1 = getErrorMessage(data, jsonObject);
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "storeRegister()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                    data.putString("field", "no_field_errors");
                    data.putString("message", "数据解析错误。");
                }
                msg.setData(data);
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_STORE_CREATE);
        new Thread(httpRunnable).start();
    }

    public static void getSalesSummary(final HttpHandler httpHandler, final int id) {
        final User user = User.getUser(null);
        HttpRunnable httpRunnable = new HttpRunnable(URL_SELLS_SUMMARY) {
            @Override
            public void run() {
                addPathParams(user.getStore(id).getId());
                byte[] urlData = httpRequest(user.getToken());

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_GET_SELL_SUM;
                msg.arg1 = getHttpCode();

                JSONObject jsonObject;
                Bundle data = new Bundle();
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    if(HTTP_OK == msg.arg1) {
                        data.putDouble("lm_sum", jsonObject.getDouble("lm_sum"));
                        data.putDouble("lm_pro", jsonObject.getDouble("lm_pro"));
                        data.putDouble("cm_sum", jsonObject.getDouble("cm_sum"));
                        data.putDouble("cm_pro", jsonObject.getDouble("cm_pro"));
                        data.putDouble("lw_sum", jsonObject.getDouble("lw_sum"));
                        data.putDouble("cw_sum", jsonObject.getDouble("cw_sum"));
                        data.putDouble("cw_pro", jsonObject.getDouble("cw_pro"));
                        data.putDouble("yd_sum", jsonObject.getDouble("yd_sum"));
                        data.putDouble("yd_pro", jsonObject.getDouble("yd_pro"));
                        data.putDouble("td_sum", jsonObject.getDouble("td_sum"));
                        data.putDouble("td_pro", jsonObject.getDouble("td_pro"));
                        msg.setData(data);
                        msg.arg2 = id;
                    }else{
                        msg.arg1 = getErrorMessage(data, jsonObject);
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "getSalesSummary()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                    data.putString("field", "no_field_errors");
                    data.putString("message", "数据解析错误。");
                }
                msg.setData(data);
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_GET_SELL_SUM);
        new Thread(httpRunnable).start();
    }

    public static void getStockGoods(final HttpHandler httpHandler, String url,
                                     final Map<String, StockGoods> goodsList) {
        final User user = User.getUser(null);
        //请求成功，则返回LocalStorage 列表；
        HttpRunnable httpRunnable = new HttpRunnable(url) {
            @Override
            public void run() {
                byte[] urlData = httpRequest(user.getToken());

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_GET_STOCK;
                msg.arg1 = getHttpCode();

                JSONArray jsonArray;
                JSONObject jsonObject;
                Bundle data = new Bundle();
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    if(HTTP_OK == msg.arg1){
                        msg.arg2 = jsonObject.getInt("count");
                        data.putString("next", jsonObject.getString("next"));
                        data.putString("previous", jsonObject.getString("previous"));
                        if(msg.arg2 > 0) {
                            jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                StockGoods stockGoods = new StockGoods(jsonObject);
                                assert stockGoods.getBarcode() != null;
                                goodsList.put(stockGoods.getBarcode(), stockGoods);
                            }
                        }
                    }else{
                        msg.arg1 = getErrorMessage(data, jsonObject);
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "getGoodsList()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                    data.putString("field", "no_field_errors");
                    data.putString("message", "数据解析错误。");
                }
                msg.setData(data);
                httpHandler.sendMessage(msg);
            }
        };
        //httpHandler.showDlg(MSG_GET_STORAGE);
        new Thread(httpRunnable).start();
    }

    public static void getStockGoods(final HttpHandler httpHandler, final Map<String, StockGoods> goodsList,
                                     final int storeId, final Integer pageNo, final Integer pageSize) {
        final User user = User.getUser(null);
        //请求成功，则返回LocalStorage 列表；
        HttpRunnable httpRunnable = new HttpRunnable(URL_STOCKS) {
            @Override
            public void run() {
                addPathParams(storeId);
                addQueryParams("page", pageNo.toString());
                addQueryParams("size", pageSize.toString());
                byte[] urlData = httpRequest(user.getToken());

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_GET_STOCK;
                msg.arg1 = getHttpCode();

                JSONArray jsonArray;
                JSONObject jsonObject;
                Bundle data = new Bundle();
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    if(HTTP_OK == msg.arg1){
                        msg.arg2 = jsonObject.getInt("count");
                        data.putString("next", jsonObject.getString("next"));
                        data.putString("previous", jsonObject.getString("previous"));
                        if(msg.arg2 > 0) {
                            jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                StockGoods stockGoods = new StockGoods(jsonObject);
                                assert stockGoods.getBarcode() != null;
                                goodsList.put(stockGoods.getBarcode(), stockGoods);
                            }
                        }
                    }else{
                        msg.arg1 = getErrorMessage(data, jsonObject);
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "getGoodsList()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                    data.putString("field", "no_field_errors");
                    data.putString("message", "数据解析错误。");
                }
                msg.setData(data);
                httpHandler.sendMessage(msg);
            }
        };
        //httpHandler.showDlg(MSG_GET_STORAGE);
        new Thread(httpRunnable).start();
    }

    public static void addPurchase(final HttpHandler httpHandler, final Purchase record, final Float sellPrice) {
        HttpRunnable httpRunnable = new HttpRunnable(URL_PURCHASE) {
            @Override
            public void run() {
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("barcode", record.getBarcode());
                dataMap.put("supplierId", record.getSupplierId());
                dataMap.put("price", record.getPrice());
                dataMap.put("count", record.getCount());
                dataMap.put("unit", record.getUnit());
                dataMap.put("sale_price", sellPrice);
                addPathParams(record.getStoreId());

                byte[] urlData = httpRequest(dataMap, User.getUser(null).getToken());

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_ADD_PURCHASE;
                msg.arg1 = getHttpCode();

                JSONObject jsonObject;
                Bundle data = new Bundle();
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    if(HTTP_OK != msg.arg1){
                        msg.arg1 = getErrorMessage(data, jsonObject);
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "addPurchase()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                    data.putString("field", "no_field_errors");
                    data.putString("message", "数据解析错误。");
                }
                msg.setData(data);
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_ADD_PURCHASE);
        new Thread(httpRunnable).start();
    }

    public static void getGoodsCategories(final HttpHandler httpHandler) {
        final User user = User.getUser(null);

        HttpRunnable httpRunnable = new HttpRunnable(URL_GOODS_CATEGORIES) {
            @Override
            public void run() {
                byte[] urlData = httpRequest(user.getToken());

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_GET_CATEGORY;
                msg.arg1 = getHttpCode();

                Bundle data = new Bundle();
                String jsonString = new String(urlData);
                List<Categories.Category> categories = Categories.getGoodsCategories();
                categories.clear();
                //categories.add(new Categories.Category((short)-1,"-- 选择商品类别 --"));
                try {
                    if(msg.arg1 == HTTP_OK){
                        JSONArray jsonArray = new JSONArray(jsonString);
                        for(int i = 0; i < jsonArray.length(); i++) {
                            categories.add(new Categories.Category(jsonArray.getJSONObject(i)));
                        }
                    }else {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        msg.arg1 = getErrorMessage(data, jsonObject);
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "getGoodsCategories()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                    data.putString("field", "no_field_errors");
                    data.putString("message", "数据解析错误。");
                }
                msg.setData(data);
                httpHandler.sendMessage(msg);
            }
        };
        //httpHandler.showDlg(MSG_GET_CATEGORY);
        new Thread(httpRunnable).start();
    }

    public static void addGoods(final HttpHandler httpHandler, final Goods goods) {
        final User user = User.getUser(null);

        HttpRunnable httpRunnable = new HttpRunnable(URL_GOODS) {
            @Override
            public void run() {
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("name", goods.getName());
                dataMap.put("barcode", goods.getBarcode());
                dataMap.put("spec", goods.getSpec());
                dataMap.put("category", goods.getCategoryId2());
                byte[] urlData = httpRequest(dataMap, user.getToken());

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_ADD_GOODS;
                msg.arg1 = getHttpCode();

                JSONObject jsonObject;
                Bundle data = new Bundle();
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    if(HTTP_OK != msg.arg1){
                        msg.arg1 = getErrorMessage(data, jsonObject);
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "addGoods()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                    data.putString("field", "no_field_errors");
                    data.putString("message", "数据解析错误。");
                }
                msg.setData(data);
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_ADD_GOODS);
        new Thread(httpRunnable).start();
    }

    public static void clerkRegister(final HttpHandler httpHandler, final Clerk clerk) {
        final User user = User.getUser(null);

        HttpRunnable httpRunnable = new HttpRunnable(URL_UPDATE_CLERK) {
            @Override
            public void run() {
                addJsonParams("name", clerk.getName());
                addJsonParams("phone", clerk.getTel());
                addJsonParams("idCard", clerk.getIdCard());
                addJsonParams("password", clerk.getPassword());
                addJsonParams("address", clerk.getAddress());
                addJsonParams("regDate", clerk.getDate()+"T00:00:00");
                addPathParams(clerk.getStoreId());
                //addFormParams("store_id", clerk.getStore());

                byte[] urlData = httpRequest("POST", "CONTENT_TYPE_JSON");

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_REGISTER_CLERK;
                if((msg.arg1 = transHttpCode(getHttpCode())) != HTTP_OK){
                    httpHandler.sendMessage(msg);
                    return;
                }

                String errDetail;
                JSONObject jsonObject;
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    errDetail = jsonObject.getString(KEY_ERR_MSG);
                    if(HTTP_OK != (msg.arg1 = jsonObject.getInt(KEY_ERR_CODE))){
                        Log.d(GCV.D_TAG, "clerkRegister()--->Error message: " + errDetail);
                        //msg.arg1 = HTTP_NO_EXIST;
                    }else{
                        msg.arg2 = jsonObject.getInt("clerk_id");
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "clerkRegister()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                }
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_REGISTER_CLERK);
        new Thread(httpRunnable).start();
    }

    public static void clerkUpdate(final HttpHandler httpHandler, final Clerk clerk) {
        final User user = User.getUser(null);

        HttpRunnable httpRunnable = new HttpRunnable(URL_UPDATE_CLERK) {
            @Override
            public void run() {
                addJsonParams("id", clerk.getId());
                addJsonParams("name", clerk.getName());
                addJsonParams("phone", clerk.getTel());
                addJsonParams("idCard", clerk.getIdCard());
                addJsonParams("address", clerk.getAddress());
                //addJsonParams("regDate", clerk.getDate());
                addPathParams(clerk.getStoreId());
                //addFormParams("store_id", clerk.getStore());

                byte[] urlData = httpRequest("PUT", "CONTENT_TYPE_JSON");

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_UPDATE_CLERK;
                if((msg.arg1 = transHttpCode(getHttpCode())) != HTTP_OK){
                    httpHandler.sendMessage(msg);
                    return;
                }

                String errDetail;
                JSONObject jsonObject;
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    errDetail = jsonObject.getString(KEY_ERR_MSG);
                    if(HTTP_OK != (msg.arg1 = jsonObject.getInt(KEY_ERR_CODE))){
                        Log.d(GCV.D_TAG, "clerkUpdate()--->Error message: " + errDetail);
                        //msg.arg1 = HTTP_NO_EXIST;
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "clerkUpdate()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                }
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_UPDATE_CLERK);
        new Thread(httpRunnable).start();
    }

    public static void clerkDelete(final HttpHandler httpHandler, final Clerk clerk) {
        final User user = User.getUser(null);

        HttpRunnable httpRunnable = new HttpRunnable(URL_UPDATE_CLERK) {
            @Override
            public void run() {
                addPathParams(clerk.getId());
                byte[] urlData = httpRequest("DELETE", "CONTENT_TYPE_JSON");

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_DELETE_CLERK;
                if((msg.arg1 = transHttpCode(getHttpCode())) != HTTP_OK){
                    httpHandler.sendMessage(msg);
                    return;
                }

                String errDetail;
                JSONObject jsonObject;
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    errDetail = jsonObject.getString(KEY_ERR_MSG);
                    if(HTTP_OK != (msg.arg1 = jsonObject.getInt(KEY_ERR_CODE))){
                        Log.d(GCV.D_TAG, "clerkDelete()--->Error message: " + errDetail);
                        //msg.arg1 = HTTP_NO_EXIST;
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "clerkUpdate()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                }
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_DELETE_CLERK);
        new Thread(httpRunnable).start();
    }

    public static void getClerks(final HttpHandler httpHandler, final List<Clerk> clerkList) {
        final User user = User.getUser(null);
        HttpRunnable httpRunnable = new HttpRunnable(URL_USER_CLERKS) {
            @Override
            public void run() {
                byte[] urlData = httpRequest("GET", "CONTENT_TYPE_JSON");

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_GET_CLERKS_INFO;
                msg.arg1 = transHttpCode(getHttpCode());
                if(msg.arg1 != HTTP_OK){
                    httpHandler.sendMessage(msg);
                    return;
                }

                String errDetail;
                JSONObject jsonObject;
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    errDetail = jsonObject.getString(KEY_ERR_MSG);
                    if(HTTP_OK == (msg.arg1 = jsonObject.getInt(KEY_ERR_CODE))){
                        JSONArray jsonArray = jsonObject.getJSONArray("clerks");
                        for(int i = 0; i < jsonArray.length(); i++){
                            jsonObject = jsonArray.getJSONObject(i);
                            clerkList.add(new Clerk(jsonObject));
                        }
                    }else{
                        Log.d(GCV.D_TAG, "getClerks()--->Error message: " + errDetail);
                        msg.arg1 = HTTP_NO_EXIST;
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "getClerks()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                }
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_GET_CLERKS_INFO);
        new Thread(httpRunnable).start();
    }

    public static void imgDownload(final HttpHandler httpHandler, final File imgFile, final int pageNo) {
        HttpRunnable httpRunnable = new HttpRunnable(URL_GET_FILE_IMG) {
            @Override
            public void run() {
                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_DOWNLOAD_FILE;
                msg.arg2 = pageNo;
                Bundle data = new Bundle();
                data.putString("file_name", imgFile.getAbsolutePath());
                msg.setData(data);

                BufferedOutputStream bufOutputStream = null;
                try {
                    bufOutputStream = new BufferedOutputStream(new FileOutputStream(imgFile));
                }catch (IOException ioe){
                    msg.arg1 = HTTP_FILE_ERROR;
                    httpHandler.sendMessage(msg);
                    return;
                }

                addQueryParams("file_name", imgFile.getName());
                //httpDownload(bufOutputStream);
                try {
                    bufOutputStream.close();
                }catch (IOException ioe){
                    ioe.printStackTrace();
                }

                msg.arg1 = transHttpCode(getHttpCode());
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_DOWNLOAD_FILE);
        new Thread(httpRunnable).start();
    }

    public static void uploadFile(final HttpHandler httpHandler, final File uploadFile, final Integer storeId) {
        HttpRunnable httpRunnable = new HttpRunnable(URL_POST_FILE_IMG) {
            @Override
            public void run() {
                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_UPLOAD_FILE;

                BufferedInputStream bufInputStream = null;
                try {
                    bufInputStream = new BufferedInputStream(new FileInputStream(uploadFile));
                }catch (IOException ioe){
                    msg.arg1 = HTTP_FILE_ERROR;
                    httpHandler.sendMessage(msg);
                    return;
                }
                if(storeId != null)
                    addQueryParams("store_id", storeId.toString());
                byte[] urlData = null; //httpUpload(bufInputStream, uploadFile.length());
                try {
                    bufInputStream.close();
                }catch (IOException ioe){
                    ioe.printStackTrace();
                }

                if((msg.arg1 = transHttpCode(getHttpCode())) != HTTP_OK){
                    httpHandler.sendMessage(msg);
                    return;
                }

                String errMsg;
                JSONObject jsonObject;
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    errMsg = jsonObject.getString(KEY_ERR_MSG);
                    if(HTTP_OK != (msg.arg1 = jsonObject.getInt(KEY_ERR_CODE))){
                        Log.d(GCV.D_TAG, "uploadFile()--->Error message: " + errMsg);
                        msg.arg1 = HTTP_UPLOAD_FAIL;
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "uploadFile()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                    httpHandler.sendMessage(msg);
                    return;
                }
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_UPLOAD_FILE);
        new Thread(httpRunnable).start();
    }

    public static void updateStorage(final HttpHandler httpHandler, final StockGoods record, final int itemId) {
        final User user = User.getUser(null);

        HttpRunnable httpRunnable = new HttpRunnable(URL_STORE_STORAGE) {
            @Override
            public void run() {
                //addPathParams(record.getStoreId(), record.getBarcode());
                addJsonParams("price", record.getPrice());
                addJsonParams("discount", record.getDiscount());

                //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
                addJsonParams("startDate", record.getStartDate());
                addJsonParams("endDate", record.getEndDate());
                addJsonParams("editDate", record.getEditDate());

                byte[] urlData = httpRequest("PUT", "CONTENT_TYPE_JSON");

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_UPDATE_STORAGE;
                msg.arg2 = itemId;
                if((msg.arg1 = getHttpCode()) != HTTP_OK){
                    httpHandler.sendMessage(msg);
                    return;
                }

                String returnDetail;
                JSONObject jsonObject;
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    msg.arg1 = jsonObject.getInt(KEY_ERR_CODE);
                    returnDetail = jsonObject.getString(KEY_ERR_MSG);
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "updateStorage()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                    httpHandler.sendMessage(msg);
                    return;
                }

                if(msg.arg1 != HTTP_OK){
                    Log.d(GCV.D_TAG, "updateStorage()--->Error message: " + returnDetail);
                    msg.arg1 = HTTP_NO_EXIST;
                }
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_UPDATE_STORAGE);
        new Thread(httpRunnable).start();
    }

    public static void addStorage(final HttpHandler httpHandler, final StockGoods record) {
        final User user = User.getUser(null);

        HttpRunnable httpRunnable = new HttpRunnable(URL_STORE_STORAGE) {
            @Override
            public void run() {
                //addPathParams(record.getStoreId(), record.getBarcode());
                //addJsonParams("storeId", record.getStoreId());
                addJsonParams("barcode", record.getBarcode());
                //addJsonParams("count", record.getCount());
                addJsonParams("price", record.getPrice());
                addJsonParams("discount", record.getDiscount());
                //addJsonParams("editorId", record.getEditorId());

                //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
                //Timestamp date = new Timestamp(record.getStartDate());
                addJsonParams("startDate", record.getStartDate());
                //date.setTime(record.getEndDate());
                addJsonParams("endDate", record.getEndDate());
                //date.setTime(record.getEditDate());
                addJsonParams("editDate", record.getEditDate());

                byte[] urlData = httpRequest("POST", "CONTENT_TYPE_JSON");

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_ADD_STORAGE;
                if((msg.arg1 = getHttpCode()) != HTTP_OK){
                    httpHandler.sendMessage(msg);
                    return;
                }

                String returnDetail;
                JSONObject jsonObject;
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    msg.arg1 = jsonObject.getInt(KEY_ERR_CODE);
                    returnDetail = jsonObject.getString(KEY_ERR_MSG);
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "addStorage()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                    httpHandler.sendMessage(msg);
                    return;
                }

                if(msg.arg1 != HTTP_OK){
                    Log.d(GCV.D_TAG, "addStorage()--->Error message: " + returnDetail);
                    msg.arg1 = HTTP_NO_EXIST;
                }
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_ADD_STORAGE);
        new Thread(httpRunnable).start();
    }

    public static void getStorage(final HttpHandler httpHandler, final Integer storeId, final String barcode) {
        final User user = User.getUser(null);
        //请求成功，则返回 DetailStorage；
        HttpRunnable httpRunnable = new HttpRunnable(URL_STORE_STORAGE) {
            @Override
            public void run() {
                addPathParams(storeId, barcode);
                byte[] urlData = httpRequest("GET", "");

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_GET_STOCK;
                if((msg.arg1 = getHttpCode()) != HTTP_OK){
                    httpHandler.sendMessage(msg);
                    return;
                }

                String returnDetail;
                JSONObject jsonObject;
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    returnDetail = jsonObject.getString(KEY_ERR_MSG);
                    if(HTTP_OK == (msg.arg1 = jsonObject.getInt(KEY_ERR_CODE))){
                        jsonObject = jsonObject.getJSONObject("result");
                        StockGoods storage = new StockGoods(jsonObject);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("goods_input", storage);
                        msg.setData(bundle);
                    }else{
                        Log.d(GCV.D_TAG, "getStorage()--->Error message: " + returnDetail);
                        msg.arg1 = HTTP_NO_EXIST;
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "getStorage()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                }
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_GET_STOCK);
        new Thread(httpRunnable).start();
    }

    public static void getSaleSumByDay(final HttpHandler httpHandler, final int id,
                                       final long startTime, final long endTime) {
        final User user = User.getUser(null);

        HttpRunnable httpRunnable = new HttpRunnable(URL_GET_SELLS_DAYSUM ) {
            @Override
            public void run() {
                Integer storeId = user.getStore(id).getId();
                addQueryParams("store_id", storeId.toString());
                addQueryParams("start", Long.valueOf(startTime).toString());
                addQueryParams("end", Long.valueOf(endTime).toString());

                byte[] urlData = httpRequest("GET", "CONTENT_TYPE_JSON");

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_GET_SELL_SUM_BY_DAY;
                if((msg.arg1 = transHttpCode(getHttpCode())) != HTTP_OK){
                    httpHandler.sendMessage(msg);
                    return;
                }

                String errMsg;
                JSONObject jsonObject;
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    errMsg = jsonObject.getString(KEY_ERR_MSG);
                    if(HTTP_OK != (msg.arg1 = jsonObject.getInt(KEY_ERR_CODE))){
                        msg.arg2 = id;
                        httpHandler.sendMessage(msg);
                        Log.d(GCV.D_TAG, "getSaleSumByDay()--->" + errMsg);
                        return;
                    }
                    msg.arg2 = jsonObject.getInt("count");
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "getSaleSumByDay()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                    msg.arg2 = id;
                    httpHandler.sendMessage(msg);
                    return;
                }

                long date[] = new long[msg.arg2];
                double sum[] = new double[msg.arg2];
                double cost[] = new double[msg.arg2];
                Bundle bundle = new Bundle();
                JSONArray jsonArray;
                try {
                    jsonArray = jsonObject.getJSONArray("sum_list");
                    for (int i = 0; i < msg.arg2; i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        date[i] = jsonObject.getLong("date");
                        sum[i] = jsonObject.getDouble("sum");
                        cost[i] = jsonObject.getDouble("cost");
                    }
                }catch (JSONException jae){
                    Log.d(GCV.D_TAG, "getSaleSumByDay()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                }
                bundle.putLongArray("date", date);
                bundle.putDoubleArray("sum", sum);
                bundle.putDoubleArray("cost", cost);
                msg.setData(bundle);
                msg.arg2 = id;
                httpHandler.sendMessage(msg);
            }
        };
        //httpHandler.showDlg(MSG_GET_SELL_SUM_BY_DAY);
        new Thread(httpRunnable).start();
    }

    public static void getSalesDetail(final HttpHandler httpHandler, final List<SalesRecord> saleList, final Integer storeId,
                                      final Integer pageNo, final Integer pageSize, final Long startTime, final Long endTime) {
        final User user = User.getUser(null);

        HttpRunnable httpRunnable = new HttpRunnable(URL_GET_SELLS_DETAIL ) {
            @Override
            public void run() {
                addQueryParams("id", storeId.toString());
                addQueryParams("No.", pageNo.toString());
                addQueryParams("size", pageSize.toString());
                addQueryParams("start", startTime.toString());
                addQueryParams("end", endTime.toString());

                byte[] urlData = httpRequest("GET", "CONTENT_TYPE_JSON");

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_GET_SELL_DETAIL;
                if((msg.arg1 = transHttpCode(getHttpCode())) != HTTP_OK){
                    httpHandler.sendMessage(msg);
                    return;
                }

                String errDetail;
                JSONObject jsonObject;
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    errDetail = jsonObject.getString(KEY_ERR_MSG);
                    if(HTTP_OK != (msg.arg1 = jsonObject.getInt(KEY_ERR_CODE))){
                        Log.d(GCV.D_TAG, "getSalesDetail()--->Error message: " + errDetail);
                        httpHandler.sendMessage(msg);
                        return;
                    }
                    JSONArray jsonArray = jsonObject.getJSONArray("sales");
                    msg.arg2 = jsonArray.length();
                    for(int i = 0; i < msg.arg2; i++){
                        saleList.add(new SalesRecord(jsonArray.getJSONObject(i)));
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "getSalesSummary()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                    httpHandler.sendMessage(msg);
                    return;
                }
                httpHandler.sendMessage(msg);
            }
        };
        //httpHandler.showDlg(MSG_GET_SELL_DETAIL);
        new Thread(httpRunnable).start();
    }

    public static void addSell(final HttpHandler httpHandler, final Sales record) {
        final User user = User.getUser(null);

        HttpRunnable httpRunnable = new HttpRunnable(URL_ADD_SELL ) {
            @Override
            public void run() {
                addJsonParams("storeId", record.getStoreId());
                addJsonParams("barcode", record.getBarcode());
                //addJsonParams("sellerId", record.getSellerId());
                addJsonParams("sn", record.getSellSn());
                addJsonParams("amount", record.getSum());
                addJsonParams("quantity", record.getCount());
                addJsonParams("price", record.getPrice());
                addJsonParams("status", record.getStatus());
                addJsonParams("discount", record.getDiscount());

                byte[] urlData = httpRequest("POST", "CONTENT_TYPE_JSON");

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_ADD_SELL;
                if((msg.arg1 = transHttpCode(getHttpCode())) != HTTP_OK){
                    //上传失败，返回记录作本地缓存；
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("sell_record",record);
                    msg.setData(bundle);
                    httpHandler.sendMessage(msg);
                    return;
                }

                String errMsg;
                JSONObject jsonObject;
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    errMsg = jsonObject.getString(KEY_ERR_MSG);
                    msg.arg1 = jsonObject.getInt(KEY_ERR_CODE);
                } catch (JSONException je) {
                    errMsg = "addSellRecord()--->output data error: " + jsonString;
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                }

                if(msg.arg1 != HTTP_OK){
                    //上传失败，返回记录作本地缓存；
                    Log.d(GCV.D_TAG, "addSellRecord()--->Error message: " + errMsg);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("sell_record",record);
                    msg.setData(bundle);
                }
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_ADD_SELL);
        new Thread(httpRunnable).start();
    }

    public static void uploadSell(final HttpHandler httpHandler, final Sales record) {
        final User user = User.getUser(null);

        HttpRunnable httpRunnable = new HttpRunnable(URL_ADD_SELL) {
            @Override
            public void run() {
                addJsonParams("storeId", record.getStoreId());
                addJsonParams("barcode", record.getBarcode());
                addJsonParams("sn", record.getSellSn());
                addJsonParams("amount", record.getSum());
                addJsonParams("quantity", record.getCount());
                addJsonParams("price", record.getPrice());
                addJsonParams("status", record.getStatus());
                addJsonParams("discount", record.getDiscount());

                byte[] urlData = httpRequest("POST", "CONTENT_TYPE_JSON");

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_UPLOAD_SELL;
                if((msg.arg1 = transHttpCode(getHttpCode())) != HTTP_OK){
                    httpHandler.sendMessage(msg);
                    return;
                }

                String errMsg;
                JSONObject jsonObject;
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    errMsg = jsonObject.getString(KEY_ERR_MSG);
                    msg.arg1 = jsonObject.getInt(KEY_ERR_CODE);
                } catch (JSONException je) {
                    errMsg = "output data error = " + jsonString;
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                }

                if(msg.arg1 != HTTP_OK) {
                    Log.d(GCV.D_TAG, "addSellRecord()--->Error message: " + errMsg);
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("sell_record",record);
                    msg.setData(bundle);
                }

                httpHandler.sendMessage(msg);
            }
        };
        //httpHandler.showDlg(MSG_UPLOAD_SELL);
        new Thread(httpRunnable).start();
    }

    public static void getGoods(final HttpHandler httpHandler, final String barcode) {
        final User user = User.getUser(null);
        HttpRunnable httpRunnable = new HttpRunnable(URL_GET_GOODS_BARCODE) {
            @Override
            public void run() {
                addPathParams(barcode);
                byte[] urlData = httpRequest("GET", "CONTENT_TYPE_JSON");

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_GET_GOODS;
                if((msg.arg1 = getHttpCode()) != HTTP_OK){
                    httpHandler.sendMessage(msg);
                    return;
                }

                String returnDetail;
                JSONObject jsonObject;
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    returnDetail = jsonObject.getString(KEY_ERR_MSG);
                    if(HTTP_OK == (msg.arg1 = jsonObject.getInt(KEY_ERR_CODE))){
                        Bundle bundle = new Bundle();
                        jsonObject = jsonObject.getJSONObject("goods");
                        bundle.putSerializable("goods", new Goods(jsonObject));
                        msg.setData(bundle);
                    }else{
                        Log.d(GCV.D_TAG, "getGoods()--->Error message: " + returnDetail);
                        msg.arg1 = HTTP_NO_EXIST;
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "getGoods()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                }
                httpHandler.sendMessage(msg);
            }
        };
        new Thread(httpRunnable).start();
    }

    public static void userUpdate(final HttpHandler httpHandler, final Map<String, Object> dataMaps) {
        final User user = User.getUser(null);

        HttpRunnable httpRunnable = new HttpRunnable(URL_USER) {
            @Override
            public void run() {
                addFormParams("pwd", dataMaps.get("pwd"));
                addFormParams("tel", dataMaps.get("tel"));
                addFormParams("name", dataMaps.get("name"));
                addFormParams("idcard", dataMaps.get("idcard"));
                addFormParams("email", dataMaps.get("email"));

                byte[] urlData = httpRequest("PUT", CONTENT_TYPE_FORM);

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_UPDATE_USER;
                if((msg.arg1 = transHttpCode(getHttpCode())) != HTTP_OK){
                    httpHandler.sendMessage(msg);
                    return;
                }

                String errDetail;
                JSONObject jsonObject;
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    errDetail = jsonObject.getString(KEY_ERR_MSG);
                    if(HTTP_OK != (msg.arg1 = jsonObject.getInt(KEY_ERR_CODE))){
                        Log.d(GCV.D_TAG, "userUpdate()--->Error message: " + errDetail);
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "userUpdate()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                }
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_UPDATE_USER);
        new Thread(httpRunnable).start();
    }

    public static void storeUpdate(final HttpHandler httpHandler, final Store store) {
        final User user = User.getUser(null);

        HttpRunnable httpRunnable = new HttpRunnable(URL_STORES ) {
            @Override
            public void run() {
                addJsonParams("name", store.getName());
                addJsonParams("phone", store.getPhone());
                addJsonParams("addrDetail", store.getAddress());
                addJsonParams("addrStreet", store.getStreet());
                addJsonParams("addrDistrict", store.getDistrict());
                addJsonParams("addrCity", store.getCity());
                addJsonParams("addrProvince", store.getProvince());
                //addJsonParams("categoryId", store.getCategory());
                addJsonParams("codeWx", store.getWxCode());
                addJsonParams("codeAli", store.getAliCode());
                addJsonParams("id", store.getId());

                byte[] urlData = httpRequest("PUT", "CONTENT_TYPE_JSON");

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_STORE_UPDATE;
                if((msg.arg1 = transHttpCode(getHttpCode())) != HTTP_OK){
                    httpHandler.sendMessage(msg);
                    return;
                }

                String errMsg;
                JSONObject jsonObject;
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    errMsg = jsonObject.getString(KEY_ERR_MSG);
                    if(HTTP_OK != (msg.arg1 = jsonObject.getInt(KEY_ERR_CODE))){
                        Log.d(GCV.D_TAG, "storeUpdate()--->error: " + errMsg);
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "storeUpdate()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                }
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_STORE_UPDATE);
        new Thread(httpRunnable).start();
    }

    public static void resetPassword(final HttpHandler httpHandler, final String phoneNum,
                                     final String password, final String authCode) {
        final User user = User.getUser(null);

        HttpRunnable httpRunnable = new HttpRunnable(URL_UPDATE_PASSWORD ) {
            @Override
            public void run() {
                addFormParams("tel", phoneNum);
                addFormParams("pwd", password);
                addFormParams("code", authCode);

                byte[] urlData = httpRequest("POST", CONTENT_TYPE_FORM);
                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_RESET_PWD;
                if((msg.arg1 = transHttpCode(getHttpCode())) != HTTP_OK){
                    httpHandler.sendMessage(msg);
                    return;
                }

                String msgDetail;
                JSONObject jsonObject;
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    msg.arg1 = jsonObject.getInt(KEY_ERR_CODE);
                    msgDetail = jsonObject.getString(KEY_ERR_MSG);
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "resetPassword()--->output data error:" + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                    return;
                }

                Bundle data = new Bundle();
                data.putString("msgDetail", msgDetail);
                msg.setData(data);
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_RESET_PWD);
        new Thread(httpRunnable).start();
    }

    public static void modifyPassword(final HttpHandler httpHandler, final String newPassword, final String oldPassword) {
        final User user = User.getUser(null);

        HttpRunnable httpRunnable = new HttpRunnable(URL_UPDATE_PASSWORD) {
            @Override
            public void run() {
                addFormParams("n_pwd", newPassword);
                addFormParams("o_pwd", oldPassword);

                byte[] urlData = httpRequest("PUT", CONTENT_TYPE_FORM);

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_MODIFY_PWD;
                if((msg.arg1 = transHttpCode(getHttpCode())) != HTTP_OK){
                    httpHandler.sendMessage(msg);
                    return;
                }

                String msgDetail;
                JSONObject jsonObject;
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    msg.arg1 = jsonObject.getInt(KEY_ERR_CODE);
                    msgDetail = jsonObject.getString(KEY_ERR_MSG);
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "modifyPassword()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                    httpHandler.sendMessage(msg);
                    return;
                }
                if(msg.arg1 != HTTP_OK)
                    Log.d(GCV.D_TAG, "modifyPassword()--->Error code: " + msg.arg1 + ", error message: " + msgDetail);
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_MODIFY_PWD);
        new Thread(httpRunnable).start();
    }

    public static void getStoreCategories(final HttpHandler httpHandler) {
        final User user = User.getUser(null);

        HttpRunnable httpRunnable = new HttpRunnable(URL_GET_SHOP_TYPE) {
            @Override
            public void run() {
                byte[] urlData = httpRequest("GET", "CONTENT_TYPE_JSON");

                Message msg = httpHandler.obtainMessage();

                msg.what = MSG_GET_CATEGORY;
                if((msg.arg1 = getHttpCode()) != HTTP_OK){
                    httpHandler.sendMessage(msg);
                    return;
                }

                String msgDetail;
                JSONArray jsonArray;
                JSONObject jsonObject;
                String jsonString = new String(urlData);
                List<Categories.Category> categories = Categories.getStoreCategories();
                categories.clear();
                categories.add(new Categories.Category((short)-1,"--选择店铺类型--"));

                try {
                    jsonObject = new JSONObject(jsonString);
                    msgDetail = jsonObject.getString(KEY_ERR_MSG);
                    if((msg.arg1 = jsonObject.getInt(KEY_ERR_CODE)) == HTTP_OK){
                        jsonArray = jsonObject.getJSONArray("store_category");
                        for(int i = 0; i < jsonArray.length(); i++) {
                            categories.add(new Categories.Category(jsonArray.getJSONObject(i)));
                        }
                    }else {
                        Log.d(GCV.D_TAG, "getShopType()--->Error message: " + msgDetail);
                        msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "getShopType()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                    httpHandler.sendMessage(msg);
                    return;
                }
                httpHandler.sendMessage(msg);
            }
        };
        //httpHandler.showDlg(MSG_GET_CATEGORY);
        new Thread(httpRunnable).start();
    }

    public static void getUserInfo(final HttpHandler httpHandler) {
        final User user = User.getUser(null);

        HttpRunnable httpRunnable = new HttpRunnable(URL_GET_USERINFO) {
            @Override
            public void run() {
                byte[] returnData = httpRequest("GET", user.getToken());

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_GET_USERINFO;
                msg.arg1 = getHttpCode();

                if( msg.arg1 != HTTP_OK) {
                    httpHandler.sendMessage(msg);
                    return;
                }

                String returnDetail = "";
                JSONObject jsonObject;
                String jsonString = new String(returnData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    msg.arg1 = jsonObject.getInt("result");
                    //returnDetail = jsonObject.getString("detail");
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "getUserInfo()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                    httpHandler.sendMessage(msg);
                    return;
                }
                if(msg.arg1 != HTTP_OK){
                    Log.d(GCV.D_TAG, "getUserInfo()--->Error code: " + msg.arg1 + ", error message: " + returnDetail);
                    msg.arg1 = HTTP_NO_EXIST;
                } else {
                    //user.setStaffStatus(jsonObject);
                    user.setEmail(jsonObject);
                    user.setIdCard(jsonObject);
                    user.setUserName(jsonObject);
                }
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_GET_USERINFO);
        new Thread(httpRunnable).start();
    }

    public static void updateSupplier(final HttpHandler httpHandler, final Supplier supplier) {
        final User user = User.getUser(null);

        HttpRunnable httpRunnable = new HttpRunnable(URL_SUPPLIER ) {
            @Override
            public void run() {
                addJsonParams("id", supplier.getId());
                addJsonParams("storeId", supplier.getShopId());
                addJsonParams("name", supplier.getName());
                addJsonParams("addr", supplier.getAddr());
                addJsonParams("phone", supplier.getPhone());
                addJsonParams("contacter", supplier.getContacter());

                byte[] urlData = httpRequest("PUT", "CONTENT_TYPE_JSON");

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_UPDATE_SUPPLIER;
                if((msg.arg1 = transHttpCode(getHttpCode())) != HTTP_OK){
                    httpHandler.sendMessage(msg);
                    return;
                }

                String errMsg;
                JSONObject jsonObject;
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    errMsg = jsonObject.getString("err_msg");
                    if((msg.arg1 = jsonObject.getInt("err_code")) != HTTP_OK){
                        Log.d(GCV.D_TAG, "updateSupplier()--->Error message: " + errMsg);
                    }else{
                        Bundle data = new Bundle();
                        data.putSerializable("supplier", supplier);
                        msg.setData(data);
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "updateSupplier()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                }
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_UPDATE_SUPPLIER);
        new Thread(httpRunnable).start();
    }

    public static void getSuppliers(final HttpHandler httpHandler, final List<Supplier> supplierList, final Integer storeId) {
        HttpRunnable httpRunnable = new HttpRunnable(URL_SUPPLIER) {
            @Override
            public void run() {
                addQueryParams("store_id", storeId.toString());
                byte[] urlData = httpRequest("GET", "CONTENT_TYPE_JSON");

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_GET_SUPPLIER;
                if((msg.arg1 = getHttpCode()) != HTTP_OK){
                    httpHandler.sendMessage(msg);
                    return;
                }

                String msgDetail;
                JSONArray jsonArray;
                JSONObject jsonObject;
                supplierList.clear();
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    msgDetail = jsonObject.getString(KEY_ERR_MSG);
                    if((msg.arg1 = jsonObject.getInt(KEY_ERR_CODE)) == HTTP_OK) {
                        jsonArray = jsonObject.getJSONArray("supplier");
                        for(int i = 0; i < jsonArray.length(); i++){
                            supplierList.add(new Supplier(jsonArray.getJSONObject(i)));
                        }
                    }else {
                        Log.d(GCV.D_TAG, "getSuppliers()--->Error message: " + msgDetail);
                        msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "getSuppliers()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                }
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_GET_SUPPLIER);
        new Thread(httpRunnable).start();
    }

    public static void addSupplier(final HttpHandler httpHandler, final Supplier supplier) {
        final User user = User.getUser(null);

        HttpRunnable httpRunnable = new HttpRunnable(URL_SUPPLIER) {
            @Override
            public void run() {
                addJsonParams("storeId", supplier.getShopId());
                addJsonParams("name", supplier.getName());
                addJsonParams("addr", supplier.getAddr());
                addJsonParams("phone", supplier.getPhone());
                addJsonParams("contacter", supplier.getContacter());

                byte[] urlData = httpRequest("POST", "CONTENT_TYPE_JSON");

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_ADD_SUPPLIER;
                if((msg.arg1 = transHttpCode(getHttpCode())) != HTTP_OK){
                    httpHandler.sendMessage(msg);
                    return;
                }

                String errMsg;
                JSONObject jsonObject;
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    errMsg = jsonObject.getString("err_msg");
                    if(HTTP_OK == (msg.arg1 = jsonObject.getInt("err_code"))){
                        supplier.setId(jsonObject.getInt("id"));
                        Bundle data = new Bundle();
                        data.putSerializable("supplier", supplier);
                        msg.setData(data);
                    }else{
                        Log.d(GCV.D_TAG, "updateSupplier()--->Error message: " + errMsg);
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "updateSupplier()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                }
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_ADD_SUPPLIER);
        new Thread(httpRunnable).start();
    }

    public static void deleteSupplier(final HttpHandler httpHandler, final Integer id) {
        final User user = User.getUser(null);

        HttpRunnable httpRunnable = new HttpRunnable(URL_SUPPLIER) {
            @Override
            public void run() {
                addQueryParams("id", id.toString());

                byte[] urlData = httpRequest("DELETE", "CONTENT_TYPE_JSON");

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_DEL_SUPPLIER;
                if((msg.arg1 = transHttpCode(getHttpCode())) != HTTP_OK){
                    httpHandler.sendMessage(msg);
                    return;
                }

                String errMsg;
                JSONObject jsonObject;
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    errMsg = jsonObject.getString("err_msg");
                    if(HTTP_OK == (msg.arg1 = jsonObject.getInt("err_code"))){
                        msg.arg2 = id;
                    }else{
                        Log.d(GCV.D_TAG, "updateSupplier()--->Error message: " + errMsg);
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "updateSupplier()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                }
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_DEL_SUPPLIER);
        new Thread(httpRunnable).start();
    }

    public static void getPurchaseRds(final HttpHandler httpHandler, final List<Purchase> purchaseRds,
                        final Integer storeId, final Integer pageNo, final Integer pageSize,
                        final Long startDate, final Long endDate) {
        final User user = User.getUser(null);
        HttpRunnable httpRunnable = new HttpRunnable(URL_GET_PURCHASE ) {
            @Override
            public void run() {
                addPathParams(pageNo);
                addQueryParams("store_id", storeId.toString());
                addQueryParams("page_size", pageSize.toString());
                addQueryParams("start_time", startDate.toString());
                addQueryParams("end_time", endDate.toString());

                byte[] urlData = httpRequest("GET", "CONTENT_TYPE_JSON");

                Message msg = httpHandler.obtainMessage();
                msg.what = MSG_GET_PURCHASE;
                if((msg.arg1 = transHttpCode(getHttpCode())) != HTTP_OK){
                    httpHandler.sendMessage(msg);
                    return;
                }

                String errMsg;
                JSONObject jsonObject;
                JSONArray jsonArray;
                String jsonString = new String(urlData);
                try {
                    jsonObject = new JSONObject(jsonString);
                    errMsg = jsonObject.getString(KEY_ERR_MSG);
                    if(HTTP_OK == (msg.arg1 = jsonObject.getInt(KEY_ERR_CODE))){
                        msg.arg2 = jsonObject.getInt("page_no");
                        int count = jsonObject.getInt("page_size");
                        if(pageSize == count)
                            msg.arg1 = HTTP_CONTINUE;
                        jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < count; i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            purchaseRds.add(new Purchase(jsonObject));
                        }
                    }else{
                        Log.d(GCV.D_TAG, "getPurchaseRds()--->Error message: " + errMsg);
                    }
                } catch (JSONException je) {
                    Log.d(GCV.D_TAG, "getPurchaseRds()--->output data error: " + jsonString);
                    msg.arg1 = HttpRunnable.HTTP_RESP_ERROR;
                }
                httpHandler.sendMessage(msg);
            }
        };
        httpHandler.showDlg(MSG_GET_PURCHASE);
        new Thread(httpRunnable).start();
    }

}
