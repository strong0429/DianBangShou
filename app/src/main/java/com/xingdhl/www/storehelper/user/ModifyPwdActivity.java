package com.xingdhl.www.storehelper.user;

import android.content.DialogInterface;
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
import com.xingdhl.www.storehelper.ObjectDefine.User;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;

public class ModifyPwdActivity extends AppCompatActivity implements
        View.OnClickListener, QueryDialog.QueryDlgListener{
    private HttpHandler mHttpHandler;
    private EditText mTxOldPwd, mTxNewPwd, mTxRevPwd;
    private Button mButtonOk;

    private byte mEditMarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);

        mEditMarks = 0;
        mHttpHandler = (HttpHandler) User.getUser(null).getData("msg_handler");

        mButtonOk = (Button)findViewById(R.id.button_pwd_ok);
        mButtonOk.setOnClickListener(this);
        mButtonOk.setEnabled(false);

        mTxOldPwd = (EditText)findViewById(R.id.text_old_pwd);
        mTxOldPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEditMarks |= 0x01;
                if(s.length() == 0){
                    mEditMarks &= 0xFE;
                }
                mButtonOk.setEnabled(mEditMarks != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mTxNewPwd = (EditText)findViewById(R.id.text_new_pwd);
        mTxNewPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEditMarks |= 0x02;
                if(s.length() == 0){
                    mEditMarks &= 0xFD;
                }else if(s.length() > 8){
                    String text = s.toString();
                    FreeToast.makeText(ModifyPwdActivity.this, "密码长度超过 8 位字符和数字!",
                            Toast.LENGTH_SHORT).show();
                    mTxNewPwd.setText(text.substring(0, start) + text.substring(start + count));
                }
                mButtonOk.setEnabled(mEditMarks != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mTxRevPwd = (EditText)findViewById(R.id.text_review_pwd);
        mTxRevPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEditMarks |= 0x04;
                if(s.length() == 0){
                    mEditMarks &= 0xFB;
                }
                mButtonOk.setEnabled(mEditMarks != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() != R.id.button_pwd_ok)
            return;

        String oldPwd = mTxOldPwd.getText().toString();
        String newPwd = mTxNewPwd.getText().toString();
        String reviewPwd = mTxRevPwd.getText().toString();

        if(oldPwd.length() == 0){
            FreeToast.makeText(this, "请输入原来的密码！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(newPwd.length() == 0 || newPwd.length() < 4){
            FreeToast.makeText(this, "请输入新的密码！密码长度 4~8 位数字和字符",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(!newPwd.equals(reviewPwd)){
            FreeToast.makeText(this, "输入的新密码不一致，请重新输入！", Toast.LENGTH_SHORT).show();
            return;
        }

        WebServiceAPIs.modifyPassword(mHttpHandler, newPwd, oldPwd);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(mEditMarks == 0) {
            super.onBackPressed();
        }else{
            new QueryDialog(this, "确认放弃修改密码吗？").show();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogInterface dialog, int which) {

    }

    @Override
    public void onDialogPositiveClick(DialogInterface dialog, int which) {
        finish();
    }
}
