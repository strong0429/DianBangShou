package com.xingdhl.www.storehelper.ObjectDefine;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Strong on 17/12/30.
 *
 */

public class Storage implements Serializable{
    private int storeId;
    private String barcode;
    private float count;
    private float price;
    private float discount;
    private long startDate;
    private long endDate;
    private long editDate;
    private int editorId;
    private int supplierId;

    public Storage(){
    }

    public Storage(JSONObject jsonObject){
        try{
            storeId = jsonObject.getInt("storeId");
        }catch (JSONException je){
            je.printStackTrace();
        }
        try{
            editorId = jsonObject.getInt("editorId");
        }catch (JSONException je){
            je.printStackTrace();
        }
        try{
            count = (float)jsonObject.getDouble("count");
        }catch (JSONException je){
            je.printStackTrace();
        }
        try{
            price = (float)jsonObject.getDouble("price");
        }catch (JSONException je){
            je.printStackTrace();
        }
        try{
            discount = (float)jsonObject.getDouble("discount");
        }catch (JSONException je){
            je.printStackTrace();
        }
        try{
            barcode = jsonObject.getString("barcode");
        }catch (JSONException je){
            je.printStackTrace();
        }
        try{
            startDate = Timestamp.valueOf(jsonObject.getString("startDate")).getTime();
        }catch (JSONException je){
            je.printStackTrace();
        }
        try{
            endDate = Timestamp.valueOf(jsonObject.getString("endDate")).getTime();
        }catch (JSONException je){
            je.printStackTrace();
        }
        try{
            editDate = Timestamp.valueOf(jsonObject.getString("editDate")).getTime();
        }catch (JSONException je){
            je.printStackTrace();
        }
        try{
            supplierId = jsonObject.getInt("supplierId");
        }catch (JSONException je){
            je.printStackTrace();
        }
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public long getEditDate() {
        return editDate;
    }

    public void setEditDate(long editDate) {
        this.editDate = editDate;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getCount() {
        return count;
    }

    public void setCount(float count) {
        this.count = count;
    }

    public int getEditorId() {
        return editorId;
    }

    public void setEditorId(int editorId) {
        this.editorId = editorId;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }
}
