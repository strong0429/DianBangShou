package com.xingdhl.www.storehelper.store;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xingdhl.www.storehelper.ObjectDefine.Store;
import com.xingdhl.www.storehelper.ObjectDefine.User;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import static java.net.HttpURLConnection.HTTP_OK;

public class ViewStoreFragment extends Fragment{
    private Store mStore;
    private ImageView mStoreLogo;
    private File mLogoFile;

    public ViewStoreFragment(Store store){
        mStore = store;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.pager_shop_info, container, false);

        ((TextView)view.findViewById(R.id.shop_name)).setText(mStore.getName());
        ((TextView)view.findViewById(R.id.shop_addr)).setText(String.format(Locale.PRC, "%s%s%s%s",
                mStore.getCity(), mStore.getDistrict(), mStore.getStreet(), mStore.getAddress()));
        ((TextView)view.findViewById(R.id.shop_phone)).setText(mStore.getPhone());
        mStoreLogo = view.findViewById(R.id.shop_photo);
        if(mStore.getLogo() == null || mStore.getLogo().isEmpty())
            mStoreLogo.setImageResource(R.drawable.store_default);
        else{
            mLogoFile = new File(mStore.getLogo());
            mLogoFile = new File(getActivity().getFilesDir(), "images/store/" + mLogoFile.getName());
            if(mLogoFile.exists()) {    //本地是否已缓存logo文件
                mStoreLogo.setImageURI(Uri.parse(mLogoFile.getAbsolutePath()));
            }else{
                //创建消息循环，接收WebService消息；
                HttpHandler httpHandler = new HttpHandler(getContext(), new HttpHandler.handlerCallback() {
                    @Override
                    public void onMsgHandler(Message msg) {
                        if (msg.what != WebServiceAPIs.MSG_STORE_LOGO)
                            return;
                        if (msg.arg1 == HTTP_OK)    //logo文件下载成功
                            mStoreLogo.setImageURI(Uri.parse(mLogoFile.getAbsolutePath()));
                        else
                            mStoreLogo.setImageResource(R.drawable.store_default);
                    }
                });
                //从Web下载logo文件；
                try {
                    mLogoFile.getParentFile().mkdirs();
                    mLogoFile.createNewFile();  //先创建本地文件以接收web文件数据；
                    WebServiceAPIs.downloadFile(httpHandler, mLogoFile.getName(), mLogoFile);
                }catch (IOException e){
                    mStoreLogo.setImageResource(R.drawable.store_default);
                }
            }
        }
        mStoreLogo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainBusinessActivity.class);
                //Intent intent = new Intent(getContext(), StoreOwnerActivity.class);
                for(int id = 0; id < User.getUser(null).getStores().size(); id++)
                    if(User.getUser(null).getStore(id).getId() == mStore.getId())
                        intent.putExtra("store_id", id);
                startActivity(intent);
                //getActivity().finish();
            }
        });
        return view;
    }
}
