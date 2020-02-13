package com.xingdhl.www.storehelper.Trading;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.Result;
import com.xingdhl.www.storehelper.CustomStuff.FreeToast;
import com.xingdhl.www.storehelper.ObjectDefine.GCV;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.storehelper.utility.ScanBarcodeUtil;

import java.util.Locale;

/**
 * Created by Leeyc on 2018/3/17.
 *
 */

public class TradeFragmentByCode extends TradeFragment implements View.OnClickListener{
    private EditText mBarCode;
    private RecyclerView mRecyclerView;
    private ImageButton mImgButton, mFlash;

    private ScanBarcodeUtil mScanBarcodeUtil;

    private void sendMessage(String barcode){
        if(barcode == null)
            return;

        Message msg = mHttpHandler.obtainMessage();

        //查看销售列表中是否已有该条码对应商品；
        for(int i = 0; i < mSalesList.size(); i++){
            if(!barcode.equals(mSalesList.get(i).getBarcode()))
                continue;
            msg.what = GCV.MSG_SALE_ADD;
            msg.arg1 = mGoodsId.get(i); //该商品在本地商品列表中的位置；
            msg.arg2 = i;   //该商品在销售列表中的位置；
            mSalesList.get(i).setCount(mSalesList.get(i).getCount() + 1);
            mHttpHandler.sendMessage(msg);
            mScanBarcodeUtil.startScan(1500);
            return;
        }
        //查看商品列表中是否有该条码对应商品；
        for(int i = 0; i < mGoodsList.size(); i++){
            if(!barcode.equals(mGoodsList.get(i).getBarcode()))
                continue;
            msg.what = GCV.MSG_SALE_NEW;
            msg.arg1 = i;
            mHttpHandler.sendMessage(msg);
            return;
        }
        //列表中无该条码商品信息，提示；
        FreeToast.makeText(getActivity(), "该商品未入库，不能销售", Toast.LENGTH_SHORT).show();
        mScanBarcodeUtil.startScan(1500);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_trade_barcode, container, false);

        mScanBarcodeUtil = new ScanBarcodeUtil(getActivity(),
                (SurfaceView)view.findViewById(R.id.capture_preview),
                (RelativeLayout)view.findViewById(R.id.capture_crop_view),
                (ImageView)view.findViewById(R.id.capture_scan_line)) {
            @Override
            public void handleDecode(Result rawResult, Bundle bundle) {
                //声音提示捕捉到条码；
                beep();
                //成功扫描条码后，关闭相机；
                stopScan();

                mBarCode.setEnabled(false);
                mBarCode.setText(rawResult.getText());
                //mImgButton.setBackgroundResource(R.drawable.keyboard);
                sendMessage(rawResult.getText());
                //startScan(1000);
            }
        };

        mBarCode = (EditText)view.findViewById(R.id.edit_barcode);
        mBarCode.setEnabled(false);

        mImgButton = (ImageButton)view.findViewById(R.id.barcode_input);
        mImgButton.setOnClickListener(this);

        mFlash = (ImageButton)view.findViewById(R.id.flash_switch);
        mFlash.setOnClickListener(this);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.rv_sale_record);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new GoodsAdapter(TradeFragmentByCode.class));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flash_switch:
                if(mFlash.getTag().equals("on")){
                    mFlash.setTag("off");
                    mScanBarcodeUtil.closeFlash();
                    mFlash.setBackgroundResource(R.drawable.flash_on);
                }else if(mFlash.getTag().equals("off")) {
                    mFlash.setTag("on");
                    mScanBarcodeUtil.openFlash();
                    mFlash.setBackgroundResource(R.drawable.flash_off);
                }
                break;
            case R.id.barcode_input:
                if (!mBarCode.isEnabled()) {
                    //打开条码输入框，接受条码输入
                    mBarCode.setEnabled(true);
                    mBarCode.setText("");
                    mBarCode.requestFocus();
                    mImgButton.setBackgroundResource(R.drawable.submit);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    mScanBarcodeUtil.stopScan();
                    return;
                }

                mBarCode.setEnabled(false);
                mScanBarcodeUtil.startScan(10);
                mImgButton.setBackgroundResource(R.drawable.keyboard);

                if (mBarCode.getText().length() == 0) {
                    return;
                }

                String barcode = mBarCode.getText().toString();
                if (barcode.length() <= 4) {
                    barcode = String.format(Locale.CHINA, "%1$08d-%2$04d", mStoreId,
                            Integer.valueOf(mBarCode.getText().toString()));
                }
                //提交输入条码到父窗口作相关处理；
                sendMessage(barcode);
        }
    }

    @Override
    public void startScan(int delayMsTime){
        mScanBarcodeUtil.startScan(delayMsTime);
    }

    @Override
    public void stopScan() {
        mScanBarcodeUtil.stopScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode != 111)
            return;
        if(grantResults[0] == 0 )
            return;

        Toast.makeText(getActivity(), "您拒绝了相机权限申请，将无法使用相机扫描条码", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();

        mScanBarcodeUtil.onResume(getActivity().getApplication());
    }

    @Override
    public void onPause() {
        mScanBarcodeUtil.onPause();

        super.onPause();
    }

    @Override
    public void onDestroy() {
        mScanBarcodeUtil.onDestroy();

        super.onDestroy();
    }

    public static TradeFragmentByCode instance(){
        return new TradeFragmentByCode();
    }

    @Override
    protected int getGoodsListId(int position) {
        return position == -1 ? -1 : mGoodsId.get(position);
    }

    @Override
    protected int getSaleListId(int position) {
        return position;
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
