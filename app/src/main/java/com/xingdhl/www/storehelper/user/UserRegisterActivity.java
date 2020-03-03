package com.xingdhl.www.storehelper.user;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;

import java.util.Locale;

public class UserRegisterActivity extends AppCompatActivity implements
        HttpHandler.handlerCallback, QueryDialog.QueryDlgListener, View.OnClickListener{
    private HttpHandler mHttpHandler;
    private User mUser;

    private Button mBtnRegister;
    private Button mBtnChkNum;

    private EditText mETPhoneNum;
    private EditText mETChkNum;
    private EditText mETPasswd;
    private EditText mETPwdRev;

    private CheckBox mChkBProtocol;

    @Override
    public void onDialogNegativeClick(DialogInterface dialog, int which) {

    }

    @Override
    public void onDialogPositiveClick(DialogInterface dialog, int which) {
        startActivity(new Intent(this, UserLoginActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if(mETPhoneNum.getText().length() > 0) {
            new QueryDialog(this, "确认要终止注册吗？").show();
        }else{
            startActivity(new Intent(this, UserLoginActivity.class));
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_chknum:
                if(!mETPhoneNum.getText().toString().matches(GCV.RegExp_cell)){
                    FreeToast.makeText(UserRegisterActivity.this, "请输入正确的手机号码！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                mBtnChkNum.setEnabled(false);
                mHttpHandler.justWait(1000, 60);
                WebServiceAPIs.getCheckCode(mHttpHandler, mETPhoneNum.getText().toString(), 0);
                break;
            case R.id.chkbox_protocol:
                mBtnRegister.setEnabled(mChkBProtocol.isChecked());
                break;
            case R.id.button_register:
                if(!mETPhoneNum.getText().toString().matches(GCV.RegExp_cell)){
                    FreeToast.makeText(this, "请输入正确的手机号码！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!mETChkNum.getText().toString().matches(GCV.RegExp_ChkNum)) {
                    FreeToast.makeText(this, "请输入正确的手机短信校验码！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mETPasswd.getText().length() < 4) {
                    FreeToast.makeText(this, "密码为4~8位数字和字符，请重新输入！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!mETPasswd.getText().toString().equals(mETPwdRev.getText().toString())) {
                    FreeToast.makeText(this, "两次输入密码不一致，请重新输入！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                //申请注册用户
                WebServiceAPIs.userRegister(mHttpHandler, mETPhoneNum.getText().toString(),
                        mETPasswd.getText().toString(), mETChkNum.getText().toString());
                break;
            case R.id.button_protocol:
                FreeToast.makeText(this, "欢迎使用“店帮手”!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMsgHandler(Message msg) {
        switch (msg.what){
            case WebServiceAPIs.MSG_CHECK_NUM:
                if(msg.arg1 == WebServiceAPIs.HTTP_OK) break;
                if(msg.arg1 == WebServiceAPIs.HTTP_NETWORK_ERROR){
                    FreeToast.makeText(this, "无法连接网络！请确认已打开手机网络连接",
                            Toast.LENGTH_SHORT).show();
                }else if(msg.arg1 == -11) {
                    String msgStr = "手机:" + mETPhoneNum.getText() + "已注册!";
                    FreeToast.makeText(this, msgStr, Toast.LENGTH_SHORT).show();
                }else{
                    FreeToast.makeText(this, "验证码短消息发送失败！", Toast.LENGTH_SHORT).show();
                }
                mHttpHandler.stopWait();
                break;
            case WebServiceAPIs.MSG_USER_REGISTER:
                if(msg.arg1 == WebServiceAPIs.HTTP_OK) {
                    mUser.setPhoneNum(mETPhoneNum.getText().toString());
                    mUser.setPassword(mETPasswd.getText().toString());
                    mUser.setAutoLogin(false);
                    startActivity(new Intent(this, UserLoginActivity.class));
                    this.finish();
                    return;
                }
                if(msg.arg1 == -4){
                    FreeToast.makeText(this, "注册失败！校验码和申请注册手机号不一致",
                            Toast.LENGTH_SHORT).show();
                }else if(msg.arg1 == -13) {
                    FreeToast.makeText(this, "注册失败！校验码失效，请重新获取",
                            Toast.LENGTH_SHORT).show();
                } else {
                    FreeToast.makeText(this, "注册失败！请确认输入信息正确",
                            Toast.LENGTH_SHORT).show();
                }
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
        setContentView(R.layout.activity_user_register);

        mHttpHandler = new HttpHandler(this);

        mUser = User.getUser(getApplicationContext());

        mETChkNum = findViewById(R.id.edit_chknum);
        mETPasswd = findViewById(R.id.edit_passward);
        mETPasswd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 8){
                    FreeToast.makeText(UserRegisterActivity.this, "密码长度不能超过8位！",
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
        mETPhoneNum.setText(getIntent().getStringExtra("phone"));

        mBtnChkNum = (Button)findViewById(R.id.button_chknum);
        mBtnChkNum.setOnClickListener(this);

        mChkBProtocol = (CheckBox)findViewById(R.id.chkbox_protocol);
        mChkBProtocol.setOnClickListener(this);

        mBtnRegister = (Button)findViewById(R.id.button_register);
        mBtnRegister.setOnClickListener(this);
        mBtnRegister.setEnabled(mChkBProtocol.isChecked());

        findViewById(R.id.button_protocol).setOnClickListener(this);
    }
}
