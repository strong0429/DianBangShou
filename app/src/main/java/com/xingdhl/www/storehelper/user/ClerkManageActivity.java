package com.xingdhl.www.storehelper.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xingdhl.www.storehelper.ObjectDefine.Clerk;
import com.xingdhl.www.storehelper.ObjectDefine.OnItemClickListener;
import com.xingdhl.www.storehelper.ObjectDefine.Store;
import com.xingdhl.www.storehelper.ObjectDefine.User;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;

import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

public class ClerkManageActivity extends AppCompatActivity implements
        HttpHandler.handlerCallback, OnItemClickListener{
    private final int REQ_CODE_EDIT = 1;
    private final int REQ_CODE_ADD = 2;

    private HttpHandler mHttpHandler;
    private List<Clerk> mClerkList;
    private RecyclerView mClerkRecyclerView;

    @Override
    public void onClick(View v, int position) {
    }

    @Override
    public void onLongClick(View v, int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("clerk", mClerkList.get(position));
        Intent intent = new Intent(ClerkManageActivity.this, ClerkEditActivity.class);
        intent.putExtra("bundle", bundle);
        startActivityForResult(intent, REQ_CODE_EDIT);
    }

    @Override
    public void onMsgHandler(Message msg) {
        if(msg.what != WebServiceAPIs.MSG_GET_CLERKS_INFO)
            return;
        if(msg.arg1 != HTTP_OK)
            return;
        mClerkRecyclerView.getAdapter().notifyDataSetChanged();
    }

    private View.OnClickListener onClickListener_Add = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ClerkManageActivity.this, ClerkEditActivity.class);
            startActivityForResult(intent, REQ_CODE_ADD);
        }
    };

    private class ClerkHolder extends RecyclerView.ViewHolder{
        private TextView mStoreName;
        private TextView mClerkName;
        private TextView mClerkTel;
        private TextView mRegDate;

        private ClerkHolder(View v){
            super(v);
            mStoreName = (TextView)v.findViewById(R.id.store_name);
            mClerkName = (TextView)v.findViewById(R.id.clerk_name);
            mClerkTel = (TextView)v.findViewById(R.id.clerk_tel);
            mRegDate = (TextView)v.findViewById(R.id.register_date);
        }

        private void Bind(Clerk clerk){
            for(Store store : User.getUser(null).getStores()) {
                if (clerk.getStoreId() == store.getId())
                    mStoreName.setText("工作店铺：" + store.getName());
            }
            mClerkName.setText("姓名：" + clerk.getName());
            mClerkTel.setText("电话：" + clerk.getTel());
            mRegDate.setText("入职日期：" + clerk.getDate());
        }
    }

    private class ClerkAdapter extends RecyclerView.Adapter<ClerkManageActivity.ClerkHolder>{
        private OnItemClickListener mOnItemClickListener;
        private List<Clerk> mClerkList;

        private ClerkAdapter(List<Clerk> clerkList){
            mClerkList = clerkList;
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
            this.mOnItemClickListener=onItemClickListener;
        }

        @Override
        public int getItemCount() {
            return mClerkList.size();
        }

        @Override
        public ClerkManageActivity.ClerkHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.item_clerk_rv, parent,false);
            ClerkManageActivity.ClerkHolder holder = new ClerkManageActivity.ClerkHolder(v);

            return holder;
        }

        @Override
        public void onBindViewHolder(ClerkManageActivity.ClerkHolder holder, final int position) {
            holder.Bind(mClerkList.get(position));
            if(mOnItemClickListener == null)
                return;
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onLongClick(v, position);
                    return false;
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clerk_manage);

        mClerkList = new ArrayList<>();

        mHttpHandler = new HttpHandler(this);
        mClerkRecyclerView = (RecyclerView)findViewById(R.id.recyclerview_clerk);
        mClerkRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ClerkAdapter clerkAdapter = new ClerkAdapter(mClerkList);
        clerkAdapter.setOnItemClickListener(this);
        mClerkRecyclerView.setAdapter(clerkAdapter);
        mClerkRecyclerView.addItemDecoration(
                new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        findViewById(R.id.add_clerk).setOnClickListener(onClickListener_Add);

        WebServiceAPIs.getClerks(mHttpHandler, mClerkList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK || data == null)
            return;

        if(requestCode != REQ_CODE_EDIT && requestCode != REQ_CODE_ADD)
            return;

        int clerkId;
        Bundle bundle;
        Clerk clerk = null;
        if((bundle = data.getBundleExtra("bundle")) == null){
            clerkId = data.getIntExtra("clerk_id", -1);
        }else{
            clerk = (Clerk)bundle.getSerializable("clerk");
            clerkId = clerk.getId();
        }
        for(int i = 0; i < mClerkList.size(); i ++){
            if(mClerkList.get(i).getId() != clerkId)
                continue;
            mClerkList.remove(i);
            if(clerk != null)
                mClerkList.add(i, clerk);
            mClerkRecyclerView.getAdapter().notifyDataSetChanged();
            return;
        }
        if(clerk != null) {
            mClerkList.add(clerk);
            mClerkRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }
}
