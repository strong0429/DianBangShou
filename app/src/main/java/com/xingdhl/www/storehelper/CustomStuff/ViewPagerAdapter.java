package com.xingdhl.www.storehelper.CustomStuff;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leeyc on 2018/3/22.
 *
 */

public class ViewPagerAdapter extends FragmentStateAdapter {
    //private int mItemCount;
    //private Class mClass;
    private List<?> mFragments;
    //private Object[] mInitParams;
    private boolean[] mBinded;

    public ViewPagerAdapter(FragmentActivity activity, List<?> fragments){
        super(activity);
        mFragments = fragments;
        mBinded = new boolean[fragments.size()];
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return (Fragment)mFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return mFragments.size();
    }

    @Override
    public void onBindViewHolder(@NonNull FragmentViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        mBinded[position] = true;
    }

    public boolean isBinded(int position){
        return mBinded[position];
    }
    /*
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
        try {
            Constructor constructor;
            if(mInitParams == null || mInitParams.length == 0){
                constructor = mClass.getConstructor(int.class);
            }else {
                Class[] classes = new Class[mInitParams.length + 1];
                for (int i = 0; i < mInitParams.length; i++)
                    classes[i] = mInitParams[i].getClass();
                classes[mInitParams.length] = int.class;
                constructor = mClass.getConstructor(classes);
            }
            Object[] params = new Object[mInitParams.length+1];
            for(int i = 0; i < mInitParams.length; i++)
                params[i] = mInitParams[i];
            params[mInitParams.length] = position;
            fragment = (Fragment) constructor.newInstance(params);
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
*/
}
