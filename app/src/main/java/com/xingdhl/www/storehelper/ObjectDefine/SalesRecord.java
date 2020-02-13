package com.xingdhl.www.storehelper.ObjectDefine;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by Leeyc on 2018/2/24.
 *
 */

public class SalesRecord implements Serializable{
    private String mGoodsName;
    private String mBarcode;

    private float mSaleSum;
    private float mBuyPrice;
    private float mSalePrice;
    private float mSaleCount;

    private long mSaleSn;
    private short mGoodsCategory;
    private byte mPayMode;

    public final static Comparator<SalesRecord> comparatorDate = new Comparator<SalesRecord>() {
        @Override
        public int compare(SalesRecord o1, SalesRecord o2) {
            //时间降序排列；
            if(o1.mSaleSn > o2.mSaleSn)
                return 1;
            else if(o1.mSaleSn < o2.mSaleSn)
                return -1;
            return 0;
        }
    };

    public final static Comparator<SalesRecord> comparatorCategory = new Comparator<SalesRecord>() {
        @Override
        public int compare(SalesRecord o1, SalesRecord o2) {
            //商品类型升序排列；
            if(o1.mGoodsCategory < o2.mGoodsCategory)
                return 1;
            else if(o1.mGoodsCategory > o2.mGoodsCategory)
                return -1;

            //同类型商品，按时间降序排列
            if(o1.mSaleSn > o2.mSaleSn)
                return 1;
            else if(o1.mSaleSn < o2.mSaleSn)
                return -1;
            return 0;
        }
    };

    public SalesRecord(){
    }

    public SalesRecord(JSONObject jsonObject){
        try{
            mSaleSn = jsonObject.getLong("sn");
        }catch (JSONException je){
            je.printStackTrace();
        }
        try{
            mGoodsName = jsonObject.getString("name");
        }catch (JSONException je){
            je.printStackTrace();
        }
        try{
            mBarcode = jsonObject.getString("barcode");
        }catch (JSONException je){
            je.printStackTrace();
        }
        try{
            mSaleSum = (float)jsonObject.getDouble("sum");
        }catch (JSONException je){
            je.printStackTrace();
        }
        try{
            mBuyPrice = (float)jsonObject.getDouble("buyPrice");
        }catch (JSONException je){
            je.printStackTrace();
        }
        try{
            mSalePrice = (float)jsonObject.getDouble("salePrice");
        }catch (JSONException je){
            je.printStackTrace();
        }
        try{
            mGoodsCategory = (short)jsonObject.getInt("categoryId");
        }catch (JSONException je){
            je.printStackTrace();
        }
        try{
            mSaleCount = (float)jsonObject.getDouble("count");
        }catch (JSONException je){
            je.printStackTrace();
        }
        try{
            mPayMode = (byte)jsonObject.getInt("payMode");
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

    public float getSalePrice() {
        return mSalePrice;
    }

    public void setSalePrice(float salePrice) {
        mSalePrice = salePrice;
    }

    public String getBarcode() {
        return mBarcode;
    }

    public String getSaleDateToString() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
                .format(new Timestamp(mSaleSn));
    }

    public String getSnDateString(){
        return new SimpleDateFormat("yyyyMMdd", Locale.CHINA)
                .format(new Timestamp(mSaleSn));
    }

    public String getSnTimeString(){
        return new SimpleDateFormat("hhmmssSSS", Locale.CHINA)
                .format(new Timestamp(mSaleSn));
        /*
        Calendar calendar = Calendar.getInstance(Locale.PRC);
        calendar.setTimeInMillis(mSaleSN);
        return String.format(Locale.CHINA, "%02d%02d%02d%03d",
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND),
                calendar.get(Calendar.MILLISECOND));
                */
    }

    public void setSaleSn(long saleSn) {
        mSaleSn = saleSn;
    }

    public String getGoodsName() {
        return mGoodsName;
    }

    public void setGoodsName(String goodsName) {
        mGoodsName = goodsName;
    }

    public float getSaleSum() {
        return mSaleSum;
    }

    public void setSaleSum(float saleSum) {
        mSaleSum = saleSum;
    }

    public float getProfit() {
        return  mSaleSum - mBuyPrice * mSaleCount;
    }

    public short getGoodsCategory() {
        return mGoodsCategory;
    }

    public void setGoodsCategory(short goodsCategory) {
        mGoodsCategory = goodsCategory;
    }

    public float getSaleCount() {
        return mSaleCount;
    }

    public void setSaleCount(float saleCount) {
        mSaleCount = saleCount;
    }
}
