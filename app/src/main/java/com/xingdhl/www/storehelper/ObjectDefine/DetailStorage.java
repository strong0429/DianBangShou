package com.xingdhl.www.storehelper.ObjectDefine;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Strong on 18/1/8.
 * 对应本地数据库库存表信息
 * 为减少数据库访问，已对User表、Goods表、Goods_category表、Supplier表作关联处理；
 */

public class DetailStorage implements Serializable {
    //商品库存信息；
    private int mStoreId;
    private String mBarcode;
    private String mUnit;
    private float mCount;
    private float mPrice; //商品售价
    private float mDiscount;
    private long mStartDate;
    private long mEndDate;
    private long mEditTime;

    //关联商品信息；
    private String mName;
    private String mName_M;
    private String mRemark;
    private int mPhotoId;
    //关联商品类别信息;
    private short mCategoryId;

    //关联供应商信息
    private String mSupplier;

    //关联用户信息
    private String mEditor;

    public DetailStorage(){
    }

    public DetailStorage(int storeId){
        mStoreId = storeId;
    }
    public DetailStorage(int storeId, String barcode){
        mStoreId = storeId;
        mBarcode = barcode;
    }

    public DetailStorage(JSONObject jsonObject){
        //获取关联供应商信息：name；
        try{
            mSupplier = jsonObject.getString("supplier");
        }catch (JSONException je){
            je.printStackTrace();
        }
        //获取关联用户信息：phone；
        try{
            mEditor = jsonObject.getString("editor");
        }catch (JSONException je){
            je.printStackTrace();
        }
        try {
            initStorage(jsonObject.getJSONObject("storage"));
        }catch (JSONException je){
            je.printStackTrace();
        }
    }

    public DetailStorage init(Goods goods){
        if(goods == null)
            return this;

        mName = goods.getName();
        mBarcode = goods.getBarcode();
        mName_M = goods.getName_M();
        mRemark = goods.getRemark();
        mCategoryId = goods.getCategory().getId();
        mPhotoId = goods.getPhotoId();

        return this;
    }

    private void initStorage(JSONObject jsonObject){
        try{
            mStoreId = jsonObject.getInt("storeId");
        }catch (JSONException je){
            je.printStackTrace();
        }
        try{
            mBarcode = jsonObject.getString("barcode");
        }catch (JSONException je){
            je.printStackTrace();
        }
        try{
            mCount = (float)jsonObject.getDouble("count");
        }catch (JSONException je){
            je.printStackTrace();
        }
        try{
            mUnit = jsonObject.getString("unit");
        }catch (JSONException je){
            mUnit = "件";
            //je.printStackTrace();
        }
        try{
            mPrice = (float)jsonObject.getDouble("price");
        }catch (JSONException je){
            je.printStackTrace();
        }
        try{
            mDiscount = (float)jsonObject.getDouble("discount");
        }catch (JSONException je){
            je.printStackTrace();
        }
        try{
            mStartDate = Timestamp.valueOf(jsonObject.getString("startDate")).getTime();
        }catch (JSONException je){
            je.printStackTrace();
        }
        try{
            mEndDate = Timestamp.valueOf(jsonObject.getString("endDate")).getTime();
        }catch (JSONException je){
            je.printStackTrace();
        }
        try{
            mEditTime = Timestamp.valueOf(jsonObject.getString("editDate")).getTime();
        }catch (JSONException je){
            je.printStackTrace();
        }
        //获取关联商品信息；
        try{
            initGoods(jsonObject.getJSONObject("goods"));
        }catch (JSONException je){
            je.printStackTrace();
        }
    }

    private void initGoods(JSONObject jsonObject){
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
            mPhotoId = jsonObject.getInt("photoId");
        }catch (JSONException je) {
            je.printStackTrace();
        }
        try {
            JSONObject category = jsonObject.getJSONObject("goodsCategory");
            mCategoryId = (short)category.getInt("id");
        }catch (JSONException je) {
            je.printStackTrace();
        }
    }

    public Storage getStorage(){
        Storage storage = new Storage();
        storage.setStoreId(mStoreId);
        storage.setBarcode(mBarcode);
        storage.setCount(mCount);
        storage.setPrice(mPrice);
        storage.setDiscount(mDiscount);
        storage.setStartDate(mStartDate);
        storage.setEndDate(mEndDate);
        storage.setEditDate(mEditTime);
        return storage;
    }

    public float getCount() {
        return mCount;
    }

    public void setCount(float count) {
        mCount = count;
    }

    public String getUnit() {
        return mUnit;
    }

    public void setUnit(String unit) {
        mUnit = unit;
    }

    public String getEditor() {
        return mEditor;
    }

    public void setEditor(String editor) {
        mEditor = editor;
    }

    public long getEditTime_L(){
        return mEditTime;
    }

    public String getEditTime() {
        Timestamp dateTime = new Timestamp(mEditTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return sdf.format(dateTime);
    }

    public void setEditTime(long editTime) {
        mEditTime = editTime;
    }

    public String getSupplier() {
        return mSupplier;
    }

    public void setSupplier(String supplier) {
        mSupplier = supplier;
    }

    public Long getStartDate_L() {
        return mStartDate;
    }

    public String getStartDate(){
        Timestamp dateTime = new Timestamp(mStartDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return sdf.format(dateTime);
    }
    public void setStartDate(Long startDate) {
        mStartDate = startDate;
    }

    public long getEndDate_L(){
        return mEndDate;
    }

    public String getEndDate() {
        Timestamp dateTime = new Timestamp(mEndDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return sdf.format(dateTime);
    }

    public void setEndDate(long endDate) {
        mEndDate = endDate;
    }

    public int getStoreId() {
        return mStoreId;
    }

    public void setStoreId(int storeId) {
        mStoreId = storeId;
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

    public short getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(short category) {
        mCategoryId = category;
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
