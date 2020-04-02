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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.xingdhl.www.storehelper.user.StoreOwnerActivity;
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.HttpRunnable;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;
import com.xingdhl.www.zxinglibrary.encoding.EncodingUtils;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Strong on 17/11/14.
 *
 */

public class CreateStoreActivity extends AppCompatActivity implements View.OnClickListener,
        HttpHandler.handlerCallback, AMapLocationListener, QueryDialog.QueryDlgListener{
    private final int SET_CCODE_ALI = 2;
    private final int SET_CCODE_WX = 3;

    private AMapLocationClient mLocationClient;

    private User mUser;
    private Store mStore;
    private HttpHandler mHttpHandler;

    private Button mButtonMap;
    private EditText mShopName, mShopPhone,mCCodeWx, mCCodeAli;
    private EditText mShopAddrP, mShopAddrC, mShopAddrS, mShopAddrD, mShopAddrL;

    @Override
    public void onDialogPositiveClick(DialogInterface dialog, int which) {
        finish();
    }

    @Override
    public void onDialogNegativeClick(DialogInterface dialog, int which) {
    }

    @Override
    public void onBackPressed() {
        if("null".equals(getIntent().getStringExtra("Parent")))
            new QueryDialog(this, "终止注册店铺将退出程序。\n\n确认要终止注册吗？").show();
        else
            new QueryDialog(this, "确认终止注册店铺吗？").show();
    }

    @Override
    public void onMsgHandler(Message msg) {
        if(msg.what != WebServiceAPIs.MSG_STORE_CREATE)
            return;

        if(msg.arg1 == HTTP_OK) {
            mStore.setPosition("OW");
            mUser.getStores().add(mStore);
            if ("null".equals(this.getIntent().getStringExtra("Parent"))) {
                startActivity(new Intent(this, StoreOwnerActivity.class));
            }
            setResult(RESULT_OK);
            this.finish();
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

        mUser = User.getUser(getApplicationContext());
        mHttpHandler = new HttpHandler(this);
        mStore = new Store();

        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(this);
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setHttpTimeOut(30000);
        mLocationOption.setNeedAddress(true);
        mLocationClient.setLocationOption(mLocationOption);

        mShopName = (EditText)findViewById(R.id.edit_shop_name);
        mShopName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 45){
                    FreeToast.makeText(CreateStoreActivity.this, "超出名字允许长度!", Toast.LENGTH_SHORT).show();
                    mShopName.setText(text.substring(0, start) + text.substring(start + count));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mShopPhone = (EditText)findViewById(R.id.edit_shop_phone);
        mShopAddrP = (EditText)findViewById(R.id.addr_province);
        mShopAddrP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 16){
                    FreeToast.makeText(CreateStoreActivity.this, "超出地址允许长度!", Toast.LENGTH_SHORT).show();
                    mShopAddrP.setText(text.substring(0, start) + text.substring(start + count));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mShopAddrC = (EditText)findViewById(R.id.addr_city);
        mShopAddrC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 16){
                    FreeToast.makeText(CreateStoreActivity.this, "超出地址允许长度!", Toast.LENGTH_SHORT).show();
                    mShopAddrC.setText(text.substring(0, start) + text.substring(start + count));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mShopAddrD = (EditText)findViewById(R.id.addr_district);
        mShopAddrD.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 16){
                    FreeToast.makeText(CreateStoreActivity.this, "超出地址允许长度!", Toast.LENGTH_SHORT).show();
                    mShopAddrD.setText(text.substring(0, start) + text.substring(start + count));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mShopAddrS = (EditText)findViewById(R.id.addr_street);
        mShopAddrS.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 16){
                    FreeToast.makeText(CreateStoreActivity.this, "超出地址允许长度!", Toast.LENGTH_SHORT).show();
                    mShopAddrS.setText(text.substring(0, start) + text.substring(start + count));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mShopAddrL = (EditText)findViewById(R.id.addr_detail);
        mShopAddrL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 32){
                    FreeToast.makeText(CreateStoreActivity.this, "超出地址允许长度!", Toast.LENGTH_SHORT).show();
                    mShopAddrL.setText(text.substring(0, start) + text.substring(start + count));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mCCodeWx = (EditText)findViewById(R.id.edit_ccode_wx);
        mCCodeAli = (EditText)findViewById(R.id.edit_ccode_ali);

        mButtonMap = (Button)findViewById(R.id.button_map);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            mButtonMap.setEnabled(false);
        }

        mButtonMap.setOnClickListener(this);
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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK || (requestCode != SET_CCODE_ALI && requestCode != SET_CCODE_WX))
            return;

        String imgFile = null;
        Uri uri = data.getData();
        String[] columns = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, columns, null, null, null);
        if(cursor.moveToFirst()){
            imgFile = cursor.getString(0);
            cursor.close();
        }
        Bitmap bitmap = BitmapFactory.decodeFile(imgFile);

        String barcode = EncodingUtils.decodeQRMap(bitmap);
        if(barcode == null){
            FreeToast.makeText(this, "无法识别的二维码！", Toast.LENGTH_SHORT).show();
            return;
        }

        if(requestCode == SET_CCODE_WX) {
            mCCodeWx.setText(barcode);
        }else {
            mCCodeAli.setText(barcode);
        }
    }

    public void shopRegister() {
        if (mShopName.getText().toString().isEmpty()) {
            Toast.makeText(CreateStoreActivity.this, "请输入店铺的名字！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mShopAddrP.getText().toString().isEmpty() ||
                mShopAddrC.getText().toString().isEmpty() ||
                mShopAddrD.getText().toString().isEmpty() ||
                mShopAddrS.getText().toString().isEmpty() ||
                mShopAddrL.getText().toString().isEmpty()) {
            Toast.makeText(CreateStoreActivity.this, "请输入店铺的地址！", Toast.LENGTH_SHORT).show();
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

        WebServiceAPIs.createStore(mHttpHandler, mStore);
    }

    @Override
    protected void onDestroy() {
        mLocationClient.onDestroy();
        super.onDestroy();
    }
}
