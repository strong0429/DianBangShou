package com.xingdhl.www.storehelper.ObjectDefine;

/**
 * Created by Strong on 17/12/18.
 */

public final class ErrorCode {
    public static final int OK = 0;
    public static final int ERR_NOT_NULL = -1;
    public static final int ERR_NOT_EXIST = -2;
    public static final int ERR_DATA_INVALID = -3;
    public static final int ERR_DATA_INCONSISTENT = -4;
    public static final int ERR_DB_INSERT = -5;
    public static final int ERR_DB_QUERY = -6;
    public static final int ERR_ROLE_INVALID = -7;

    private ErrorCode(){

    }
}