package com.xingdhl.www.storehelper.store;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xingdhl.www.storehelper.CustomStuff.FreeToast;
import com.xingdhl.www.storehelper.R;

public class HomePageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        view.findViewById(R.id.create_store_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity().getApplication(), CreateStoreActivity.class));
                getActivity().finish();
            }
        });

        view.findViewById(R.id.just_look_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FreeToast.makeText(getActivity(), "开发中，敬请期待...", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
