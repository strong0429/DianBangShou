package com.xingdhl.www.storehelper.ObjectDefine;

/**
 * Created by Leeyc on 2018/3/18.
 *
 */

public final class GCV {
    public static final String D_TAG = "DBS_DMsg";
    public static String APPID = "5afa3495";

    public static final int OWNER = 1;
    public static final int CLERK = 2;

    public static final int COLOR_SELECTED = 0xaaaaaaaa;

    public static final int CUSTOM_BARCODE_LEN = 4;
    public static final int STORAGE_PAGE_SIZE = 10;

    public static final String RegExp_phone = "^(0[1-9]{2,3}\\-)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?$";
    public static final String RegExp_cell = "^[1][3,5,7,8][0-9]{9}$";
    public static final String RegExp_id = "^[0-9]{17}[0-9,*]$";
    public static final String RegExp_ChkNum = "^[0-9]{6}$";
    public static final String RegExp_Barcode = "^[0-9]{8}[0-9,-][0-9]{4}$";

    public final static int MSG_GOODS_NEW = -2000;
    public final static int MSG_SALE_NEW = -2001;
    public final static int MSG_SALE_ADD = -2002;
    public final static int MSG_SALE_SUB = -2003;
    public final static int MSG_SALE_DONE = -2004;
    public final static int MSG_SIMPLE_ADD = -2005;
    public final static int MSG_SIMPLE_SUB = -2006;
    public final static int MSG_MANUAL_ADD = -2007;
    public final static int MSG_MANUAL_SUB = -2008;
    public final static int MSG_CLEAR_ITEM = -2009;

    private GCV(){
    }
}
