package com.xingdhl.www.storehelper.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xingdhl.www.storehelper.CustomStuff.FreeToast;
import com.xingdhl.www.storehelper.CustomStuff.QueryDialog;
import com.xingdhl.www.storehelper.ObjectDefine.GCV;
import com.xingdhl.www.storehelper.ObjectDefine.User;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.HttpRunnable;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;

import java.util.Locale;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Strong on 17/11/11.
 *
 */

public class ResetPasswordActivity extends AppCompatActivity implements
        HttpHandler.handlerCallback, View.OnClickListener, QueryDialog.QueryDlgListener{
    private HttpHandler mHttpHandler;
    private User mUser;

    private Button mBtnChkNum;
    private EditText mETPhoneNum;
    private EditText mETChkNum;
    private EditText mETPasswd;
    private EditText mETPwdRev;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_chknum:
                if(!mETPhoneNum.getText().toString().matches(GCV.RegExp_cell)){
                    FreeToast.makeText(ResetPasswordActivity.this, "请输入正确的手机号码！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                mBtnChkNum.setEnabled(false);
                mHttpHandler.justWait(1000, 60);
                WebServiceAPIs.getSmsCode(mHttpHandler, mETPhoneNum.getText().toString(), true);
                break;
            case R.id.button_reset:
                if(!mETPhoneNum.getText().toString().matches(GCV.RegExp_cell)){
                    FreeToast.makeText(ResetPasswordActivity.this, "请输入正确的手机号码！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mETPasswd.getText().length() < 4) {
                    Toast.makeText(ResetPasswordActivity.this, "请输入4~8位登录密码！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!mETPasswd.getText().equals(mETPwdRev.getText())) {
                    Toast.makeText(ResetPasswordActivity.this, "密码输入不一致，请重新输入！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!mETChkNum.getText().toString().matches(GCV.RegExp_ChkNum)) {
                    Toast.makeText(ResetPasswordActivity.this, "请输入手机短信校验码！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                WebServiceAPIs.resetPassword(mHttpHandler, mETPhoneNum.getText().toString(),
                        mETPasswd.getText().toString(), mETChkNum.getText().toString());
        }
    }

    @Override
    public void onMsgHandler(Message msg) {
        switch (msg.what){
            case WebServiceAPIs.MSG_SMS_CODE:
                if(msg.arg1 == HTTP_OK)
                    break;
                if(msg.arg1 == HttpRunnable.HTTP_NETWORK_ERR)
                    FreeToast.makeText(this, "发送验证码失败，请确认已打开手机网络连接。",
                            Toast.LENGTH_SHORT).show();
                else
                    FreeToast.makeText(this, "发送验证码失败，请稍后重试。", Toast.LENGTH_SHORT).show();
                mHttpHandler.stopWait();
                break;
            case WebServiceAPIs.MSG_RESET_PWD:
                if(msg.arg1 == HTTP_OK){
                    mUser.setPhoneNum(mETPhoneNum.getText().toString());
                    mUser.setPassword(mETPasswd.getText().toString());
                    mUser.setAutoLogin(false);
                    startActivity(new Intent(this, UserLoginActivity.class));
                    finish();
                    return;
                }
                if(msg.arg1 == HttpRunnable.HTTP_NETWORK_ERR)
                    FreeToast.makeText(this, "重置密码失败，请确认已打开手机网络连接。",
                            Toast.LENGTH_SHORT).show();
                else if(msg.arg1 == -13)
                    FreeToast.makeText(this, "重置密码失败！校验码失效，请重新获取。", Toast.LENGTH_SHORT).show();
               else
                    FreeToast.makeText(this, "重置密码失败，请稍后重试。", Toast.LENGTH_SHORT).show();
                break;
            case HttpHandler.JUST_WAITING:
                if(msg.arg1 > 0){
                    mBtnChkNum.setText(String.format(Locale.CHINA, "%d秒后重发", msg.arg1));
                } else{
                    mBtnChkNum.setText("获取验证码");
                    mBtnChkNum.setEnabled(true);
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mUser = User.getUser(getApplicationContext());
        mHttpHandler = new HttpHandler(this);

        mETChkNum = (EditText)findViewById(R.id.edit_chknum);
        mETPasswd = (EditText)findViewById(R.id.edit_passward);
        mETPasswd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 8){
                    FreeToast.makeText(ResetPasswordActivity.this, "密码长度不能超过8位字符和数字！",
                            Toast.LENGTH_SHORT).show();
                    mETPasswd.setText(text.substring(0, start) + text.substring(start + count));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mETPwdRev = (EditText)findViewById(R.id.edit_pwdrev);

        mETPhoneNum = (EditText)findViewById(R.id.edit_phone);
        mETPhoneNum.setText(mUser.getPhoneNum());

        mBtnChkNum = (Button)findViewById(R.id.button_chknum);
        mBtnChkNum.setOnClickListener(this);

        findViewById(R.id.button_reset).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if(mETPhoneNum.getText().toString().matches(GCV.RegExp_cell)) {
            new QueryDialog(this, "确认放弃重置密码吗？").show();
        }else{
            startActivity(new Intent(this, UserLoginActivity.class));
            super.onBackPressed();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogInterface dialog, int which) {

    }

    @Override
    public void onDialogPositiveClick(DialogInterface dialog, int which) {
        startActivity(new Intent(this, UserLoginActivity.class));
        finish();
    }
}
