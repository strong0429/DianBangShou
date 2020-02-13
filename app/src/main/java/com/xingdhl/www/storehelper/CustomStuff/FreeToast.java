package com.xingdhl.www.storehelper.CustomStuff;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xingdhl.www.storehelper.R;

/**
 * Created by Leeyc on 2018/3/6.
 *
 */

public class FreeToast {
    private Toast mToast;
    private FreeToast(Context context, CharSequence text, int duration) {
        View v = LayoutInflater.from(context).inflate(R.layout.free_toast, null);
        TextView msgTextView = (TextView) v.findViewById(R.id.msgTextView);
        msgTextView.setText(text);
        mToast = new Toast(context);
        mToast.setView(v);
        mToast.setDuration(duration);
        mToast.setGravity(Gravity.CENTER, 0, 0);
    }

    public static FreeToast makeText(Context context, CharSequence text, int duration) {
        return new FreeToast(context, text, duration);
    }

    public void show() {
        if (mToast != null) {
            mToast.show();
        }
    }

    public void setGravity(int gravity, int xOffset, int yOffset) {
        if (mToast != null) {
            mToast.setGravity(gravity, xOffset, yOffset);
        }
    }
}
