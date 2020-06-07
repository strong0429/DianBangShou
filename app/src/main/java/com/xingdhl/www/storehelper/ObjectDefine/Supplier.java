package com.xingdhl.www.storehelper.ObjectDefine;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    //private int mShopId;

    public Supplier()
    {
        mId = -1;
    }

    public Supplier(JSONObject jsonObject){
        try {
            mId = jsonObject.getInt("id");
        }catch(JSONException ignored){ }
        try {
            mName = jsonObject.getString("name");
        }catch(JSONException ignored){ }
        try {
            mPhone = jsonObject.getString("phone");
        }catch(JSONException ignored){ }
        try {
            mContacter = jsonObject.getString("contacter");
        }catch(JSONException ignored){ }
        try {
            mAddr = jsonObject.getString("addr");
        }catch(JSONException ignored){ }

        //mShopId = jsonObject.getInt("store");
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

    public Supplier(int storeId, String name, String addr, String tel, String contacter){
        //mShopId = storeId;
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
}
