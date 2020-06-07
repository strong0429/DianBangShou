package com.xingdhl.www.storehelper.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xingdhl.www.storehelper.CustomStuff.FreeToast;
import com.xingdhl.www.storehelper.CustomStuff.QueryDialog;
import com.xingdhl.www.storehelper.ObjectDefine.GCV;
import com.xingdhl.www.storehelper.ObjectDefine.Supplier;
import com.xingdhl.www.storehelper.ObjectDefine.User;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Strong on 17/11/28.
 *
 */

public class SupplierEditActivity extends AppCompatActivity implements
        HttpHandler.handlerCallback, QueryDialog.QueryDlgListener{
    private EditText mName;
    private EditText mAddr;
    private EditText mTel;
    private EditText mContacter;
    private Button mButtonEdit;

    private HttpHandler mHttpHandler;
    private Supplier mSupplier;

    private int mStoreNo;
    private int mMarks;

    @Override
    public void onMsgHandler(Message msg) {
        Intent arg = new Intent();
        switch (msg.what){
            case WebServiceAPIs.MSG_ADD_SUPPLIER:
                if(msg.arg1 != HTTP_OK){
                    FreeToast.makeText(this, "添加供应商失败，请稍后重试!", Toast.LENGTH_SHORT).show();
                    return;
                }
            case WebServiceAPIs.MSG_UPDATE_SUPPLIER:
                if(msg.arg1 != HTTP_OK){
                    FreeToast.makeText(this, "更新供应商信息失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    return;
                }
                arg.putExtra("bundle", msg.getData());
                setResult(RESULT_OK, arg);
                finish();
                break;
            case WebServiceAPIs.MSG_DEL_SUPPLIER:
                if(msg.arg1 == HTTP_OK) {
                    arg.putExtra("id", mSupplier.getId());
                    setResult(RESULT_OK, arg);
                    finish();
                }else{
                    FreeToast.makeText(this, "删除供应商失败，请稍后重试", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_editer);

        mMarks = 0;
        mHttpHandler = new HttpHandler(this);
        mStoreNo = getIntent().getIntExtra("store_No", -1);

        mContacter = findViewById(R.id.supp_contacter);
        mContacter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 16){
                    FreeToast.makeText(SupplierEditActivity.this, "超过允许最大长度！", Toast.LENGTH_SHORT).show();
                    mContacter.setText(text.substring(0, start) + text.substring(start + count));
                    return;
                }
                if((mSupplier == null && text.length() > 0) ||
                        (mSupplier != null && !text.equals(mSupplier.getContacter()))){
                    mMarks |= 0x01;
                }else{
                    mMarks &= 0x0E;
                }
                mButtonEdit.setEnabled((mMarks & 0x0F) != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mName = findViewById(R.id.supp_name);
        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 32){
                    FreeToast.makeText(SupplierEditActivity.this, "超过允许最大长度！", Toast.LENGTH_SHORT).show();
                    mName.setText(text.substring(0, start) + text.substring(start + count));
                    return;
                }
                if((mSupplier == null && text.length() > 0) ||
                        (mSupplier != null && !text.equals(mSupplier.getName()))){
                    mMarks |= 0x02;
                }else{
                    mMarks &= 0x0D;
                }
                mButtonEdit.setEnabled((mMarks & 0x0F) != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mAddr = findViewById(R.id.supp_addr);
        mAddr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 64){
                    FreeToast.makeText(SupplierEditActivity.this, "超过允许最大长度！", Toast.LENGTH_SHORT).show();
                    mAddr.setText(text.substring(0, start) + text.substring(start + count));
                    return;
                }
                if((mSupplier == null && text.length() > 0) ||
                        (mSupplier != null && !text.equals(mSupplier.getAddr()))){
                    mMarks |= 0x04;
                }else{
                    mMarks &= 0x0B;
                }
                mButtonEdit.setEnabled((mMarks & 0x0F) != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mTel = findViewById(R.id.supp_tel);
        mTel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if((mSupplier == null && text.length() > 0) ||
                        (mSupplier != null && !text.equals(mSupplier.getPhone()))){
                    mMarks |= 0x08;
                }else{
                    mMarks &= 0x07;
                }
                mButtonEdit.setEnabled((mMarks & 0x0F) != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mButtonEdit = findViewById(R.id.supp_edit);
        Button buttonDel = findViewById(R.id.supp_del);

        Bundle bundle = getIntent().getBundleExtra("bundle");
        if(bundle != null) {
            mSupplier = (Supplier) bundle.getSerializable("supplier");
            mContacter.setText(mSupplier.getContacter());
            mName.setText(mSupplier.getName());
            mAddr.setText(mSupplier.getAddr());
            mTel.setText(mSupplier.getPhone());
        }else{
            buttonDel.setEnabled(false);
            //mSupplier = new Supplier();
        }
        mButtonEdit.setEnabled((mMarks & 0x0F) != 0);

        mButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mName.getText().toString().trim().length() == 0){
                    FreeToast.makeText(SupplierEditActivity.this, "请输入供应商名称！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mContacter.getText().toString().trim().length() == 0 ){
                    FreeToast.makeText(SupplierEditActivity.this, "请输入联系人！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!mTel.getText().toString().matches(GCV.RegExp_cell) &&
                        !mTel.getText().toString().matches(GCV.RegExp_phone)){
                    FreeToast.makeText(SupplierEditActivity.this, "请输入正确的联系电话！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mAddr.getText().toString().trim().length() == 0){
                    FreeToast.makeText(SupplierEditActivity.this, "请输入供应商地址！", Toast.LENGTH_SHORT).show();
                    return;
                }

                Supplier supplier = new Supplier(User.getUser(null).getStore(mStoreNo).getId(),
                        mName.getText().toString().trim(),
                        mAddr.getText().toString().trim(),
                        mTel.getText().toString().trim(),
                        mContacter.getText().toString().trim());
                if(mSupplier != null) {
                    supplier.setId(mSupplier.getId());
                    //supplier.setShopId(mSupplier.getShopId());
                    WebServiceAPIs.updateSupplier(mHttpHandler, supplier);
                } else {
                    WebServiceAPIs.addSupplier(mHttpHandler,
                            User.getUser(null).getStore(mStoreNo).getId(), supplier);
                }
            }
        });

        buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryDialog.whoIs = 1;
                new QueryDialog(SupplierEditActivity.this, "确认要删除该供应商?").show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if((mMarks & 0x0F) != 0){
            QueryDialog.whoIs = 0;
            new QueryDialog(this, "退出将丢弃当前输入内容，确定吗？").show();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onDialogPositiveClick(DialogInterface dialog, int which) {
        if(QueryDialog.whoIs == 0){
            finish();
        }else{
            WebServiceAPIs.deleteSupplier(mHttpHandler, mSupplier.getId());
        }
    }

    @Override
    public void onDialogNegativeClick(DialogInterface dialog, int which) {

    }
}
