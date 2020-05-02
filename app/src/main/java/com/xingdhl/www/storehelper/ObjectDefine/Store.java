package com.xingdhl.www.storehelper.ObjectDefine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.content.FileProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Strong on 17/11/15.
 *
 */

public class Store {
    private int mId;
    private String mName;
    private String mPhone;
    private String mProvince;
    private String mCity;
    private String mDistrict;
    private String mStreet;
    private String mAddress;
    private String mRegDate;
    private String mWxCode;
    private String mAliCode;
    private String mLogo;
    private String mExpDate;

    //当前用户在该店铺的角色：O，店主；M，经理；C，店员
    private String mPosition;
    //店铺库存商品信息；
    private Map<String, StockGoods> mGoodsList;


    public Store() {
        mId = 0;
        mLogo = null;
        mGoodsList = new HashMap<>();
    }

    public String getLogo(){
        return mLogo;
    }

    public String toString(){
        return mName;
    }

    public Map<String, StockGoods> getGoodsList(){
        return mGoodsList;
    }

    public StockGoods getGoods(String barcode){
        return mGoodsList.get(barcode);
    }

    public void addGoods(StockGoods stockGoods){
        mGoodsList.put(stockGoods.getBarcode(), stockGoods);
    }

    public String getPosition() {
        return mPosition;
    }

    public void setPosition(String mPosition) {
        this.mPosition = mPosition;
    }

    public void setPosition(JSONObject jsonObject){
        try {
            mPosition = jsonObject.getString("position");
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    public String getExpDate() {
        return mExpDate;
    }

    public void setExpDate(String mExpDate) {
        this.mExpDate = mExpDate;
    }

    public void setmExpDate(JSONObject jsonObject){
        try {
            mExpDate = jsonObject.getString("exp_date");
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }
    public boolean isSetLogo() {
        return mLogo != null;
    }

    public void setLogo(String logo) {
        mLogo = logo;
    }

    public void setLogo(JSONObject jsonObject){
        try {
            mLogo = jsonObject.getString("logo");
        }catch (JSONException je){
            je.printStackTrace();
        }
    }

    public Store(JSONObject jsonObject){
        setId(jsonObject);
        setName(jsonObject);
        setPosition(jsonObject);
        setPhone(jsonObject);
        setRegDate(jsonObject);
        setProvince(jsonObject);
        setCity(jsonObject);
        setDistrict(jsonObject);
        setStreet(jsonObject);
        setAddress(jsonObject);
        setLogo(jsonObject);
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
            mWxCode = jsonObject.getString("paycode_wec");
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
            mAliCode = jsonObject.getString("paycode_ali");
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
            mRegDate = jsonObject.getString("reg_date");
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
            je.printStackTrace();
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
            mStreet = jsonObject.getString("addr_street");
        } catch (JSONException je) {
            je.printStackTrace();
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
            mCity = jsonObject.getString("addr_city");
        } catch (JSONException je) {
            je.printStackTrace();
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
            mDistrict = jsonObject.getString("addr_district");
        } catch (JSONException je) {
            je.printStackTrace();
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
            je.printStackTrace();
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
            je.printStackTrace();
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
            mProvince = jsonObject.getString("addr_province");
        } catch (JSONException je) {
            je.printStackTrace();
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
            mAddress = jsonObject.getString("addr_detail");
        } catch (JSONException je) {
            je.printStackTrace();
        }
    }
}
