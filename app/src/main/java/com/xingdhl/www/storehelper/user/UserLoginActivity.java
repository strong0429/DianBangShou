package com.xingdhl.www.storehelper.user;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.xingdhl.www.storehelper.CustomStuff.FreeToast;
import com.xingdhl.www.storehelper.CustomStuff.QueryDialog;
import com.xingdhl.www.storehelper.ObjectDefine.User;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.store.CreateStoreActivity;
import com.xingdhl.www.storehelper.store.ListStoreActivity;
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.HttpRunnable;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;

import static java.net.HttpURLConnection.HTTP_OK;

public class UserLoginActivity extends AppCompatActivity implements
        HttpHandler.handlerCallback, QueryDialog.QueryDlgListener, View.OnClickListener{
    private EditText mTextMobile;
    private EditText mTextPasswd;
    private CheckBox mAutoLogin;

    private HttpHandler mHttpHandler;
    private User mUser;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_login:
                mUser.setMobile(mTextMobile.getText().toString());
                mUser.setPassword(mTextPasswd.getText().toString());
                mUser.setAutoLogin(mAutoLogin.isChecked());

                if(mUser.getMobile().isEmpty() || mUser.getPassword().isEmpty()) {
                    FreeToast.makeText(UserLoginActivity.this, "请输入手机号码和密码！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                //申请用户登录
                WebServiceAPIs.userLogin(mHttpHandler);
                break;
            case R.id.button_register:
                Intent iRegister = new Intent(UserLoginActivity.this, UserRegisterActivity.class);
                iRegister.putExtra("phone", mTextMobile.getText().toString());
                startActivity(iRegister);
                finish();
                break;
            case R.id.forgot_pwd:
                Intent iForgot = new Intent(UserLoginActivity.this, ResetPasswordActivity.class);
                iForgot.putExtra("phone", mTextMobile.getText().toString());
                startActivity(iForgot);
                finish();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogInterface dialog, int which) {
        Intent intent = new Intent(this, CreateStoreActivity.class);
        intent.putExtra("Parent", "null");
        startActivity(intent);
        finish();
    }

    @Override
    public void onDialogNegativeClick(DialogInterface dialog, int which) {
        finish();
    }

    @Override
    public void onMsgHandler(Message msg) {
        if(msg.what == WebServiceAPIs.MSG_GET_TOKEN) {
            if (msg.arg1 == HTTP_OK) {
                mUser.setToken(msg.getData().getString("token"));
            }
            return;
        }

        if(msg.what != WebServiceAPIs.MSG_USER_LOGIN)
            return;
        if(msg.arg1 == HTTP_OK) {
            mUser.saveInfo();
            startActivity(new Intent(this, ListStoreActivity.class));
            finish();
        } else if(msg.arg1 >= HttpRunnable.HTTP_NETWORK_ERR) {
            String err_msg = msg.getData().getString("message");
            FreeToast.makeText(this, err_msg, Toast.LENGTH_SHORT).show();
        } else {
            FreeToast.makeText(this, "登录失败！用户名不存在或密码错误。", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        mHttpHandler = new HttpHandler(this);
        mUser = User.getUser(getApplicationContext());

        Intent intent = getIntent();
        if(intent != null){
            String origin = intent.getStringExtra("ORIGIN");
            if(origin != null && origin.equals("UserRegisterActivity")){
                //新注册用户，重新获取App Token；
                WebServiceAPIs.getToken(mHttpHandler, this.getPackageName());
            }
        }

        mTextPasswd = findViewById(R.id.user_passward);
        if(mUser.isAutoLogin()) //是否填充密码；
            mTextPasswd.setText(mUser.getPassword());

        mTextMobile = findViewById(R.id.user_mobile);
        mTextMobile.setText(mUser.getMobile());

        mAutoLogin = findViewById(R.id.remember_pwd);
        mAutoLogin.setChecked(mUser.isAutoLogin());

        Button buttonLog = findViewById(R.id.button_login);
        Button buttonReg = findViewById(R.id.button_register);
        if(mUser.getMobile() == null || mUser.getMobile().isEmpty()){
            buttonLog.setBackgroundResource(R.drawable.selector_button_bg_s);
            buttonReg.setBackgroundResource(R.drawable.selector_button_bg);
            buttonLog.setTextColor(getResources().getColorStateList(R.drawable.selector_button_txtcolor_s));
            buttonReg.setTextColor(getResources().getColorStateList(R.drawable.selector_button_txtcolor));
        }
        buttonLog.setOnClickListener(this);
        buttonReg.setOnClickListener(this);

        findViewById(R.id.forgot_pwd).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTextMobile.setText(mUser.getMobile());
    }
}
