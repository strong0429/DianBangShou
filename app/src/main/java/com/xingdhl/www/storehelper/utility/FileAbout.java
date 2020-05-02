package com.xingdhl.www.storehelper.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileAbout {
    public static File transImgFile(Context context, String sourFile, float rate){
        Bitmap bitmap = BitmapFactory.decodeFile(sourFile);
        int w = (int)(bitmap.getWidth() * rate);
        int h = (int)(bitmap.getHeight() * rate);
        bitmap = Bitmap.createScaledBitmap(bitmap, w, h, false);
        try {
            File tmpFile = File.createTempFile("tmp", ".jpg", context.getCacheDir());
            FileOutputStream fOutputStream = new FileOutputStream(tmpFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOutputStream);
            fOutputStream.flush();
            fOutputStream.close();
            return tmpFile;
        } catch (IOException ie) {
            return null;
        }
    }
}
