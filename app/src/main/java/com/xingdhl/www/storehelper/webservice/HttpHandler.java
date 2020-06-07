package com.xingdhl.www.storehelper.webservice;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.xingdhl.www.storehelper.CustomStuff.WaitingDlg;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Strong on 17/11/24.
 *
 */

public class HttpHandler extends Handler {
    public static final int JUST_WAITING = -2000;
    private int mCount;

    private WaitingDlg mWaitingDlg;
    private List<Integer> mMsgList;

    public interface handlerCallback {
        void onMsgHandler(Message msg);
    }

    private handlerCallback mCallback;

    public HttpHandler(Context context, handlerCallback callback){
        super(context.getMainLooper());

        mMsgList = new ArrayList<>();
        mWaitingDlg = new WaitingDlg(context);
        mCallback = callback;
    }

    public HttpHandler(Context context){
        super(context.getMainLooper());

        mMsgList = new ArrayList<>();
        mWaitingDlg = new WaitingDlg(context);
        mCallback = (handlerCallback)context;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        mCallback.onMsgHandler(msg);

        for(int i = 0; i < mMsgList.size(); i++){
            if(mMsgList.get(i).equals(msg.what)){
                mMsgList.remove(i);
                mWaitingDlg.dismiss();
                break;
            }
        }
    }

    public void showDlg(int msgId){
        mMsgList.add(msgId);
        mWaitingDlg.show();
    }

    public void justWait(final int gapTimeMs, final int count) {
        mCount = count;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                mCount -= 1;
                if (mCount > 0) {
                    Message msg = obtainMessage();
                    msg.what = JUST_WAITING;
                    msg.arg1 = mCount;
                    sendMessage(msg);
                    postDelayed(this, gapTimeMs);
                } else {
                    Message msg = obtainMessage();
                    msg.what = JUST_WAITING;
                    msg.arg1 = mCount;
                    sendMessage(msg);
                }
            }
        };
        postDelayed(r, gapTimeMs);
    }

    public void stopWait(){
        mCount = 0;
    }
}
