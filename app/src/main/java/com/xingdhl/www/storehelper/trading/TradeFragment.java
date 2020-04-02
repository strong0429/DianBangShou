package com.xingdhl.www.storehelper.trading;

import android.graphics.Color;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xingdhl.www.storehelper.ObjectDefine.DetailStorage;
import com.xingdhl.www.storehelper.ObjectDefine.GCV;
import com.xingdhl.www.storehelper.ObjectDefine.Sales;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.webservice.HttpHandler;

import java.util.List;
import java.util.Locale;

/**
 * Created by Leeyc on 2018/3/16.
 *
 */

public abstract class TradeFragment extends Fragment {
    protected static List<DetailStorage> mGoodsList = null;
    protected static List<Sales> mSalesList = null;
    protected static List<Integer> mGoodsId = null;
    protected static HttpHandler mHttpHandler = null;
    protected static int mStoreId;

    private boolean isModified = false;

    protected abstract int getSaleListId(int position);
    protected abstract int getGoodsListId(int position);

    public abstract void updateItem(Integer position);

    public static void init(List<DetailStorage> goodsList, List<Sales> salesList,
                            List<Integer> goodsId, HttpHandler httpHandler, int storeId){
        mGoodsList = goodsList;
        mSalesList = salesList;
        mGoodsId = goodsId;
        mHttpHandler = httpHandler;
        mStoreId = storeId;
    }

    private class GoodsViewHolder extends RecyclerView.ViewHolder{
        private TextView mName;
        private TextView mSpec;
        private TextView mPrice;
        private EditText mCount;
        private ImageView mBtnAdd;
        private ImageView mBtnSub;

        private TextWatcher mTextWatcher;
        private View mView;

        private GoodsViewHolder(View view){
            super(view);

            mView = view;
            mName = (TextView)view.findViewById(R.id.goods_name);
            mPrice = (TextView)view.findViewById(R.id.goods_price);
            mSpec = (TextView)view.findViewById(R.id.goods_spec);

            mCount = (EditText)view.findViewById(R.id.goods_count);
            mTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if(s.length() == 0 || s.toString().equals(".") || Float.valueOf(s.toString()) < 0.0001)
                        return;

                    Message msg = mHttpHandler.obtainMessage();
                    msg.what = GCV.MSG_MANUAL_SUB;

                    if((msg.arg2 = getSaleListId(getAdapterPosition())) == -1)
                        return;
                    //需要暂时从总额中扣减去当前的销售值；
                    msg.arg1 = (int)mSalesList.get(msg.arg2).getSum();
                    msg.arg2 = (int)((mSalesList.get(msg.arg2).getSum() - msg.arg1) * 10000);
                    //mSalesList.get(saleId).setCount(0);
                    mHttpHandler.sendMessage(msg);
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    isModified = true;
                    if(s.length() == 0 || s.toString().equals("."))
                        return;

                    Message msg = mHttpHandler.obtainMessage();
                    msg.what = GCV.MSG_MANUAL_ADD;

                    float goodsCount = Float.valueOf(s.toString());
                    if((msg.arg2 = getSaleListId(getAdapterPosition())) == -1)
                        return;
                    mSalesList.get(msg.arg2).setCount(goodsCount);
                    //msg.arg1 = 0;   //要更新的Fragment的序号；
                    mHttpHandler.sendMessage(msg);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };
            mCount.addTextChangedListener(mTextWatcher);
            mCount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus && isModified){
                        isModified = false;
                        Message msg = mHttpHandler.obtainMessage();
                        msg.what = GCV.MSG_CLEAR_ITEM;
                        msg.arg1 = getGoodsListId(getAdapterPosition());
                        msg.arg2 = getSaleListId(getAdapterPosition());

                        String text = mCount.getText().toString();
                        if(text.isEmpty() || text.equals(".") || Float.valueOf(text) < 0.0001){
                            if(msg.arg2 != -1){
                                mSalesList.remove(msg.arg2);
                                mGoodsId.remove(msg.arg2);
                                msg.arg2 = -1;
                            }
                        }
                        mHttpHandler.sendMessage(msg);
                    }
                }
            });

            mBtnAdd = (ImageView)view.findViewById(R.id.goods_num_add);
            mBtnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg = mHttpHandler.obtainMessage();
                    msg.what = GCV.MSG_SALE_ADD;
                    msg.arg1 = getGoodsListId(getAdapterPosition());    //传递商品在mStorageList 的索引位置；

                    if((msg.arg2 = getSaleListId(getAdapterPosition())) == -1){
                        //销售列表中没有该商品；
                        msg.what = GCV.MSG_SALE_NEW;
                        mHttpHandler.sendMessage(msg);
                        return;
                    }

                    float saleCount = mSalesList.get(msg.arg2).getCount() + 1;
                    mSalesList.get(msg.arg2).setCount(saleCount);
                    mHttpHandler.sendMessage(msg);
                }
            });
            mBtnSub = (ImageView)view.findViewById(R.id.goods_num_sub);
            mBtnSub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg = mHttpHandler.obtainMessage();
                    msg.what = GCV.MSG_SALE_SUB;
                    msg.arg1 = getGoodsListId(getAdapterPosition());

                    if((msg.arg2 = getSaleListId(getAdapterPosition())) != -1){
                        float saleCount = mSalesList.get(msg.arg2).getCount() - 1;
                        mSalesList.get(msg.arg2).setCount(saleCount);
                        mHttpHandler.sendMessage(msg);
                    }
                }
            });
        }

        private void Bind(DetailStorage storage, float count, boolean selected){
            if(selected){
                mView.setBackgroundColor(GCV.COLOR_SELECTED);
            }else {
                mView.setBackgroundColor(0);
            }
            mName.setText(storage.getName());
            mSpec.setText(storage.getRemark());
            if(storage.getDiscount() < 0.999){
                mPrice.setTextColor(Color.RED);
            }else{
                mPrice.setTextColor(Color.BLUE);
            }
            mPrice.setText(getString(R.string.goods_price_discount,
                    storage.getPrice(), storage.getDiscount()));
            mCount.removeTextChangedListener(mTextWatcher);
            if(count > 0){
                mBtnAdd.setImageResource(R.drawable.newsales_add);
                mBtnSub.setVisibility(View.VISIBLE);
                mCount.setText(String.format(Locale.CHINA, "%.2f", count));
                mCount.setVisibility(View.VISIBLE);
            }else{
                mCount.setVisibility(View.INVISIBLE);
                mBtnSub.setVisibility(View.INVISIBLE);
                mBtnAdd.setImageResource(R.drawable.newsales_add_grid);
            }
            mCount.addTextChangedListener(mTextWatcher);
        }
    }

    protected class GoodsAdapter extends RecyclerView.Adapter<GoodsViewHolder>{
        private List<?> mDataList;
        private int mSelectedItem;

        protected GoodsAdapter(Class<?> cls){
            if(cls.equals(TradeFragmentByName.class))
                mDataList = mGoodsList;
            else if(cls.equals(TradeFragmentByCode.class))
                mDataList = mSalesList;
            mSelectedItem = -1;
        }

        public void setSelectedItem(int itemId){
            if(itemId == mSelectedItem)
                return;

            int id = mSelectedItem;
            mSelectedItem = itemId;

            if(id >= 0 && id < getItemCount()) {
                notifyItemChanged(id);
            }
            if(itemId >= 0 && itemId < getItemCount()) {
                notifyItemChanged(itemId);
            }
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }

        @Override
        public GoodsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.item_sale_record_rv, parent, false);
            return new GoodsViewHolder(view);

        }

        @Override
        public void onBindViewHolder(GoodsViewHolder holder, int position) {
            int goodsId = getGoodsListId(position);
            int salesId = getSaleListId(position);

            float count = (salesId == -1) ? 0f : mSalesList.get(salesId).getCount();

            DetailStorage storage = mGoodsList.get(goodsId);
            if(storage.getEndDate_L() < System.currentTimeMillis() ||
                    storage.getStartDate_L() > System.currentTimeMillis()) {
                storage.setDiscount(1.0f);
            }

            if(position == mSelectedItem)
                holder.Bind(storage, count, true);
            else
                holder.Bind(storage, count, false);
        }
    }

    public void startScan(int delayMsTime){
    }

    public void stopScan(){
    }
}
