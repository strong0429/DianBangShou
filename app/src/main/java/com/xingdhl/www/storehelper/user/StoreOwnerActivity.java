package com.xingdhl.www.storehelper.user;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.xingdhl.www.storehelper.AboutActivity;
import com.xingdhl.www.storehelper.CustomStuff.FreeToast;
import com.xingdhl.www.storehelper.CustomStuff.QueryDialog;
import com.xingdhl.www.storehelper.CustomStuff.ViewPagerAdapter;
import com.xingdhl.www.storehelper.ObjectDefine.GCV;
import com.xingdhl.www.storehelper.ObjectDefine.SaleSummary;
import com.xingdhl.www.storehelper.ObjectDefine.User;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.trading.SalesAnalysisActivity;
import com.xingdhl.www.storehelper.trading.SalesSumDataFragment;
import com.xingdhl.www.storehelper.trading.TradingActivity;
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
import java.util.List;
import java.util.Objects;

import static java.net.HttpURLConnection.HTTP_OK;

public class StoreOwnerActivity extends AppCompatActivity implements
        HttpHandler.handlerCallback, QueryDialog.QueryDlgListener,
        View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{
    private final int ACCESS_LOCATION_REQUEST_CODE = 1000;

    private ViewPager2 mSellCalcPager;
    private ViewPager2 mStorePager;

    private DrawerLayout mDrawer;
    private TextView mTitle;

    private List<SaleSummary> mSaleSummaries;
    private HttpHandler mHttpHandler;
    private User mUser;

    @Override
    public void onClick(@NonNull View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.button_sale: //‘开单销售’
                intent = new Intent(StoreOwnerActivity.this, TradingActivity.class);
                intent.putExtra("store_No", mStorePager.getCurrentItem());
                startActivityForResult(intent, 1);
                break;
            case R.id.button_purchase: //‘商品入库’, 店员无权限
                if (mUser.getStore(mStorePager.getCurrentItem()).getPosition().equals("C")) {
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
                if(mUser.getStaffStatus() == GCV.CLERK){
                    FreeToast.makeText(this, "您未授权，不能执行该操作！", Toast.LENGTH_SHORT).show();
                    break;
                }
                intent = new Intent(StoreOwnerActivity.this, SalesAnalysisActivity.class);
                intent.putExtra("store_id", mUser.getStore(mStorePager.getCurrentItem()).getId());
                startActivity(intent);
                break;
            case R.id.menu_clerk_manage:
                if(mUser.getStaffStatus() == GCV.CLERK){
                    FreeToast.makeText(this, "您未授权，不能执行该操作！", Toast.LENGTH_SHORT).show();
                    break;
                }
                startActivity(new Intent(StoreOwnerActivity.this, ClerkManageActivity.class));
                break;
            case R.id.menu_store_register:
                if(mUser.getStaffStatus() == GCV.CLERK){
                    FreeToast.makeText(this, "您未授权，不能执行该操作！", Toast.LENGTH_SHORT).show();
                    break;
                }
                intent = new Intent(StoreOwnerActivity.this, CreateStoreActivity.class);
                intent.putExtra("Parent", "StoreOwnerActivity");
                startActivityForResult(intent, 0);
                break;
            case R.id.menu_supplier_manage:
                if(mUser.getStaffStatus() == GCV.CLERK){
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
                if(mUser.getStaffStatus() == GCV.CLERK){
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
                if(msg.arg1 == HTTP_OK){
                    Bundle sumData = msg.getData();
                    setSaleSummary(msg.arg2, sumData);
                }
                if (msg.arg2 == mStorePager.getCurrentItem()) {
                    updateSellPager(msg.arg2);
                }
                break;
            case WebServiceAPIs.MSG_DOWNLOAD_FILE:
                FragmentStateAdapter adapter = (FragmentStateAdapter)mStorePager.getAdapter();
                //StorePagerFragment fragment = (StorePagerFragment)adapter.instantiateItem(mStorePager, msg.arg2);
                StorePagerFragment fragment = null; //adapter.mapFragment.get(mStorePager.getCurrentItem());

                String filePath = msg.getData().getString("file_name");
                if(msg.arg1 == HTTP_OK){
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

        //查询当前用户所有店铺上月至今的销售额统计；
        for(int i = 0; i < mUser.getStores().size(); i++) {
            mSaleSummaries.add(new SaleSummary());
            WebServiceAPIs.getSalesSummary(mHttpHandler, i);
        }

        mTitle = findViewById(R.id.sell_sum_title);
        mTitle.setText(getString(R.string.sell_calc_title, mUser.getStore(0).getName()));

        findViewById(R.id.button_purchase).setOnClickListener(this);
        findViewById(R.id.button_sale).setOnClickListener(this);
        findViewById(R.id.button_storage).setOnClickListener(this);
        findViewById(R.id.button_more).setOnClickListener(this);

        mDrawer = findViewById(R.id.drawer_layout);
        NavigationView mMainMenu = findViewById(R.id.main_menu_view);
        //mMainMenu.setCheckedItem(R.id.menu_supplier_manage);
        mMainMenu.setNavigationItemSelectedListener(this);

        mSellCalcPager = findViewById(R.id.sell_calc_pager);
        mSellCalcPager.setAdapter(new ViewPagerAdapter(this, SalesSumDataFragment.class, 2));

        mStorePager = findViewById(R.id.shop_pager);
        mStorePager.setAdapter(new ViewPagerAdapter(this, StorePagerFragment.class,
                mUser.getStores().size(), mHttpHandler, mStorePager.getCurrentItem()));

        TabLayout tabLayout = findViewById(R.id.layout_calc_title);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        new TabLayoutMediator(tabLayout, mSellCalcPager,
                new TabLayoutMediator.TabConfigurationStrategy(){
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        String[] tabText = {"销售收入", "销售利润"};
                        tab.setText(tabText[position]);
                    }
                }).attach();

        mStorePager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                mTitle.setText(getString(R.string.sell_calc_title,
                        mUser.getStore(position).getName()));
                updateSellPager(position);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {  //注册店铺返回
            mSaleSummaries.add(new SaleSummary());
            Objects.requireNonNull(mStorePager.getAdapter()).notifyDataSetChanged();
            return;
        }
        if (requestCode == 1) {   //商品销售返回
            WebServiceAPIs.getSalesSummary(mHttpHandler, mStorePager.getCurrentItem());
            return;
        }
        if (resultCode == RESULT_OK && requestCode == 2) {   //更新店铺信息返回
            mTitle.setText(getString(R.string.sell_calc_title, mUser.getStore(0).getName()));
            Objects.requireNonNull(mStorePager.getAdapter()).notifyDataSetChanged();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if(grantResults[0] != 0 || grantResults[1] != 0){
                FreeToast.makeText(this, "拒绝位置定位权限申请，将无法自动获取位置信息！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateSellPager(int position){
        SaleSummary saleSummary = mSaleSummaries.get(position);
        ViewPagerAdapter adapter = (ViewPagerAdapter)mSellCalcPager.getAdapter();
        List<Object> fragments = adapter.getFragments();
        if(fragments.size() < 2) return;
        ((SalesSumDataFragment)(fragments.get(0))).updateText(
                saleSummary.getDaySum(), saleSummary.getDayRise(),
                saleSummary.getWeekSum(), saleSummary.getWeekRise(),
                saleSummary.getMonthSum(), saleSummary.getMonthRise());
        ((SalesSumDataFragment)(fragments.get(1))).updateText(
                saleSummary.getDayProfit(), saleSummary.getDayProfitRise(),
                saleSummary.getWeekProfit(), saleSummary.getWeekProfitRise(),
                saleSummary.getMonthProfit(), saleSummary.getMonthProfitRise());
    }

    private void setSaleSummary(int id, Bundle sumData){
        SaleSummary saleSummary = mSaleSummaries.get(id);
        if(saleSummary == null){
            Log.d(GCV.D_TAG, "setSaleSummary(): id = " + id);
            return;
        }

        //当天销售、利润及环比增长；
        saleSummary.setDaySum(sumData.getDouble("td_sum"));
        saleSummary.setDayProfit(sumData.getDouble("td_pro"));
        saleSummary.setDayRise(saleSummary.getDaySum() - sumData.getDouble("yd_sum"));
        saleSummary.setDayProfitRise(saleSummary.getDayProfit() - sumData.getDouble("yd_pro"));

        //本周销售及环比增长
        saleSummary.setWeekSum(sumData.getDouble("cw_sum"));
        saleSummary.setWeekProfit(sumData.getDouble("cw_pro"));
        saleSummary.setWeekRise(saleSummary.getWeekSum() - sumData.getDouble("lw_sum"));
        saleSummary.setWeekProfitRise(saleSummary.getWeekProfit() - sumData.getDouble("lw_pro"));

        //本月销售及环比增长
        saleSummary.setMonthSum(sumData.getDouble("cm_sum"));
        saleSummary.setMonthProfit(sumData.getDouble("cm_pro"));
        saleSummary.setMonthRise(saleSummary.getMonthSum() - sumData.getDouble("lm_sum"));
        saleSummary.setMonthProfitRise(saleSummary.getMonthProfit() - sumData.getDouble("lm_pro"));
    }

}
