package com.xingdhl.www.storehelper.store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.xingdhl.www.storehelper.CustomStuff.ViewPagerAdapter;
import com.xingdhl.www.storehelper.ObjectDefine.BusinessItems;
import com.xingdhl.www.storehelper.ObjectDefine.Store;
import com.xingdhl.www.storehelper.ObjectDefine.User;
import com.xingdhl.www.storehelper.R;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainBusinessActivity extends AppCompatActivity {
    private int mStorePager_Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_business);

        mStorePager_Id = getIntent().getIntExtra("store_id", 0);
        Store store = User.getUser(null).getStore(mStorePager_Id);

        CircleImageView civ = findViewById(R.id.image_store_logo);
        if(store.getLogo() == null || store.getLogo().isEmpty())
            civ.setImageResource(R.drawable.store_default);
        else{
            File logoFile = new File(store.getLogo());
            logoFile = new File(getFilesDir(), "images/store/" + logoFile.getName());
            if(logoFile.exists())   //本地是否已缓存logo文件
                civ.setImageURI(Uri.parse(logoFile.getAbsolutePath()));
            else
                civ.setImageResource(R.drawable.store_default);
        }
        ((TextView)findViewById(R.id.txt_store_name)).setText(store.getName());
        ((TextView)findViewById(R.id.txt_store_addr)).setText(store.getAddress());
        ((TextView)findViewById(R.id.txt_store_phone)).setText(store.getPhone());

        ViewPager2 viewPager = findViewById(R.id.viewpager_container);
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return new BusinessListFragment(mStorePager_Id, position);
            }

            @Override
            public int getItemCount() {
                return BusinessItems.getPagerTitles().length;
            }
        });

        TabLayout tabLayout = findViewById(R.id.business_tablayout);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy(){
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        String[] tabText = BusinessItems.getPagerTitles();
                        tab.setText(tabText[position]);
                    }
                }).attach();
    }
}
