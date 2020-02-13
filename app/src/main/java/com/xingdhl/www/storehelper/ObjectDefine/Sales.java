package com.xingdhl.www.storehelper.ObjectDefine;

import java.io.Serializable;

/**
 * Created by Strong on 17/12/1.
 *
 */

public class Sales implements Serializable{
    private int mStoreId;
    private int mSellerId;
    private float mCount;
    private float mSum;
    private float mPrice;
    private float mDiscount;
    private long mSellSn;
    private String mBarcode;
    private byte mStatus;

    //临时存储，不写入数据库；
    private String mName;

    public Sales(){

    }

    public Sales(int storeId, float price, float discount, String barcode, String goodsName, long sellSn){
        mStoreId = storeId;
        mBarcode = barcode;
        mName = goodsName;
        mSellSn = sellSn;

        mCount = 1.0f;
        mPrice = price;
        mDiscount = discount;
        mSum = price * discount;
    }

    public float getDiscount() {
        return mDiscount;
    }

    public void setDiscount(float discount) {
        mDiscount = discount;
    }

    public String getBarcode() {
        return mBarcode;
    }

    public void setBarcode(String barcode) {
        mBarcode = barcode;
    }

    public float getPrice() {
        return mPrice;
    }

    public void setPrice(float price) {
        mPrice = price;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public long getSellSn() {
        return mSellSn;
    }

    public void setSellSn(long sellSn) {
        mSellSn = sellSn;
    }

    public int getSellerId() {
        return mSellerId;
    }

    public void setSellerId(int sellerId) {
        mSellerId = sellerId;
    }

    public int getStoreId() {
        return mStoreId;
    }

    public void setStoreId(int storeId) {
        mStoreId = storeId;
    }

    public float getCount() {
        return mCount;
    }

    public void setCount(float count) {
        mCount = count;
        mSum = count * mPrice * mDiscount;
    }

    public float getSum() {
        return mSum;
    }

    public void setSum(float sum) {
        mSum = sum;
    }

    public byte getStatus() {
        return mStatus;
    }

    public void setStatus(byte status) {
        mStatus = status;
    }
}
