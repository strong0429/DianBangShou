package com.xingdhl.www.storehelper.CustomStuff;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by Strong on 17/12/24.
 *
 */

public class QueryDialog {
    public interface QueryDlgListener {
        void onDialogPositiveClick(DialogInterface dialog, int which);
        void onDialogNegativeClick(DialogInterface dialog, int which);
    }
    public static int whoIs;

    private AlertDialog mAlertDialog;
    private QueryDlgListener mListener;

    public static void showAlertMsg(Context context, String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        builder.create().show();
    }

    public QueryDialog(Context context, String msg, QueryDlgListener... listeners) {
        if(listeners.length == 0) {
            try {
                mListener = (QueryDlgListener) context;
            } catch (ClassCastException cce) {
                cce.printStackTrace();
            }
        }else{
            mListener = listeners[0];
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("是", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onDialogPositiveClick(dialog, which);
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mListener.onDialogNegativeClick(dialog, which);
            }
        });
        builder.setMessage(msg);

        mAlertDialog = builder.create();
        mAlertDialog.setCancelable(false);
        mAlertDialog.setCanceledOnTouchOutside(false);
    }

    public void show(){
        mAlertDialog.show();
    }
}
