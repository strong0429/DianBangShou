package com.xingdhl.www.storehelper.store;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xingdhl.www.storehelper.ObjectDefine.GCV;
import com.xingdhl.www.storehelper.ObjectDefine.Store;
import com.xingdhl.www.storehelper.ObjectDefine.User;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.webservice.HttpHandler;
import com.xingdhl.www.storehelper.webservice.WebServiceAPIs;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by Strong on 17/11/22.
 *
 */

public class StorePagerFragment extends Fragment {
    private static final String ARG_PAGER_POSITION = "CARD_POSITION";

    private static HttpHandler mHttpHandler;
    private ImageView mStoreImgView;
    private Store mStore;
    private int mPageNo;

    public static StorePagerFragment newInstance(int position, HttpHandler httpHandler){
        Bundle args = new Bundle();
        args.putInt(ARG_PAGER_POSITION, position);

        StorePagerFragment fragment = new StorePagerFragment();
        fragment.setArguments(args);
        mHttpHandler = httpHandler;
        return fragment;
    }

    public void setShopImg(Bitmap storeImg){
        if(mStoreImgView == null)
            return;

        mStoreImgView.setImageBitmap(storeImg);
    }

    public void setShopImg(int imgResId){
        if(mStoreImgView == null)
            return;

        mStoreImgView.setImageResource(imgResId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPageNo =  getArguments().getInt(ARG_PAGER_POSITION);
        mStore = User.getUser(null).getStores().get(mPageNo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.pager_shop_info, container, false);
        ((TextView)view.findViewById(R.id.shop_name)).setText(mStore.getName());
        ((TextView)view.findViewById(R.id.shop_addr)).setText(String.format(Locale.PRC, "%s%s%s%s",
                mStore.getCity(), mStore.getDistrict(), mStore.getStreet(), mStore.getAddress()));
        ((TextView)view.findViewById(R.id.shop_phone)).setText(mStore.getPhone());
        mStoreImgView = (ImageView)view.findViewById(R.id.shop_photo);
        mStoreImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(User.getUser(null).getStaffStatus() != GCV.OWNER){
                    return;
                }
                Intent intent = new Intent(getActivity(), StoreEditActivity.class);
                intent.putExtra("page_no", mPageNo);
                getActivity().startActivityForResult(intent, 2);
            }
        });

        if(!mStore.isSetPhoto()) {
            mStoreImgView.setImageResource(R.drawable.store_default);
            return view;
        }

        String imgPath = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath() + "/store_" + mStore.getId() + ".jpg";
        File imgFile = new File(imgPath);
        if(imgFile.exists()){
            Bitmap imgStore = BitmapFactory.decodeFile(imgPath);
            mStoreImgView.setImageBitmap(imgStore);
        }else{
            imgFile.getParentFile().mkdirs();
            try {
                imgFile.createNewFile();
            }catch (IOException e){
                mStoreImgView.setImageResource(R.drawable.store_default);
                return view;
            }
            WebServiceAPIs.imgDownload(mHttpHandler, imgFile, mPageNo);
        }
        return view;
    }
}
