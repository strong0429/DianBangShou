package com.xingdhl.www.storehelper.Trading;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xingdhl.www.storehelper.R;

import java.util.List;
import java.util.Locale;

public class SalesSumDataFragment extends SalesSumFragment {
    private TextView mDaySum, mDayRaise;
    private TextView mWeekSum, mWeekRaise;
    private TextView mMonthSum, mMontRaise;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.pager_sell_calc, container, false);

        mDaySum = (TextView)view.findViewById(R.id.sale_today);
        mDayRaise = (TextView)view.findViewById(R.id.sale_yesterday);
        mWeekSum = (TextView)view.findViewById(R.id.sale_week);
        mWeekRaise = (TextView)view.findViewById(R.id.sale_last_week);
        mMonthSum = (TextView)view.findViewById(R.id.sale_month);
        mMontRaise = (TextView)view.findViewById(R.id.sale_last_month);

        return view;
    }

    @Override
    public void updateText(double... data) {
        if(data == null || data.length != 6)
            return;

        mDaySum.setText(String.format(Locale.CHINA, "%.2f", data[0]));
        if(data[1] > 0)
            mDayRaise.setTextColor(0xFFAA0000);
        else if(data[1] < 0)
            mDayRaise.setTextColor(0xFF00AA00);
        else
            mDayRaise.setTextColor(0xFF000000);
        mDayRaise.setText(String.format(Locale.CHINA, "%.2f", data[1]));
        mWeekSum.setText(String.format(Locale.CHINA, "%.2f", data[2]));
        if(data[3] > 0)
            mWeekRaise.setTextColor(0xFFAA0000);
        else if(data[3] < 0)
            mWeekRaise.setTextColor(0xFF00AA00);
        else
            mWeekRaise.setTextColor(0xFF000000);
        mWeekRaise.setText(String.format(Locale.CHINA, "%.2f", data[3]));
        mMonthSum.setText(String.format(Locale.CHINA, "%.2f", data[4]));
        if(data[5] > 0)
            mMontRaise.setTextColor(0xFFAA0000);
        else if(data[5] < 0)
            mMontRaise.setTextColor(0xFF00AA00);
        else
            mMontRaise.setTextColor(0xFF000000);
        mMontRaise.setText(String.format(Locale.CHINA, "%.2f", data[5]));
    }

    @Override
    public void setObjectList(List<?> objectList) {

    }

    @Override
    public List<Float> getSums(List<?> records) {
        return null;
    }
}
