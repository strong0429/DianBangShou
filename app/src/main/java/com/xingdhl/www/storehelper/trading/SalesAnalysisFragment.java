package com.xingdhl.www.storehelper.trading;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xingdhl.www.storehelper.ObjectDefine.GCV;
import com.xingdhl.www.storehelper.ObjectDefine.SalesRecord;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.utility.PinyinUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SalesAnalysisFragment extends SalesSumFragment implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener{
    private RecyclerView mSalesSumRv;
    private TextView mSalesTotal, mProfitTotal;
    private TextView mProfitRate, mProfitProportion;
    private ImageButton mImgBtnClear;
    private EditText mGoodsName;

    private List<SalesRecord> mSalesRecords;
    private List<SalesSumRecord> mSalesSumRecords;
    private List<Float> mSalesSums;

    private class SalesSumViewHold extends RecyclerView.ViewHolder{
        private TextView mItemName, mSalesTotal, mProfitTotal;
        private TextView mProfitRate, mProfitProportion;

        private SalesSumViewHold(View v){
            super(v);

            mItemName = (TextView)v.findViewById(R.id.goods_name);
            mSalesTotal = (TextView)v.findViewById(R.id.sale_amount);
            mProfitTotal = (TextView)v.findViewById(R.id.sale_profit);
            mProfitRate = (TextView)v.findViewById(R.id.profit_rate);
            mProfitProportion = (TextView)v.findViewById(R.id.profit_proportion);
        }

        private void Bind(SalesSumRecord recorder, boolean isSelected){
            if(isSelected)
                itemView.setBackgroundColor(GCV.COLOR_SELECTED);
            else
                itemView.setBackgroundColor(0);

            mItemName.setText(recorder.mItemName);
            mSalesTotal.setText(String.format(Locale.PRC, "%.2f", recorder.mSalesTotal));
            mProfitTotal.setText(String.format(Locale.PRC, "%.2f", recorder.mProfitTotal));
            mProfitRate.setText(String.format(Locale.PRC, "%.2f", recorder.getProfitRate()));
            if(mSalesSums.get(1) > 0)
                mProfitProportion.setText(String.format(Locale.PRC, "%.2f",
                    recorder.mProfitTotal / mSalesSums.get(1) * 100));
            else
                mProfitProportion.setText("0.00");
        }
    }

    private class SalesSumRvAdapter extends RecyclerView.Adapter<SalesSumViewHold>{
        private int mSelectedId = -1;

        public SalesSumRvAdapter(){
        }

        @Override
        public int getItemCount() {
            return mSalesSumRecords.size();
        }

        @Override
        public SalesSumViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_sale_calc_sum, parent, false);

            SalesSumViewHold holder = new SalesSumViewHold(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(SalesSumViewHold holder, final int position) {
            if(mSelectedId == position)
                holder.Bind(mSalesSumRecords.get(position), true);
            else
                holder.Bind(mSalesSumRecords.get(position), false);
        }

        public void selectItem(int position){
            if(position == mSelectedId)
                return;

            int oldId = mSelectedId;
            mSelectedId = position;

            if(oldId >= 0 && oldId <= mSalesSumRecords.size())
                notifyItemChanged(oldId);
            if(position >= 0 && position <= mSalesSumRecords.size())
                notifyItemChanged(position);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSalesSumRecords = getSumsByGoods(mSalesRecords);
        mSalesSums = getSums(mSalesSumRecords);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.pager_sale_calc_sum, container, false);

        mSalesTotal = (TextView)view.findViewById(R.id.sale_total);
        mProfitTotal = (TextView)view.findViewById(R.id.profit_total);
        mProfitRate = (TextView)view.findViewById(R.id.profit_rate);
        mProfitProportion = (TextView)view.findViewById(R.id.profit_proportion);

        mGoodsName = (EditText)view.findViewById(R.id.goods_name);
        mGoodsName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0) {
                    ((SalesSumRvAdapter)mSalesSumRv.getAdapter()).selectItem(-1);
                    return;
                }
                int id = 0;
                String text = s.toString();
                if(PinyinUtil.isContainsChinese(s.toString())){ //输入中有汉字，直接比较
                    for (; id < mSalesSumRecords.size(); id++) {
                        if(!mSalesSumRecords.get(id).mItemName.contains(text)){
                            break;
                        }
                    }
                }else { //输入字母，以拼音首字母比较
                    for (; id < mSalesSumRecords.size(); id++) {
                        String pinyinString = mSalesSumRecords.get(id).mItemName;
                        pinyinString = PinyinUtil.converterToFirstSpell(pinyinString);
                        if(pinyinString.contains(text)){
                            break;
                        }
                    }
                }
                if(id < mSalesSumRecords.size()) {
                    mSalesSumRv.scrollToPosition(id);
                }
                ((SalesSumRvAdapter)mSalesSumRv.getAdapter()).selectItem(id);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mImgBtnClear = (ImageButton)view.findViewById(R.id.button_clear);
        mImgBtnClear.setOnClickListener(this);
        ((RadioGroup)view.findViewById(R.id.radio_group)).setOnCheckedChangeListener(this);

        mSalesSumRv = (RecyclerView)view.findViewById(R.id.sale_sum_list);
        mSalesSumRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSalesSumRv.setAdapter( new SalesSumRvAdapter());

        mSalesTotal.setText(String.format(Locale.PRC, "%.2f", mSalesSums.get(0)));
        mProfitTotal.setText(String.format(Locale.PRC, "%.2f", mSalesSums.get(1)));
        if(mSalesSums.get(0) > 0)
            mProfitRate.setText(String.format(Locale.PRC, "%.2f",
                    mSalesSums.get(1) / mSalesSums.get(0) * 100));
        else
            mProfitRate.setText("0.00");
        if(mSalesSums.get(1) > 0)
            mProfitProportion.setText("100.00");
        else
            mProfitProportion.setText("0.00");

        return view;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb_name:
                mSalesSumRecords.clear();
                mSalesSumRecords = getSumsByGoods(mSalesRecords);
                mImgBtnClear.setEnabled(true);
                mGoodsName.setEnabled(true);
                break;
            case R.id.rb_category:
                mSalesSumRecords.clear();
                mSalesSumRecords = getSumsByCategory(mSalesRecords);
                mImgBtnClear.setEnabled(false);
                mGoodsName.setEnabled(false);
                mGoodsName.setText("");
                break;
            default:
                return;
        }
        mSalesSumRv.getAdapter().notifyDataSetChanged();
    }

    @Override
    public List<Float> getSums(List<?> records) {
        List<Float> sumList = new ArrayList<>();
        List<SalesSumRecord> sumRecords = (List<SalesSumRecord>) records;
        float salesTotal = 0;
        float profitTotal = 0;
        for(SalesSumRecord record : sumRecords){
            salesTotal += record.mSalesTotal;
            profitTotal += record.mProfitTotal;
        }
        sumList.add(salesTotal);    //总销售金额；
        sumList.add(profitTotal);    //总利润；

        return sumList;
    }

    @Override
    public void onClick(View v) {
        mGoodsName.setText("");
    }

    @Override
    public void setObjectList(List<?> objectList) {
        mSalesRecords = (List<SalesRecord>)objectList;
    }

    @Override
    public void updateText(double... params) {
        mSalesSumRecords = getSumsByGoods(mSalesRecords);
        mSalesSums = getSums(mSalesSumRecords);

        mSalesTotal.setText(String.format(Locale.PRC, "%.2f", mSalesSums.get(0)));
        mProfitTotal.setText(String.format(Locale.PRC, "%.2f", mSalesSums.get(1)));
        if(mSalesSums.get(0) > 0)
            mProfitRate.setText(String.format(Locale.PRC, "%.2f",
                    mSalesSums.get(1) / mSalesSums.get(0) * 100));
        else
            mProfitRate.setText("0.00");
        if(mSalesSums.get(1) > 0)
            mProfitProportion.setText("100.00");
        else
            mProfitProportion.setText("0.00");

        mSalesSumRv.getAdapter().notifyDataSetChanged();
    }
}
