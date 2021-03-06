package com.xingdhl.www.storehelper.store;

import android.os.Message;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.xingdhl.www.storehelper.ObjectDefine.StockGoods;
import com.xingdhl.www.storehelper.ObjectDefine.User;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;

import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

public class StorageEditActivity extends AppCompatActivity implements HttpHandler.handlerCallback{
    public static final int MSG_STORAGE_MODIFIED = -100;
    private ViewPager mStoragePager;

    private List<StockGoods> mStorageList;
    private List<Integer> mUpdateItemId;

    private FragmentStatePagerAdapter mAdapter;

    private HttpHandler mHttpHandler;
    private boolean mFinish = false;

    @Override
    public void onMsgHandler(Message msg) {
        if(msg.what == MSG_STORAGE_MODIFIED){
            StockGoods stockGoods = mStorageList.get(msg.arg1);
            WebServiceAPIs.updateStorage(mHttpHandler, stockGoods, msg.arg1);
            return;
        }
        if(msg.what == WebServiceAPIs.MSG_UPDATE_STORAGE){
            if(msg.arg1 == HTTP_OK){
                mUpdateItemId.add(msg.arg2);
            }
            if(mFinish)
                finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_edit);

        mHttpHandler = new HttpHandler(this);

        int itemId = getIntent().getIntExtra("item_id", -1);
        mStorageList = (List<StockGoods>) User.getUser(null).getData("goods_list");

        mUpdateItemId = new ArrayList<>();
        User.getUser(null).putData("update_item", mUpdateItemId);

        FragmentManager fm = getSupportFragmentManager();
        mStoragePager = (ViewPager)findViewById(R.id.storage_pager);
        mAdapter = new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Bundle data = new Bundle();
                data.putInt("item_id", position);
                StorageEditFragment fragment = StorageEditFragment.instantiate(mHttpHandler, mStorageList);
                fragment.setArguments(data);
                return fragment;
            }

            @Override
            public int getCount() {
                return mStorageList.size();
            }
        };
        mStoragePager.setAdapter(mAdapter);
        mStoragePager.setCurrentItem(itemId);

        mStoragePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state != ViewPager.SCROLL_STATE_DRAGGING)
                    return;
                StorageEditFragment fragment = (StorageEditFragment)mAdapter.instantiateItem(
                        mStoragePager, mStoragePager.getCurrentItem());
                fragment.pageOut(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        ((StorageEditFragment)mAdapter.instantiateItem(mStoragePager, mStoragePager.getCurrentItem()))
                .pageOut(true);
        mFinish = true;
    }
}
