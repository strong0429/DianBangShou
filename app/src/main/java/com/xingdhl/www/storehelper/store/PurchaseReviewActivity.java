package com.xingdhl.www.storehelper.store;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xingdhl.www.storehelper.CustomStuff.FreeToast;
import com.xingdhl.www.storehelper.ObjectDefine.Purchase;
import com.xingdhl.www.storehelper.ObjectDefine.Supplier;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.Trading.CalenderActivity;
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PurchaseReviewActivity extends AppCompatActivity implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener, HttpHandler.handlerCallback {
    private final int PAGE_SIZE = 50;

    private TextView mTextStartDate, mTextEndDate;
    private RecyclerView mRecyclerView;
    private RadioGroup mRadioGroup;

    private List<Purchase> mPurchaseRds;
    private List<Supplier> mSuppliers;

    private long mStartDate, mEndDate;
    private int mStoreId;

    private HttpHandler mHttpHandler;

    @Override
    public void onMsgHandler(Message msg) {
        if(msg.what != WebServiceAPIs.MSG_GET_PURCHASE)
            return;

        if(msg.arg1 == WebServiceAPIs.HTTP_CONTINUE){
            int pageNo = msg.arg2 + 1;
            WebServiceAPIs.getPurchaseRds(mHttpHandler, mPurchaseRds, mStoreId, pageNo,
                    PAGE_SIZE, mStartDate, mEndDate);
        }
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private class PurchaseViewHolder extends RecyclerView.ViewHolder{
        private TextView mName, mBarcode;
        private TextView mCount, mPrice, mSum;
        private TextView mSupplier, mBuyer, mDate;

        private PurchaseViewHolder(View view){
            super(view);

            mName = (TextView)view.findViewById(R.id.goods_name);
            mBarcode = (TextView)view.findViewById(R.id.goods_barcode);
            mSupplier = (TextView)view.findViewById(R.id.goods_supplier);
            mBuyer = (TextView)view.findViewById(R.id.goods_buyer);
            mCount = (TextView)view.findViewById(R.id.goods_count);
            mPrice = (TextView)view.findViewById(R.id.goods_price);
            mSum = (TextView)view.findViewById(R.id.goods_sum);
            mDate = (TextView)view.findViewById(R.id.purchase_date);
        }

        private void Bind(Purchase record){
            mName.setText(record.getGoodsName());
            mBarcode.setText(record.getBarcode());
            mCount.setText(getString(R.string.purchase_goods_count, record.getCount(),
                    (record.getUnit() == null) ? "" : record.getUnit()));

            String supplierName = "";
            for(Supplier supplier : mSuppliers){
                if(supplier.getId() == record.getSupplierId())
                    supplierName = supplier.getName();
            }
            mSupplier.setText(getString(R.string.purchase_supplier, supplierName));

            mPrice.setText(getString(R.string.purchase_goods_price, record.getPrice()));
            mBuyer.setText(getString(R.string.purchase_buyer, record.getBuyer()));
            mSum.setText(getString(R.string.purchase_goods_sum,
                    record.getCount() * record.getPrice()));
            mDate.setText(getString(R.string.purchase_date, record.getDateTime()));
        }
    }

    private class PurchaseViewHolderAdapter extends RecyclerView.Adapter<PurchaseViewHolder>{

        @Override
        public PurchaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_purchase_review, parent, false);

            return new PurchaseViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return mPurchaseRds.size();
        }

        @Override
        public void onBindViewHolder(PurchaseViewHolder holder, int position) {
            holder.Bind(mPurchaseRds.get(position));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_review);

        mStoreId = getIntent().getIntExtra("store_id", -1);

        mHttpHandler = new HttpHandler(this);
        mPurchaseRds = new ArrayList<>();
        mSuppliers = new ArrayList<>();

        WebServiceAPIs.getSuppliers(mHttpHandler, mSuppliers, mStoreId);

        Calendar calendar = Calendar.getInstance(Locale.PRC);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        mEndDate = calendar.getTimeInMillis();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1, 0, 0, 0);
        mStartDate = calendar.getTimeInMillis();

        WebServiceAPIs.getPurchaseRds(mHttpHandler, mPurchaseRds, mStoreId, 0,
                PAGE_SIZE, mStartDate, mEndDate);

        mTextStartDate = (TextView)findViewById(R.id.date_start);
        mTextEndDate = (TextView)findViewById(R.id.date_end);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.PRC);
        mTextStartDate.setText(dateFormat.format(mStartDate));
        mTextEndDate.setText(dateFormat.format(mEndDate));

        findViewById(R.id.img_calendar).setOnClickListener(this);
        mRadioGroup = (RadioGroup)findViewById(R.id.radio_group);
        mRadioGroup.setOnCheckedChangeListener(this);
        mRadioGroup.check(R.id.rb_month);

        mRecyclerView = (RecyclerView)findViewById(R.id.list_purchase_rd);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new PurchaseViewHolderAdapter());
    }

    @Override
    public void onClick(View v) {
        if(v.getId() != R.id.img_calendar)
            return;

        startActivityForResult(new Intent(this, CalenderActivity.class), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK || requestCode != 1 || data == null)
            return;

        long startTime = data.getLongExtra("start_time", -1);
        long endTime = data.getLongExtra("end_time", -1);

        if(endTime <= startTime){
            FreeToast.makeText(this, "开始日期不能晚于结束日期，请重新选择！", Toast.LENGTH_SHORT).show();
            return;
        }
        if((endTime - startTime) / (24*60*60*1000) > 366) {
            FreeToast.makeText(this, "日期范围不能超过一年，请重新选择！", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mStartDate == startTime && mEndDate == endTime)
            return;
        mRadioGroup.clearCheck();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.PRC);
        mTextStartDate.setText(dateFormat.format(startTime));
        mTextEndDate.setText(dateFormat.format(endTime));
        mStartDate = startTime;
        mEndDate = endTime;

        mPurchaseRds.clear();
        WebServiceAPIs.getPurchaseRds(mHttpHandler, mPurchaseRds, mStoreId, 0,
                PAGE_SIZE, mStartDate, mEndDate);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId == -1 || !findViewById(checkedId).isPressed())
            return;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.PRC);
        Calendar calendar = Calendar.getInstance(Locale.PRC);

        switch (checkedId){
            case R.id.rb_month: //当月
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                mEndDate = calendar.getTimeInMillis();
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1, 0, 0, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                mStartDate = calendar.getTimeInMillis();
                break;
            case R.id.rb_last_month:    //上月
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1, 23, 59, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                mEndDate = calendar.getTimeInMillis() - 24*60*60*1000;
                calendar.setTimeInMillis(mEndDate);
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1, 0, 0, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                mStartDate = calendar.getTimeInMillis();
                break;
            case R.id.rb_half_year: //半年内
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                mEndDate = calendar.getTimeInMillis();
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR) - (month < 5 ? 1 : 0);
                month = (month < 5) ? (12 + month - 5) : (month - 5);
                calendar.set(year, month, 1, 0, 0, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                mStartDate = calendar.getTimeInMillis();
        }
        mTextStartDate.setText(dateFormat.format(mStartDate));
        mTextEndDate.setText(dateFormat.format(mEndDate));

        mPurchaseRds.clear();
        WebServiceAPIs.getPurchaseRds(mHttpHandler, mPurchaseRds, mStoreId, 0,
                PAGE_SIZE, mStartDate, mEndDate);
    }
}
