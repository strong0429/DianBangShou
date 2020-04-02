package com.xingdhl.www.storehelper.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;

import java.util.HashMap;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;

public class UserInfomationActivity extends AppCompatActivity implements View.OnClickListener,
        HttpHandler.handlerCallback, QueryDialog.QueryDlgListener{
    private EditText mEtPhone, mEtName, mEtIdcard, mEtEmail;
    private EditText mEtDate, mEtPasswd;

    private Button mButtonOk;

    private User mUser;
    private int mEditMark;
    private HttpHandler mHttpHandler;

    @Override
    public void onMsgHandler(Message msg) {
        switch (msg.what){
            case WebServiceAPIs.MSG_MODIFY_PWD:
                if(msg.arg1 != HTTP_OK)
                    FreeToast.makeText(this, "更改密码失败!", Toast.LENGTH_SHORT).show();
                else
                    FreeToast.makeText(this, "更改密码成功！", Toast.LENGTH_SHORT).show();
                break;
            case WebServiceAPIs.MSG_UPDATE_USER:
                if(msg.arg1 != HTTP_OK){
                    FreeToast.makeText(this, "更新用户信息失败!", Toast.LENGTH_SHORT).show();
                    break;
                }
                FreeToast.makeText(this, "更新用户信息成功!", Toast.LENGTH_SHORT).show();
                mButtonOk.setEnabled(false);
                mUser.setPhoneNum(mEtPhone.getText().toString());
                mUser.setUserName(mEtName.getText().toString());
                mUser.setIdCard(mEtIdcard.getText().toString());
                mUser.setEmail(mEtEmail.getText().toString());
                mEditMark = 0x00;
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_infomation);

        mEditMark = 0;
        mUser = User.getUser(null);
        mHttpHandler = new HttpHandler(this);

        mButtonOk = (Button)findViewById(R.id.button_save);
        mButtonOk.setOnClickListener(this);

        mEtPhone = (EditText)findViewById(R.id.text_phone);
        mEtPhone.setText(mUser.getPhoneNum());
        mEtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().equals(mUser.getPhoneNum())){
                    mEditMark &= 0xFE;
                }else{
                    mEditMark |= 0x01;
                }
                mButtonOk.setEnabled((mEditMark&0xFF) != 0);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mEtDate = (EditText)findViewById(R.id.text_date);
        mEtDate.setKeyListener(null);
        mEtDate.setText(mUser.getRegDate());
        mEtPasswd = (EditText)findViewById(R.id.text_pwd);
        mEtPasswd.setKeyListener(null);
        mEtPasswd.setText("12345678");

        mEtName = (EditText)findViewById(R.id.text_name);
        mEtName.setText(mUser.getUserName());
        mEtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if((s.length() == 0 && (mUser.getUserName() == null || mUser.getUserName().length() == 0)) ||
                        s.toString().equals(mUser.getUserName())){
                    mEditMark &= 0xFD;
                }else{
                    mEditMark |= 0x02;
                }
                mButtonOk.setEnabled((mEditMark&0xFF) != 0);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mEtIdcard = (EditText)findViewById(R.id.text_idcard);
        mEtIdcard.setText(mUser.getIdCard());
        mEtIdcard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if((s.length() == 0 && (mUser.getIdCard() == null || mUser.getIdCard().length() == 0)) ||
                        s.toString().equals(mUser.getIdCard())){
                    mEditMark &= 0xFB;
                }else{
                    mEditMark |= 0x04;
                }
                mButtonOk.setEnabled((mEditMark&0xFF) != 0);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mEtEmail = (EditText)findViewById(R.id.text_email);
        mEtEmail.setText(mUser.getEmail());
        mEtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 128){
                    String text = s.toString();
                    mEtEmail.setText(text.substring(0, start) + text.substring(start + count));
                    FreeToast.makeText(UserInfomationActivity.this, "Email地址超过允许长度！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if((s.length() == 0 && (mUser.getEmail() == null || mUser.getEmail().length() == 0)) ||
                        s.toString().equals(mUser.getEmail())){
                    mEditMark &= 0xF7;
                }else{
                    mEditMark |= 0x08;
                }
                mButtonOk.setEnabled((mEditMark&0xFF) != 0);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        findViewById(R.id.img_photo).setOnClickListener(this);
        findViewById(R.id.button_pwd).setOnClickListener(this);
        findViewById(R.id.button_upload).setOnClickListener(this);
        findViewById(R.id.button_pwd_ok).setOnClickListener(this);
        findViewById(R.id.button_pwd_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_upload:
            case R.id.img_photo:
                FreeToast.makeText(this, "抱歉，该功能暂时未实现。", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_pwd:
                mUser.putData("msg_handler", mHttpHandler);
                startActivity(new Intent(this, ModifyPwdActivity.class));
                break;
            case R.id.button_save:
                if((mEditMark & 0x01) == 1){
                    if(!mEtPhone.getText().toString().matches(GCV.RegExp_cell)){
                        FreeToast.makeText(this, "手机号码格式错误，请重新输入！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if((mEditMark & 0x02) == 2) {
                    if (mEtName.getText().length() > 32) {
                        FreeToast.makeText(UserInfomationActivity.this, "名字超过允许长度！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if((mEditMark & 0x04) == 4){
                    if(!mEtIdcard.getText().toString().matches(GCV.RegExp_id)){
                        FreeToast.makeText(this, "身份证格式错误，请重新输入！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if((mEditMark & 0x08) == 8){
                    String email = mEtEmail.getText().toString();
                    if(email.indexOf('@') == -1 || email.indexOf('.') == -1){
                        FreeToast.makeText(this, "Email地址格式错误，请重新输入！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                v.setVisibility(View.INVISIBLE);
                ((EditText)findViewById(R.id.text_chk_pwd)).setText("");
                findViewById(R.id.layout_popup).setVisibility(View.VISIBLE);
                break;
            case R.id.button_pwd_ok:
                String textPwd = ((EditText)findViewById(R.id.text_chk_pwd)).getText().toString();
                if(textPwd.length() == 0){
                    FreeToast.makeText(this, "请输入密码！", Toast.LENGTH_SHORT).show();
                    return;
                }

                findViewById(R.id.layout_popup).setVisibility(View.INVISIBLE);
                mButtonOk.setVisibility(View.VISIBLE);

                Map<String, Object> mapDatas = new HashMap<>();
                mapDatas.put("pwd", ((EditText)findViewById(R.id.text_chk_pwd)).getText().toString());
                if((mEditMark & 0x01) == 1){
                    mapDatas.put("tel", mEtPhone.getText().toString());
                }
                if((mEditMark & 0x02) == 2){
                    mapDatas.put("name", mEtName.getText().toString());
                }
                if((mEditMark & 0x04) == 4){
                    mapDatas.put("idcard", mEtIdcard.getText().toString());
                }
                if((mEditMark & 0x08) == 8){
                    mapDatas.put("email", mEtEmail.getText().toString());
                }
                WebServiceAPIs.userUpdate(mHttpHandler, mapDatas);
                break;
            case R.id.button_pwd_cancel:
                findViewById(R.id.layout_popup).setVisibility(View.INVISIBLE);
                mButtonOk.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if((mEditMark & 0x0F) != 0){
            new QueryDialog(this, "退出将放弃当前更改内容，确定吗？").show();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onDialogPositiveClick(DialogInterface dialog, int which) {
        finish();
    }

    @Override
    public void onDialogNegativeClick(DialogInterface dialog, int which) {

    }
}
