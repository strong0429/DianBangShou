package com.xingdhl.www.storehelper.ObjectDefine;

import android.media.TimedText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Strong on 18/1/8.
 */

public class StockGoods implements Serializable {
    //商品库存信息；
    private String mBarcode;
    private String mName;
    private String mSpec;
    private String mUnit;
    private String mPhoto;
    private String mEditor;
    private String mStartDate;
    private String mEndDate;
    private String mEditDate;

    private float mPrice; //商品售价
    private float mDiscount;
    private float mCount_h;
    private float mCount_C;

    private int mSupplierId;

    public StockGoods(Goods goods){
        mName = goods.getName();
        mBarcode = goods.getBarcode();
        mSpec = goods.getSpec();
    }

    public StockGoods(JSONObject jsonObject){
        try{
            mCount_C = (float)jsonObject.getDouble("count_c");
        }catch (JSONException je){
            mCount_C = 0.f;
        }
        try{
            mCount_h = (float)jsonObject.getDouble("count_h");
        }catch (JSONException je){
            mCount_h = 0.f;
        }
        try{
            mEditor = jsonObject.getString("editor_name");
        }catch (JSONException je){
            mEditor = null;
        }
        try{
            mName = jsonObject.getString("goods_name");
        }catch (JSONException je){
            mName = null;
        }
        try{
            mBarcode = jsonObject.getString("barcode");
        }catch (JSONException je){
            mBarcode = null;
        }
        try{
            mUnit = jsonObject.getString("spec");
        }catch (JSONException je){
            mUnit = "无";
        }
        try{
            mUnit = jsonObject.getString("unit");
        }catch (JSONException je){
            mUnit = "件";
        }
        try{
            mPrice = (float)jsonObject.getDouble("price");
        }catch (JSONException je){
            mPrice = 0.f;
        }
        try{
            mDiscount = (float)jsonObject.getDouble("discount");
        }catch (JSONException je){
            mDiscount = 1.f;
        }
        try{
            mEditDate = jsonObject.getString("edit_date");
        }catch (JSONException je){
            mStartDate = null;
        }
        try{
            mStartDate = jsonObject.getString("date_ps");
        }catch (JSONException je){
            mStartDate = null;
        }
        try{
            //mEndDate = Timestamp.valueOf(jsonObject.getString("date_pe")).getTime();
            mEndDate = jsonObject.getString("date_pe");
        }catch (JSONException je){
            mEndDate = null;
        }
        try{
            mPhoto = jsonObject.getString("photo");
        }catch (JSONException je){
            mPhoto = null;
        }
    }

    public String getEditor() {
        return mEditor;
    }

    public void setEditor(String editor) {
        mEditor = editor;
    }

    public String getEditDate() {
        return mEditDate;
    }
    public void setEditDate(String editDate) {
        mEditDate = editDate;
    }
    public void setEditDate(Timestamp editDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
        mEditDate = sdf.format(editDate);
    }

    public float getCount_h() {
        return mCount_h;
    }

    public void setCount_h(float count_h) {
        mCount_h = count_h;
    }

    public float getCount_C() {
        return mCount_C;
    }

    public void setCount_C(float count_C) {
        mCount_C = count_C;
    }

    public int getSupplierId() {
        return mSupplierId;
    }

    public void setSupplierId(int supplierId) {
        this.mSupplierId = supplierId;
    }

    public String getSpec() {
        return mSpec;
    }

    public void setSpec(String spec) {
        mSpec = spec;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public String getUnit() {
        return mUnit;
    }

    public void setUnit(String unit) {
        mUnit = unit;
    }

    public String getStartDate(){
        return mStartDate;
    }
    public long getStartDate_L(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
        try {
            Date date = sdf.parse(mStartDate);
            return date.getTime();
        }catch (ParseException pe){
            pe.printStackTrace();
        }
        return 0;
    }

    public void setStartDate(Timestamp dateTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
        mStartDate = sdf.format(dateTime);
    }
    public void setStartDate(String startDate) {
        mStartDate = startDate;
    }

    public String getEndDate() {
        return mEndDate;
    }
    public long getEndDate_L(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
        try {
            Date date = sdf.parse(mEndDate);
            return date.getTime();
        }catch (ParseException pe){
            pe.printStackTrace();
        }
        return 0;
    }

    public void setEndDate(Timestamp dateTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
        mEndDate = sdf.format(dateTime);
    }
    public void setEndDate(String endDate) {
        mEndDate = endDate;
    }

    public float getDiscount() {
        return mDiscount;
    }

    public void setDiscount(float discount) {
        mDiscount = discount;
    }

    public float getPrice() {
        return mPrice;
    }

    public void setPrice(float price) {
        mPrice = price;
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

}
