package com.xingdhl.www.storehelper.store;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.xingdhl.www.storehelper.CustomStuff.FreeToast;
import com.xingdhl.www.storehelper.CustomStuff.QueryDialog;
import com.xingdhl.www.storehelper.ObjectDefine.GCV;
import com.xingdhl.www.storehelper.ObjectDefine.Store;
import com.xingdhl.www.storehelper.ObjectDefine.User;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.utility.FileAbout;
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.HttpRunnable;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;
import com.xingdhl.www.zxinglibrary.encoding.EncodingUtils;

import java.io.File;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Strong on 17/11/14.
 *
 */

public class CreateStoreActivity extends AppCompatActivity implements View.OnClickListener,
        HttpHandler.handlerCallback, AMapLocationListener, QueryDialog.QueryDlgListener{
    private final int ACCESS_LOCATION_REQUEST_CODE = 1000;
    private final int SET_CCODE_ALI = 2;
    private final int SET_CCODE_WX = 3;
    private final int SET_TITLE_IMAGE = 4;

    private AMapLocationClient mLocationClient;

    private User mUser;
    private Store mStore;
    private HttpHandler mHttpHandler;

    private File mLogoFile;
    private Button mButtonMap;
    private ImageView mTitleImage;
    private EditText mShopName, mShopPhone,mCCodeWx, mCCodeAli;
    private EditText mShopAddrP, mShopAddrC, mShopAddrS, mShopAddrD, mShopAddrL;

    @Override
    public void onDialogPositiveClick(DialogInterface dialog, int which) {
        if(mStore != null && mStore.getId() > 0)
            startActivity(new Intent(getApplication(), ListStoreActivity.class));
        finish();
    }

    @Override
    public void onDialogNegativeClick(DialogInterface dialog, int which) {
    }

    @Override
    public void onBackPressed() {
        new QueryDialog(this, "终止注册店铺将退出程序，确定终止吗？").show();
    }

    @Override
    public void onMsgHandler(Message msg) {
        if(msg.what != WebServiceAPIs.MSG_STORE_CREATE)
            return;

        if(msg.arg1 == HTTP_OK) {
            mStore.setPosition("O");
            mUser.getStores().add(mStore);
            if(mStore.getLogo() != null && !mStore.getLogo().isEmpty()) {
                File fileName = new File(mStore.getLogo());
                File filePath = new File(getFilesDir(), "images/store");
                if (!filePath.exists())
                    filePath.mkdirs();
                fileName = new File(filePath, fileName.getName());
                mLogoFile.renameTo(fileName);
            }
            QueryDialog.showAlertMsg(this, "已为您创建店铺, 开始营业吧！", "确定");
            //setResult(RESULT_OK, null);

            return;
        }
        if(msg.arg1 == HttpRunnable.HTTP_NETWORK_ERR)
            FreeToast.makeText(this,"无法链接网络！请确认已打开手机网络连接。", Toast.LENGTH_SHORT).show();
        else
            FreeToast.makeText(this, msg.getData().getString("message"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        mButtonMap.setText("位置定位");
        mButtonMap.setEnabled(true);

        if (aMapLocation == null)
            return;

        if (aMapLocation.getErrorCode() == 0) {
            //解析amapLocation获取相应内容。
            mShopAddrP.setText(aMapLocation.getProvince());
            mShopAddrC.setText(aMapLocation.getCity());
            mShopAddrD.setText(aMapLocation.getDistrict());
            mShopAddrS.setText(aMapLocation.getStreet());
            mShopAddrL.setText(aMapLocation.getStreetNum());
        }else {
            //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
            mStore.setProvince("");
            mStore.setCity("");
            mStore.setDistrict("");
            mStore.setStreet("");
            mStore.setAddress("");
            FreeToast.makeText(this, "定位失败，请确认打开手机GPS或网络连接。",
                    Toast.LENGTH_SHORT).show();
            Log.e("AmapError","Location error, ErrCode:" +
                    aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getLocationDetail());
        }
        mLocationClient.stopLocation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_register);

        mLogoFile = null;
        mStore = new Store();
        mUser = User.getUser(null);
        mHttpHandler = new HttpHandler(this);

        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(this);
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setHttpTimeOut(30000);
        mLocationOption.setNeedAddress(true);
        mLocationClient.setLocationOption(mLocationOption);

        mShopName = findViewById(R.id.edit_shop_name);
        mShopPhone = findViewById(R.id.edit_shop_phone);
        mShopAddrP = findViewById(R.id.addr_province);
        mShopAddrC = findViewById(R.id.addr_city);
        mShopAddrD = findViewById(R.id.addr_district);
        mShopAddrS = findViewById(R.id.addr_street);
        mShopAddrL = findViewById(R.id.addr_detail);
        mCCodeWx = findViewById(R.id.edit_ccode_wx);
        mCCodeAli = findViewById(R.id.edit_ccode_ali);

        mButtonMap = findViewById(R.id.button_map);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_LOCATION_REQUEST_CODE);
        }
        mButtonMap.setOnClickListener(this);

        mTitleImage = findViewById(R.id.title_image);
        mTitleImage.setOnClickListener(this);

        findViewById(R.id.scan_wx).setOnClickListener(this);
        findViewById(R.id.scan_ali).setOnClickListener(this);
        findViewById(R.id.button_shop_register).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_map:
                mLocationClient.startLocation();
                mButtonMap.setText("位置定位中...");
                mButtonMap.setEnabled(false);
                break;
            case R.id.button_shop_register:
                shopRegister();
                break;
            case R.id.scan_wx:
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore
                        .Images.Media.EXTERNAL_CONTENT_URI), SET_CCODE_WX);
                break;
            case R.id.scan_ali:
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore
                        .Images.Media.EXTERNAL_CONTENT_URI), SET_CCODE_ALI);
                break;
            case R.id.title_image:
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore
                        .Images.Media.EXTERNAL_CONTENT_URI), SET_TITLE_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK )
            return;

        String imgFile = null;
        Uri uri = data.getData();
        String[] columns = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, columns, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            imgFile = cursor.getString(0);
            cursor.close();
        } else
            return;

        switch (requestCode) {
            case SET_TITLE_IMAGE:
                mLogoFile = new File(imgFile);
                if(mLogoFile.length() > 1024 * 1024){   //文件大小>1M, 缩小文件；
                    float rate = (float)Math.sqrt((1024. * 1024) / mLogoFile.length());
                    mLogoFile = FileAbout.transImgFile(this, imgFile, rate);
                }
                mTitleImage.setImageURI(Uri.parse(mLogoFile.getAbsolutePath()));
                break;
            case SET_CCODE_ALI:
            case SET_CCODE_WX:
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile);
                String barcode = EncodingUtils.decodeQRMap(bitmap);
                if (barcode == null) {
                    FreeToast.makeText(this, "无法识别的二维码！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (requestCode == SET_CCODE_WX) {
                    mCCodeWx.setText(barcode);
                } else {
                    mCCodeAli.setText(barcode);
                }
        }
    }

    public void shopRegister() {
        if (mShopName.getText().toString().isEmpty()) {
            Toast.makeText(CreateStoreActivity.this, "请输入店铺的名字！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mShopPhone.getText().toString().matches(GCV.RegExp_phone) &&
                !mShopPhone.getText().toString().matches(GCV.RegExp_cell)) {
            Toast.makeText(CreateStoreActivity.this, "请输入正确的店铺联系电话！", Toast.LENGTH_SHORT).show();
            return;
        }
        //Store mStore = mUser.getStore();
        mStore.setName(mShopName.getText().toString());
        mStore.setPhone(mShopPhone.getText().toString());

        mStore.setProvince(mShopAddrP.getText().toString());
        mStore.setCity(mShopAddrC.getText().toString());
        mStore.setDistrict(mShopAddrD.getText().toString());
        mStore.setStreet(mShopAddrS.getText().toString());
        mStore.setAddress(mShopAddrL.getText().toString());
        mStore.setWxCode(mCCodeWx.getText().toString());
        mStore.setAliCode(mCCodeAli.getText().toString());

        WebServiceAPIs.createStore(mHttpHandler, mStore, mLogoFile);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == ACCESS_LOCATION_REQUEST_CODE){
            if(grantResults[0] != 0 && grantResults[1] != 0){
                FreeToast.makeText(this, "您拒绝了位置定位权限申请，将无法自动获取位置信息！", Toast.LENGTH_SHORT).show();
                mButtonMap.setEnabled(false);
            }
        }
    }

    @Override
    protected void onDestroy() {
        mLocationClient.onDestroy();
        super.onDestroy();
    }
}
