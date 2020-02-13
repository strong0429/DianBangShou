package com.xingdhl.www.storehelper.user;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xingdhl.www.storehelper.AboutActivity;
import com.xingdhl.www.storehelper.CustomStuff.FreeToast;
import com.xingdhl.www.storehelper.CustomStuff.QueryDialog;
import com.xingdhl.www.storehelper.CustomStuff.ViewPagerAdapter;
import com.xingdhl.www.storehelper.ObjectDefine.GCV;
import com.xingdhl.www.storehelper.ObjectDefine.SaleSummary;
import com.xingdhl.www.storehelper.ObjectDefine.User;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.Trading.SalesAnalysisActivity;
import com.xingdhl.www.storehelper.Trading.SalesSumDataFragment;
import com.xingdhl.www.storehelper.Trading.SalesSumFragment;
import com.xingdhl.www.storehelper.Trading.TradingActivity;
import com.xingdhl.www.storehelper.store.CreateStoreActivity;
import com.xingdhl.www.storehelper.store.PurchaseActivity;
import com.xingdhl.www.storehelper.store.PurchaseReviewActivity;
import com.xingdhl.www.storehelper.store.StorageManageActivity;
import com.xingdhl.www.storehelper.store.StorePagerFragment;
import com.xingdhl.www.storehelper.utility.ScanBardcodeActivity;
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StoreOwnerActivity extends AppCompatActivity implements
        HttpHandler.handlerCallback, QueryDialog.QueryDlgListener,
        View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{
    private final int ACCESS_LOCATION_REQUEST_CODE = 1000;

    private ViewPager mStorePager, mSellCalcPager;
    private TabLayout mTabLayout;
    private DrawerLayout mDrawer;
    private TextView mTitle;

    private List<SalesSumFragment> mSaleSumFragments;

    private List<SaleSummary> mSaleSummaries;
    private HttpHandler mHttpHandler;
    private User mUser;

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.button_sale: //‘开单销售’
                intent = new Intent(StoreOwnerActivity.this, TradingActivity.class);
                intent.putExtra("store_No", mStorePager.getCurrentItem());
                startActivityForResult(intent, 1);
                break;
            case R.id.button_purchase: //‘商品入库’
                if (mUser.getRole() == GCV.CLERK) {
                    FreeToast.makeText(this, "您未授权，不能执行该操作！", Toast.LENGTH_SHORT).show();
                    break;
                }
                intent = new Intent(StoreOwnerActivity.this, PurchaseActivity.class);
                intent.putExtra("store_No", mStorePager.getCurrentItem());
                startActivity(intent);
                break;
            case R.id.button_storage: //‘商品管理’
                intent = new Intent(StoreOwnerActivity.this, StorageManageActivity.class);
                intent.putExtra("store_No", mStorePager.getCurrentItem());
                startActivity(intent);
                break;
            case R.id.button_more: //'我'
                mDrawer.openDrawer(GravityCompat.START);
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()){
            case R.id.menu_scan_try:
                startActivity(new Intent(this, ScanBardcodeActivity.class));
                break;
            case R.id.menu_sell_calc:
                if(mUser.getRole() == GCV.CLERK){
                    FreeToast.makeText(this, "您未授权，不能执行该操作！", Toast.LENGTH_SHORT).show();
                    break;
                }
                intent = new Intent(StoreOwnerActivity.this, SalesAnalysisActivity.class);
                intent.putExtra("store_id", mUser.getStore(mStorePager.getCurrentItem()).getId());
                startActivity(intent);
                break;
            case R.id.menu_clerk_manage:
                if(mUser.getRole() == GCV.CLERK){
                    FreeToast.makeText(this, "您未授权，不能执行该操作！", Toast.LENGTH_SHORT).show();
                    break;
                }
                startActivity(new Intent(StoreOwnerActivity.this, ClerkManageActivity.class));
                break;
            case R.id.menu_store_register:
                if(mUser.getRole() == GCV.CLERK){
                    FreeToast.makeText(this, "您未授权，不能执行该操作！", Toast.LENGTH_SHORT).show();
                    break;
                }
                intent = new Intent(StoreOwnerActivity.this, CreateStoreActivity.class);
                intent.putExtra("Parent", "StoreOwnerActivity");
                startActivityForResult(intent, 0);
                break;
            case R.id.menu_supplier_manage:
                if(mUser.getRole() == GCV.CLERK){
                    FreeToast.makeText(this, "您未授权，不能执行该操作！", Toast.LENGTH_SHORT).show();
                    break;
                }
                intent = new Intent(StoreOwnerActivity.this, SupplierManageActivity.class);
                intent.putExtra("store_id", mUser.getStore(mStorePager.getCurrentItem()).getId());
                startActivity(intent);
                break;
            case R.id.menu_me:
                intent = new Intent(StoreOwnerActivity.this, UserInfomationActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_about:
                intent = new Intent(StoreOwnerActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_purchase:
                if(mUser.getRole() == GCV.CLERK){
                    FreeToast.makeText(this, "您未授权，不能执行该操作！", Toast.LENGTH_SHORT).show();
                    break;
                }
                intent = new Intent(StoreOwnerActivity.this, PurchaseReviewActivity.class);
                intent.putExtra("store_id", mUser.getStore(mStorePager.getCurrentItem()).getId());
                startActivity(intent);
                break;
        }
        mDrawer.closeDrawers();
        return true;
    }

    @Override
    public void onMsgHandler(Message msg) {
        switch (msg.what){
            case WebServiceAPIs.MSG_GET_SELL_SUM:
                if(msg.arg1 == WebServiceAPIs.HTTP_OK){
                    Bundle sumData = msg.getData();
                    setSaleSummary(msg.arg2, sumData);
                }
                if (msg.arg2 == mStorePager.getCurrentItem()) {
                    SaleSummary saleSummary = mSaleSummaries.get(msg.arg2);
                    mSaleSumFragments.get(0).updateText(
                            saleSummary.getDaySum(), saleSummary.getDayRise(),
                            saleSummary.getWeekSum(), saleSummary.getWeekRise(),
                            saleSummary.getMonthSum(), saleSummary.getMonthRise());
                    mSaleSumFragments.get(1).updateText(
                            saleSummary.getDayProfit(), saleSummary.getDayProfitRise(),
                            saleSummary.getWeekProfit(), saleSummary.getWeekProfitRise(),
                            saleSummary.getMonthProfit(), saleSummary.getMonthProfitRise());
                }
                break;
            case WebServiceAPIs.MSG_GET_SELL_SUM_BY_DAY:
                if(msg.arg1 != WebServiceAPIs.HTTP_OK)
                    return;

                double[] daysSum = msg.getData().getDoubleArray("sum");
                double[] cost = msg.getData().getDoubleArray("cost");

                if(daysSum == null || cost == null || daysSum.length == 0){
                    return;
                }
                cost[0] = daysSum[0] - cost[0]; //转换为利润；

                SaleSummary saleSummary = mSaleSummaries.get(msg.arg2);

                daysSum[0] -= saleSummary.getDaySum();
                saleSummary.setDaySum(saleSummary.getDaySum() + daysSum[0]);
                saleSummary.setDayRise(saleSummary.getDayRise() + daysSum[0]);
                saleSummary.setWeekSum(saleSummary.getWeekSum() + daysSum[0]);
                saleSummary.setWeekRise(saleSummary.getWeekRise() + daysSum[0]);
                saleSummary.setMonthSum(saleSummary.getMonthSum() + daysSum[0]);
                saleSummary.setMonthRise(saleSummary.getMonthRise() + daysSum[0]);

                cost[0] -= saleSummary.getDayProfit();  //转换为增长利润；
                saleSummary.setDayProfit(saleSummary.getDayProfit() + cost[0]);
                saleSummary.setWeekProfit(saleSummary.getWeekProfit() + cost[0]);
                saleSummary.setMonthProfit(saleSummary.getMonthProfit() + cost[0]);
                saleSummary.setDayProfitRise(saleSummary.getDayProfitRise() + cost[0]);
                saleSummary.setWeekProfitRise(saleSummary.getWeekProfitRise() + cost[0]);
                saleSummary.setMonthProfitRise(saleSummary.getMonthProfitRise() + cost[0]);

                mSaleSumFragments.get(0).updateText(
                        saleSummary.getDaySum(), saleSummary.getDayRise(),
                        saleSummary.getWeekSum(), saleSummary.getWeekRise(),
                        saleSummary.getMonthSum(), saleSummary.getMonthRise());
                mSaleSumFragments.get(1).updateText(
                        saleSummary.getDayProfit(), saleSummary.getDayProfitRise(),
                        saleSummary.getWeekProfit(), saleSummary.getWeekProfitRise(),
                        saleSummary.getMonthProfit(), saleSummary.getMonthProfitRise());
                break;
            case WebServiceAPIs.MSG_DOWNLOAD_FILE:
                FragmentStatePagerAdapter adapter = (FragmentStatePagerAdapter)mStorePager.getAdapter();
                StorePagerFragment fragment = (StorePagerFragment)adapter.instantiateItem(mStorePager, msg.arg2);

                String filePath = msg.getData().getString("file_name");
                if(msg.arg1 == WebServiceAPIs.HTTP_OK){
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    fragment.setShopImg(bitmap);
                }else{
                    File tmp = new File(filePath);
                    tmp.delete();
                    fragment.setShopImg(R.drawable.store_default);
                }
            default:
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_owner);

        mSaleSummaries = new ArrayList<>();
        mHttpHandler = new HttpHandler(this);
        mUser = User.getUser(null);

        //申请网络定位权限
        if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            /*Manifest.permission.LOCATION_HARDWARE,*/
                            Manifest.permission.RECORD_AUDIO }, ACCESS_LOCATION_REQUEST_CODE);
        }

        //查询当前用户所有店铺指定时间内的日销售额；
        Calendar calendar = Calendar.getInstance(Locale.PRC);
        long endTime = calendar.getTimeInMillis();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        year -= (month == 0 ? 1 : 0);
        month = month == 0 ? 11 : (month - 1);
        calendar.set(year, month, 1, 0, 0, 0);
        long startTime = calendar.getTimeInMillis();
        for(int i = 0; i < mUser.getStores().size(); i++) {
            mSaleSummaries.add(new SaleSummary());
            WebServiceAPIs.getSalesSummary(mHttpHandler, i, startTime, endTime);
        }

        mTitle = (TextView)findViewById(R.id.sell_sum_title);
        mTitle.setText(getString(R.string.sell_calc_title, mUser.getStore(0).getName()));

        findViewById(R.id.button_purchase).setOnClickListener(this);
        findViewById(R.id.button_sale).setOnClickListener(this);
        findViewById(R.id.button_storage).setOnClickListener(this);
        findViewById(R.id.button_more).setOnClickListener(this);

        mDrawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView mMainMenu = (NavigationView)findViewById(R.id.main_menu_view);
        //mMainMenu.setCheckedItem(R.id.menu_supplier_manage);
        mMainMenu.setNavigationItemSelectedListener(this);

        FragmentManager fm = getSupportFragmentManager();

        mSaleSumFragments = new ArrayList<>();
        mSaleSumFragments.add(new SalesSumDataFragment());
        mSaleSumFragments.add(new SalesSumDataFragment());

        mSellCalcPager = (ViewPager)findViewById(R.id.sell_calc_pager);
        mSellCalcPager.setAdapter(new ViewPagerAdapter(fm) {
            @Override
            public int getCurrentPosition() {
                return mSellCalcPager.getCurrentItem();
            }

            @Override
            public Fragment getItem(int position) {
                return mSaleSumFragments.get(position);
            }
            @Override
            public int getCount() {
                return mSaleSumFragments.size();
            }
        });

        mStorePager = (ViewPager)findViewById(R.id.shop_pager);
        mStorePager.setAdapter(new ViewPagerAdapter(fm) {
            @Override
            public int getCurrentPosition() {
                return mStorePager.getCurrentItem();
            }

            @Override
            public Fragment getItem(int position) {
                return StorePagerFragment.newInstance(position, mHttpHandler);
            }
            @Override
            public int getCount() {
                return mUser.getStores().size();
            }
        });
        mStorePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                SaleSummary saleSummary = mSaleSummaries.get(position);
                mSaleSumFragments.get(0).updateText(
                        saleSummary.getDaySum(), saleSummary.getDayRise(),
                        saleSummary.getWeekSum(), saleSummary.getWeekRise(),
                        saleSummary.getMonthSum(), saleSummary.getMonthRise());
                mSaleSumFragments.get(1).updateText(
                        saleSummary.getDayProfit(), saleSummary.getDayProfitRise(),
                        saleSummary.getWeekProfit(), saleSummary.getWeekProfitRise(),
                        saleSummary.getMonthProfit(), saleSummary.getMonthProfitRise());
                mTitle.setText(getString(R.string.sell_calc_title,
                        mUser.getStore(position).getName()));
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mTabLayout = (TabLayout)findViewById(R.id.layout_calc_title);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.setupWithViewPager(mSellCalcPager);
        mTabLayout.getTabAt(0).setText("销售收入");
        mTabLayout.getTabAt(1).setText("销售利润");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == 0) {  //注册店铺返回
            mSaleSummaries.add(new SaleSummary());
            mStorePager.getAdapter().notifyDataSetChanged();
            return;
        }
        if(requestCode == 1){   //商品销售返回
            Calendar calendar = Calendar.getInstance(Locale.PRC);
            long endTime = calendar.getTimeInMillis();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long startTime = calendar.getTimeInMillis();
            WebServiceAPIs.getSaleSumByDay(mHttpHandler, mStorePager.getCurrentItem(), startTime, endTime);
            return;
        }
        if(resultCode == RESULT_OK && requestCode == 2){   //更新店铺信息返回
            mTitle.setText(getString(R.string.sell_calc_title, mUser.getStore(0).getName()));
            mStorePager.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        new QueryDialog(this, "您确认退出吗？").show();
    }

    @Override
    public void onDialogNegativeClick(DialogInterface dialog, int which) {

    }

    @Override
    public void onDialogPositiveClick(DialogInterface dialog, int which) {
        finish();
    }

    private void setSaleSummary(int id, Bundle sumData){
        SaleSummary saleSummary = mSaleSummaries.get(id);
        if(saleSummary == null){
            Log.d(GCV.D_TAG, "setSaleSummary(): id = " + id);
            return;
        }

        //当天销售、利润及环比增长；
        saleSummary.setDaySum(sumData.getDouble("today"));
        saleSummary.setDayProfit(sumData.getDouble("today_p"));
        saleSummary.setDayRise(saleSummary.getDaySum() - sumData.getDouble("yesterday"));
        saleSummary.setDayProfitRise(saleSummary.getDayProfit() - sumData.getDouble("yesterday_p"));

        //本周销售及环比增长
        saleSummary.setWeekSum(sumData.getDouble("week"));
        saleSummary.setWeekProfit(sumData.getDouble("week_p"));
        saleSummary.setWeekRise(saleSummary.getWeekSum() - sumData.getDouble("last_week"));
        saleSummary.setWeekProfitRise(saleSummary.getWeekProfit() - sumData.getDouble("last_week_p"));

        //本月销售及环比增长
        saleSummary.setMonthSum(sumData.getDouble("month"));
        saleSummary.setMonthProfit(sumData.getDouble("month_p"));
        saleSummary.setMonthRise(saleSummary.getMonthSum() - sumData.getDouble("last_month"));
        saleSummary.setMonthProfitRise(saleSummary.getMonthProfit() - sumData.getDouble("last_month_p"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if(grantResults[0] != 0 || grantResults[1] != 0){
                FreeToast.makeText(this, "拒绝位置定位权限申请，将无法自动获取位置信息！", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
