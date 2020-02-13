package com.xingdhl.www.storehelper.Database;

/**
 * Created by Strong on 17/12/2.
 *
 */

public class DbSchemaBase {
    public static final String NAME = "store_helper.db";
    public static final int VERSION = 50;

    public static final class Sell_Table {
        public static final String NAME = "sell_record";  //表名称;

        public static final class Cols{
            public static final String STORE_ID = "store_id";
            public static final String BARCODE = "barcode";
            public static final String SELLER_ID = "seller_id";
            public static final String PRICE = "price"; //商品售价
            public static final String COUNT = "count";
            public static final String SUM = "sum";
            public static final String DISCOUNT = "discount";
            public static final String SELL_SN = "sell_sn"; //销售流水号（单号），取日期时间；
            public static final String STATUS = "status";   //支付方式，-1 为退货；
        }
    }
}
