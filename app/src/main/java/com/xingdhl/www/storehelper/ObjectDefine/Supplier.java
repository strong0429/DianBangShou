package com.xingdhl.www.storehelper.ObjectDefine;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Strong on 17/11/28.
 *
 */

public class Supplier implements Serializable{
    private String mName;
    private String mPhone;
    private String mContacter;
    private String mAddr;
    private int mId;
    private int mShopId;

    public Supplier()
    {
        mId = -1;
    }

    public Supplier(JSONObject jsonObject){
        try{
            mId = jsonObject.getInt("id");
            mShopId = jsonObject.getInt("storeId");
            mName = jsonObject.getString("name");
            mPhone = jsonObject.getString("phone");
            mContacter = jsonObject.getString("contacter");
            mAddr = jsonObject.getString("addr");
        }catch (JSONException je){
            Log.d("XDB_DebugInfo", "parse Supplier JSON error: ", je);
        }
    }

    public String getContacter() {
        return mContacter;
    }

    public void setContacter(String contacter) {
        mContacter = contacter;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id){
        mId = id;
    }

    public Supplier(String name, String addr, String tel, String contacter){
        mName = name;
        mAddr = addr;
        mPhone = tel;
        mContacter = contacter;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAddr() {
        return mAddr;
    }

    public void setAddr(String addr) {
        mAddr = addr;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public int getShopId() {
        return mShopId;
    }

    public void setShopId(int shopId) {
        mShopId = shopId;
    }
}
