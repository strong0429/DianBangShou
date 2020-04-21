package com.xingdhl.www.storehelper.trading;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.utility.PinyinUtil;

/**
 * Created by Leeyc on 2018/3/16.
 *
 */

public class TradeFragmentByName extends TradeFragment {
    private RecyclerView mRecyclerView;
    private EditText mGoodsName;

    //private int mSelectedId = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_trade_name, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.goods_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new GoodsAdapter(TradeFragmentByName.class));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        mGoodsName = (EditText)view.findViewById(R.id.goods_name);
        mGoodsName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0) {
                    ((GoodsAdapter)mRecyclerView.getAdapter()).setSelectedItem(-1);
                    return;
                }
                int id = 0;
                String text = s.toString();
                if(PinyinUtil.isContainsChinese(s.toString())){ //输入中有汉字，直接比较
                    for (; id < mGoodsList.size(); id++) {
                        if(!mGoodsList.get(id).getName().contains(text)){
                            break;
                        }
                    }
                }else { //输入字母，以拼音首字母比较
                    for (; id < mGoodsList.size(); id++) {
                        String pinyinString = mGoodsList.get(id).getName();
                        pinyinString = PinyinUtil.converterToFirstSpell(pinyinString);
                        if(pinyinString.contains(text)){
                            break;
                        }
                    }
                }
                if(id < mGoodsList.size()){
                    mRecyclerView.scrollToPosition(id);
                }
                ((GoodsAdapter)mRecyclerView.getAdapter()).setSelectedItem(id);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        view.findViewById(R.id.button_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoodsName.setText("");
            }
        });
        return view;
    }

    public static TradeFragmentByName instance( ){
        return new TradeFragmentByName();
    }

    @Override
    protected int getGoodsListId(int position) {
        return position;
    }

    @Override
    protected int getSaleListId(int position) {
        for(int i = 0; i < mGoodsId.size(); i++){
            if(mGoodsId.get(i) == position)
                return i;
        }
        return -1;
    }

    @Override
    public void updateItem(Integer position) {
        if(mRecyclerView.getAdapter() == null)
            return;

        if(position == null){
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }else{
            mRecyclerView.getAdapter().notifyItemChanged(position);
        }
    }
}
