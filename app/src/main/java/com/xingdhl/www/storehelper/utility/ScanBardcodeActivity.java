package com.xingdhl.www.storehelper.utility;

import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;
import com.xingdhl.www.storehelper.CustomStuff.FreeToast;
import com.xingdhl.www.storehelper.R;

public class ScanBardcodeActivity extends AppCompatActivity {
    private ScanBarcodeUtil mScanBarcodeUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_bardcode);

        //创建条码扫描功能对象；
        mScanBarcodeUtil = new ScanBarcodeUtil(this,
                (SurfaceView)findViewById(R.id.capture_preview),
                (RelativeLayout)findViewById(R.id.capture_crop_view),
                (ImageView)findViewById(R.id.capture_scan_line)) {
            @Override
            public void handleDecode(Result rawResult, Bundle bundle) {
                //声音提示捕捉到条码；
                beep();
                //成功扫描条码后，关闭相机；
                stopScan();
                /*
                //成功读取到条码后, 返回；
                Intent barcode = new Intent();
                barcode.putExtra("barcode", rawResult.getText());
                setResult(RESULT_OK, barcode);
                finish();
                */
                ((TextView)findViewById(R.id.text_name)).setText(rawResult.getText());
                startScan(1500);
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();

        mScanBarcodeUtil.onResume(getApplication());
        mScanBarcodeUtil.startScan(100);
    }

    @Override
    public void onPause() {
        mScanBarcodeUtil.onPause();

        super.onPause();
    }

    @Override
    public void onDestroy() {
        mScanBarcodeUtil.onDestroy();

        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode != 111)
            return;
        if(grantResults[0] == 0 )
            return;

        FreeToast.makeText(this, "您拒绝了相机权限申请，将无法自动识别条码。", Toast.LENGTH_SHORT).show();
    }
}
