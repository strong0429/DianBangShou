package com.xingdhl.www.storehelper.ObjectDefine;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Strong on 17/12/2.
 * 对应云端商品信息表，
 *
 */

public class Goods implements Serializable{
    private String mBarcode;
    private String mName;
    private String mSpec;
    private int mCategoryId1;
    private int mCategoryId2;

    public Goods(){
    }

    public Goods(JSONObject jsonObject){
        try {
            mBarcode = jsonObject.getString("barcode");
        }catch (JSONException je) {
            mBarcode = null;
        }
        try {
            mName = jsonObject.getString("name");
        }catch (JSONException je) {
            mName = null;
        }
        try {
            mSpec = jsonObject.getString("spec");
        }catch (JSONException je) {
            mSpec = null;
        }
        try {
            mCategoryId2 = jsonObject.getInt("id");
        }catch (JSONException je) {
            mCategoryId2 = -1;
        }
    }

    public String getBarcode() {
        return mBarcode;
    }

    public void setBarcode(String barcode) {
        mBarcode = barcode;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getCategoryId1() {
        return mCategoryId1;
    }

    public void setCategoryId1(int categoryId1) {
        mCategoryId1 = categoryId1;
    }

    public int getCategoryId2() {
        return mCategoryId2;
    }

    public void setCategoryId2(int categoryId2) {
        mCategoryId2 = categoryId2;
    }

    public String getSpec() {
        return mSpec;
    }

    public void setSpec(String spec) {
        mSpec = spec;
    }
}
