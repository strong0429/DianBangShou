package com.xingdhl.www.storehelper.store;

import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xingdhl.www.storehelper.CustomStuff.FreeToast;
import com.xingdhl.www.storehelper.ObjectDefine.Store;
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
            if(mLogoFile.exists()) {
                mStoreLogo.setImageURI(Uri.parse(mLogoFile.getAbsolutePath()));
            }else{
                HttpHandler httpHandler = new HttpHandler(getContext(), new HttpHandler.handlerCallback() {
                    @Override
                    public void onMsgHandler(Message msg) {
                        if (msg.what != WebServiceAPIs.MSG_STORE_LOGO)
                            return;
                        if (msg.arg1 == HTTP_OK)
                            mStoreLogo.setImageURI(Uri.parse(mLogoFile.getAbsolutePath()));
                        else
                            mStoreLogo.setImageResource(R.drawable.store_default);
                    }
                });

                try {
                    mLogoFile.getParentFile().mkdirs();
                    mLogoFile.createNewFile();
                    WebServiceAPIs.downloadFile(httpHandler, mLogoFile.getName(), mLogoFile);
                }catch (IOException e){
                    mStoreLogo.setImageResource(R.drawable.store_default);
                }
            }
        }
        return view;
    }
}
