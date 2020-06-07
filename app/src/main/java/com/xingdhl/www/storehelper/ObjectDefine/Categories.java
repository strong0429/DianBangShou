package com.xingdhl.www.storehelper.ObjectDefine;

import org.json.JSONArray;
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
        private int id;
        private String name;
        private List<Category> mSubCategories;

        public Category(int id, String name){
            this.id = id;
            this.name = name;
            this.mSubCategories = null;
        }

        public Category(JSONObject jsonObject, String... item_name){
            mSubCategories = null;
            try{
                if(item_name.length == 0){
                    this.id = jsonObject.getInt("id");
                    this.name = jsonObject.getString("name");
                    JSONArray jsonArray = jsonObject.getJSONArray("sub_category");
                    mSubCategories = new ArrayList<>();
                    for(int i=0; i < jsonArray.length(); i++){
                        JSONObject obj = jsonArray.getJSONObject(i);
                        int id = obj.getInt("id");
                        String name = obj.getString("name");
                        mSubCategories.add(new Category(id, name));
                    }
                }else if(item_name.length == 3){
                    this.id = jsonObject.getInt(item_name[0]);
                    this.name = jsonObject.getString(item_name[1]);
                    JSONArray jsonArray = jsonObject.getJSONArray(item_name[2]);
                    mSubCategories = new ArrayList<>();
                    for(int i=0; i < jsonArray.length(); i++){
                        JSONObject obj = jsonArray.getJSONObject(i);
                        int id = obj.getInt(item_name[0]);
                        String name = obj.getString(item_name[1]);
                        mSubCategories.add(new Category(id, name));
                    }
                }
            }catch (JSONException je){
                je.printStackTrace();
            }
        }

        public int getId() {
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

        public List<Category> getSubCategories(){
            if(mSubCategories == null)
                mSubCategories = new ArrayList<>();
            return mSubCategories;
        }
    }

    private static List<Category> mStoreCategories;
    private static List<Category> mGoodsCategories;

    private Categories(){
    }

    public static List<Category> getStoreCategories(){
        if(mStoreCategories == null)
            mStoreCategories = new ArrayList<>();
        return mStoreCategories;
    }

    public static List<Category> getGoodsCategories(){
        if(mGoodsCategories == null)
            mGoodsCategories = new ArrayList<>();
        return mGoodsCategories;
    }
}
