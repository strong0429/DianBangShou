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
    private String mMobile;
    private String mWechat;
    private String mEmail;
    private String mPhoto;

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
        mToken = null;

        SharedPreferences preferences = mContext.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        mPassword = preferences.getString("password", "");
        mUserName = preferences.getString("username", "");
        mAutoLogin = preferences.getBoolean("rem_passwd", false);
        mPhoto = preferences.getString("photo", null);

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
        editor.putString("photo", mPhoto);

        //editor.commit();
        editor.apply();
    }

    public void setUser(JSONObject jsonObject){
        setRegDate(jsonObject);
        setUserName(jsonObject);
        setFirstName(jsonObject);
        setIdCard(jsonObject);
        setMobile(jsonObject);
        setEmail(jsonObject);
        setWechat(jsonObject);
        setPhoto(jsonObject);
    }

    public List<DetailStorage> getGoodsList(){
        return mGoodsList;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public void setPhoto(String photo) {
        this.mPhoto = photo;
    }

    public void setPhoto(JSONObject jsonObject){
        try {
            this.mPhoto = jsonObject.getString("photo");
        }catch (JSONException je){
            je.printStackTrace();
        }
    }

    public String getWechat() {
        return mWechat;
    }

    public String getMobile(){
        return mMobile;
    }

    public void setMobile(String mobile){
        mMobile = mobile;
    }

    public void setMobile(JSONObject jsonObject){
        try{
            mMobile = jsonObject.getString("mobile");
        }catch (JSONException je){
            mMobile = null;
        }
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
            mIdCard = jsonObject.getString("id_card");
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

    public void setRole(byte role){

    }

    public int getStaffStatus(){
        return 0;
    }
}
