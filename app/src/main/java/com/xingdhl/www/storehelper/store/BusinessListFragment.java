package com.xingdhl.www.storehelper.store;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xingdhl.www.storehelper.ObjectDefine.BusinessItems;
import com.xingdhl.www.storehelper.R;

public class BusinessListFragment extends Fragment {
    private int mFragmentId;
    private int mStoreId;

    public BusinessListFragment(int storeId, int fragmentId){
        mFragmentId = fragmentId;
        mStoreId = storeId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.business_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new BusinessAdapter());

        return view;
    }

    private class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mLogoImage;
        private TextView mTitleTextview;
        private TextView mDetailTextview;

        public ItemHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_business, parent, false));

            itemView.setOnClickListener(this);
            mLogoImage = itemView.findViewById(R.id.logo_imageview);
            mTitleTextview = itemView.findViewById(R.id.business_title_textview);
            mDetailTextview = itemView.findViewById(R.id.business_detail_textview);
        }

        public void bind(int itemId){
            int resourceId = BusinessItems.getItemResIds(mFragmentId)[itemId];
            String title = BusinessItems.getItemTitles(mFragmentId)[itemId];
            String detail = BusinessItems.getItemDetails(mFragmentId)[itemId];
            mLogoImage.setImageResource(resourceId);
            mTitleTextview.setText(title);
            mDetailTextview.setText(detail);
        }

        @Override
        public void onClick(View v) {
            int itemId = getBindingAdapterPosition();
            Intent intent = new Intent(getContext(), BusinessItems.getItemActivities(mFragmentId)[itemId]);
            intent.putExtra("store_No", mStoreId);
            startActivity(intent);
        }
    }

    private class BusinessAdapter extends RecyclerView.Adapter<ItemHolder>{
        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater =LayoutInflater.from(getActivity());
            return new ItemHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return BusinessItems.getItemTitles(mFragmentId).length;
        }
    }
}
