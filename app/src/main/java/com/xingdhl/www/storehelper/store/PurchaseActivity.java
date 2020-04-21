package com.xingdhl.www.storehelper.store;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;
import com.xingdhl.www.storehelper.CustomStuff.FreeToast;
import com.xingdhl.www.storehelper.CustomStuff.QueryDialog;
import com.xingdhl.www.storehelper.ObjectDefine.StockGoods;
import com.xingdhl.www.storehelper.ObjectDefine.GCV;
import com.xingdhl.www.storehelper.ObjectDefine.Goods;
import com.xingdhl.www.storehelper.ObjectDefine.Purchase;
import com.xingdhl.www.storehelper.ObjectDefine.Store;
import com.xingdhl.www.storehelper.ObjectDefine.Supplier;
import com.xingdhl.www.storehelper.ObjectDefine.User;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.utility.ScanBarcodeUtil;
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;

public class PurchaseActivity extends AppCompatActivity implements View.OnClickListener,
        QueryDialog.QueryDlgListener, HttpHandler.handlerCallback{
    private EditText mBarcode, mGoodsName, mGoodsSpec;
    private EditText mBuyPrice, mPrice, mCount, mUnit;
    private Button mButtonKey, mFlash;
    private Spinner mSpinner;

    private HttpHandler mHttpHandler;
    private ScanBarcodeUtil mScanBarcodeUtil;

    private List<Supplier> mSuppliers;
    private Map<String, StockGoods> mStockGoodsMap;

    private StockGoods mStockGoods;
    private int mStoreId;
    //private int mGoodsId;   //记录入库商品在列表中的索引号；

    @Override
    public void onDialogPositiveClick(DialogInterface dialog, int which){
        if(QueryDialog.whoIs == 1) {
            Intent intent = new Intent(this, GoodsInputActivity.class);
            String barcode = mBarcode.getText().toString();
            if(barcode.length() <= GCV.CUSTOM_BARCODE_LEN){
                barcode = String.format(Locale.CHINA, "%1$08d-%2$04d",
                        mStoreId, Integer.valueOf(barcode));
            }
            intent.putExtra("barcode", barcode);
            startActivityForResult(intent, 1);
        }else if(QueryDialog.whoIs == 0){
            finish();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogInterface dialog, int which){
        mBarcode.setText("");
        mGoodsName.setText("");
        mGoodsSpec.setText("");
        mScanBarcodeUtil.startScan(10);
    }

    @Override
    public void onMsgHandler(Message msg) {
        switch (msg.what){
            case WebServiceAPIs.MSG_GET_SUPPLIER:
                if(msg.arg1 != HTTP_OK)
                    break;
                List<String> spinnerText = new ArrayList<>();
                spinnerText.add("--选择供货商--");
                for(Supplier supplier : mSuppliers){
                    spinnerText.add(supplier.getName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        R.layout.spinner_display_style, R.id.txtvwSpinner, spinnerText);
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
                mSpinner.setAdapter(adapter);
                break;
            case WebServiceAPIs.MSG_GET_GOODS:
                if(msg.arg1 == HTTP_OK){
                    Goods goods = (Goods)msg.getData().getSerializable("goods");
                    assert goods != null;
                    mGoodsName.setText(goods.getName());
                    mGoodsSpec.setText(goods.getSpec());

                    //首次入库，创建库存记录；
                    mStockGoods = new StockGoods(goods);
                } else if(msg.arg1 == WebServiceAPIs.HTTP_NO_EXIST){
                    //弹出对话框确认是否要添加新商品；
                    QueryDialog.whoIs = 1;
                    new QueryDialog(this, "该条码对应商品信息还未录入。\n\n\t现在录入吗？").show();
                }else{
                    FreeToast.makeText(this, "获取商品信息失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                }
                break;
            case WebServiceAPIs.MSG_ADD_PURCHASE:
                if(!mBarcode.isEnabled())
                    mScanBarcodeUtil.startScan(1000);

                if(msg.arg1 == HTTP_OK){
                    if(mStockGoods != null){
                        //新商品，添加到库存列表中；
                        //mStockGoodsMap.add(mStockGoods);
                        mStockGoodsMap.put(mStockGoods.getBarcode(), mStockGoods);
                        mStockGoods = null;
                    }
                    //提交成功，清除GoodsInfo数据；
                    //mSpinner.setSelection(0);
                    mBarcode.setText("");
                    mGoodsName.setText("");
                    mBuyPrice.setText("");
                    mGoodsSpec.setText("");
                    mCount.setText("");
                    mPrice.setText("");
                    //mUnit.setText("件");
                    mUnit.setEnabled(true);
                    mPrice.setEnabled(true);
                }else{
                    FreeToast.makeText(PurchaseActivity.this, "商品入库失败，请稍后重试！",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case WebServiceAPIs.MSG_GET_STOCK:
                String next = msg.getData().getString("next");
                if(next != null && !next.equals("null")){
                    WebServiceAPIs.getStockGoods(mHttpHandler, next, mStockGoodsMap);
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_goods_purchase);

        int storeNo = getIntent().getIntExtra("store_No", -1); //这里是店铺在列表中的序号，非店铺id；
        Store store = User.getUser(null).getStore(storeNo);
        mStoreId = store.getId();

        mHttpHandler = new HttpHandler(this);

        //获取当前店铺的商品价格表；
        mStockGoodsMap = store.getGoodsList();
        if(mStockGoodsMap.size() == 0){
            WebServiceAPIs.getStockGoods(mHttpHandler, mStockGoodsMap, mStoreId, 0, GCV.PAGE_SIZE);
        }

        //远端获取供应商信息;
        mSuppliers = new ArrayList<>();
        mSpinner = findViewById(R.id.spinner_supp);
        WebServiceAPIs.getSuppliers(mHttpHandler, mSuppliers, mStoreId);

        //创建条码扫描功能对象；
        mScanBarcodeUtil = new ScanBarcodeUtil(this,
                (SurfaceView)findViewById(R.id.capture_preview),
                (RelativeLayout)findViewById(R.id.capture_crop_view),
                (ImageView)findViewById(R.id.capture_scan_line)) {
            @Override
            public void handleDecode(Result rawResult, Bundle bundle) {
                //声音提示捕捉到条码；
                beep();
                //成功扫描条码后，关闭相机；
                stopScan();

                //成功读取到条码后，屏蔽手工输入
                mBarcode.setEnabled(false);
                mBarcode.setText(rawResult.getText());
                mButtonKey.setBackgroundResource(R.drawable.keyboard);

                if(!rawResult.getText().matches(GCV.RegExp_Barcode)){
                    FreeToast.makeText(PurchaseActivity.this, "无法识别非标准商品条码，请重新扫描！",
                            Toast.LENGTH_SHORT).show();
                    mScanBarcodeUtil.startScan(1500);
                    return;
                }
                //查询该条码对应的商品信息并显示；
                if(!displayGoodsInfo(rawResult.getText())){
                    //库存列表没有该条码对应商品信息，远端查询商品库；
                    WebServiceAPIs.getGoods(mHttpHandler, rawResult.getText());
                }
            }
        };

        mButtonKey = (Button)findViewById(R.id.key_input);
        mButtonKey.setOnClickListener(this);

        findViewById(R.id.button_ok).setOnClickListener(this);

        mFlash = (Button)findViewById(R.id.flash_switch);
        mFlash.setOnClickListener(this);

        //其它输入栏控件；
        mBarcode = (EditText)findViewById(R.id.goods_barcode);
        mBarcode.setEnabled(false); //条码框默认不允许输入；

        mGoodsName = (EditText)findViewById(R.id.goods_name);
        mGoodsSpec = (EditText)findViewById(R.id.goods_spec);
        mCount = (EditText)findViewById(R.id.goods_count);
        mBuyPrice = (EditText)findViewById(R.id.buy_price);
        mPrice = (EditText)findViewById(R.id.goods_price);
        mUnit = (EditText)findViewById(R.id.goods_unit);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.flash_switch:
                if(mFlash.getTag().equals("on")){
                    mFlash.setTag("off");
                    mScanBarcodeUtil.closeFlash();
                    mFlash.setBackgroundResource(R.drawable.flash_on);
                }else if(mFlash.getTag().equals("off")) {
                    mFlash.setTag("on");
                    mScanBarcodeUtil.openFlash();
                    mFlash.setBackgroundResource(R.drawable.flash_off);
                }
                break;
            case R.id.key_input:
                if(mBarcode.isEnabled()){
                    //确认条码输入完成，提交信息；
                    if(mBarcode.getText().length() == 0) {
                        mBarcode.setEnabled(false);
                        mButtonKey.setBackgroundResource(R.drawable.keyboard);
                        mScanBarcodeUtil.startScan(100);
                        return;
                    }

                    String barcode = mBarcode.getText().toString();
                    if(barcode.length() <= 4){ //自定义商品编码；
                        barcode = String.format(Locale.CHINA,"%1$08d-%2$04d", mStoreId, Integer.valueOf(barcode));
                    }
                    if(!barcode.matches(GCV.RegExp_Barcode)){
                        FreeToast.makeText(this, "条码格式错误，请重新输入！", Toast.LENGTH_SHORT).show();
                        mScanBarcodeUtil.startScan(1500);
                        return;
                    }

                    //在库存商品列表中查询该条码对应的商品信息，并显示；
                    if(!displayGoodsInfo(mBarcode.getText().toString())){
                        WebServiceAPIs.getGoods(mHttpHandler, barcode);
                    }
                }else{  //使能条码输入框，并清除原有内容
                    mButtonKey.setBackgroundResource(R.drawable.submit);
                    mBarcode.setEnabled(true);
                    mBuyPrice.setText("");
                    mBarcode.setText("");
                    mGoodsName.setText("");
                    mGoodsSpec.setText("");
                    mCount.setText("");
                    mPrice.setText("");
                    mUnit.setText("");
                    mPrice.setEnabled(true);
                    mUnit.setEnabled(true);
                    mBarcode.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                    //关闭相机扫描条码；
                    mScanBarcodeUtil.stopScan();
                }
                break;
            case R.id.button_ok:
                if(mBarcode.getText().length() == 0){
                    FreeToast.makeText(PurchaseActivity.this, "请扫描商品条码或输入自定义条码",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mCount.getText().length() ==0){
                    FreeToast.makeText(PurchaseActivity.this, "请输入商品入库数量", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mBuyPrice.getText().length() == 0){
                    FreeToast.makeText(PurchaseActivity.this, "请输入商品采购单价",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mPrice.getText().length() == 0){
                    FreeToast.makeText(PurchaseActivity.this, "请输入商品售价",Toast.LENGTH_SHORT).show();
                    return;
                }

                String barcode = mBarcode.getText().toString();
                if(barcode.length() <= 4){
                    barcode = String.format(Locale.CHINA,"%1$08d-%2$04d", mStoreId, Integer.valueOf(barcode));
                }
                //判断数据完整性；
                int select = mSpinner.getSelectedItemPosition();

                //填充采购记录数据；
                Purchase purchaseRd = new Purchase();
                purchaseRd.setStoreId(mStoreId);
                purchaseRd.setBarcode(barcode);
                purchaseRd.setPrice(Float.parseFloat(mBuyPrice.getText().toString()));
                purchaseRd.setCount(Float.parseFloat(mCount.getText().toString()));
                purchaseRd.setSellPrice(Float.parseFloat(mPrice.getText().toString()));
                //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
                //purchaseRd.setDateTime(dateFormat.format(new Date(System.currentTimeMillis())));
                purchaseRd.setUnit(mUnit.getText().toString());
                if(select > 0) {
                    purchaseRd.setSupplierId(mSuppliers.get(select - 1).getId());
                }

                //填充库存记录数据；
/*
                if(mGoodsPrice != null){
                    mGoodsPrice.setCount(purchaseRd.getCount());
                    mGoodsPrice.setUnit(purchaseRd.getUnit());
                    mGoodsPrice.setPrice(purchaseRd.getSellPrice());
                    mGoodsPrice.setDiscount(1.0f);
                    mGoodsPrice.setEditor(User.getUser(null).getPhoneNum());
                    mGoodsPrice.setEditTime(System.currentTimeMillis());
                    if(select > 0)
                        mGoodsPrice.setSupplier(mSuppliers.get(select - 1).getName());
                }
*/
                WebServiceAPIs.addPurchase(mHttpHandler, purchaseRd, purchaseRd.getSellPrice());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK){
            mBarcode.setText("");
            return;
        }
        if(requestCode == 1){
            //从GoodsInputActivity返回参数中取出商品信息；
            Bundle bundle = data.getBundleExtra("goods");
            Goods goods = (Goods)bundle.getSerializable("goods");
            mGoodsName.setText(goods.getName());
            mGoodsSpec.setText(goods.getSpec());

            //新商品，生成入库记录；
            mStockGoods = new StockGoods(goods);
        }
    }

    private boolean displayGoodsInfo(String barcode){
        if(barcode == null || barcode.length() == 0)
            return false;

        //库存列表中查询该条码对应的商品信息；
        mSpinner.setSelection(0, true);
        StockGoods goods = mStockGoodsMap.get(barcode);
        if(null == goods) {
            //未找到条码对应商品；
            mPrice.setEnabled(true);
            mUnit.setEnabled(true);
            return false;
        }
        mGoodsName.setText(goods.getName());
        mGoodsSpec.setText(goods.getSpec());
        mPrice.setText(String.format(Locale.CHINA, "%.2f", goods.getPrice()));
        mUnit.setText(goods.getUnit());
        mPrice.setEnabled(false);
        mUnit.setEnabled(false);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(mBarcode.getText().length() > 0){
            QueryDialog.whoIs = 0;
            new QueryDialog(this, "退出将终止当前商品入库，确定吗？").show();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();

        mScanBarcodeUtil.onResume(getApplication());
    }

    @Override
    public void onPause() {
        mScanBarcodeUtil.onPause();

        super.onPause();
    }

    @Override
    public void onDestroy() {
        mScanBarcodeUtil.onDestroy();

        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode != 111)
            return;
        if(grantResults[0] == 0 )
            return;

        FreeToast.makeText(this, "您拒绝了相机权限申请，将无法自动识别条码。", Toast.LENGTH_SHORT).show();
    }
}
