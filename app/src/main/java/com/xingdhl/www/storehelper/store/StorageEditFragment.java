package com.xingdhl.www.storehelper.store;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.xingdhl.www.storehelper.CustomStuff.FreeToast;
import com.xingdhl.www.storehelper.CustomStuff.QueryDialog;
import com.xingdhl.www.storehelper.ObjectDefine.Categories;
import com.xingdhl.www.storehelper.ObjectDefine.DetailStorage;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.Trading.CalenderActivity;
import com.xingdhl.www.storehelper.webservice.HttpHandler;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Strong on 18/1/7.
 *
 */
public class StorageEditFragment extends Fragment {
    private final int DATE_PICKER = 1;

    private static HttpHandler mHttpHandler;
    private static List<DetailStorage> mStorageList;

    private EditText mPrice;
    private EditText mDiscount;
    private EditText mPromptDate;

    private boolean mFinished;
    private int mItemId;

    private DetailStorage mStorage;

    public static StorageEditFragment instantiate(HttpHandler httpHandler, List<DetailStorage> storageList){
        mHttpHandler = httpHandler;
        mStorageList = storageList;

        return new StorageEditFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItemId = getArguments().getInt("item_id");
        mStorage = mStorageList.get(mItemId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_storage_edit, container, false);

        ((EditText)view.findViewById(R.id.goods_barcode)).setText(mStorage.getBarcode());
        for(Categories.Category category : Categories.getGoodsCategories()){
            if(category.getId() != mStorage.getCategoryId())
                continue;
            ((EditText)view.findViewById(R.id.goods_category)).setText(category.getName());
        }
        ((EditText)view.findViewById(R.id.goods_name)).setText(mStorage.getName());
        ((EditText)view.findViewById(R.id.goods_spec)).setText(mStorage.getRemark());
        ((EditText)view.findViewById(R.id.goods_manuf)).setText(mStorage.getName_M());
        ((EditText) view.findViewById(R.id.goods_supplier)).setText(mStorage.getSupplier());

        /*最后编辑的操作人和日期*/
        ((EditText) view.findViewById(R.id.edit_operator)).setText(mStorage.getEditor());
        ((EditText) view.findViewById(R.id.edit_date)).setText(mStorage.getEditTime());

        String price = String.format(Locale.CHINA, "%.2f", mStorage.getPrice());
        mPrice = (EditText)view.findViewById(R.id.goods_price);
        mPrice.setText(price);
        String discount = String.format(Locale.CHINA, "%.2f", mStorage.getDiscount());
        mDiscount = (EditText)view.findViewById(R.id.goods_discount);
        mDiscount.setText(discount);
        mPromptDate = (EditText) view.findViewById(R.id.prompt_date);
        mPromptDate.setText(getString(R.string.storage_prom_date,
                mStorage.getStartDate(), mStorage.getEndDate()));

        mPromptDate.setFocusable(false);
        mPromptDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开日期选择Activity
                Intent intent = new Intent(getActivity(), CalenderActivity.class);
                intent.putExtra("start_time", mStorage.getStartDate_L());
                intent.putExtra("end_time", mStorage.getEndDate_L());
                startActivityForResult(intent, DATE_PICKER);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK || requestCode != DATE_PICKER)
            return;

        long startTime = data.getLongExtra("start_time", System.currentTimeMillis());
        long endTime  = data.getLongExtra("end_time", startTime);
        if(startTime > endTime){
            QueryDialog.showAlertMsg(getActivity(), "开始日期不能晚于结束日期，请重新选择日期！");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        mPromptDate.setText(getString(R.string.storage_prom_date,
                sdf.format(startTime), sdf.format(endTime)));
    }

    public void pageOut(boolean finished){
        float price, discount;
        try{
            price = Float.parseFloat(mPrice.getText().toString());
            discount = Float.parseFloat(mDiscount.getText().toString());
        }catch (Exception e){
            FreeToast.makeText(getActivity(), "请输入正确的商品售价和折扣!", Toast.LENGTH_SHORT).show();
            return;
        }

        mFinished = finished;
        String startDate = mPromptDate.getText().toString().substring(0, 10);
        String endDate = mPromptDate.getText().toString().substring(14, 24);

        if(price != mStorage.getPrice() || discount != mStorage.getDiscount() ||
                !mStorage.getStartDate().equals(startDate) ||
                !mStorage.getEndDate().equals(endDate)){
            QueryDialog dlg = new QueryDialog(getActivity(), String.format(
                    "你已调整了商品(%s)售价，是否需要保存？", mStorage.getBarcode()), mDlgListener);
            dlg.show();
        }else if(mFinished){
            getActivity().finish();
        }
    }

    private final QueryDialog.QueryDlgListener mDlgListener = new QueryDialog.QueryDlgListener() {
        @Override
        public void onDialogPositiveClick(DialogInterface dialog, int which) {
            float price, discount;
            try{
                price = Float.parseFloat(mPrice.getText().toString());
            }catch (Exception e){
                FreeToast.makeText(getActivity(), "请输入正确的商品售价!", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                discount = Float.parseFloat(mDiscount.getText().toString());
                if(discount > 1.0 || discount < 0.0001){
                    FreeToast.makeText(getActivity(), "折扣值应在 0 和 1 之间!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }catch (Exception e){
                FreeToast.makeText(getActivity(), "请输入正确的商品折扣!", Toast.LENGTH_SHORT).show();
                return;
            }
            String startDate = mPromptDate.getText().toString().substring(0, 10) + " 00:00:00";
            String endDate = mPromptDate.getText().toString().substring(14, 24) + " 23:59:59";
            long startTime = Timestamp.valueOf(startDate).getTime();
            long endTime = Timestamp.valueOf(endDate).getTime();
            if(startTime > endTime){
                FreeToast.makeText(getActivity(), "结束日期不能早于开始日期！", Toast.LENGTH_SHORT).show();
                return;
            }

            mStorage.setPrice(price);
            mStorage.setDiscount(discount);
            mStorage.setStartDate(startTime);
            mStorage.setEndDate(endTime);
            mStorage.setEditTime(System.currentTimeMillis());

            Message msg = mHttpHandler.obtainMessage();
            msg.what = StorageEditActivity.MSG_STORAGE_MODIFIED;
            msg.arg1 = mItemId;
            mHttpHandler.sendMessage(msg);
        }

        @Override
        public void onDialogNegativeClick(DialogInterface dialog, int which) {
            if(mFinished){
                getActivity().finish();
                return;
            }

            mPrice.setText(String.format(Locale.CHINA, "%.2f", mStorage.getPrice()));
            mDiscount.setText(String.format(Locale.CHINA, "%.2f", mStorage.getDiscount()));
            mPromptDate.setText(getString(R.string.storage_prom_date,
                    mStorage.getStartDate(), mStorage.getEndDate()));
        }
    };


}
