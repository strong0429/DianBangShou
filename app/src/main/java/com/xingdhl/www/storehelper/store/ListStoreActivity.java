package com.xingdhl.www.storehelper.store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.os.Message;
import com.xingdhl.www.storehelper.ObjectDefine.Store;
import com.xingdhl.www.storehelper.ObjectDefine.User;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.webservice.HttpHandler;

import java.util.ArrayList;
import java.util.List;

public class ListStoreActivity extends AppCompatActivity implements
        HttpHandler.handlerCallback {
    public static final int NEW_STORE_ADDED = -100;

    private User mUser;
    private ViewPager2 mViewPager;
    private HttpHandler mHttpHandler;
    private List<Fragment> mFragments;

    @Override
    public void onMsgHandler(Message msg) {
        if(msg.what != NEW_STORE_ADDED)
            return;
        //mFragments.clear();
        mFragments.add(new ViewStoreFragment(mUser.getStore(0)));
        mViewPager.getAdapter().notifyDataSetChanged();
        mViewPager.setCurrentItem(mFragments.size() - 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_pager);

        mHttpHandler = new HttpHandler(this);
        mViewPager = findViewById(R.id.viewpager_container);
        mViewPager.setOffscreenPageLimit(1);

        mUser = User.getUser(null);
        mFragments = new ArrayList<>();
        mFragments.add(new HomePageFragment());
        for(Store store : mUser.getStores())
            mFragments.add(new ViewStoreFragment(store));

        mViewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getItemCount() {
                return mFragments.size();
            }
        });
        /*
        FragmentManager fm =getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if(fragment != null) return;

        if(user.getStores().size() == 0){
            fragment = new HomePageFragment();
        }else{
            fragment = new HomePageFragment();
        }
        fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
         */
    }

    public HttpHandler getHttpHandler(){
        return mHttpHandler;
    }
}
