package com.xingdhl.www.storehelper.ObjectDefine;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Strong on 17/11/15.
 *
 */

public class Store {
    private String mName;
    private String mPhone;
    private String mProvince;
    private String mCity;
    private String mDistrict;
    private String mStreet;
    private String mAddress;
    private String mPayDate;
    private String mRegDate;
    private String mWxCode;
    private String mAliCode;

    private int mId;
    private byte mPhoto;
    private byte mPayMode;

    private float mPaySum;

    public Store() {
    }

    public String toString(){
        return mName;
    }

    public boolean isSetPhoto() {
        return mPhoto == 1;
    }

    public void setPhoto(boolean isSet) {
        mPhoto = (byte)(isSet ? 1 : 0);
    }

    public void setPhoto(JSONObject jsonObject){
        try {
            mPhoto = (byte)jsonObject.getInt("photo");
        }catch (JSONException je){
            je.printStackTrace();
        }
    }

    public Store(JSONObject jsonObject){
        setId(jsonObject);
        setName(jsonObject);
        setPhone(jsonObject);
        setRegDate(jsonObject);
        setProvince(jsonObject);
        setCity(jsonObject);
        setDistrict(jsonObject);
        setStreet(jsonObject);
        setAddress(jsonObject);
        setPayMode(jsonObject);
        setPayDate(jsonObject);
        setPaySum(jsonObject);
        setPhoto(jsonObject);
        setWxCode(jsonObject);
        setAliCode(jsonObject);
    }

    public String getWxCode() {
        return mWxCode;
    }

    public void setWxCode(String wxCode) {
        mWxCode = wxCode;
    }

    public void setWxCode(JSONObject jsonObject){
        try{
            mWxCode = jsonObject.getString("codeWx");
        }catch (JSONException je){
            je.printStackTrace();
        }
    }

    public String getAliCode() {
        return mAliCode;
    }

    public void setAliCode(String aliCode) {
        mAliCode = aliCode;
    }

    public void setAliCode(JSONObject jsonObject){
        try{
            mAliCode = jsonObject.getString("codeAli");
        }catch (JSONException je){
            je.printStackTrace();
        }
    }

    public float getPaySum() {
        return mPaySum;
    }

    public void setPaySum(float paySum) {
        mPaySum = paySum;
    }

    public void setPaySum(JSONObject jsonObject){
        try {
            mPaySum = (float)jsonObject.getDouble("paySum");
        }catch (JSONException je){
            je.printStackTrace();
        }
    }

    public byte getPayMode() {
        return mPayMode;
    }

    public void setPayMode(byte payMode) {
        mPayMode = payMode;
    }

    public void setPayMode(JSONObject jsonObject){
        try {
            mPayMode = (byte)jsonObject.getInt("payMode");
        }catch (JSONException je){
            je.printStackTrace();
        }
    }

    public String getRegDate() {
        return mRegDate;
    }

    public void setRegDate(String regDate) {
        mRegDate = regDate;
    }

    public void setRegDate(JSONObject jsonObject){
        try {
            mRegDate = jsonObject.getString("regDate");
        }catch (JSONException je){
            je.printStackTrace();
        }
    }

    public String getPayDate() {
        return mPayDate;
    }

    public void setPayDate(String payDate) {
        mPayDate = payDate;
    }

    public void setPayDate(JSONObject jsonObject){
        try {
            mPayDate = jsonObject.getString("payDay");
        }catch (JSONException je){
            je.printStackTrace();
        }
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public void setPhone(JSONObject jsonObject) {
        try {
            mPhone = jsonObject.getString("phone");
        } catch (JSONException je) {
            mPhone = null;
        }
    }

    public String getStreet() {
        return mStreet == null ? "" : mStreet;
    }

    public void setStreet(String street) {
        mStreet = street;
    }

    public void setStreet(JSONObject jsonObject) {
        try {
            mStreet = jsonObject.getString("addrStreet");
        } catch (JSONException je) {
            mStreet = null;
        }
    }

    public String getCity() {
        return mCity == null ? "" : mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public void setCity(JSONObject jsonObject) {
        try {
            mCity = jsonObject.getString("addrCity");
        } catch (JSONException je) {
            mCity = null;
        }
    }

    public String getDistrict() {
        return mDistrict == null ? "" : mDistrict;
    }

    public void setDistrict(String district) {
        mDistrict = district;
    }

    public void setDistrict(JSONObject jsonObject) {
        try {
            mDistrict = jsonObject.getString("addrDistrict");
        } catch (JSONException je) {
            mDistrict = null;
        }
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public void setId(JSONObject jsonObject) {
        try {
            mId = jsonObject.getInt("id");
        } catch (JSONException je) {
            mId = -1;
        }
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setName(JSONObject jsonObject) {
        try {
            mName = jsonObject.getString("name");
        } catch (JSONException je) {
            mName = null;
        }
    }

    public String getProvince() {
        return mProvince == null ? "" : mProvince;
    }

    public void setProvince(String province) {
        mProvince = province;
    }

    public void setProvince(JSONObject jsonObject) {
        try {
            mProvince = jsonObject.getString("addrProvince");
        } catch (JSONException je) {
            mProvince = null;
        }
    }

    public String getAddress() {
        return mAddress == null ? "" : mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public void setAddress(JSONObject jsonObject) {
        try {
            mAddress = jsonObject.getString("addrDetail");
        } catch (JSONException je) {
            mAddress = null;
        }
    }
}
