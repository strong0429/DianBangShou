package com.xingdhl.www.storehelper.trading;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.xingdhl.www.storehelper.CustomStuff.FreeToast;
import com.xingdhl.www.storehelper.CustomStuff.QueryDialog;
import com.xingdhl.www.storehelper.Database.DbSchemaXingDB;
import com.xingdhl.www.storehelper.ObjectDefine.StockGoods;
import com.xingdhl.www.storehelper.ObjectDefine.GCV;
import com.xingdhl.www.storehelper.ObjectDefine.Sales;
import com.xingdhl.www.storehelper.ObjectDefine.User;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.net.HttpURLConnection.HTTP_OK;

public class TradingActivity extends AppCompatActivity implements HttpHandler.handlerCallback,
        QueryDialog.QueryDlgListener, View.OnClickListener{
    private final int REQ_PAYMENT = 1;

    private TextView mTotalMoney;

    private List<TradeFragment> mFragments;
    private Map<String, StockGoods> mStoreGoods;
    private List<Integer> mGoodsId;
    private List<Sales> mSales;

    private HttpHandler mHttpHandler;
    private String mCodeWx, mCodeAli;
    private double mSaleSum;
    private int mStoreId;

    @Override
    public void onMsgHandler(Message msg) {
        Sales sales = null;
        StockGoods storage = null;

        switch (msg.what){
            case GCV.MSG_SALE_ADD:
                sales = mSales.get(msg.arg2);
                mSaleSum += sales.getPrice() * sales.getDiscount();
                mFragments.get(0).updateItem(msg.arg2);
                mFragments.get(1).updateItem(msg.arg1);
                mTotalMoney.setText(getString(R.string.trade_total, mSaleSum));
                break;
            case GCV.MSG_SALE_SUB:
                sales = mSales.get(msg.arg2);
                if(sales.getCount() < 0.0001){ //销量为零，清除该条记录
                    mSaleSum -= sales.getPrice() * sales.getDiscount() * (1 + sales.getCount());
                    mSaleSum = mSaleSum < 0 ? 0 : mSaleSum;
                    mSales.remove(msg.arg2);
                    mGoodsId.remove(msg.arg2);
                }else {
                    mSaleSum -= sales.getPrice() * sales.getDiscount();
                }
                mFragments.get(0).updateItem(null);
                mFragments.get(1).updateItem(msg.arg1);
                mTotalMoney.setText(getString(R.string.trade_total, mSaleSum));
                break;
            case GCV.MSG_MANUAL_SUB:
                mSaleSum -= msg.arg1 + (float)msg.arg2 / 10000.0f;
                mTotalMoney.setText(getString(R.string.trade_total, mSaleSum));
                break;
            case GCV.MSG_MANUAL_ADD:    //手动更新商品销售数量；
                sales = mSales.get(msg.arg2);
                mSaleSum += sales.getSum();
                mTotalMoney.setText(getString(R.string.trade_total, mSaleSum));
                break;
            case GCV.MSG_SALE_NEW: //本地数据库有该商品，新加入销售单；
                storage = mStoreGoods.get(msg.arg1);
                //生成一条新的销售记录
                if(System.currentTimeMillis() > storage.getEndDate_L() ||
                        System.currentTimeMillis() < storage.getStartDate_L())
                    storage.setDiscount(1.0f);
                sales = new Sales(mStoreId, storage.getPrice(),
                        storage.getDiscount(), storage.getBarcode(), storage.getName(), 0);
                mSales.add(sales);
                mGoodsId.add(msg.arg1); //记录该条销售记录对应LocalStorage列表的索引；
                mSaleSum += sales.getSum();
                mFragments.get(0).startScan(1500);
                mFragments.get(0).updateItem(mSales.size() -1);
                mFragments.get(1).updateItem(msg.arg1);
                mTotalMoney.setText(getString(R.string.trade_total, mSaleSum));
                break;
            case WebServiceAPIs.MSG_GET_STOCK:
                if(msg.arg1 == WebServiceAPIs.HTTP_CONTINUE){
                    WebServiceAPIs.getStockGoods(mHttpHandler, mStoreGoods, mStoreId,
                            msg.arg2 + 1, GCV.PAGE_SIZE);
                }else if(msg.arg1 != HTTP_OK) {
                    mStoreGoods.clear();
                    FreeToast.makeText(this, "查询库存商品信息失败，请稍后重试", Toast.LENGTH_SHORT).show();
                }
                mFragments.get(1).updateItem(null);
                break;
            case GCV.MSG_CLEAR_ITEM:
                if(-1 == msg.arg2) {
                    mFragments.get(0).updateItem(null);
                }else {
                    mFragments.get(0).updateItem(msg.arg2);
                }
                mFragments.get(1).updateItem(msg.arg1);
                break;
            case GCV.MSG_SIMPLE_ADD:
                mSaleSum += msg.getData().getDouble("total");
                mTotalMoney.setText(getString(R.string.trade_total, mSaleSum));
                break;
            case GCV.MSG_SIMPLE_SUB:
                mSaleSum -= msg.getData().getDouble("total");
                mTotalMoney.setText(getString(R.string.trade_total, mSaleSum));
                break;
            case GCV.MSG_SALE_DONE: //结账，更新界面及初始化数据；
                mSaleSum = 0f;
                mSales.clear();
                mGoodsId.clear();
                mTotalMoney.setText(getString(R.string.trade_total, mSaleSum));
                mFragments.get(0).updateItem(null);
                mFragments.get(1).updateItem(null);
                mFragments.get(2).updateItem(null);
            case WebServiceAPIs.MSG_ADD_SELL:
                if(msg.arg1 == HTTP_OK)
                    break;
                //数据上传失败，本地缓存处理；
                sales = (Sales)msg.getData().getSerializable("sell_record");
                DbSchemaXingDB.instantiate(null).addRecord(sales);
                break;
            case WebServiceAPIs.MSG_UPLOAD_SELL:
                if(msg.arg1 != HTTP_OK)
                    break;
                sales = (Sales)msg.getData().getSerializable("sell_record");
                DbSchemaXingDB.instantiate(null).delRecord(sales);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trading);

        User user = User.getUser(getApplicationContext());

        int storeNo = getIntent().getIntExtra("store_No", -1);
        mStoreId = user.getStore(storeNo).getId();
        mCodeAli = user.getStore(storeNo).getAliCode();
        mCodeWx = user.getStore(storeNo).getWxCode();
        mStoreGoods = user.getStore(storeNo).getGoodsList();

        mGoodsId = new ArrayList<>();
        mSales = new ArrayList<>();

        mHttpHandler = new HttpHandler(this);

        TradeFragment.init(new ArrayList<>(mStoreGoods.values()), mSales, mGoodsId, mHttpHandler, mStoreId);
        mFragments = new ArrayList<>();
        mFragments.add(TradeFragmentByCode.instance());
        mFragments.add(TradeFragmentByName.instance());
        mFragments.add(TradeFragmentSimple.instance());

        ViewPager mTradePager = (ViewPager)findViewById(R.id.trade_pager);
        mTradePager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }
            @Override
            public int getCount() {
                return mFragments.size();
            }
        });
        mTradePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                if(position == 0) {   //条码销售页
                    mFragments.get(0).startScan(10);
                }else{
                    mFragments.get(0).stopScan();
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout_title);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setupWithViewPager(mTradePager);
        tabLayout.getTabAt(0).setText(getString(R.string.trade_barcode));
        tabLayout.getTabAt(1).setText(getString(R.string.trade_name));
        tabLayout.getTabAt(2).setText(getString(R.string.trade_simple));

        //结算流程处理：弹出窗口选择支付方式并确认金额；
        findViewById(R.id.button_submit).setOnClickListener(this);

        if(mStoreGoods.size() == 0){
            //数据库获取库存商品信息；
            mStoreGoods.clear();
            WebServiceAPIs.getStockGoods(mHttpHandler, mStoreGoods, mStoreId, 0, GCV.PAGE_SIZE);
        }

        mTotalMoney = (TextView)findViewById(R.id.total_money);
        mTotalMoney.setText(getString(R.string.trade_total, 0f));

        uploadSales();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button_submit){
            if(mSales.size() == 0){
                FreeToast.makeText(this, "请添加待销售商品！", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra("pay_sum", mSaleSum);
            intent.putExtra("wx_code", mCodeWx);
            intent.putExtra("ali_code", mCodeAli);
            startActivityForResult(intent, REQ_PAYMENT);
            /*
            final PopupWindow popupWindow = new PopupWindow(TradingActivity.this);
            popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            LayoutInflater inflater = LayoutInflater.from(TradingActivity.this);
            View view = inflater.inflate(R.layout.activity_payment, null);
            view.findViewById(R.id.pay_weichat).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sellDown(1, popupWindow);
                }
            });
            view.findViewById(R.id.pay_alipay).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sellDown(2, popupWindow);
                }
            });
            view.findViewById(R.id.pay_cash).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sellDown(0, popupWindow);
                }
            });
            view.findViewById(R.id.pay_other).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sellDown(3, popupWindow);
                }
            });
            popupWindow.setContentView(view);
            popupWindow.setBackgroundDrawable(new ColorDrawable(0xfff0f0f0));
            popupWindow.setOutsideTouchable(false);
            popupWindow.setFocusable(true);

            ((TextView)view.findViewById(R.id.class_number)).setText(String.valueOf(mSales.size()));
            int totalCount = 0;
            for(Sales sales : mSales)
                totalCount += sales.getCount();
            ((TextView)view.findViewById(R.id.total_count)).setText(String.valueOf(totalCount));
            ((TextView)view.findViewById(R.id.total_money)).setText(
                    getString(R.string.format_float,mSaleSum));

            popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0 );
            */
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK || requestCode != REQ_PAYMENT)
            return;

        int payMode = data.getIntExtra("pay_mode", -1);
        if( payMode != -1){
            Long sellSerialNo = System.currentTimeMillis();
            for(Sales record : mSales){
                record.setSellSn(sellSerialNo);
                record.setStatus((byte)payMode);
                WebServiceAPIs.addSell(mHttpHandler, record);
            }
        }

        Message msg = mHttpHandler.obtainMessage();
        msg.what = GCV.MSG_SALE_DONE;
        mHttpHandler.sendMessage(msg);
    }

    private void uploadSales(){     //上传本地缓存中的交易记录；
        Runnable r = new Runnable() {
            @Override
            public void run() {
                List<Sales> saleList = DbSchemaXingDB.instantiate(null).getSaleRecord(mStoreId);
                if(saleList == null || saleList.size() == 0){
                    mHttpHandler.postDelayed(this, 3 * 60 * 1000);
                }else {
                    WebServiceAPIs.uploadSell(mHttpHandler, saleList.get(0));
                    mHttpHandler.postDelayed(this, 30 * 1000);
                }
            }
        };
        mHttpHandler.post(r);
    }

    @Override
    public void onBackPressed() {
        if(mSales == null || mSales.size() == 0)
            super.onBackPressed();
        else
            new QueryDialog(this, "有商品未结账，确认退出吗？").show();
    }

    @Override
    public void onDialogNegativeClick(DialogInterface dialog, int which) {

    }

    @Override
    public void onDialogPositiveClick(DialogInterface dialog, int which) {
        finish();
    }
}
