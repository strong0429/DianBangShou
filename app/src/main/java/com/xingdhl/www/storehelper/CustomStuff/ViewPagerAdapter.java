package com.xingdhl.www.storehelper.CustomStuff;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Leeyc on 2018/3/22.
 *
 */

public abstract class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private int mUpdatePosition;

    public abstract int getCurrentPosition();

    public ViewPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public void notifyDataSetChanged() {
        mUpdatePosition = 0;
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        if(mUpdatePosition == getCurrentPosition()){
            return POSITION_NONE;
        }

        mUpdatePosition += 1;
        return super.getItemPosition(object);
    }
}
