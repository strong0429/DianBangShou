package com.xingdhl.www.storehelper.user;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.support.design.widget.NavigationView;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xingdhl.www.storehelper.AboutActivity;
import com.xingdhl.www.storehelper.CustomStuff.FreeToast;
import com.xingdhl.www.storehelper.CustomStuff.QueryDialog;
import com.xingdhl.www.storehelper.CustomStuff.ViewPagerAdapter;
import com.xingdhl.www.storehelper.ObjectDefine.GCV;
import com.xingdhl.www.storehelper.ObjectDefine.OnItemClickListener;
import com.xingdhl.www.storehelper.ObjectDefine.User;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.Trading.SalesAnalysisActivity;
import com.xingdhl.www.storehelper.Trading.TradingActivity;
import com.xingdhl.www.storehelper.store.CreateStoreActivity;
import com.xingdhl.www.storehelper.store.PurchaseActivity;
import com.xingdhl.www.storehelper.store.StorageManageActivity;
import com.xingdhl.www.storehelper.store.StorePagerFragment;
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StoreOwnerActivity extends AppCompatActivity implements
        HttpHandler.handlerCallback, OnItemClickListener, QueryDialog.QueryDlgListener{
    private final int ACCESS_LOCATION_REQUEST_CODE = 1000;

    private DrawerLayout mDrawer;
    private ViewPager mStorePager, mSellCalcPage;

    private TextView mDaySumTV, mDayRiseTV;
    private TextView mWeekSumTV, mWeekRiseTV;
    private TextView mMonthSumTV, mMonthRiseTV;

    private List<SaleSummary> mSaleSummaries;
    private HttpHandler mHttpHandler;
    private User mUser;

    private class SaleSummary{
        double daySum, dayRise;
        double weekSum, weekRise;
        double monthSum, monthRise;

        private SaleSummary(){

        }
    }

    private class FunctionIcon{
        private Integer iconSrcId;
        private String iconText;

        private FunctionIcon(Integer iconSrcId, String iconText){
            this.iconSrcId = iconSrcId;
            this.iconText = iconText;
        }
    }

    private class IconHolder extends RecyclerView.ViewHolder{
        private TextView iconText;
        private ImageView iconImg;

        private IconHolder(View view) {
            super(view);

            iconText = (TextView)view.findViewById(R.id.icon_text);
            iconImg = (ImageView)view.findViewById(R.id.icon_image);
        }

        private void Bind(FunctionIcon functionIcon){
            iconImg.setImageResource(functionIcon.iconSrcId);
            iconText.setText(functionIcon.iconText);
        }
    }

    private class IconListAdapter extends RecyclerView.Adapter<IconHolder> {
        private OnItemClickListener mOnClickListener;
        private List<FunctionIcon> iconList;

        public void setOnItemClickListener(OnItemClickListener onClickListener){
            mOnClickListener = onClickListener;
        }

        private IconListAdapter(List<FunctionIcon> iconList){
            this.iconList = iconList;
        }

        @Override
        public int getItemCount() {
            return iconList.size();
        }

        @Override
        public IconHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_icon_list, parent, false);

            IconHolder holder = new IconHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(IconHolder holder, final int position) {
            holder.Bind(iconList.get(position));
            if(mOnClickListener == null)
                return;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.onClick(v, position);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnClickListener.onLongClick(v, position);
                    return false;
                }
            });
        }
    }

    @Override
    public void onClick(View v, int position) {
        Intent intent;
        switch (position){
            case 0: //'我'
                mDrawer.openDrawer(GravityCompat.START);
                break;
            case 1: //‘开单销售’
                intent = new Intent(StoreOwnerActivity.this, TradingActivity.class);
                intent.putExtra("store_No",  mStorePager.getCurrentItem());
                startActivityForResult(intent, 1);
                break;
            case 2: //‘商品入库’
                if(mUser.getRole() == GCV.CLERK){
                    FreeToast.makeText(this, "您未授权，不能执行该操作！", Toast.LENGTH_SHORT).show();
                    break;
                }
                intent = new Intent(StoreOwnerActivity.this, PurchaseActivity.class);
                intent.putExtra("store_No", mStorePager.getCurrentItem());
                startActivity(intent);
                break;
            case 3: //‘商品管理’
                intent = new Intent(StoreOwnerActivity.this, StorageManageActivity.class);
                intent.putExtra("store_No", mStorePager.getCurrentItem());
                startActivity(intent);
                break;
            case 4: //‘销售统计’
                intent = new Intent(StoreOwnerActivity.this, SalesAnalysisActivity.class);
                intent.putExtra("store_id", mUser.getStore(mStorePager.getCurrentItem()).getId());
                startActivity(intent);
                break;
            case 5: //‘店员管理’
                if(mUser.getRole() == GCV.CLERK){
                    FreeToast.makeText(this, "您未授权，不能执行该操作！", Toast.LENGTH_SHORT).show();
                    break;
                }
                startActivity(new Intent(StoreOwnerActivity.this, ClerkManageActivity.class));
                break;
            case 6: //‘注册新店’
                if(mUser.getRole() == GCV.CLERK){
                    FreeToast.makeText(this, "您未授权，不能执行该操作！", Toast.LENGTH_SHORT).show();
                    break;
                }
                intent = new Intent(StoreOwnerActivity.this, CreateStoreActivity.class);
                intent.putExtra("Parent", "StoreOwnerActivity");
                startActivityForResult(intent, 0);
                break;
            case 7: //‘供货商管理’
                if(mUser.getRole() == GCV.CLERK){
                    FreeToast.makeText(this, "您未授权，不能执行该操作！", Toast.LENGTH_SHORT).show();
                    break;
                }
                intent = new Intent(StoreOwnerActivity.this, SupplierManageActivity.class);
                intent.putExtra("store_No", mStorePager.getCurrentItem());
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onLongClick(View v, int position) {
    }

    @Override
    public void onMsgHandler(Message msg) {
        switch (msg.what){
            case WebServiceAPIs.MSG_GET_SELL_SUM:
                if(msg.arg1 == WebServiceAPIs.HTTP_OK){
                    setSaleSummary(msg.arg2);
                }
                if (msg.arg2 == mStorePager.getCurrentItem()) {
                    updateTextView(msg.arg2);
                }
                break;
            case WebServiceAPIs.MSG_GET_SELL_SUM_ONE_DAY:
                if(msg.arg1 != WebServiceAPIs.HTTP_OK)
                    return;
                double daySum = ((List<Double>) mUser.getData("sale_sum_list")).get(0);
                SaleSummary saleSummary = mSaleSummaries.get(msg.arg2);
                daySum -= saleSummary.daySum;
                saleSummary.daySum += daySum;
                saleSummary.dayRise += daySum;
                saleSummary.weekSum += daySum;
                saleSummary.weekRise += daySum;
                saleSummary.monthSum += daySum;
                saleSummary.monthRise += daySum;
                updateTextView(msg.arg2);
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
                    fragment.setShopImg(R.drawable.store);
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
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION },
                    ACCESS_LOCATION_REQUEST_CODE);//自定义的code
        }

        //查询当前用户所有店铺指定时间内的日销售额；
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        year -= (month == 0 ? 1 : 0);
        month = month == 0 ? 11 : (month - 1);
        calendar.set(year, month, 1, 0, 0, 0);
        long startTime = calendar.getTimeInMillis();
        for(int i = 0; i < mUser.getStores().size(); i++) {
            mSaleSummaries.add(new SaleSummary());
            WebServiceAPIs.getSalesByDay(mHttpHandler, i, startTime, endTime);
        }

        mDrawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView mMainMenu = (NavigationView)findViewById(R.id.main_menu_view);
        //mMainMenu.setCheckedItem(R.id.menu_supplier_manage);
        mMainMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Intent intent;
                switch(item.getItemId()){
                    case R.id.menu_about:
                        intent = new Intent(StoreOwnerActivity.this, AboutActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.menu_me:
                        intent = new Intent(StoreOwnerActivity.this, UserInfomationActivity.class);
                        startActivity(intent);
                        break;
                }
                mDrawer.closeDrawers();
                return true;
            }
        });

        mDaySumTV = (TextView)findViewById(R.id.sale_today);
        mDayRiseTV = (TextView)findViewById(R.id.sale_yesterday);
        mWeekSumTV = (TextView)findViewById(R.id.sale_week);
        mWeekRiseTV = (TextView)findViewById(R.id.sale_last_week);
        mMonthSumTV = (TextView)findViewById(R.id.sale_month);
        mMonthRiseTV = (TextView)findViewById(R.id.sale_last_month);

        FragmentManager fm = getSupportFragmentManager();
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
                updateTextView(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        List<FunctionIcon> iconList = new ArrayList<>();
        iconList.add(new FunctionIcon(R.drawable.me,"我的"));
        iconList.add(new FunctionIcon(R.drawable.icon_sell,"开单销售"));
        iconList.add(new FunctionIcon(R.drawable.purchase, "进货入库"));
        iconList.add(new FunctionIcon(R.drawable.goods_input, "货物管理"));
        iconList.add(new FunctionIcon(R.drawable.saleanalysis, "销售统计"));
        iconList.add(new FunctionIcon(R.drawable.clerks, "店员管理"));
        iconList.add(new FunctionIcon(R.drawable.store_icon, "注册新店"));
        iconList.add(new FunctionIcon(R.drawable.supplier, "供货商管理"));

        RecyclerView iconsView;
        iconsView = (RecyclerView)findViewById(R.id.icon_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        iconsView.setLayoutManager(layoutManager);
        IconListAdapter iconListAdapter = new IconListAdapter(iconList);
        iconListAdapter.setOnItemClickListener(this);
        iconsView.setAdapter(iconListAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == 0) {  //注册店铺返回
            mSaleSummaries.add(new SaleSummary());
            mStorePager.getAdapter().notifyDataSetChanged();
            return;
        }
        if(requestCode == 1){   //商品销售返回
            Calendar calendar = Calendar.getInstance();
            long endTime = calendar.getTimeInMillis();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.set(year, month, day, 0, 0, 0);
            long startTime = calendar.getTimeInMillis();
            WebServiceAPIs.getSalesOneDay(mHttpHandler, mStorePager.getCurrentItem(), startTime, endTime);
            return;
        }
        if(resultCode == RESULT_OK && requestCode == 2){   //更新店铺信息返回
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

    private void updateTextView(int id){
        SaleSummary saleData = mSaleSummaries.get(id);
        mDaySumTV.setText(String.format(Locale.CHINA, "%.2f", saleData.daySum));
        mDayRiseTV.setText(String.format(Locale.CHINA, "%.2f", saleData.dayRise));
        mWeekSumTV.setText(String.format(Locale.CHINA, "%.2f", saleData.weekSum));
        mWeekRiseTV.setText(String.format(Locale.CHINA, "%.2f", saleData.weekRise));
        mMonthSumTV.setText(String.format(Locale.CHINA, "%.2f", saleData.monthSum));
        mMonthRiseTV.setText(String.format(Locale.CHINA, "%.2f", saleData.monthRise));
    }
    private void setSaleSummary(int id){
        List<Double> sumList = (List<Double>) mUser.getData("sale_sum_list" + id);
        SaleSummary saleSummary = mSaleSummaries.get(id);
        //当天销售及增长；
        saleSummary.daySum = sumList.get(1); //今天
        saleSummary.dayRise = saleSummary.daySum - sumList.get(2); //昨天
        //开始日期
        long endDate = sumList.get(0).longValue();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(endDate);
        //本周
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        dayOfWeek = dayOfWeek == 1 ? 7 : (dayOfWeek - 1);
        saleSummary.weekSum = 0;
        for(int i = 1; i <= dayOfWeek; i++) {
            saleSummary.weekSum += sumList.get(i);
        }
        //上周
        saleSummary.weekRise = 0;
        for(int i = dayOfWeek + 1; i <= 7; i ++){
            saleSummary.weekRise += sumList.get(i);
        }
        saleSummary.weekRise = saleSummary.weekSum - saleSummary.weekRise;
        //本月
        saleSummary.monthSum = 0;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        for(int i = 1; i <= dayOfMonth; i++){
            saleSummary.monthSum += sumList.get(i);
        }
        //上月
        saleSummary.monthRise = 0;
        for(int i = dayOfMonth + 1; i < sumList.size() - 1; i++){
            saleSummary.monthRise += sumList.get(i);
        }
        saleSummary.monthRise = saleSummary.monthSum - saleSummary.monthRise;
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
