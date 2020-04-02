package com.xingdhl.www.storehelper.trading;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xingdhl.www.storehelper.CustomStuff.FreeToast;
import com.xingdhl.www.storehelper.CustomStuff.QueryDialog;
import com.xingdhl.www.storehelper.CustomStuff.ViewPagerAdapter;
import com.xingdhl.www.storehelper.ObjectDefine.Categories;
import com.xingdhl.www.storehelper.ObjectDefine.GCV;
import com.xingdhl.www.storehelper.ObjectDefine.SalesRecord;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static java.net.HttpURLConnection.HTTP_OK;

public class SalesAnalysisActivity extends AppCompatActivity implements
        HttpHandler.handlerCallback, View.OnClickListener, RadioGroup.OnCheckedChangeListener{
    private final int PAGE_SIZE = 50;
    private final int DATE_PICKER = 1;

    private TextView mStartDate, mEndDate;
    private TabLayout mTabLayout;
    private ViewPager mSalesPager;

    private List<SalesSumFragment> mPagerFragments;
    private List<SalesRecord> mSaleDataList;

    private int mStoreId;

    private HttpHandler mHttpHandler;
    private boolean mIsOnFreshing;
    private long mStartTime, mEndTime;

    @Override
    public void onMsgHandler(Message msg) {
        switch (msg.what){
            case WebServiceAPIs.MSG_GET_SELL_DETAIL:
                if(msg.arg1 != HTTP_OK || msg.arg2 != PAGE_SIZE){
                    mIsOnFreshing = false;
                }else { //继续请求数据
                    WebServiceAPIs.getSalesDetail(mHttpHandler, mSaleDataList, mStoreId,
                            mSaleDataList.size() / PAGE_SIZE, PAGE_SIZE, mStartTime, mEndTime);
                }

                //更新ViewPager页面；
                mPagerFragments.get(0).updateText();
                mPagerFragments.get(1).updateText();
            case WebServiceAPIs.MSG_GET_CATEGORY:
                if(msg.arg1 != HTTP_OK){
                    Log.d(GCV.D_TAG, "SalesAnalysisActivity: " + "查询商品类别失败.");
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_analysis);

        mIsOnFreshing = false;
        mSaleDataList = new ArrayList<>();
        mHttpHandler = new HttpHandler(this);
        mStoreId = getIntent().getIntExtra("store_id", -1);

        if(Categories.getGoodsCategories().size() == 0){
            WebServiceAPIs.getGoodsCategories(mHttpHandler);
        }

        mStartDate = (TextView)findViewById(R.id.date_start);
        mEndDate = (TextView)findViewById(R.id.date_end);

        findViewById(R.id.img_calendar).setOnClickListener(this);
        RadioGroup rg = (RadioGroup)findViewById(R.id.radio_group);
        rg.setOnCheckedChangeListener(this);
        RadioButton rb = (RadioButton)findViewById(R.id.rb_day);
        rb.setPressed(true);
        rb.toggle();

        mPagerFragments = new ArrayList<>();
        mPagerFragments.add(new SalesListFragment());
        mPagerFragments.add(new SalesAnalysisFragment());

        FragmentManager fm = getSupportFragmentManager();
        mSalesPager = (ViewPager)findViewById(R.id.pager_sale_calc);
        mSalesPager.setAdapter(new ViewPagerAdapter(fm) {
            @Override
            public int getCurrentPosition() {
                return mSalesPager.getCurrentItem();
            }

            @Override
            public Fragment getItem(int position) {
                mPagerFragments.get(position).setObjectList(mSaleDataList);
                return mPagerFragments.get(position);
            }

            @Override
            public int getCount() {
                return mPagerFragments.size();
            }
        });

        mTabLayout = (TabLayout)findViewById(R.id.tab_sale_calc);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.setupWithViewPager(mSalesPager);
        mTabLayout.getTabAt(0).setText("售货清单");
        mTabLayout.getTabAt(1).setText("销售分析");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK || requestCode != DATE_PICKER){
            return;
        }
        long startTime = data.getLongExtra("start_time", -1);
        long endTime  = data.getLongExtra("end_time", -1);

        //判断日期是否正确；
        if(startTime >= endTime){
            QueryDialog.showAlertMsg(this, "开始日期不能晚于结束日期，请重新选择日期！");
            return;
        }
        //判断日期是否改变；
        if(mStartTime == startTime && mEndTime == endTime){
            return;
        }

        //取消RadioButton checked状态；
        ((RadioGroup)findViewById(R.id.radio_group)).clearCheck();

        //清除远数据，重新加载指定日期内的销售数据；
        mStartTime = startTime;
        mEndTime = endTime;

        mIsOnFreshing = true;
        mSaleDataList.clear();
        WebServiceAPIs.getSalesDetail(mHttpHandler, mSaleDataList, mStoreId, 0, PAGE_SIZE, mStartTime, mEndTime);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        mStartDate.setText(sdf.format(mStartTime));
        mEndDate.setText(sdf.format(mEndTime));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        /*
        当执行RadioGroup的check或clearCheck方法时，会多次触发该回调函数，
        以下判断为避免不必要的重复执行；
        */
        if(checkedId == -1 || !findViewById(checkedId).isPressed())
            return;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        mEndTime = calendar.getTimeInMillis();

        switch (checkedId){
            case R.id.rb_day:
                mStartTime = mEndTime - 24 * 60 * 60 * 1000 + 1;
                break;
            case R.id.rb_week:
                int weekDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                weekDay = (weekDay == 0)?7:weekDay;
                mStartTime = mEndTime - weekDay * 24 * 60 * 60 * 1000 + 1;
                break;
            case R.id.rb_month:
                int monthDay = calendar.get(Calendar.DAY_OF_MONTH);
                mStartTime = mEndTime - monthDay * 24 * 60 * 60 * 1000 + 1;
                break;
            default:
                return;
        }
        mIsOnFreshing = true;
        mSaleDataList.clear();
        WebServiceAPIs.getSalesDetail(mHttpHandler, mSaleDataList, mStoreId, 0, PAGE_SIZE, mStartTime, mEndTime);
        mStartDate.setText(sdf.format(mStartTime));
        mEndDate.setText(sdf.format(mEndTime));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_calendar:
                if(mIsOnFreshing){
                    FreeToast.makeText(this, "正在刷新数据，请稍后...", Toast.LENGTH_SHORT).show();
                    break;
                }
                //打开日期选择Activity
                Intent intent = new Intent(this, CalenderActivity.class);
                intent.putExtra("start_time", mStartTime);
                intent.putExtra("end_time", mEndTime);
                startActivityForResult(intent, DATE_PICKER);
                break;
        }
    }
}
