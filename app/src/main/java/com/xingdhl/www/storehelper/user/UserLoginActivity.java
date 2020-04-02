package com.xingdhl.www.storehelper.user;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.xingdhl.www.storehelper.CustomStuff.FreeToast;
import com.xingdhl.www.storehelper.CustomStuff.QueryDialog;
import com.xingdhl.www.storehelper.ObjectDefine.GCV;
import com.xingdhl.www.storehelper.ObjectDefine.User;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.store.CreateStoreActivity;
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.HttpRunnable;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;

import static java.net.HttpURLConnection.HTTP_OK;

public class UserLoginActivity extends AppCompatActivity implements
        HttpHandler.handlerCallback, QueryDialog.QueryDlgListener, View.OnClickListener{
    private EditText mTextUserName;
    private EditText mTextPasswd;
    private CheckBox mAutoLogin;

    private HttpHandler mHttpHandler;
    private User mUser;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_login:
                mUser.setUserName(mTextUserName.getText().toString());
                mUser.setPassword(mTextPasswd.getText().toString());
                mUser.setAutoLogin(mAutoLogin.isChecked());

                if(mUser.getUserName().isEmpty() || mUser.getPassword().isEmpty()) {
                    FreeToast.makeText(UserLoginActivity.this, "请输入用户名和密码！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                //申请用户登录
                WebServiceAPIs.userLogin(mHttpHandler);
                break;
            case R.id.button_register:
                Intent iRegister = new Intent(UserLoginActivity.this, UserRegisterActivity.class);
                iRegister.putExtra("phone", mTextUserName.getText().toString());
                startActivity(iRegister);
                finish();
                break;
            case R.id.forgot_pwd:
                Intent iForgot = new Intent(UserLoginActivity.this, ResetPasswordActivity.class);
                iForgot.putExtra("phone", mTextUserName.getText().toString());
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
        if(msg.what != WebServiceAPIs.MSG_USER_LOGIN)
            return;

        if(msg.arg1 == HTTP_OK) {
            mUser.saveInfo();
            Intent intent;
            if (mUser.getStores().size() > 0) {
                intent = new Intent(this, StoreOwnerActivity.class);
            } else {
                new QueryDialog(this, "您还没有注册店铺! 现在注册？\n\n\t‘是’注册新店铺。\n\t‘否’退出程序。").show();
                return;
            }
            startActivity(intent);
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

        mTextPasswd = (EditText)findViewById(R.id.user_passward);
        if(mUser.isAutoLogin()) //是否填充密码；
            mTextPasswd.setText(mUser.getPassword());

        mTextUserName = (EditText)findViewById(R.id.user_name);
        mTextUserName.setText(mUser.getUserName());

        mAutoLogin = (CheckBox)findViewById(R.id.remember_pwd);
        mAutoLogin.setChecked(mUser.isAutoLogin());

        Button buttonLog = (Button)findViewById(R.id.button_login);
        Button buttonReg = (Button)findViewById(R.id.button_register);
        if(mUser.getPhoneNum() == null || mUser.getPhoneNum().isEmpty()){
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

        mTextUserName.setText(mUser.getUserName());
    }
}
