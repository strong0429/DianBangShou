package com.xingdhl.www.storehelper.store;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.xingdhl.www.storehelper.CustomStuff.FreeToast;
import com.xingdhl.www.storehelper.CustomStuff.QueryDialog;
import com.xingdhl.www.storehelper.ObjectDefine.Categories;
import com.xingdhl.www.storehelper.ObjectDefine.Goods;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.utility.VoiceInput;
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;

import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

public class GoodsInputActivity extends AppCompatActivity implements VoiceInput.OnVoiceFinish,
        HttpHandler.handlerCallback, QueryDialog.QueryDlgListener{
    private EditText mGoodsName,mGoodsSpec;
    private Spinner mCategorySpinner1, mCategorySpinner2;

    private VoiceInput mVoiceInput;

    private HttpHandler mHttpHandler;
    private Goods mGoods;

    public AdapterView.OnItemSelectedListener mItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            List<Categories.Category> categories = Categories.getGoodsCategories().get(position).getSubCategories();
            ArrayAdapter<Categories.Category> adapter = new ArrayAdapter<>(GoodsInputActivity.this,
                    R.layout.spinner_display_style, R.id.txtvwSpinner, categories);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
            mCategorySpinner2.setAdapter(adapter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_input);

        mHttpHandler = new HttpHandler(this);

        mGoods = new Goods();
        mGoods.setBarcode(getIntent().getStringExtra("barcode"));

        EditText barcode = findViewById(R.id.goods_barcode);
        barcode.setText(mGoods.getBarcode());
        barcode.setKeyListener(null);

        mCategorySpinner2 = findViewById(R.id.goods_category_2);
        mCategorySpinner1 = findViewById(R.id.goods_category_1);
        mCategorySpinner1.setOnItemSelectedListener(mItemSelectedListener);
        if(Categories.getGoodsCategories().size() == 0){
            WebServiceAPIs.getGoodsCategories(mHttpHandler);
        }else{
            ArrayAdapter<Categories.Category> adapter = new ArrayAdapter<>(this,
                    R.layout.spinner_display_style, R.id.txtvwSpinner, Categories.getGoodsCategories());
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
            mCategorySpinner1.setAdapter(adapter);
        }

        mGoodsName =findViewById(R.id.goods_name);
        mGoodsName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressLint("SetTextI18n")
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
        mGoodsSpec = findViewById(R.id.goods_remark);
        mGoodsSpec.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if(text.length() > 32){
                    FreeToast.makeText(GoodsInputActivity.this, "超出最大允许长度！", Toast.LENGTH_SHORT).show();
                    mGoodsSpec.setText(text.substring(0, start) + text.substring(start + count));
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
                    mCategorySpinner1.setAdapter(adapter);
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
    public void onVoiceFinish(String textString, int whoIs) {
        switch (whoIs){
            case R.id.phone_name:
                mGoodsName.setText(textString);
                break;
            case R.id.phone_spec:
                mGoodsSpec.setText(textString);
                break;
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
        if(mCategorySpinner1.getSelectedItemPosition() == 0){
            FreeToast.makeText(GoodsInputActivity.this, "请选择商品大类别", Toast.LENGTH_SHORT).show();
            return;
        }
        if(mCategorySpinner2.getSelectedItemPosition() == 0){
            FreeToast.makeText(GoodsInputActivity.this, "请选择商品子类别", Toast.LENGTH_SHORT).show();
            return;
        }
        mGoods.setName(mGoodsName.getText().toString());
        mGoods.setSpec(mGoodsSpec.getText().toString());
        Categories.Category category = (Categories.Category)mCategorySpinner1.getSelectedItem();
        mGoods.setCategoryId1(category.getId());
        category = (Categories.Category)mCategorySpinner2.getSelectedItem();
        mGoods.setCategoryId2(category.getId());

        WebServiceAPIs.addGoods(mHttpHandler, mGoods);
    }

    @Override
    public void onBackPressed() {
        new QueryDialog(this, "确定放弃录入商品信息吗？").show();
    }
}
