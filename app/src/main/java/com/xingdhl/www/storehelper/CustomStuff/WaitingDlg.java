package com.xingdhl.www.storehelper.CustomStuff;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import com.xingdhl.www.storehelper.R;

/**
 * Created by Strong on 17/11/10.
 *
 */

public class WaitingDlg extends ProgressDialog {
    private int mShowTimes;

    public WaitingDlg(Context context) {
        super(context, R.style.CustomProgressDialog);
        mShowTimes = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置不可取消，点击其他区域不能取消，实际中可以抽出去封装供外包设置
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        setContentView(R.layout.dialog_custom_webtask);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }

    @Override
    public void show() {
        if(mShowTimes == 0) {
            super.show();
        }
        mShowTimes++;
    }

    @Override
    public void dismiss() {
        mShowTimes--;
        if(mShowTimes == 0) {
            super.dismiss();
        }
        mShowTimes = (mShowTimes < 0) ? 0 : mShowTimes;
    }
}
