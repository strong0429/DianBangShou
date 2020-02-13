package com.xingdhl.www.storehelper.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xingdhl.www.storehelper.Database.DbSchemaBase.*;

/**
 * Created by Strong on 17/12/2.
 *
 */

public class DbSchemaHelper extends SQLiteOpenHelper {

    public DbSchemaHelper(Context context){
        super(context, DbSchemaBase.NAME, null, DbSchemaBase.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + Sell_Table.NAME + "(" +
            Sell_Table.Cols.STORE_ID + ", " +
            Sell_Table.Cols.BARCODE + ", " +
            Sell_Table.Cols.SELLER_ID + ", " +
            Sell_Table.Cols.PRICE + ", " +
            Sell_Table.Cols.COUNT + ", " +
            Sell_Table.Cols.SUM + ", " +
            Sell_Table.Cols.DISCOUNT + ", " +
            Sell_Table.Cols.SELL_SN + ", " +
            Sell_Table.Cols.STATUS + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + Sell_Table.NAME);
        onCreate(db);
    }
}
