package com.xingdhl.www.storehelper.store;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.xingdhl.www.storehelper.CustomStuff.FreeToast;
import com.xingdhl.www.storehelper.CustomStuff.QueryDialog;
import com.xingdhl.www.storehelper.ObjectDefine.Categories;
import com.xingdhl.www.storehelper.ObjectDefine.Goods;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.utility.VoiceInput;
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;

import static java.net.HttpURLConnection.HTTP_OK;

public class GoodsInputActivity extends AppCompatActivity implements VoiceInput.OnVoiceFinish,
        HttpHandler.handlerCallback, QueryDialog.QueryDlgListener{
    private EditText mGoodsName,mGoodsSpec, mGoodsManuf;
    private Spinner mCategoryList;

    private VoiceInput mVoiceInput;

    private HttpHandler mHttpHandler;
    private Goods mGoods;

    @Override
    public void onDialogPositiveClick(DialogInterface dialog, int which) {
        setResult(RESULT_CANCELED);
        mVoiceInput.destroyIatDialog();
        finish();
    }

    @Override
    public void onDialogNegativeClick(DialogInterface dialog, int which) {

    }

    @Override
    public void onMsgHandler(Message msg) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        switch (msg.what){
            case WebServiceAPIs.MSG_GET_CATEGORY:
                if(msg.arg1 == HTTP_OK) {
                    ArrayAdapter<Categories.Category> adapter = new ArrayAdapter<>(this,
                            R.layout.spinner_display_style, R.id.txtvwSpinner, Categories.getGoodsCategories());
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
                    mCategoryList.setAdapter(adapter);
                }
                break;
            case WebServiceAPIs.MSG_ADD_GOODS:
                if(msg.arg1 != HTTP_OK){
                    FreeToast.makeText(this, "录入商品失败，请稍后重试!", Toast.LENGTH_SHORT).show();
                    break;
                }
                bundle.putSerializable("goods", mGoods);
                intent.putExtra("goods", bundle);
                setResult(RESULT_OK,intent);
                finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_input);

        mHttpHandler = new HttpHandler(this);

        mGoods = new Goods();
        mGoods.setBarcode(getIntent().getStringExtra("barcode"));

        EditText barcode = (EditText)findViewById(R.id.goods_barcode);
        barcode.setText(mGoods.getBarcode());
        barcode.setKeyListener(null);

        mCategoryList = (Spinner)findViewById(R.id.goods_category);
        if(Categories.getGoodsCategories().size() == 0){
            WebServiceAPIs.getGoodsCategories(mHttpHandler);
        }else{
            ArrayAdapter<Categories.Category> adapter = new ArrayAdapter<>(this,
                    R.layout.spinner_display_style, R.id.txtvwSpinner, Categories.getGoodsCategories());
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
            mCategoryList.setAdapter(adapter);
        }

        mGoodsName = (EditText)findViewById(R.id.goods_name);
        mGoodsName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 32){
                    FreeToast.makeText(GoodsInputActivity.this, "超出最大允许长度！", Toast.LENGTH_SHORT).show();
                    mGoodsName.setText(text.substring(0, start) + text.substring(start + count));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mGoodsSpec = (EditText)findViewById(R.id.goods_remark);
        mGoodsSpec.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 64){
                    FreeToast.makeText(GoodsInputActivity.this, "超出最大允许长度！", Toast.LENGTH_SHORT).show();
                    mGoodsSpec.setText(text.substring(0, start) + text.substring(start + count));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mGoodsManuf = (EditText)findViewById(R.id.goods_manuf);
        mGoodsManuf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 32){
                    FreeToast.makeText(GoodsInputActivity.this, "超出最大允许长度！", Toast.LENGTH_SHORT).show();
                    mGoodsManuf.setText(text.substring(0, start) + text.substring(start + count));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mVoiceInput = new VoiceInput(this);
        mVoiceInput.createIatDialog(this);
    }

    @Override
    public void onVoiceFinish(String textString, int whoIs) {
        switch (whoIs){
            case R.id.phone_name:
                mGoodsName.setText(textString);
                break;
            case R.id.phone_spec:
                mGoodsSpec.setText(textString);
                break;
            case R.id.phone_manuf:
                mGoodsManuf.setText(textString);
        }
    }

    public void onClick_VoiceInput(View view){
        mVoiceInput.startSpeak(view.getId());
    }

    public void onClick_Input(View view){
        if(mGoodsName.getText().length() == 0){
            FreeToast.makeText(GoodsInputActivity.this, "请输入商品名称", Toast.LENGTH_SHORT).show();
            return;
        }
        if(mGoodsSpec.getText().length() == 0){
            FreeToast.makeText(GoodsInputActivity.this, "请输入商品规格", Toast.LENGTH_SHORT).show();
            return;
        }
        if(mGoodsManuf.getText().length() == 0){
            FreeToast.makeText(GoodsInputActivity.this, "请输入商品生产商", Toast.LENGTH_SHORT).show();
            return;
        }
        if(mCategoryList.getSelectedItemPosition() == 0){
            FreeToast.makeText(GoodsInputActivity.this, "请选择商品类别", Toast.LENGTH_SHORT).show();
            return;
        }
        mGoods.setName(mGoodsName.getText().toString());
        mGoods.setCategory((Categories.Category)mCategoryList.getSelectedItem());
        mGoods.setName_M(mGoodsManuf.getText().toString());
        mGoods.setRemark(mGoodsSpec.getText().toString());
        WebServiceAPIs.addGoods(mHttpHandler, mGoods);
    }

    @Override
    public void onBackPressed() {
        new QueryDialog(this, "确定放弃录入商品信息吗？").show();
    }
}
