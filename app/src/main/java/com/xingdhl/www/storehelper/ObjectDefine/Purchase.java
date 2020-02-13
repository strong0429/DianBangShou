package com.xingdhl.www.storehelper.ObjectDefine;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Strong on 17/12/3.
 *
 */

public class Purchase {
    private int id;
    private int mStoreId;
    private int mSupplierId;
    //private int mBuyerId;
    private float mCount;
    private float mPrice;
    private String mUnit;
    private String mBarcode;
    private String mDateTime;

    //只用于参数传递，不对应表字段；
    private float mSellPrice;
    private String mGoodsName;
    private String mBuyer;

    public Purchase(){

    }

    public Purchase(JSONObject jsonObject){
        try {
            mGoodsName = jsonObject.getString("name");
        }catch (JSONException je) {
            je.printStackTrace();
        }
        try {
            mBarcode = jsonObject.getString("barcode");
        }catch (JSONException je) {
            je.printStackTrace();
        }
        try{
            mBuyer = jsonObject.getString("buyer");
        }catch (JSONException je) {
            je.printStackTrace();
        }
        try{
            mSupplierId = jsonObject.getInt("supplierId");
        }catch (JSONException je) {
            je.printStackTrace();
        }
        try{
            mCount = (float)jsonObject.getDouble("count");
        }catch (JSONException je) {
            je.printStackTrace();
        }
        try{
            mPrice = (float)jsonObject.getDouble("price");
        }catch (JSONException je) {
            je.printStackTrace();
        }
        try{
            Date date = new Date(jsonObject.getLong("date"));
            mDateTime = new SimpleDateFormat("yyyy-MM-dd", Locale.PRC).format(date);
        }catch (JSONException je) {
            je.printStackTrace();
        }
        try{
            mUnit = jsonObject.getString("unit");
        }catch (JSONException je){
            je.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getSellPrice() {
        return mSellPrice;
    }

    public void setSellPrice(float sellPrice) {
        mSellPrice = sellPrice;
    }

    public int getStoreId() {
        return mStoreId;
    }

    public void setStoreId(int storeId) {
        mStoreId = storeId;
    }

    public String getBarcode() {
        return mBarcode;
    }

    public void setBarcode(String barcode) {
        mBarcode = barcode;
    }

    public int getSupplierId() {
        return mSupplierId;
    }

    public void setSupplierId(int supplierId) {
        mSupplierId = supplierId;
    }

    public float getCount() {
        return mCount;
    }

    public void setCount(float count) {
        mCount = count;
    }

    public float getPrice() {
        return mPrice;
    }

    public void setPrice(float price) {
        mPrice = price;
    }

    public String getDateTime() {
        return mDateTime;
    }

    public void setDateTime(String dateTime) {
        mDateTime = dateTime;
    }

    public String getUnit() {
        return mUnit;
    }

    public void setUnit(String unit) {
        mUnit = unit;
    }

    public String getGoodsName() {
        return mGoodsName;
    }

    public String getBuyer() {
        return mBuyer;
    }
}
