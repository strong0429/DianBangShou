package com.xingdhl.www.storehelper.Trading;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xingdhl.www.storehelper.CustomStuff.QueryDialog;
import com.xingdhl.www.storehelper.ObjectDefine.Categories;
import com.xingdhl.www.storehelper.ObjectDefine.SaleAnalysis;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.utility.PinyinUtil;
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SalesAnalysisActivity extends AppCompatActivity implements HttpHandler.handlerCallback{
    private final int PAGE_SIZE = 50;

    private Spinner mSpinnerCategory;
    private RecyclerView mRecyclerView;
    private EditText mNameBarcode_Tv;
    private TextView mStartTime_Tv, mEndTime_Tv;
    private TextView mSaleTotal_Tv, mProfitTotal_Tv;
    private TextView mSaleSub_Tv, mProfitSub_Tv;
    private TextView mProfitRateTotal_Tv, mProfitRateSub_Tv;
    private TextView mSaleProportion_Tv, mProfitProportion_Tv;

    private TextWatcher mTextWatcher;
    private AdapterView.OnItemSelectedListener mSpinnerListener;

    private HttpHandler mHttpHandler;
    private List<SaleAnalysis> mSaleDataList;
    private List<SaleAnalysis> mRecycleList;

    private int mStoreId;
    private int mCategoryId;
    private boolean mIsOnFreshing;
    private long mStartTime, mEndTime;

    private float mSaleTotal, mProfitTotal;          //总销售额及总利润；
    private float mSaleSub, mProfitSub;    //单个类别销售额及利润；

    @Override
    public void onMsgHandler(Message msg) {
        switch (msg.what){
            case WebServiceAPIs.MSG_GET_CATEGORY:
                if(msg.arg1 != WebServiceAPIs.HTTP_OK){
                    Toast.makeText(this, "获取商品类型失败!", Toast.LENGTH_LONG).show();
                    break;
                }
                List<String> categoryList = new ArrayList<>();
                for(Categories.Category category : Categories.getGoodsCategories()){
                    categoryList.add(category.getName());
                }
                categoryList.set(0, "全部");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        R.layout.spinner_display_style, R.id.txtvwSpinner, categoryList);
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
                mSpinnerCategory.setAdapter(adapter);
                mSpinnerCategory.setOnItemSelectedListener(mSpinnerListener);
                break;
            case WebServiceAPIs.MSG_GET_SELL_DETAIL:
                if(msg.arg1 != WebServiceAPIs.HTTP_OK || msg.arg2 != PAGE_SIZE){
                    mIsOnFreshing = false;
                }else { //继续请求数据
                    WebServiceAPIs.getSalesDetail(mHttpHandler, mSaleDataList, mStoreId,
                            mSaleDataList.size() / PAGE_SIZE, PAGE_SIZE, mStartTime, mEndTime);
                }
                //检索符合条件数据，并刷新界面;
                if(mCategoryId == -1 && mNameBarcode_Tv.getText().length() == 0) {  //全部商品
                    int size = mSaleDataList.size();
                    mRecycleList.addAll(mSaleDataList.subList(size - msg.arg2, size));
                }else if(mCategoryId == -1){ //全部商品中检索指定名称商品；
                    String text = mNameBarcode_Tv.getText().toString();
                    if(PinyinUtil.isContainsChinese(text)) {
                        for (int id = 0; id < mSaleDataList.size(); id++) {
                            if (mSaleDataList.get(id).getGoodsName().contains(text))
                                mRecycleList.add(mSaleDataList.get(id));
                        }
                    }else{
                        for (int id = 0; id < mSaleDataList.size(); id++) {
                            if (PinyinUtil.converterToFirstSpell(mSaleDataList.get(id).getGoodsName()).contains(text))
                                mRecycleList.add(mSaleDataList.get(id));
                        }
                    }
                }else if(mCategoryId != -1 && mNameBarcode_Tv.getText().length() == 0){
                    //全部商品中检索指定类型商品；
                    for(int id = 0; id < mSaleDataList.size(); id ++){
                        if(mCategoryId == mSaleDataList.get(id).getGoodsCategory())
                            mRecycleList.add(mSaleDataList.get(id));
                    }
                }else if(mCategoryId != -1){ //全部商品中检索指定类型、名称商品；
                    String text = mNameBarcode_Tv.getText().toString();
                    if(PinyinUtil.isContainsChinese(text)) {
                        for (int id = 0; id < mSaleDataList.size(); id++) {
                            if (mCategoryId == mSaleDataList.get(id).getGoodsCategory() &&
                                    mSaleDataList.get(id).getGoodsName().contains(text))
                                mRecycleList.add(mSaleDataList.get(id));
                        }
                    }else{
                        for (int id = 0; id < mSaleDataList.size(); id++) {
                            if (mCategoryId == mSaleDataList.get(id).getGoodsCategory() &&
                                    PinyinUtil.converterToFirstSpell(mSaleDataList.get(id).getGoodsName()).contains(text))
                                mRecycleList.add(mSaleDataList.get(id));
                        }
                    }
                }
                mRecyclerView.getAdapter().notifyDataSetChanged();

                //刷新统计数据；
                mSaleTotal = mProfitTotal = mSaleSub = mProfitSub = 0f;
                for(SaleAnalysis sale : mSaleDataList) { //总销售额及总利润
                    mSaleTotal += sale.getSaleSum();
                    mProfitTotal += sale.getProfit();
                }
                for(SaleAnalysis sale : mRecycleList){ //子商品销售额及利润
                    mSaleSub += sale.getSaleSum();
                    mProfitSub += sale.getProfit();
                }
                updateAnalysis();
                break;
        }
    }

    private class SaleViewHold extends RecyclerView.ViewHolder{
        private TextView mSaleDate;
        private TextView mSaleCount;
        private TextView mGoodsName;
        private TextView mSaleMoney;

        private SaleViewHold(View v){
            super(v);
            mSaleDate = (TextView)v.findViewById(R.id.sale_date);
            mSaleCount = (TextView)v.findViewById(R.id.sale_count);
            mGoodsName = (TextView)v.findViewById(R.id.goods_name);
            mSaleMoney = (TextView)v.findViewById(R.id.sale_money);
        }

        private void Bind(SaleAnalysis recorder){
            mGoodsName.setText(recorder.getGoodsName());
            mSaleDate.setText(recorder.getSaleDateToString());
            mSaleCount.setText(String.format(Locale.CHINA, "%.2f", recorder.getSaleCount()));
            mSaleMoney.setText(String.format(Locale.CHINA, "%.2f", recorder.getSaleSum()));
        }
    }

    private class SaleViewAdapter extends RecyclerView.Adapter<SaleViewHold>{
        private List<SaleAnalysis> mRecoders;

        public SaleViewAdapter(List<SaleAnalysis> recoders){
            mRecoders = recoders;
        }

        @Override
        public int getItemCount() {
            return mRecoders.size();
        }

        @Override
        public SaleViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_sale_calc_list, parent, false);

            SaleViewHold holder = new SaleViewHold(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(SaleViewHold holder, final int position) {
            holder.Bind(mRecoders.get(position));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_analysis);

        mStoreId = getIntent().getIntExtra("store_id", -1);

        mCategoryId = -1;
        mHttpHandler = new HttpHandler(this);
        mSaleDataList = new ArrayList<>();
        mRecycleList = new ArrayList<>();

        //初始化日期为当日；
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        mStartTime = calendar.getTimeInMillis();
        mEndTime = mStartTime + 24 * 60 * 60 * 1000 - 1;

        //初始化销售统计记录列表框RecyclerView；
        mRecyclerView = (RecyclerView)findViewById(R.id.sales_list_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        SaleViewAdapter rvAdapter = new SaleViewAdapter(mRecycleList);
        mRecyclerView.setAdapter(rvAdapter);

        //向服务器请求当天的销售数据
        mIsOnFreshing = true;
        WebServiceAPIs.getSalesDetail(mHttpHandler, mSaleDataList, mStoreId, 0, PAGE_SIZE, mStartTime, mEndTime);

        //初始化日期TextView内容；
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        mStartTime_Tv = (TextView)findViewById(R.id.date_start);
        mEndTime_Tv = (TextView)findViewById(R.id.date_end);
        mStartTime_Tv.setText(sdf.format(mStartTime));
        mEndTime_Tv.setText(sdf.format(mEndTime));

        //初始化统计数据TextView
        mSaleTotal_Tv = (TextView)findViewById(R.id.sale_total);
        mProfitTotal_Tv = (TextView)findViewById(R.id.profit_total);
        mSaleSub_Tv = (TextView)findViewById(R.id.sale_sub);
        mProfitSub_Tv = (TextView)findViewById(R.id.profit_sub);
        mProfitRateTotal_Tv = (TextView)findViewById(R.id.profit_rate_total);
        mProfitRateSub_Tv = (TextView)findViewById(R.id.profit_rate_sub);
        mSaleProportion_Tv = (TextView)findViewById(R.id.sale_proportion);
        mProfitProportion_Tv = (TextView)findViewById(R.id.profit_proportion);

        //初始化商品类型下拉列表框
        mSpinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //清除输入的商品名称或条码；为避免激活监听事件，先去使能；
                mNameBarcode_Tv.removeTextChangedListener(mTextWatcher);
                mNameBarcode_Tv.setText("");
                mNameBarcode_Tv.addTextChangedListener(mTextWatcher);

                //重新装填数据并刷新界面；
                mRecycleList.clear();
                mCategoryId = Categories.getGoodsCategories().get(position).getId();
                if(mCategoryId == -1){  //全部，不分商品类别；
                    mRecycleList.addAll(mSaleDataList);
                }else { //提取指定类型的商品销售数据；
                    for(SaleAnalysis sale : mSaleDataList){
                        if(sale.getGoodsCategory() == mCategoryId) {
                            mRecycleList.add(sale);
                        }
                    }
                }
                mRecyclerView.getAdapter().notifyDataSetChanged();

                //重新计算子销售额、利润等统计数据，并刷新界面；
                mSaleSub = mProfitSub = 0f;
                for(SaleAnalysis sale : mRecycleList) {
                    mSaleSub += sale.getSaleSum();
                    mProfitSub += sale.getProfit();
                }
                updateAnalysis();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        mSpinnerCategory = (Spinner)findViewById(R.id.goods_category);
        if(Categories.getGoodsCategories().size() == 0){
            WebServiceAPIs.getGoodsCategories(mHttpHandler);
        }else{
            List<String> categoryList = new ArrayList<>();
            for(Categories.Category category : Categories.getGoodsCategories()){
                categoryList.add(category.getName());
            }
            categoryList.set(0, "全部");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    R.layout.spinner_display_style, R.id.txtvwSpinner, categoryList);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_style);
            mSpinnerCategory.setAdapter(adapter);
            mSpinnerCategory.setOnItemSelectedListener(mSpinnerListener);
        }

        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0){    //清除商品名检索条件
                    mRecycleList.clear();
                    if(mCategoryId == -1){  //全部商品
                        mRecycleList.addAll(mSaleDataList);
                    }else{ //指定类型商品
                        for(SaleAnalysis sale : mSaleDataList){
                            if(mCategoryId == sale.getGoodsCategory())
                                mRecycleList.add(sale);
                        }
                    }
                }else { //指定商品名称
                    List<SaleAnalysis> tmpList = new ArrayList<>();
                    String text = s.toString();
                    if(PinyinUtil.isContainsChinese(text)) {
                        for (SaleAnalysis sale : mRecycleList) {
                            if (sale.getGoodsName().contains(s))
                                tmpList.add(sale);
                        }
                    }else{
                        for (SaleAnalysis sale : mRecycleList) {
                            if (PinyinUtil.converterToFirstSpell(sale.getGoodsName()).contains(s))
                                tmpList.add(sale);
                        }
                    }
                    mRecycleList.clear();
                    mRecycleList.addAll(tmpList);
                }
                mRecyclerView.getAdapter().notifyDataSetChanged();

                //重新计算子销售额、利润等统计数据，并刷新界面；
                mSaleSub = mProfitSub = 0f;
                for(SaleAnalysis sale : mRecycleList) {
                    mSaleSub += sale.getSaleSum();
                    mProfitSub += sale.getProfit();
                }
                updateAnalysis();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        mNameBarcode_Tv = (EditText) findViewById(R.id.goods_barcode);
        mNameBarcode_Tv.addTextChangedListener(mTextWatcher);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK || requestCode != 1){
            return;
        }

        long startTime = data.getLongExtra("start_time", System.currentTimeMillis());
        long endTime  = data.getLongExtra("end_time", System.currentTimeMillis());

        //判断日期是否正确；
        if(startTime >= endTime){
            QueryDialog.showAlertMsg(this, "开始日期不能晚于结束日期，请重新选择日期！");
            return;
        }
        //判断日期是否改变；
        if(mStartTime == startTime && mEndTime == endTime){
            return;
        }

        //清除远数据，重新加载指定日期内的销售数据；
        mStartTime = startTime;
        mEndTime = endTime;
        mRecycleList.clear();
        mSaleDataList.clear();
        mIsOnFreshing = true;
        WebServiceAPIs.getSalesDetail(mHttpHandler, mSaleDataList, mStoreId, 0, PAGE_SIZE, mStartTime, mEndTime);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        mStartTime_Tv.setText(sdf.format(mStartTime));
        mEndTime_Tv.setText(sdf.format(mEndTime));
    }

    //打开日期选择Activity
    public void onClick_Calendar(View view){
        if(mIsOnFreshing){
            Toast.makeText(this, "正在刷新数据，请稍后...", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, CalenderActivity.class);
        intent.putExtra("start_time", mStartTime);
        intent.putExtra("end_time", mEndTime);
        startActivityForResult(intent, 1);
    }

    private void updateAnalysis(){
        mSaleTotal_Tv.setText(getString(R.string.format_float, mSaleTotal));
        mProfitTotal_Tv.setText(getString(R.string.format_float, mProfitTotal));
        mSaleSub_Tv.setText(getString(R.string.format_float, mSaleSub));
        mProfitSub_Tv.setText(getString(R.string.format_float, mProfitSub));
        if(mSaleTotal != 0f){
            mProfitRateTotal_Tv.setText(getString(R.string.format_float, mProfitTotal / mSaleTotal * 100));
            mSaleProportion_Tv.setText(getString(R.string.format_float, mSaleSub / mSaleTotal * 100));
        }else{
            mProfitRateTotal_Tv.setText(getString(R.string.format_float, 0f));
            mSaleProportion_Tv.setText(getString(R.string.format_float, 0f));
        }
        if(mSaleSub != 0f){
            mProfitRateSub_Tv.setText(getString(R.string.format_float, mProfitSub / mSaleSub * 100));
        }else {
            mProfitRateSub_Tv.setText(getString(R.string.format_float, 0f));
        }
        if(mProfitTotal != 0){
            mProfitProportion_Tv.setText(getString(R.string.format_float, mProfitSub / mProfitTotal * 100));
        }else{
            mProfitProportion_Tv.setText(getString(R.string.format_float, 0f));
        }
    }
}
