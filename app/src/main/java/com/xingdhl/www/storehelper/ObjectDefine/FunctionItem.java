package com.xingdhl.www.storehelper.ObjectDefine;

/**
 * Created by Strong on 17/11/13.
 *
 */

public class FunctionItem {
    private String funcTitle;
    private String funcDescribe;
    private int funcResId;

    public FunctionItem(String name, String describe, int id){
        funcTitle = name;
        funcDescribe = describe;
        funcResId = id;
    }

    public String getFuncTitle() {
        return funcTitle;
    }

    public void setFuncTitle(String funcTitle) {
        this.funcTitle = funcTitle;
    }

    public String getFuncDescribe() {
        return funcDescribe;
    }

    public void setFuncDescribe(String funcDescribe) {
        this.funcDescribe = funcDescribe;
    }

    public int getFuncResId() {
        return funcResId;
    }

    public void setFuncResId(int funcResId) {
        this.funcResId = funcResId;
    }
}
