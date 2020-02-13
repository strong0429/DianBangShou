package com.xingdhl.www.storehelper.ObjectDefine;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Leeyc on 2018/2/21.
 *
 */

public class Clerk implements Serializable{
    private int mClerkId;
    private int mStoreId;
    //private String mStoreName;
    private String mName;
    private String mTel;
    private String mDate;
    private String mIdCard;
    private String mAddress;
    private String mPassword;

    public Clerk(){
        mClerkId = 0;
    }

    public Clerk(JSONObject jsonObject){
        try{
            mClerkId = jsonObject.getInt("clerkId");
            mStoreId = jsonObject.getInt("storeId");
        }catch (JSONException je){
            je.printStackTrace();
        }
        /*
        try {
            JSONObject jsonStore = jsonObject.getJSONObject("store");
            mStoreName = jsonStore.getString("name");
        }catch (JSONException je){
            je.printStackTrace();
        }
        */
        try{
            JSONObject jsonUser = jsonObject.getJSONObject("user");
            mName = jsonUser.getString("name");
            mTel = jsonUser.getString("phone");
            mIdCard = jsonUser.getString("idCard");
            mDate = jsonUser.getString("regDate").substring(0, 10);
            mAddress = jsonUser.getString("address");
        }catch (JSONException je){
            je.printStackTrace();
        }
        mPassword = "********";
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public int getId() {
        return mClerkId;
    }

    public void setId(int id){
        this.mClerkId = id;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getIdCard() {

        return mIdCard;
    }

    public void setIdCard(String idCard) {
        mIdCard = idCard;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getTel() {

        return mTel;
    }

    public void setTel(String tel) {
        mTel = tel;
    }

    public String getName() {

        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getStoreId() {
        return mStoreId;
    }

    public void setStoreId(int storeId) {
        mStoreId = storeId;
    }
/*
    public String getStoreName() {
        return mStoreName;
    }

    public void setStoreName(String storeName) {
        mStoreName = storeName;
    }
    */
}
