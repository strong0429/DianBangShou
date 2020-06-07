package com.xingdhl.www.storehelper.store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.xingdhl.www.storehelper.ObjectDefine.Store;
import com.xingdhl.www.storehelper.ObjectDefine.User;
import com.xingdhl.www.storehelper.R;

import java.util.ArrayList;
import java.util.List;

public class ListStoreActivity extends AppCompatActivity {
    private List<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_pager);

        ViewPager2 viewPager = findViewById(R.id.viewpager_container);
        //mViewPager.setOffscreenPageLimit(1);

        User user = User.getUser(null);
        mFragments = new ArrayList<>();
        for(Store store : user.getStores())
            mFragments.add(new ViewStoreFragment(store));
        if(mFragments.size() == 0)
            mFragments.add(new HomePageFragment());

        viewPager.setAdapter(new FragmentStateAdapter(this) {
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
}
