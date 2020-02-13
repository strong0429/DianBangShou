package com.xingdhl.www.storehelper.Trading;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xingdhl.www.storehelper.CustomStuff.FreeToast;
import com.xingdhl.www.storehelper.CustomStuff.QueryDialog;
import com.xingdhl.www.storehelper.R;
import com.xingdhl.www.zxinglibrary.encoding.EncodingUtils;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener,
        QueryDialog.QueryDlgListener{
    private int payMode = -1;

    private String mCodeWx, mCodeAli;

    @Override
    public void onDialogPositiveClick(DialogInterface dialog, int which) {
        payMode = -1;
        setResult(RESULT_OK, new Intent().putExtra("pay_mode", payMode));
        finish();
    }

    @Override
    public void onDialogNegativeClick(DialogInterface dialog, int which) {

    }

    @Override
    public void onClick(View v) {
        int buttonId, resId;

        switch (payMode){
            case 0: //微信
                buttonId = R.id.pay_weichat;
                resId = R.drawable.weixin_s;
                break;
            case 1: //支付宝
                buttonId = R.id.pay_alipay;
                resId = R.drawable.ailipay_s;
                break;
            case 2: //现金
                buttonId = R.id.pay_cash;
                resId = R.drawable.cash_s;
                break;
            default:
                buttonId = R.id.pay_other;
                resId = R.drawable.others_s;
        }
        if(v.getId() != buttonId)
            findViewById(buttonId).setBackgroundResource(resId);

        switch (v.getId()){
            case R.id.button_ok:
                if(payMode == -1){
                    FreeToast.makeText(this, "请选择付款方式！", Toast.LENGTH_SHORT).show();
                    return;
                }
                setResult(RESULT_OK, new Intent().putExtra("pay_mode", payMode));
                finish();
                break;
            case R.id.pay_weichat:
                v.setBackgroundResource(R.drawable.weixin);
                setPayCodeImage(mCodeWx, R.drawable.weixin);
                payMode = 0;
                break;
            case R.id.pay_alipay:
                v.setBackgroundResource(R.drawable.ailipay);
                setPayCodeImage(mCodeAli, R.drawable.ailipay);
                payMode = 1;
                break;
            case R.id.pay_cash:
                v.setBackgroundResource(R.drawable.cash);
                setPayCodeImage(null, 0);
                payMode = 2;
                break;
            case R.id.pay_other:
                v.setBackgroundResource(R.drawable.others);
                setPayCodeImage(null, 0);
                payMode = 3;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        mCodeWx = getIntent().getStringExtra("wx_code");
        mCodeAli = getIntent().getStringExtra("ali_code");
        double paySum = getIntent().getDoubleExtra("pay_sum", 0);
        ((TextView)findViewById(R.id.total_money)).setText(getString(R.string.format_float, paySum));

        payMode = 0; //缺省微信支付；
        setPayCodeImage(mCodeWx, R.drawable.weixin);

        findViewById(R.id.pay_weichat).setOnClickListener(this);
        findViewById(R.id.pay_alipay).setOnClickListener(this);
        findViewById(R.id.pay_cash).setOnClickListener(this);
        findViewById(R.id.pay_other).setOnClickListener(this);
        findViewById(R.id.button_ok).setOnClickListener(this);
    }

    private void setPayCodeImage(String payCode, int logoId){
        ImageView codeImgView = (ImageView)findViewById(R.id.img_paycode);

        if(payCode == null || payCode.isEmpty()){
            codeImgView.setImageResource(0);
            return;
        }

        Bitmap logoImg = BitmapFactory.decodeResource(getResources(), logoId);
        float scale = getResources().getDisplayMetrics().density;
        Bitmap codeImg = EncodingUtils.createQRCode(payCode, (int)(240*scale), (int)(240*scale), logoImg);
        codeImgView.setImageBitmap(codeImg);
    }

    @Override
    public void onBackPressed() {
        new QueryDialog(this, "您还未收款，退出将取消本次交易！确认取消吗？").show();
    }
}
