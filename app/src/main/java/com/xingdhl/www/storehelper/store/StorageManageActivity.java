package com.xingdhl.www.storehelper.store;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xingdhl.www.storehelper.CustomStuff.FreeToast;
import com.xingdhl.www.storehelper.ObjectDefine.Categories;
import com.xingdhl.www.storehelper.ObjectDefine.DetailStorage;
import com.xingdhl.www.storehelper.ObjectDefine.GCV;
import com.xingdhl.www.storehelper.ObjectDefine.OnItemClickListener;
import com.xingdhl.www.storehelper.ObjectDefine.User;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.utility.PinyinUtil;
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;

import java.util.List;

public class StorageManageActivity extends AppCompatActivity implements
        HttpHandler.handlerCallback, OnItemClickListener, View.OnClickListener {
    private RecyclerView mListView;
    private EditText mGoodsName;

    private HttpHandler mHttpHandler;
    private List<DetailStorage> mStoreGoods;
    private int mStoreId;

    private class GoodsViewHolder extends RecyclerView.ViewHolder{
        private TextView mName, mSpec, mCount, mPrice, mPromDate;

        private GoodsViewHolder(View itemView) {
            super(itemView);

            mName = (TextView)itemView.findViewById(R.id.goods_name);
            mSpec = (TextView)itemView.findViewById(R.id.goods_spec);
            mCount = (TextView)itemView.findViewById(R.id.goods_count);
            mPrice = (TextView)itemView.findViewById(R.id.goods_price);
            mPromDate = (TextView)itemView.findViewById(R.id.prom_date);
        }

        private void Bind(DetailStorage goods, boolean isSelected){
            if(isSelected){
                itemView.setBackgroundColor(GCV.COLOR_SELECTED);
            }else{
                itemView.setBackgroundColor(0);
            }
            mName.setText(goods.getName());
            mSpec.setText(goods.getRemark());
            mCount.setText(getString(R.string.format_float, goods.getCount()));
            String strPrice = getString(R.string.format_price, goods.getPrice(), goods.getUnit());
            if(goods.getDiscount() < 1.0f){
                mPrice.setTextColor(0xFF0000FF);
                mPromDate.setTextColor(0xFF0000FF);
                strPrice += (" " + getString(R.string.format_discount, goods.getDiscount()));
                mPrice.setText(strPrice);
                mPromDate.setText(getString(R.string.storage_prom_date, goods.getStartDate(), goods.getEndDate()));
            }else{
                mPrice.setText(strPrice);
            }
        }
    }

    private class GoodsListAdapter extends RecyclerView.Adapter<GoodsViewHolder>{
        private OnItemClickListener mOnClickListener;
        private List<DetailStorage> mGoodsList;
        private int mSelectedItem;

        private GoodsListAdapter(List<DetailStorage> goodsList){
            mGoodsList = goodsList;
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

        public void setOnItemClickListener(OnItemClickListener onClickListener){
            mOnClickListener = onClickListener;
        }

        public DetailStorage getItem(int position){
            return mGoodsList.get(position);
        }

        @Override
        public int getItemCount() {
            return mGoodsList.size();
        }

        @Override
        public GoodsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.item_storage_manage, parent, false);
            return new GoodsViewHolder(v);
        }

        @Override
        public void onBindViewHolder(GoodsViewHolder holder, final int position) {
            if(position == mSelectedItem) {
                holder.Bind(mGoodsList.get(position), true);
            }else{
                holder.Bind(mGoodsList.get(position), false);
            }
            if(mOnClickListener == null)
                return;
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnClickListener.onLongClick(v, position);
                    return false;
                }
            });
        }
    }

    @Override
    public void onClick(View v, int position) {
    }

    @Override
    public void onLongClick(View v, int position) {
        if(User.getUser(null).getRole() == GCV.CLERK){
            FreeToast.makeText(this, "您未授权，不能执行该操作！", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(StorageManageActivity.this, StorageEditActivity.class);
        intent.putExtra("item_id", position);
        User.getUser(null).putData("goods_list", mStoreGoods);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onMsgHandler(Message msg) {
        switch (msg.what){
            case WebServiceAPIs.MSG_GET_PAGE_STORAGES:
                if(msg.arg1 == WebServiceAPIs.HTTP_CONTINUE){
                    WebServiceAPIs.getPageStorages(mHttpHandler, mStoreGoods, mStoreId,
                            msg.arg2 + 1, GCV.STORAGE_PAGE_SIZE);
                    mListView.getAdapter().notifyDataSetChanged();
                }else if(msg.arg1 != WebServiceAPIs.HTTP_OK){
                    mStoreGoods.clear();
                    FreeToast.makeText(this, "查询库存商品失败，请稍后重试", Toast.LENGTH_SHORT).show();
                }
                mListView.getAdapter().notifyDataSetChanged();
                break;
            case WebServiceAPIs.MSG_GET_CATEGORY:
                if(msg.arg1 != WebServiceAPIs.HTTP_OK)
                    Log.i("XDB_MSG", "Get goods category failed. error code: " + msg.arg1);
                break;
            default:
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_manage);

        mHttpHandler = new HttpHandler(this);
        int storeNo = getIntent().getIntExtra("store_No", -1);
        mStoreId = User.getUser(null).getStore(storeNo).getId();

        if(Categories.getGoodsCategories().size() == 0)
            WebServiceAPIs.getGoodsCategories(mHttpHandler);

        //获取库存商品信息；
        mStoreGoods = User.getUser(null).getGoodsList();
        mStoreGoods.clear();
        WebServiceAPIs.getPageStorages(mHttpHandler, mStoreGoods, mStoreId, 0,  GCV.STORAGE_PAGE_SIZE);

        GoodsListAdapter adapter = new GoodsListAdapter(mStoreGoods);
        adapter.setOnItemClickListener(this);
        mListView = (RecyclerView)findViewById(R.id.goods_list);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setAdapter(adapter);
        mListView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        findViewById(R.id.button_clear).setOnClickListener(this);
        mGoodsName = (EditText)findViewById(R.id.find_editbox);
        mGoodsName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0) {
                    ((GoodsListAdapter)mListView.getAdapter()).setSelectedItem(-1);
                    return;
                }
                int id = 0;
                String text = s.toString();
                if(PinyinUtil.isContainsChinese(s.toString())){ //输入中有汉字，直接比较
                    for (; id < mStoreGoods.size(); id++) {
                        if(!mStoreGoods.get(id).getName().contains(text)){
                            break;
                        }
                    }
                }else { //输入字母，以拼音首字母比较
                    for (; id < mStoreGoods.size(); id++) {
                        String pinyinString = mStoreGoods.get(id).getName();
                        pinyinString = PinyinUtil.converterToFirstSpell(pinyinString);
                        if(pinyinString.contains(text)){
                            break;
                        }
                    }
                }
                if(id < mStoreGoods.size()) {
                    mListView.scrollToPosition(id);
                }
                ((GoodsListAdapter)mListView.getAdapter()).setSelectedItem(id);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        mGoodsName.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<Integer> updateItem = (List<Integer>)User.getUser(null).getData("update_item");
        if(updateItem == null)
            return;
        for(Integer id : updateItem){
            mListView.getAdapter().notifyItemChanged(id);
        }
    }
}
