package com.xingdhl.www.storehelper.ObjectDefine;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Strong on 17/10/30.
 *
 */

public class User {
    private static User sUser;
    private Context mContext;

    private String mPhoneNum;
    private String mPassword;
    private String mUserName;
    private String mFirstName;
    private String mIdCard;
    private String mRegDate;
    private String mWechat;
    private String mEmail;
    //private String mAddr;

    //private int mPhotoId;
    private boolean mAutoLogin;

    private List<Store> mStores;
    private List<UserGroup> mUserGroups;
    private List<UserPermission> mUserPermissions;

    private List<DetailStorage> mGoodsList;

    private String mToken;

    private Map<String, Object> mMap;

    public void putData(String name, Object value){
        mMap.put(name, value);
    }

    public Object getData(String name){
        Object data = mMap.get(name);
        mMap.remove(name);
        return data;
    }

    private User(Context context) {
        mContext = context;

        SharedPreferences preferences = mContext.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        mPassword = preferences.getString("password", "");
        mUserName = preferences.getString("username", "");
        mAutoLogin = preferences.getBoolean("rem_passwd", false);
        //mPhotoId = preferences.getInt("photo_id", -1);

        mMap = new HashMap<String, Object>();
        mStores = new ArrayList<>();
        mGoodsList = new ArrayList<>();
        mUserGroups = new ArrayList<>();
        mUserPermissions = new ArrayList<>();
    }

    public static User getUser(Context context) {
        if (sUser == null) {
            sUser = new User(context);
        }
        return sUser;
    }

    public void saveInfo() {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("user_info", Context.MODE_PRIVATE).edit();
        editor.putString("password", mPassword);
        editor.putString("username", mUserName);
        editor.putBoolean("rem_passwd", mAutoLogin);
        //editor.putInt("photo_id", mPhotoId);

        //editor.commit();
        editor.apply();
    }

    public void setUser(JSONObject jsonObject){
        setRegDate(jsonObject);
        setUserName(jsonObject);
        setFirstName(jsonObject);
        setIdCard(jsonObject);
        setEmail(jsonObject);
        setWechat(jsonObject);
        //setAddr(jsonObject);
        //setPhotoId(jsonObject);
    }

    public List<DetailStorage> getGoodsList(){
        return mGoodsList;
    }
    /*
    public String getAddr() {
        return mAddr;
    }

    public void setAddr(String addr) {
        mAddr = addr;
    }

    public void setAddr(JSONObject jsonObject) {
        try{
            mAddr = jsonObject.getString("address");
        }catch (JSONException je) {
            mAddr = null;
        }
    }
    */
    public String getWechat() {
        return mWechat;
    }

    public void setWechat(String wechat) {
        mWechat = wechat;
    }

    public void setWechat(JSONObject jsonObject) {
        try{
            mWechat = jsonObject.getString("wechat");
        }catch (JSONException je) {
            mWechat = null;
        }
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public void setFirstName(JSONObject jsonObject) {
        try{
            mFirstName = jsonObject.getString("first_name");
        }catch (JSONException je) {
            mFirstName = null;
        }
    }
    /*
    public int getPhotoId() {
        return mPhotoId;
    }

    public void setPhotoId(int photoId) {
        mPhotoId = photoId;
    }

    public void setPhotoId(JSONObject jsonObject) {
        try{
            mPhotoId = jsonObject.getInt("photoId");
        }catch (JSONException je) {
            mPhotoId = -1;
        }
    }
    */
    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = "JWT " + token;
    }

    public String getRegDate() {
        return mRegDate;
    }

    public void setRegDate(String regDate) {
        mRegDate = regDate;
    }

    public void setRegDate(JSONObject jsonObject) {
        try{
            mRegDate = jsonObject.getString("date_joined");
        }catch (JSONException je) {
            mRegDate = null;
        }
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public void setEmail(JSONObject jsonObject) {
        try{
            mEmail = jsonObject.getString("email");
        }catch (JSONException je) {
            mEmail = null;
        }
    }

    public String getIdCard() {
        return mIdCard;
    }

    public void setIdCard(String idCard) {
        mIdCard = idCard;
    }

    public void setIdCard(JSONObject jsonObject) {
        try{
            mIdCard = jsonObject.getString("idCard");
        }catch (JSONException je) {
            mIdCard = null;
        }
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String name) {
        mUserName = name;
    }

    public void setUserName(JSONObject jsonObject) {
        try{
            mUserName = jsonObject.getString("username");
        }catch (JSONException je) {
            mUserName = null;
        }
    }

    public Store getStore(int id) {
        return mStores.get(id);
    }

    public List<Store> getStores(){
        return mStores;
    }

    public void addStore(Store store){
        mStores.add(store);
    }

    public String getPhoneNum() {
        return mPhoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        mPhoneNum = phoneNum;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public boolean isAutoLogin() {
        return mAutoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        mAutoLogin = autoLogin;
    }
}
