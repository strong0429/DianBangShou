package com.xingdhl.www.storehelper.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xingdhl.www.storehelper.Database.DbSchemaBase.*;
import com.xingdhl.www.storehelper.ObjectDefine.Sales;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Strong on 17/12/2.
 *
 */

public class DbSchemaXingDB {
    private static DbSchemaXingDB sXingDB;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public class XDBCursorWrapper extends CursorWrapper{
        public XDBCursorWrapper(Cursor cursor){
            super(cursor);
        }

        private Sales getRecord(Sales record){
            record.setStoreId(getInt(getColumnIndex(Sell_Table.Cols.STORE_ID)));
            record.setSellerId(getInt(getColumnIndex(Sell_Table.Cols.SELLER_ID)));
            record.setBarcode(getString(getColumnIndex(Sell_Table.Cols.BARCODE)));
            record.setSellSn(getLong(getColumnIndex(Sell_Table.Cols.SELL_SN)));
            record.setPrice(getFloat(getColumnIndex(Sell_Table.Cols.PRICE)));
            record.setCount(getFloat(getColumnIndex(Sell_Table.Cols.COUNT)));
            record.setSum(getFloat(getColumnIndex(Sell_Table.Cols.SUM)));
            record.setDiscount(getFloat(getColumnIndex(Sell_Table.Cols.DISCOUNT)));
            record.setStatus((byte)getInt(getColumnIndex(Sell_Table.Cols.STATUS)));
            return record;
        }
    }

    private DbSchemaXingDB(Context context){
        mContext = context;
        mDatabase = new DbSchemaHelper(mContext).getWritableDatabase();
    }

    public static DbSchemaXingDB instantiate(Context context){
        if(sXingDB == null){
            sXingDB = new DbSchemaXingDB(context);
        }
        return sXingDB;
    }

    private ContentValues toContentValues(Sales record){
        ContentValues values = new ContentValues();
        try {
            values.put(Sell_Table.Cols.STORE_ID, record.getStoreId());
            values.put(Sell_Table.Cols.BARCODE, record.getBarcode());
            values.put(Sell_Table.Cols.SELLER_ID, record.getSellerId());
            values.put(Sell_Table.Cols.COUNT, record.getCount());
            values.put(Sell_Table.Cols.SUM, record.getSum());
            values.put(Sell_Table.Cols.DISCOUNT, record.getDiscount());
            values.put(Sell_Table.Cols.PRICE, record.getPrice());
            values.put(Sell_Table.Cols.SELL_SN, record.getSellSn());
            values.put(Sell_Table.Cols.STATUS, record.getStatus());
        }catch (NullPointerException ne){
            ne.printStackTrace();
        }
        return values;
    }

    public long addRecord(Sales record){
        if(record == null)
            return -1;

        long rowNo;
        ContentValues values = toContentValues(record);
        if((rowNo = mDatabase.insert(Sell_Table.NAME, null, values)) == -1L)
            Log.d("XDB_DB_Msg", "insert Sell_Table fail.");

        return rowNo;
    }

    public void delRecord(Sales record){
        String where = Sell_Table.Cols.BARCODE + " = ? and " +
                Sell_Table.Cols.SELL_SN + " = ? and " +
                Sell_Table.Cols.STORE_ID + " = " + record.getStoreId();
        mDatabase.delete(Sell_Table.NAME, where, new String[] {
                record.getBarcode(), String.format("%d", record.getSellSn()) });
    }

    public void delAll(String table){
        mDatabase.delete(table, null, null);
    }

    /*
    public DetailStorage getLocalStorage(Integer storeId, String barcode){
        String where = Storage_Table.Cols.BARCODE + " = ? and " +
                Storage_Table.Cols.STORE_ID + " = " + storeId;
        Cursor cursor = mDatabase.query(Storage_Table.NAME, null, where,
                new String[]{ barcode }, null, null, null);
        if(!cursor.moveToFirst()){
            cursor.close();
            return null;
        }
        XDBCursorWrapper wrapper = new XDBCursorWrapper(cursor);
        DetailStorage record = wrapper.getRecord(new DetailStorage());
        wrapper.close();
        return record;
    }

    public List<DetailStorage> getLocalStorage(Integer storeId){
        List<DetailStorage> goodsList = new ArrayList<>();
        String where = Storage_Table.Cols.STORE_ID + " = " + storeId +
                " order by " + Storage_Table.Cols.CATEGORY + " asc";
        Cursor cursor = mDatabase.query(Storage_Table.NAME, null, where, null, null, null, null);
        if(!cursor.moveToFirst()){
            cursor.close();
            return goodsList;
        }
        XDBCursorWrapper wrapper = new XDBCursorWrapper(cursor);
        do {
            goodsList.add(wrapper.getRecord(new DetailStorage()));
        }while (wrapper.moveToNext());
        wrapper.close();
        return goodsList;
    }
    */

    public List<Sales> getSaleRecord(int storeId){
        Cursor cursor = mDatabase.query(Sell_Table.NAME, null,
                Sell_Table.Cols.STORE_ID + " = " + storeId, null, null, null, null);
        if(!cursor.moveToFirst()){
            cursor.close();
            return null;
        }

        List<Sales> recordList = new ArrayList<>();
        XDBCursorWrapper wrapper = new XDBCursorWrapper(cursor);
        do {
            recordList.add(wrapper.getRecord(new Sales()));
        }while ( wrapper.moveToNext());
        wrapper.close();

        return recordList;
    }
}
