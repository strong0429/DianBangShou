package com.xingdhl.www.storehelper.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.xingdhl.www.storehelper.CustomStuff.FreeToast;
import com.xingdhl.www.storehelper.CustomStuff.QueryDialog;
import com.xingdhl.www.storehelper.ObjectDefine.Clerk;
import com.xingdhl.www.storehelper.ObjectDefine.GCV;
import com.xingdhl.www.storehelper.ObjectDefine.Store;
import com.xingdhl.www.storehelper.ObjectDefine.User;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static java.net.HttpURLConnection.HTTP_OK;

public class ClerkEditActivity extends AppCompatActivity implements
        HttpHandler.handlerCallback, QueryDialog.QueryDlgListener{
    private EditText mClerkName, mClerkTel, mIdCard, mAddress, mRegDate, mPassword;
    private Button mBtnDel, mBtnSubmit;
    private Spinner mSpinnerStore;

    private HttpHandler mHttpHandler;
    private Clerk mClerk;

    private int mMarks;

    @Override
    public void onMsgHandler(Message msg) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        switch (msg.what){
            case WebServiceAPIs.MSG_REGISTER_CLERK:
                if(msg.arg1 == HTTP_OK){
                    mClerk.setId(msg.arg2);
                }else if(msg.arg1 == -7) {
                    FreeToast.makeText(this, "每个店铺最多可免费注册 2 个店员！", Toast.LENGTH_SHORT).show();
                    mClerk = new Clerk();
                    return;
                }else{
                    FreeToast.makeText(this, "注册店员失败！请稍后重试", Toast.LENGTH_SHORT).show();
                    mClerk = new Clerk();
                    return;
                }
            case WebServiceAPIs.MSG_UPDATE_CLERK:
                if(msg.arg1 != HTTP_OK){
                    FreeToast.makeText(this, "更新店员信息失败！请稍后重试", Toast.LENGTH_SHORT).show();
                    mClerk = (Clerk)getIntent().getBundleExtra("bundle").getSerializable("clerk");
                    return;
                }
                bundle.putSerializable("clerk", mClerk);
                intent.putExtra("bundle", bundle);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case WebServiceAPIs.MSG_DELETE_CLERK:
                if(msg.arg1 != HTTP_OK){
                    FreeToast.makeText(this, "注销店员失败！请稍后重试", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra("clerk_id", mClerk.getId());
                setResult(RESULT_OK, intent);
                finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clerk_edit);

        mMarks = 0;
        mHttpHandler = new HttpHandler(this);

        mBtnDel = (Button)findViewById(R.id.button_delete);
        mBtnSubmit = (Button)findViewById(R.id.button_submit);
        mBtnSubmit.setEnabled((mMarks & 0x3F) != 0);

        mClerkName = (EditText)findViewById(R.id.clerk_name);
        mClerkName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 16){
                    FreeToast.makeText(ClerkEditActivity.this, "超出名字允许长度!", Toast.LENGTH_SHORT).show();
                    mClerkName.setText(text.substring(0, start) + text.substring(start+count));
                    return;
                }
                if((mClerk.getName() == null && !text.isEmpty()) ||
                        (mClerk.getName() != null && !mClerk.getName().equals(text))){
                    mMarks = mMarks | 0x01;
                }else{
                    mMarks = mMarks & 0xFE;
                }
                mBtnSubmit.setEnabled((mMarks & 0x3F) != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mClerkTel = (EditText)findViewById(R.id.clerk_tel);
        mClerkTel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 11){
                    FreeToast.makeText(ClerkEditActivity.this, "手机号码为11位数字!", Toast.LENGTH_SHORT).show();
                    mClerkTel.setText(text.substring(0, start) + text.substring(start+count));
                    return;
                }
                if((mClerk.getTel() == null && !text.isEmpty()) ||
                        (mClerk.getTel() != null && !mClerk.getTel().equals(text))){
                    mMarks = mMarks | 0x02;
                }else{
                    mMarks = mMarks & 0xFD;
                }
                mBtnSubmit.setEnabled((mMarks & 0x3F) != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mIdCard = (EditText)findViewById(R.id.clerk_idcard);
        mIdCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 18){
                    FreeToast.makeText(ClerkEditActivity.this, "身份证号码不能超过18位！", Toast.LENGTH_LONG).show();
                    mIdCard.setText(text.substring(0, start) + text.substring(start + count));
                    return;
                }
                if((mClerk.getIdCard() == null && !text.isEmpty()) ||
                        (mClerk.getIdCard() != null && !mClerk.getIdCard().equals(text))){
                    mMarks = mMarks | 0x04;
                }else{
                    mMarks = mMarks & 0xFB;
                }
                mBtnSubmit.setEnabled((mMarks & 0x3F) != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mAddress = (EditText)findViewById(R.id.clerk_address);
        mAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 64){
                    FreeToast.makeText(ClerkEditActivity.this, "超出地址允许长度!", Toast.LENGTH_SHORT).show();
                    mAddress.setText(text.substring(0, start) + text.substring(start+count));
                    return;
                }
                if((mClerk.getAddress() == null && !text.isEmpty()) ||
                        (mClerk.getAddress() != null && !mClerk.getAddress().equals(text))){
                    mMarks = mMarks | 0x08;
                }else{
                    mMarks = mMarks & 0xF7;
                }
                mBtnSubmit.setEnabled((mMarks & 0x3F) != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mRegDate = (EditText)findViewById(R.id.register_date);
        mRegDate.setKeyListener(null);
        mPassword = (EditText)findViewById(R.id.clerk_passwd);
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 8){
                    FreeToast.makeText(ClerkEditActivity.this, "超出密码允许长度!", Toast.LENGTH_SHORT).show();
                    mPassword.setText(text.substring(0, start) + text.substring(start+count));
                    return;
                }
                if((mClerk.getPassword() == null && !text.isEmpty()) ||
                        (mClerk.getPassword() != null && !mClerk.getPassword().equals(text))){
                    mMarks = mMarks | 0x10;
                }else{
                    mMarks = mMarks & 0xEF;
                }
                mBtnSubmit.setEnabled((mMarks & 0x3F) != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSpinnerStore = (Spinner)findViewById(R.id.store_name);
        List<Store> storeList = User.getUser(null).getStores();
        ArrayAdapter<Store> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_display_style, R.id.txtvwSpinner, storeList);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
        mSpinnerStore.setAdapter(adapter);
        mSpinnerStore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(mClerk.getStoreId() != ((Store)mSpinnerStore.getSelectedItem()).getId()){
                    mMarks = mMarks | 0x20;
                }else {
                    mMarks = mMarks & 0xDF;
                }
                mBtnSubmit.setEnabled((mMarks & 0x3F) != 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //获取传递参数，设置界面；
        Bundle bundle = getIntent().getBundleExtra("bundle");
        if(bundle != null) {
            mClerk = (Clerk)bundle.getSerializable("clerk");
            for (int i = 0; i < storeList.size(); i++) {
                if (storeList.get(i).getId() == mClerk.getStoreId()) {
                    mSpinnerStore.setSelection(i);
                    break;
                }
            }
            mClerkName.setText(mClerk.getName());
            mClerkTel.setText(mClerk.getTel());
            mAddress.setText(mClerk.getAddress());
            mIdCard.setText(mClerk.getIdCard());
            mRegDate.setText(mClerk.getDate());
            mPassword.setText(mClerk.getPassword());
            mPassword.setEnabled(false);
        }else{
            mBtnSubmit.setText("注册");
            mBtnDel.setEnabled(false);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            mRegDate.setText(sdf.format(System.currentTimeMillis()));
            mClerk = new Clerk();
        }
    }

    public void onClick_Update(View view) {
        if (mClerkName.getText().length() <= 0) {
            FreeToast.makeText(this, "店员姓名不能为空!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!mClerkTel.getText().toString().matches(GCV.RegExp_cell)){
            FreeToast.makeText(this, "请输入正确的手机号码!", Toast.LENGTH_LONG).show();
            return;
        }

        if(!mIdCard.getText().toString().matches(GCV.RegExp_id)){
            FreeToast.makeText(this, "请输入正确的身份证号码！", Toast.LENGTH_LONG).show();
            return;
        }

        if(mPassword.getText().length() < 4){
            Toast.makeText(this, "密码不能少于4位！", Toast.LENGTH_LONG).show();
            return;
        }

        if(mAddress.getText().length() <= 0){
            Toast.makeText(this, "请输入店员居住地址！", Toast.LENGTH_LONG).show();
            return;
        }

        mClerk.setName(mClerkName.getText().toString());
        mClerk.setTel(mClerkTel.getText().toString());
        mClerk.setAddress(mAddress.getText().toString());
        mClerk.setIdCard(mIdCard.getText().toString());
        mClerk.setStoreId(((Store)mSpinnerStore.getSelectedItem()).getId());

        if(mClerk.getId() == 0){ //新增店员，Id=0；
            mClerk.setDate(mRegDate.getText().toString());
            mClerk.setPassword(mPassword.getText().toString());
            WebServiceAPIs.clerkRegister(mHttpHandler, mClerk);
        }else{
            WebServiceAPIs.clerkUpdate(mHttpHandler, mClerk);
        }
    }

    public void onClick_Delete(View view){
        QueryDialog.whoIs = 0;
        new QueryDialog(this, "确认注销店员‘" + mClerk.getName() + "’吗？" +
            "\n\n确定注销，请点击‘是’").show();
    }

    @Override
    public void onBackPressed() {
        if((mMarks & 0x3F) != 0) {
            QueryDialog.whoIs = 1;
            new QueryDialog(this, "退出将放弃当前输入内容，确定吗？").show();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onDialogNegativeClick(DialogInterface dialog, int which) {
    }

    @Override
    public void onDialogPositiveClick(DialogInterface dialog, int which) {
        if(QueryDialog.whoIs == 0)
            WebServiceAPIs.clerkDelete(mHttpHandler, mClerk);
        else if(QueryDialog.whoIs == 1)
            finish();
    }
}
