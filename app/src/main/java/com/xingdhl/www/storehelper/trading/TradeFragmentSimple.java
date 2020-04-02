package com.xingdhl.www.storehelper.trading;

import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xingdhl.www.storehelper.ObjectDefine.GCV;
import com.xingdhl.www.storehelper.R;

import java.util.Locale;

/**
 * Created by Strong on 17/11/29.
 *
 */

public class TradeFragmentSimple extends TradeFragment {
    private TextView mTextViewPrice;
    private TextView mTextViewTotal;
    private String mTotal;
    private String mPrice;

    public static TradeFragmentSimple instance(){
        return new TradeFragmentSimple();
    }

    View.OnClickListener keyClickListenner = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Double total = Double.valueOf(mTextViewTotal.getText().toString());
            Double value = Double.valueOf(((TextView)v).getText().toString());
            String text = mTextViewPrice.getText().toString();

            int index = text.lastIndexOf('+');
            if(text.length() == 0 || index == text.length() - 1){
                text += ((TextView)v).getText().toString();
                mTextViewPrice.setText(text);
                total = total + value;
                text = String.format(Locale.CHINA, "%.2f", total);
                mTextViewTotal.setText(text);
                return;
            }

            if(index > -1)
                text = text.substring(index + 1);
            index = text.indexOf('.');  //查找小数点；
            if(index == -1){  //不带小数点，进位
                Double oldValue = Double.valueOf(text);
                total = total - oldValue + oldValue * 10 + value;
            }else if(text.length() - index > 2) { //只允许小数点后两位数字；
                return;
            }else { //计算小数部分；
                total = total + value / Math.pow(10,(text.length() - index));
            }
            text = String.format(Locale.CHINA, "%.2f", total);
            mTextViewTotal.setText(text);
            text = mTextViewPrice.getText().toString();
            text += ((TextView)v).getText().toString();
            mTextViewPrice.setText(text);
        }
    };

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrice = mTotal = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_trade_simple, container, false);

        mTextViewPrice = (TextView)view.findViewById(R.id.text_price);
        if(mPrice != null)
            mTextViewPrice.setText(mPrice);
        mTextViewTotal = (TextView)view.findViewById(R.id.text_total);
        if(mTotal != null)
            mTextViewTotal.setText(mTotal);
        mTextViewTotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() == 0 || !mTextViewTotal.isEnabled()) {
                    mTextViewTotal.setEnabled(true);
                    return;
                }
                Double value = Double.valueOf(s.toString());
                Bundle bundle = new Bundle();
                bundle.putDouble("total", value);
                Message msg = mHttpHandler.obtainMessage();
                msg.what = GCV.MSG_SIMPLE_SUB;
                msg.setData(bundle);
                mHttpHandler.sendMessage(msg);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0)
                    return;
                Double value = Double.valueOf(s.toString());
                Bundle bundle = new Bundle();
                bundle.putDouble("total", value);
                Message msg = mHttpHandler.obtainMessage();
                msg.what = GCV.MSG_SIMPLE_ADD;
                msg.setData(bundle);
                mHttpHandler.sendMessage(msg);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        view.findViewById(R.id.key_0).setOnClickListener(keyClickListenner);
        view.findViewById(R.id.key_1).setOnClickListener(keyClickListenner);
        view.findViewById(R.id.key_2).setOnClickListener(keyClickListenner);
        view.findViewById(R.id.key_3).setOnClickListener(keyClickListenner);
        view.findViewById(R.id.key_4).setOnClickListener(keyClickListenner);
        view.findViewById(R.id.key_5).setOnClickListener(keyClickListenner);
        view.findViewById(R.id.key_6).setOnClickListener(keyClickListenner);
        view.findViewById(R.id.key_7).setOnClickListener(keyClickListenner);
        view.findViewById(R.id.key_8).setOnClickListener(keyClickListenner);
        view.findViewById(R.id.key_9).setOnClickListener(keyClickListenner);

        view.findViewById(R.id.key_point).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mTextViewPrice.getText().toString();
                if(text.length() == 0 || text.charAt(text.length() - 1) == '+'){
                    text = text + "0.";
                    mTextViewPrice.setText(text);
                    return;
                }

                int index = text.lastIndexOf('+');
                if(index != -1)
                    text = text.substring(index + 1);
                if(text.indexOf('.') != -1)
                    return;
                text = mTextViewPrice.getText().toString() + ".";
                mTextViewPrice.setText(text);
            }
        });

        view.findViewById(R.id.key_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mTextViewPrice.getText().toString();

                if(text.length() == 0)
                    return;

                //最后一个字符若为‘+’或‘.’，退出；
                char last = text.charAt(text.length() - 1);
                if(last == '+' || last == '.')
                    return;

                mTextViewPrice.setText(text + "+");
            }
        });

        view.findViewById(R.id.key_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mTextViewPrice.getText().toString();

                if(text.length() == 0)
                    return;

                char last = text.charAt(text.length() -1);
                if(last == '+' || last == '.'){
                    text = text.substring(0, text.length() -1);
                    mTextViewPrice.setText(text);
                    return;
                }

                int index = text.lastIndexOf('+');
                if(index != -1){
                    text = text.substring(index + 1);
                }
                Double total = Double.valueOf(mTextViewTotal.getText().toString());
                total = total - Double.valueOf(text);
                if(text.length() > 1) {
                    text = text.substring(0, text.length() - 1);
                    total = total + Double.valueOf(text);
                }
                text = mTextViewPrice.getText().toString();
                text = text.substring(0, text.length() - 1);
                mTextViewPrice.setText(text);
                text = String.format(Locale.CHINA, "%.2f", total);
                mTextViewTotal.setText(text);
            }
        });

        view.findViewById(R.id.key_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double total = Double.valueOf(mTextViewTotal.getText().toString());

                mTextViewPrice.setText("");
                mTextViewTotal.setText("0.00");
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        mTotal = mTextViewTotal.getText().toString();
        mPrice = mTextViewPrice.getText().toString();
        super.onDestroyView();
    }

    @Override
    public void updateItem(Integer nothing){
        if(mTextViewTotal == null)
            return;
        mTotal = mPrice = null;
        mTextViewTotal.setEnabled(false);
        mTextViewPrice.setText("");
        mTextViewTotal.setText("0.00");
    }

    @Override
    protected int getSaleListId(int position) {
        return -1;
    }

    @Override
    protected int getGoodsListId(int position) {
        return -1;
    }
}
