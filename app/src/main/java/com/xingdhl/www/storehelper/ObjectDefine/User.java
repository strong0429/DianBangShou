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
    private String mName;
    private String mNickName;
    private String mIdCard;
    private String mRegDate;
    private String mWechat;
    private String mEmail;
    private String mAddr;

    private int mPhotoId;
    private byte mRole;

    private String mToken;
    private boolean mAutoLogin;

    private List<Store> mStores;
    private List<DetailStorage> mGoodsList;

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
        mPhoneNum = preferences.getString("phone_num", "");
        mAutoLogin = preferences.getBoolean("rem_passwd", false);
        mPhotoId = preferences.getInt("photo_id", -1);

        mMap = new HashMap<String, Object>();
        mStores = new ArrayList<>();
        mGoodsList = new ArrayList<>();
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
        editor.putString("phone_num", mPhoneNum);
        editor.putBoolean("rem_passwd", mAutoLogin);
        editor.putInt("photo_id", mPhotoId);

        editor.commit();
    }

    public void setUser(JSONObject jsonObject){
        setRole(jsonObject);
        setRegDate(jsonObject);
        setName(jsonObject);
        setNickName(jsonObject);
        setIdCard(jsonObject);
        setEmail(jsonObject);
        setWechat(jsonObject);
        setAddr(jsonObject);
        setPhotoId(jsonObject);
    }

    public List<DetailStorage> getGoodsList(){
        return mGoodsList;
    }

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

    public String getNickName() {
        return mNickName;
    }

    public void setNickName(String nickName) {
        mNickName = nickName;
    }

    public void setNickName(JSONObject jsonObject) {
        try{
            mNickName = jsonObject.getString("nickName");
        }catch (JSONException je) {
            mNickName = null;
        }
    }

    public int getPhotoId() {
        return mPhotoId;
    }

    public void setPhotoId(int photoId) {
        mPhotoId = photoId;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public String getRegDate() {
        return mRegDate;
    }

    public void setRegDate(String regDate) {
        mRegDate = regDate;
    }

    public void setRegDate(JSONObject jsonObject) {
        try{
            mRegDate = jsonObject.getString("regDate");
        }catch (JSONException je) {
            mRegDate = null;
        }
    }

    public void setPhotoId(JSONObject jsonObject) {
        try{
            mPhotoId = jsonObject.getInt("photoId");
        }catch (JSONException je) {
            mPhotoId = -1;
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

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setName(JSONObject jsonObject) {
        try{
            mName = jsonObject.getString("name");
        }catch (JSONException je) {
            mName = null;
        }
    }

    public Store getStore(int id) {
        return mStores.get(id);
    }

    public List<Store> getStores(){
        return mStores;
    }

    public void addShop(Store shop){
        mStores.add(shop);
    }

    public byte getRole() {
        return mRole;
    }

    public void setRole(byte role) {
        mRole = role;
    }

    public void setRole(JSONObject jsonObject) {
        try{
            mRole = (byte)jsonObject.getInt("role");
        }catch (JSONException je) {
            mRole = -1;
        }
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
