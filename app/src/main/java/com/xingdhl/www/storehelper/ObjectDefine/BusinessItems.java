package com.xingdhl.www.storehelper.ObjectDefine;

import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.store.PurchaseActivity;
import com.xingdhl.www.storehelper.trading.TradingActivity;
import com.xingdhl.www.storehelper.user.SupplierManageActivity;

public class BusinessItems {
    private BusinessItems(){};

    private static final String[] mPagerTitles = {
            "销", "存", "管", "我"
    };
    private static final String[][] mItemTitles = {
            {"开单销售", "营业统计"},
            {"商品入库", "商品管理"},
            {"店铺维护", "店员管理", "供货商管理"},
            {"个人信息", "修改密码", "扫一扫"}
    };
    private static final String[][] mItemDetails = {
            {"支持手机扫码或手工录入商品条码销售商品，自动金额核算、生产销售清单。", "支持按时间、商品统计营业额及利润。"},
            {"商品采购入库录入，记录商品的采购入库信息。", "设置和维护商品的销售价格，商品促销管理维护。"},
            {"店铺信息的管理维护。", "店铺营业人员的信息管理维护。", "店铺供货商信息管理维护。"},
            {"用户个人信息的管理维护。", "修改用户个人系统登录密码。", "附加功能，扫码显示条形码、二维码信息。"}
    };
    private static final int[][] mItemResIds = {
            {R.drawable.sale, R.drawable.sale},
            {R.drawable.sale, R.drawable.sale},
            {R.drawable.sale, R.drawable.sale, R.drawable.sale},
            {R.drawable.sale, R.drawable.sale, R.drawable.sale}
    };
    private static final Class[][] mItemActivities = {
            {TradingActivity.class, },
            {PurchaseActivity.class, },
            {null, null, SupplierManageActivity.class},
            {}
    };

    public static String[] getPagerTitles(){
        return mPagerTitles;
    }

    public static String[] getItemTitles(int i) {
        return mItemTitles[i];
    }

    public static String[] getItemDetails(int i) {
        return mItemDetails[i];
    }

    public static int[] getItemResIds(int i) {
        return mItemResIds[i];
    }

    public static Class[] getItemActivities(int i) {
        return mItemActivities[i];
    }
}
