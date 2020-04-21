package com.xingdhl.www.storehelper.CustomStuff;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leeyc on 2018/3/22.
 *
 */

public class ViewPagerAdapter extends FragmentStateAdapter {
    private int mItemCount;
    private Class mClass;
    private List<Object> mFragments;
    private Object[] mInitParams;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity,
                            Class fragmentClass, int itemCount, Object ... initParams) {
        super(fragmentActivity);

        mItemCount = itemCount;
        mClass = fragmentClass;
        mInitParams = initParams;
        mFragments = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        Constructor constructor = null;
        try {
            if(mInitParams == null || mInitParams.length == 0){
                constructor = mClass.getConstructor();
            }else {
                Class[] classes = new Class[mInitParams.length];
                for (int i = 0; i < classes.length; i++)
                    classes[i] = mInitParams[i].getClass();
                constructor = mClass.getConstructor(classes);
            }
            fragment = (Fragment) constructor.newInstance(mInitParams);
            if(mFragments.size() > position){
                mFragments.set(position, fragment);
            }else {
                mFragments.add(fragment);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return mItemCount;
    }

    public List<Object> getFragments(){
        return mFragments;
    }
}
