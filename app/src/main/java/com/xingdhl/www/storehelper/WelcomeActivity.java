package com.xingdhl.www.storehelper;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Message;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.xingdhl.www.storehelper.CustomStuff.FreeToast;
import com.xingdhl.www.storehelper.CustomStuff.QueryDialog;
import com.xingdhl.www.storehelper.Database.DbSchemaXingDB;
import com.xingdhl.www.storehelper.ObjectDefine.User;
import com.xingdhl.www.storehelper.user.UserLoginActivity;
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.UpdateService;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;

import static java.net.HttpURLConnection.HTTP_OK;

public class WelcomeActivity extends AppCompatActivity implements
        HttpHandler.handlerCallback, QueryDialog.QueryDlgListener{
    private HttpHandler mHttpHandler;
    private User mUser;
    private boolean bExit = false;

    @Override
    public void onDialogNegativeClick(DialogInterface dialog, int which) {
        Intent intent = new Intent(WelcomeActivity.this, UserLoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDialogPositiveClick(DialogInterface dialog, int which) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent service = new Intent(WelcomeActivity.this, UpdateService.class);
                startService(service);
            }
        }).start();
        Intent intent = new Intent(WelcomeActivity.this, UserLoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMsgHandler(Message msg) {
        Intent intent;
        switch (msg.what){
            case WebServiceAPIs.MSG_GET_TOKEN:
                if( msg.arg1 == HTTP_OK){
                    mUser.setToken(msg.getData().getString("token"));
                    String verTxt = msg.getData().getString("ver_txt");
                    String verDate = msg.getData().getString("date_pub");
                    String verDesc = msg.getData().getString("detail");
                    if(msg.arg2 > getLocalVersion()){
                        QueryDialog dialog = new QueryDialog(this,
                                "发现新版本：" + verTxt +
                                        "\n发布日期：" + verDate +
                                        "\n更新信息：\n\t" + verDesc +
                                        "\n\n是否升级到新版本？");
                        dialog.show();
                    }else{
                        mHttpHandler.justWait(500,3);
                    }
                }else {
                    String err_msg;
                    err_msg = msg.getData().getString("message") + "\n应用将终止执行！";
                    FreeToast.makeText(WelcomeActivity.this, err_msg, Toast.LENGTH_SHORT).show();
                    mHttpHandler.justWait(500,6);
                    bExit = true;
                }
                break;
            case HttpHandler.JUST_WAITING:
                if(msg.arg1 > 0)
                    break;
                if(!bExit) {
                    intent = new Intent(WelcomeActivity.this, UserLoginActivity.class);
                    startActivity(intent);
                }
                finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 101) {
            if(grantResults[0] != 0 || grantResults[1] != 0){
                FreeToast.makeText(this, "您拒绝了手机存储读写申请，程序终止！",
                        Toast.LENGTH_SHORT).show();
                finish();
            }else{
                // WebServiceAPIs.getAppVersion(mHttpHandler);
                String appName = this.getPackageName();
                WebServiceAPIs.getToken(mHttpHandler, appName);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mHttpHandler = new HttpHandler(this);
        //初始化UserInfo；
        mUser = User.getUser(getApplicationContext());
        //创建本地数据库
        DbSchemaXingDB.instantiate(getApplicationContext());

        if (ContextCompat.checkSelfPermission(WelcomeActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请外部存储读写权限
            ActivityCompat.requestPermissions(WelcomeActivity.this,
                    new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 101);//自定义的code
        }else{
            //获取服务器版本；
            String appName = this.getPackageName();
            WebServiceAPIs.getToken(mHttpHandler, appName);
        }
    }

    private int getLocalVersion(){
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            PackageManager packageManager = this.getPackageManager();
            String packageName = this.getPackageName();
            PackageInfo packageInfo= packageManager.getPackageInfo(packageName, 0);
            versionCode = packageInfo.versionCode;
            //versionCode = packageInfo.versionCodeMajor;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    @Override
    public void onBackPressed() {
    }
}
