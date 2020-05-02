package com.xingdhl.www.storehelper.store;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.webservice.HttpHandler;

public class HomePageFragment extends Fragment {
    private final int CREATE_STORE = 1;
    private HttpHandler mHttpHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_null_store, container, false);
        Button createStoreButton = view.findViewById(R.id.create_store_button);
        createStoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), CreateStoreActivity.class), CREATE_STORE);
                //getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mHttpHandler = ((ListStoreActivity)getActivity()).getHttpHandler();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode != CREATE_STORE || resultCode != Activity.RESULT_OK)
            return;
        Message message = mHttpHandler.obtainMessage();
        message.what = ListStoreActivity.NEW_STORE_ADDED;
        mHttpHandler.sendMessage(message);
    }
}
