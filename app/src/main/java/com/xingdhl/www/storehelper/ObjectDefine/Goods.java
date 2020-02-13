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
    private String mName_M;
    private String mRemark;
    private Categories.Category mCategory;
    private int mPhotoId;

    public Goods(){
    }

    public Goods(JSONObject jsonObject){
        initGoods(jsonObject);
    }

    public void initGoods(JSONObject jsonObject){
        try {
            mBarcode = jsonObject.getString("barcode");
        }catch (JSONException je) {
            je.printStackTrace();
        }
        try {
            mName = jsonObject.getString("name");
        }catch (JSONException je) {
            je.printStackTrace();
        }
        try {
            mName_M = jsonObject.getString("manufacture");
        }catch (JSONException je) {
            je.printStackTrace();
        }
        try {
            mRemark = jsonObject.getString("remark");
        }catch (JSONException je) {
            je.printStackTrace();
        }
        try {
            JSONObject category = jsonObject.getJSONObject("goodsCategory");
            mCategory = new Categories.Category(category);
        }catch (JSONException je) {
            je.printStackTrace();
        }
        try {
            mPhotoId = jsonObject.getInt("photoId");
        }catch (JSONException je) {
            je.printStackTrace();
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

    public Categories.Category getCategory() {
        return mCategory;
    }

    public void setCategory(Categories.Category category) {
        mCategory = category;
    }

    public String getRemark() {
        return mRemark;
    }

    public void setRemark(String remark) {
        mRemark = remark;
    }

    public String getName_M() {
        return mName_M;
    }

    public void setName_M(String name_M) {
        mName_M = name_M;
    }

    public int getPhotoId() {
        return mPhotoId;
    }

    public void setPhotoId(int photoId) {
        mPhotoId = photoId;
    }
}
