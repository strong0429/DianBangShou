package com.xingdhl.www.storehelper.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xingdhl.www.storehelper.ObjectDefine.OnItemClickListener;
import com.xingdhl.www.storehelper.ObjectDefine.Supplier;
import com.xingdhl.www.storehelper.ObjectDefine.User;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;

import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

public class SupplierManageActivity extends AppCompatActivity implements
        HttpHandler.handlerCallback, OnItemClickListener{
    private final int NEW_SUPPLIER = 1;
    private final int UPDATE_SUPPLIER = 2;

    private RecyclerView mSupplierListView;
    private List<Supplier> mSupplierList;

    private int mShopId;

    @Override
    public void onLongClick(View v, int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("supplier", mSupplierList.get(position));
        Intent intent = new Intent(SupplierManageActivity.this, SupplierEditActivity.class);
        intent.putExtra("store_No", mShopId);
        intent.putExtra("bundle", bundle);
        startActivityForResult(intent, UPDATE_SUPPLIER);
    }

    @Override
    public void onClick(View v, int position) {
    }

    @Override
    public void onMsgHandler(Message msg) {
        switch (msg.what) {
            case WebServiceAPIs.MSG_GET_SUPPLIER:
                if (msg.arg1 != HTTP_OK) {
                    Toast.makeText(this, "获取供应商信息失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    return;
                }
                //更新供应商列表，并增加长按列表项进入编辑界面事件；
                if (mSupplierListView.getLayoutManager() == null) {
                    mSupplierListView.setLayoutManager(new LinearLayoutManager(this));
                    SupplierAdapter adapter = new SupplierAdapter(mSupplierList);
                    adapter.setOnItemClickListener(this);
                    mSupplierListView.setAdapter(adapter);
                }
                mSupplierListView.getAdapter().notifyDataSetChanged();
                break;
            case WebServiceAPIs.MSG_DEL_SUPPLIER:
                if (msg.arg1 != HTTP_OK) {
                    Toast.makeText(this, "获取供应商信息失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    return;
                }
                //服务器数据删除成功后，更新本地列表数据及视图；
                for(int i = 0; i < mSupplierList.size(); i++){
                    if(mSupplierList.get(i).getId() != msg.arg2)
                        continue;
                    mSupplierList.remove(i);
                    break;
                }
                mSupplierListView.getAdapter().notifyDataSetChanged();
                break;
        }
    }

    private class SupplierHolder extends RecyclerView.ViewHolder{
        private TextView mName;
        private TextView mContacter;
        private TextView mTel;

        private SupplierHolder(View v){
            super(v);
            mName = v.findViewById(R.id.supp_name);
            mContacter = v.findViewById(R.id.supp_contacter);
            mTel = v.findViewById(R.id.supp_tel);
        }

        private void Bind(Supplier supplier){
            mName.setText(supplier.getName());
            mContacter.setText(supplier.getContacter());
            mTel.setText(supplier.getPhone());
        }
    }

    private class SupplierAdapter extends RecyclerView.Adapter<SupplierHolder>{
        private OnItemClickListener mOnItemClickListener;
        private List<Supplier> mSupplierList;

        private SupplierAdapter(List<Supplier> supplierList){
            mSupplierList = supplierList;
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
            this.mOnItemClickListener = onItemClickListener;
        }

        @Override
        public int getItemCount() {
            return mSupplierList.size();
        }

        @Override
        public SupplierHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.item_supplier_rv, parent,false);
            SupplierHolder holder = new SupplierHolder(v);

            return holder;
        }

        @Override
        public void onBindViewHolder(SupplierHolder holder, final int position) {
            holder.Bind(mSupplierList.get(position));
            if(mOnItemClickListener == null)
                return;
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
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
        setContentView(R.layout.activity_supplier_manage);

        HttpHandler httpHandler = new HttpHandler(this);

        //从父Activity获取店铺id；
        mShopId = getIntent().getIntExtra("store_No", -1);

        //向服务器请求店铺id的供应商信息；
        mSupplierList = new ArrayList<>();
        WebServiceAPIs.getSuppliers(httpHandler, mSupplierList, User.getUser(null).getStore(mShopId).getId());
        mSupplierListView = findViewById(R.id.recyclerview_supp);
        mSupplierListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        //处理添加供应商浮动按钮事件；
        findViewById(R.id.add_supplier).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SupplierManageActivity.this, SupplierEditActivity.class);
                intent.putExtra("store_No", mShopId);
                startActivityForResult(intent, NEW_SUPPLIER);
            }
        });
    }

    //处理供应商编辑Activity返回事件；
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != Activity.RESULT_OK)
            return;

        int id;
        Supplier supplier = null;
        Bundle bundle = data.getBundleExtra("bundle");
        if (bundle == null) {
            id = data.getIntExtra("id", -1);
        }else{
            supplier = (Supplier) bundle.getSerializable("supplier");
            id = supplier.getId();
        }

        if(requestCode == UPDATE_SUPPLIER) {
            for (int i = 0; i < mSupplierList.size(); i++) {
                if (mSupplierList.get(i).getId() != id)
                    continue;
                if(supplier == null)  //删除操作
                    mSupplierList.remove(i);
                else
                    mSupplierList.set(i, supplier);
                break;
            }
        }else if(requestCode == NEW_SUPPLIER) {
            mSupplierList.add(supplier);
        }
        mSupplierListView.getAdapter().notifyDataSetChanged();
    }
}
