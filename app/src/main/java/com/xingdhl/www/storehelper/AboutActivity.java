package com.xingdhl.www.storehelper;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;
import com.xingdhl.www.zxinglibrary.encoding.EncodingUtils;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        RelativeLayout layoutBarcode = (RelativeLayout)findViewById(R.id.layout_barcode);
        layoutBarcode.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                int imgSize = (int)(Math.min((right - left), (bottom - top)) * 0.9);
                ImageView imgQrcode = (ImageView)findViewById(R.id.img_qrcode);
                String qrcodeContent = WebServiceAPIs.URL_GET_APP ; //+ "?user=" + User.getUser(null).getPhoneNum();
                Bitmap logIcon = BitmapFactory.decodeResource(getResources(), R.drawable.barcode_icon);
                Bitmap qrCodeBitmap = EncodingUtils.createQRCode(qrcodeContent, imgSize, imgSize, logIcon);
                imgQrcode.setImageBitmap(qrCodeBitmap);
            }
        });

        String verNameTxt = "版本：";
        TextView verName = (TextView)findViewById(R.id.text_version);
        try {
            verNameTxt += this.getPackageManager().
                    getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        verName.setText(verNameTxt);

        ((TextView)findViewById(R.id.download_url)).setText(WebServiceAPIs.URL_GET_APP);
    }
}
