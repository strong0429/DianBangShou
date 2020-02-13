package com.xingdhl.www.storehelper.ObjectDefine;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Strong on 17/12/22.
 *
 */

public class Categories {
    //Category必须定义为static，否则不能用 new Categories.Category(...)方式实例化；
    public static class Category implements Serializable{
        private short id;
        private String name;

        public Category(short id, String name){
            this.id = id;
            this.name = name;
        }

        public Category(JSONObject jsonObject, String... name){
            try{
                if(name.length != 2){
                    this.id = (short)jsonObject.getInt("id");
                    this.name = jsonObject.getString("name");
                }else{
                    this.id = (short)jsonObject.getInt(name[0]);
                    this.name = jsonObject.getString(name[1]);
                }
            }catch (JSONException je){
                je.printStackTrace();
            }
        }

        public short getId() {
            return id;
        }

        public void setId(short id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String toString(){
            return name;
        }
    }

    private static List<Category> mStore;
    private static List<Category> mGoods;

    private Categories(){
    }

    public static List<Category> getStoreCategories(){
        if(mStore == null)
            mStore = new ArrayList<>();
        return mStore;
    }

    public static List<Category> getGoodsCategories(){
        if(mGoods == null)
            mGoods = new ArrayList<>();
        return mGoods;
    }
}
