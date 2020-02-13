package com.xingdhl.www.storehelper.store;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;
import com.xingdhl.www.zxinglibrary.encoding.EncodingUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoreEditActivity extends AppCompatActivity implements View.OnClickListener,
        HttpHandler.handlerCallback, AMapLocationListener, QueryDialog.QueryDlgListener{
    private final int SET_STORE_PHOTO = 1;
    private final int SET_CCODE_ALI = 2;
    private final int SET_CCODE_WX = 3;

    private EditText mAddr_Province, mAddr_City, mAddr_District, mAddr_Street, mAddr_Detail;
    private EditText mStoreName, mStoreTel, mCcodeWx, mCcodeAli;
    private Button mBtnOk, mBtnUpload, mBtnMap;

    private AMapLocationClient mLocationClient;

    private HttpHandler mHttpHandler;
    private File mPhotoTmp;
    private Store mStore;
    private int mMarks;

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        mBtnMap.setText("位置定位");
        mBtnMap.setEnabled(true);

        if (aMapLocation.getErrorCode() == 0) {
            //解析amapLocation获取相应内容。
            mAddr_Province.setText(aMapLocation.getProvince());
            mAddr_City.setText(aMapLocation.getCity());
            mAddr_District.setText(aMapLocation.getDistrict());
            mAddr_Street.setText(aMapLocation.getStreet());
            mAddr_Detail.setText(aMapLocation.getStreetNum());
        }else {
            //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
            FreeToast.makeText(this, "定位失败，请确认打开手机GPS或网络连接。",
                    Toast.LENGTH_SHORT).show();
            Log.e("AmapError","Location error, ErrCode:" +
                    aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getLocationDetail());
        }
        mLocationClient.stopLocation();
    }

    @Override
    public void onMsgHandler(Message msg) {
        switch (msg.what){
            case WebServiceAPIs.MSG_UPLOAD_FILE:
                if(msg.arg1 == WebServiceAPIs.HTTP_OK){
                    mStore.setPhoto(true);
                    //String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
                    File storePhoto = new File(mPhotoTmp.getAbsolutePath() + mStore.getId() + ".jpg");
                    if(storePhoto.exists())
                        storePhoto.delete();
                    mPhotoTmp.renameTo(storePhoto);
                    mBtnUpload.setEnabled(false);
                    setResult(RESULT_OK);
                }else{
                    FreeToast.makeText(this, "上传店铺照片失败！", Toast.LENGTH_SHORT).show();
                }
                break;
            case WebServiceAPIs.MSG_STORE_UPDATE:
                if(msg.arg1 != WebServiceAPIs.HTTP_OK){
                    FreeToast.makeText(this, "更新店铺信息失败！", Toast.LENGTH_SHORT).show();
                    return;
                }
                initStore(mStore);
                setResult(RESULT_OK);
                finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.store_photo:
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore
                        .Images.Media.EXTERNAL_CONTENT_URI), SET_STORE_PHOTO);
                break;
            case R.id.scan_ali:
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore
                        .Images.Media.EXTERNAL_CONTENT_URI), SET_CCODE_ALI);
                break;
            case R.id.scan_wx:
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore
                        .Images.Media.EXTERNAL_CONTENT_URI), SET_CCODE_WX);
                break;
            case R.id.button_upload:
                if(mPhotoTmp != null)
                    WebServiceAPIs.uploadFile(mHttpHandler, mPhotoTmp, mStore.getId());
                break;
            case R.id.button_map:
                mLocationClient.startLocation();
                mBtnMap.setText("位置定位中...");
                mBtnMap.setEnabled(false);
                break;
            case R.id.button_ok:
                Store store = new Store();
                if(initStore(store))
                    WebServiceAPIs.storeUpdate(mHttpHandler, store);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_edit);

        mHttpHandler = new HttpHandler(this);
        int pageNo = getIntent().getIntExtra("page_no", -1);
        mStore = User.getUser(null).getStore(pageNo);
        mMarks = 0;

        mBtnUpload = (Button)findViewById(R.id.button_upload);
        mBtnUpload.setOnClickListener(this);

        mBtnMap = (Button)findViewById(R.id.button_map);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            mBtnMap.setEnabled(false);
        }
        mBtnMap.setOnClickListener(this);

        mBtnOk = (Button)findViewById(R.id.button_ok);
        mBtnOk.setOnClickListener(this);
        mBtnOk.setEnabled(mMarks != 0);

        findViewById(R.id.scan_ali).setOnClickListener(this);
        findViewById(R.id.scan_wx).setOnClickListener(this);

        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(this);
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setHttpTimeOut(30000);
        mLocationOption.setNeedAddress(true);
        mLocationClient.setLocationOption(mLocationOption);

        mStoreName = (EditText)findViewById(R.id.store_name);
        mStoreName.setText(mStore.getName());
        mStoreName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(s.length() > 45){
                    FreeToast.makeText(StoreEditActivity.this, "超过允许最大长度！", Toast.LENGTH_SHORT).show();
                    mStoreName.setText(text.substring(0, start) + text.substring(start + count));
                    return;
                }
                if(text.equals(mStore.getName())){
                    mMarks = mMarks & 0xFFFE;
                }else {
                    mMarks = mMarks | 0x0001;
                }
                mBtnOk.setEnabled(mMarks != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mStoreTel = (EditText)findViewById(R.id.store_tel);
        mStoreTel.setText(mStore.getPhone());
        mStoreTel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals(mStore.getPhone())){
                    mMarks = mMarks & 0xFFFD;
                }else {
                    mMarks = mMarks | 0x0002;
                }
                mBtnOk.setEnabled(mMarks != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mAddr_Province = (EditText)findViewById(R.id.addr_province);
        mAddr_Province.setText(mStore.getProvince());
        mAddr_Province.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(s.length() > 16){
                    FreeToast.makeText(StoreEditActivity.this, "超过允许最大长度！", Toast.LENGTH_SHORT).show();
                    mAddr_Province.setText(text.substring(0, start) + text.substring(start + count));
                    return;
                }
                if(text.equals(mStore.getProvince())){
                    mMarks = mMarks & 0xFFFB;
                }else {
                    mMarks = mMarks | 0x0004;
                }
                mBtnOk.setEnabled(mMarks != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mAddr_City = (EditText)findViewById(R.id.addr_city);
        mAddr_City.setText(mStore.getCity());
        mAddr_City.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(s.length() > 16){
                    FreeToast.makeText(StoreEditActivity.this, "超过允许最大长度！", Toast.LENGTH_SHORT).show();
                    mAddr_City.setText(text.substring(0, start) + text.substring(start + count));
                    return;
                }
                if(text.equals(mStore.getCity())){
                    mMarks = mMarks & 0xFFF7;
                }else {
                    mMarks = mMarks | 0x0008;
                }
                mBtnOk.setEnabled(mMarks != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mAddr_District = (EditText)findViewById(R.id.addr_district);
        mAddr_District.setText(mStore.getDistrict());
        mAddr_District.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(s.length() > 16){
                    FreeToast.makeText(StoreEditActivity.this, "超过允许最大长度！", Toast.LENGTH_SHORT).show();
                    mAddr_District.setText(text.substring(0, start) + text.substring(start + count));
                    return;
                }
                if(text.equals(mStore.getDistrict())){
                    mMarks = mMarks & 0xFFEF;
                }else {
                    mMarks = mMarks | 0x0010;
                }
                mBtnOk.setEnabled(mMarks != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mAddr_Street = (EditText)findViewById(R.id.addr_street);
        mAddr_Street.setText(mStore.getStreet());
        mAddr_Street.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(s.length() > 16){
                    FreeToast.makeText(StoreEditActivity.this, "超过允许最大长度！", Toast.LENGTH_SHORT).show();
                    mAddr_Street.setText(text.substring(0, start) + text.substring(start + count));
                    return;
                }
                if(text.equals(mStore.getStreet())){
                    mMarks = mMarks & 0xFFDF;
                }else {
                    mMarks = mMarks | 0x0020;
                }
                mBtnOk.setEnabled(mMarks != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mAddr_Detail = (EditText)findViewById(R.id.addr_detail);
        mAddr_Detail.setText(mStore.getAddress());
        mAddr_Detail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(s.length() > 16){
                    FreeToast.makeText(StoreEditActivity.this, "超过允许最大长度！", Toast.LENGTH_SHORT).show();
                    mAddr_Detail.setText(text.substring(0, start) + text.substring(start + count));
                    return;
                }
                if(text.equals(mStore.getCity())){
                    mMarks = mMarks & 0xFFBF;
                }else {
                    mMarks = mMarks | 0x0040;
                }
                mBtnOk.setEnabled(mMarks != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        ((EditText)findViewById(R.id.edit_date)).setText(mStore.getRegDate());
        mCcodeAli = (EditText)findViewById(R.id.ccode_ali);
        mCcodeAli.setText(mStore.getAliCode());
        mCcodeAli.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(s.length() > 256){
                    FreeToast.makeText(StoreEditActivity.this, "超过允许最大长度(256)！", Toast.LENGTH_SHORT).show();
                    mCcodeAli.setText(text.substring(0, start) + text.substring(start + count));
                    return;
                }
                if(text.equals(mStore.getCity())){
                    mMarks = mMarks & 0xFF7F;
                }else {
                    mMarks = mMarks | 0x0080;
                }
                mBtnOk.setEnabled(mMarks != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mCcodeWx = (EditText)findViewById(R.id.ccode_wx);
        mCcodeWx.setText(mStore.getWxCode());
        mCcodeWx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(s.length() > 256){
                    FreeToast.makeText(StoreEditActivity.this, "超过允许最大长度(256)！", Toast.LENGTH_SHORT).show();
                    mCcodeWx.setText(text.substring(0, start) + text.substring(start + count));
                    return;
                }
                if(text.equals(mStore.getCity())){
                    mMarks = mMarks & 0xFEFF;
                }else {
                    mMarks = mMarks | 0x0100;
                }
                mBtnOk.setEnabled(mMarks != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if(mStore.isSetPhoto()){
            String imgPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    .getAbsolutePath() + "/store_" + mStore.getId() + ".jpg";
            File imgFile = new File(imgPath);
            if(imgFile.exists()){
                Bitmap imgStore = BitmapFactory.decodeFile(imgPath);
                ((CircleImageView)findViewById(R.id.store_photo)).setImageBitmap(imgStore);
            }
        }
        findViewById(R.id.store_photo).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK || (requestCode != SET_STORE_PHOTO &&
                requestCode != SET_CCODE_ALI && requestCode != SET_CCODE_WX)){
            return;
        }

        String imgFile = null;
        Uri uri = data.getData();
        String[] columns = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, columns, null, null, null);
        if(cursor.moveToFirst()){
            imgFile = cursor.getString(0);
            cursor.close();
        }
        Bitmap bitmap = BitmapFactory.decodeFile(imgFile);

        if(requestCode == SET_STORE_PHOTO) {
            int w = Math.min(bitmap.getWidth(), 960);
            int h = (int) (bitmap.getHeight() * ((float) w / (float) bitmap.getWidth()));
            bitmap = Bitmap.createScaledBitmap(bitmap, w, h, false);
            int x = Math.max(0, (w - 640) / 2);
            int y = Math.max(0, (h - 400) / 2);
            bitmap = Bitmap.createBitmap(bitmap, x, y, Math.min(640, w), Math.min(400, h));

            //生成上传服务器临时文件；
            File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            mPhotoTmp = new File(file.getPath() + "/store_");
            try {
                if (!mPhotoTmp.exists()) {
                    mPhotoTmp.createNewFile();
                }
                FileOutputStream fOutputStream = new FileOutputStream(mPhotoTmp);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOutputStream);
                fOutputStream.flush();
                fOutputStream.close();
            } catch (IOException e) {
                mPhotoTmp = null;
                FreeToast.makeText(this, "未授权，无法创建文件！", Toast.LENGTH_SHORT).show();
                return;
            }
            ((CircleImageView) findViewById(R.id.store_photo)).setImageBitmap(bitmap);
            mBtnUpload.setEnabled(true);
            return;
        }

        String code = EncodingUtils.decodeQRMap(bitmap);
        if(code == null){
            FreeToast.makeText(this, "无法识别的二维码！", Toast.LENGTH_SHORT).show();
            return;
        }

        if(requestCode == SET_CCODE_WX){
            mCcodeWx.setText(code);
        }else {
            mCcodeAli.setText(code);
        }
    }

    @Override
    public void onBackPressed() {
        if((mMarks & 0xFFFF) == 0) {
            super.onBackPressed();
        }else{
            new QueryDialog(this, "退出将丢弃当前输入内容，确认退出吗？").show();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogInterface dialog, int which) {

    }

    @Override
    public void onDialogPositiveClick(DialogInterface dialog, int which) {
        finish();
    }

    private boolean initStore(Store store){
        store.setId(mStore.getId());

        store.setName(mStoreName.getText().toString());

        String tel = mStoreTel.getText().toString();
        if(!tel.matches(GCV.RegExp_cell) && !tel.matches(GCV.RegExp_phone)){
            FreeToast.makeText(this, "请输入有效的电话号码！", Toast.LENGTH_SHORT).show();
            return false;
        }

        store.setPhone(mStoreTel.getText().toString());
        store.setProvince(mAddr_Province.getText().toString());
        store.setCity(mAddr_City.getText().toString());
        store.setDistrict(mAddr_District.getText().toString());
        store.setStreet(mAddr_Street.getText().toString());
        store.setAddress(mAddr_Detail.getText().toString());
        store.setAliCode(mCcodeAli.getText().toString());
        store.setWxCode(mCcodeWx.getText().toString());
        return true;
    }
}
