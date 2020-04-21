package com.xingdhl.www.storehelper.trading;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xingdhl.www.storehelper.ObjectDefine.SalesRecord;
import com.xingdhl.www.storehelper.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SalesListFragment extends SalesSumFragment {
    private RecyclerView mSalesRecordRv;
    private TextView mTotalMoney, mCashPay;
    private TextView mWeichatPay, mAliPay, mOtherPay;

    private List<SalesRecord> mSalesRecords;

    private class SalesRecordViewHold extends RecyclerView.ViewHolder{
        private TextView mSaleDate, mSaleTime;
        private TextView mSaleCount, mSalePrice;
        private TextView mGoodsName, mBarcode;
        private TextView mSaleMoney, mPayMode;

        private SalesRecordViewHold(View v){
            super(v);
            mSaleDate = (TextView)v.findViewById(R.id.sn_date);
            mSaleTime = (TextView)v.findViewById(R.id.sn_time);
            mBarcode = (TextView)v.findViewById(R.id.goods_barcode);
            mGoodsName = (TextView)v.findViewById(R.id.goods_name);
            mSaleCount = (TextView)v.findViewById(R.id.goods_count);
            mSalePrice = (TextView)v.findViewById(R.id.goods_price);
            mSaleMoney = (TextView)v.findViewById(R.id.sale_count);
            mPayMode = (TextView)v.findViewById(R.id.sale_paymode);
        }

        private void Bind(SalesRecord recorder){
            //mSaleDate.setText(recorder.getSnDateString());
            mSaleDate.setText(recorder.getSaleDateToString());
            mSaleTime.setText(recorder.getSnTimeString());
            mBarcode.setText(recorder.getBarcode());
            mGoodsName.setText(recorder.getGoodsName());
            mSaleCount.setText(String.format(Locale.PRC, "%.2f", recorder.getSaleCount()));
            mSalePrice.setText(String.format(Locale.PRC, "%.2f", recorder.getSalePrice()));
            mSaleMoney.setText(String.format(Locale.PRC, "%.2f", recorder.getSaleSum()));
            switch (recorder.getPayMode()){
                case 0:
                    mPayMode.setText("微信");
                    break;
                case 1:
                    mPayMode.setText("支付宝");
                    break;
                case 2:
                    mPayMode.setText("现金");
                    break;
                default:
                    mPayMode.setText("其它");
            }
        }
    }

    private class SalesRecordRvAdapter extends RecyclerView.Adapter<SalesRecordViewHold>{
        private List<SalesRecord> mRecoders;

        public SalesRecordRvAdapter(List<SalesRecord> recoders){
            mRecoders = recoders;
        }

        @Override
        public int getItemCount() {
            return mRecoders.size();
        }

        @Override
        public SalesRecordViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_sale_calc_list, parent, false);

            SalesRecordViewHold holder = new SalesRecordViewHold(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(SalesRecordViewHold holder, final int position) {
            holder.Bind(mRecoders.get(position));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.pager_sale_calc_list, container, false);

        mTotalMoney = (TextView)view.findViewById(R.id.sale_total);
        mCashPay = (TextView)view.findViewById(R.id.pay_cash);
        mWeichatPay = (TextView)view.findViewById(R.id.pay_weichat);
        mAliPay = (TextView)view.findViewById(R.id.pay_alipay);
        mOtherPay = (TextView)view.findViewById(R.id.pay_other);

        mSalesRecordRv = (RecyclerView)view.findViewById(R.id.sale_record_list);
        mSalesRecordRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSalesRecordRv.setAdapter( new SalesRecordRvAdapter(mSalesRecords));

        List<Float> sums = getSums(mSalesRecords);
        mTotalMoney.setText(String.format(Locale.PRC, "%.2f", sums.get(0)));
        mCashPay.setText(getString(R.string.pay_cash, sums.get(1)));
        mWeichatPay.setText(getString(R.string.pay_weichat, sums.get(2)));
        mAliPay.setText(getString(R.string.pay_alipay, sums.get(3)));
        mOtherPay.setText(getString(R.string.pay_other, sums.get(4)));

        return view;
    }

    @Override
    public List<Float> getSums(List<?> records) {
        float salesTotal = 0;
        float payCash = 0;
        float payWeichat = 0;
        float payAli = 0;
        float payOther = 0;

        List<Float> sumList = new ArrayList<>();
        List<SalesRecord> salesRecords = (List<SalesRecord>) records;

        for(SalesRecord sr : salesRecords){
            salesTotal += sr.getSaleSum(); //总金额；
            switch (sr.getPayMode()){
                case 2: //现金支付金额；
                    payCash += sr.getSaleSum();
                    break;
                case 0: //微信支付金额
                    payWeichat += sr.getSaleSum();
                    break;
                case 1: //支付宝支付金额
                    payAli += sr.getSaleSum();
                    break;
                default: //其它方式支付金额
                    payOther += sr.getSaleSum();
            }
        }
        sumList.add(salesTotal);
        sumList.add(payCash);
        sumList.add(payWeichat);
        sumList.add(payAli);
        sumList.add(payOther);

        return sumList;
    }

    @Override
    public void setObjectList(List<?> objectList) {
        mSalesRecords = (List<SalesRecord>)objectList;
    }

    @Override
    public void updateText(double... params) {
        List<Float> sums = getSums(mSalesRecords);
        mTotalMoney.setText(String.format(Locale.PRC, "%.2f", sums.get(0)));
        mCashPay.setText(getString(R.string.pay_cash, sums.get(1)));
        mWeichatPay.setText(getString(R.string.pay_weichat, sums.get(2)));
        mAliPay.setText(getString(R.string.pay_alipay, sums.get(3)));
        mOtherPay.setText(getString(R.string.pay_other, sums.get(4)));

        mSalesRecordRv.getAdapter().notifyDataSetChanged();
    }
}
