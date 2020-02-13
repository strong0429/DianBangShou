package com.xingdhl.www.storehelper.utility;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.xingdhl.www.storehelper.CustomStuff.FreeToast;
import com.xingdhl.www.storehelper.ObjectDefine.GCV;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VoiceInput {
    //回调函数，用于语音录入结束后处理用户事件；
    public interface OnVoiceFinish{
        void onVoiceFinish(String textString, int whoIs);
    }

    // 语音听写对象、语音听写UI对象，根据需要创建任意一个或两个；
    private SpeechUtility mSpeechUtility;
    private SpeechRecognizer mIat;
    private RecognizerDialog mIatDialog;

    private OnVoiceFinish mCallback;
    private StringBuffer mIatResult;

    private Context mContext;
    private int whoIs;

    public VoiceInput(Context context){
        mSpeechUtility = SpeechUtility.getUtility();
        mContext = context;
        mIatResult = new StringBuffer(256);
    }

    public RecognizerDialog createIatDialog(OnVoiceFinish callback){
        if(mSpeechUtility == null){
            Log.d(GCV.D_TAG, "===  未初始化语音引擎或初始化失败  ===");
            return null;
        }

        if(mIatDialog != null)
            return mIatDialog;

        mCallback = callback;
        mIatDialog = new RecognizerDialog(mContext, null);

        RecognizerDialogListener iatDlgListener = new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean isLast) {
                if (!isLast) {
                    mIatResult.append(recognizerResult.getResultString());
                    mIatResult.append(",");
                    return;
                }
                mIatResult.append(recognizerResult.getResultString());
                mIatResult.append("]");

                String text = parseIatResult(mIatResult.toString());
                mCallback.onVoiceFinish(text, whoIs);
            }

            @Override
            public void onError(SpeechError speechError) {
                // TODO 自动生成的方法存根
                speechError.getPlainDescription(true);
            }
        };

        mIatDialog.setParameter(SpeechConstant.DOMAIN, "iat"); // domain:域名
        mIatDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIatDialog.setParameter(SpeechConstant.ACCENT, "mandarin"); // mandarin:普通话
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIatDialog.setParameter(SpeechConstant.ASR_PTT, "1");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIatDialog.setParameter(SpeechConstant.VAD_BOS, "5000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIatDialog.setParameter(SpeechConstant.VAD_EOS, "1500");

        mIatDialog.setListener(iatDlgListener);
        return mIatDialog;
    }

    public void startSpeak(int whoIs){
        if(mIatDialog == null){
            FreeToast.makeText(mContext, "抱歉！创建语音引擎失败，请手工输入。",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        this.whoIs = whoIs;
        mIatResult.replace(0, mIatResult.length(), "[");
        mIatDialog.show();
    }

    public void destroyIatDialog(){
        if(mIatDialog == null)
            return;

        mIatDialog.cancel();
        mIatDialog.destroy();
        mIatDialog = null;
    }

    public void setParameter(String key, String value){
        mIatDialog.setParameter(key, value);
    }

    private String parseIatResult(String jsonString){
        StringBuffer stringBuffer = new StringBuffer();
        try{
            JSONArray jsonArraySn = new JSONArray(jsonString);
            for(int i = 0; i < jsonArraySn.length(); i++){
                JSONObject jsonObject = jsonArraySn.getJSONObject(i);
                if("true".equals(jsonObject.getString("ls"))){
                    break;
                }
                JSONArray jsonArrayWs = jsonObject.getJSONArray("ws");
                for(int j = 0; j < jsonArrayWs.length(); j++){
                    jsonObject = jsonArrayWs.getJSONObject(j);
                    JSONArray jsonArrayCw = jsonObject.getJSONArray("cw");
                    String word = "";
                    double score = -1.0;
                    for(int k = 0; k < jsonArrayCw.length(); k++){
                        jsonObject = jsonArrayCw.getJSONObject(k);
                        if(jsonObject.getDouble("sc") >= score){
                            score = jsonObject.getDouble("sc");
                            word = jsonObject.getString("w");
                        }
                    }
                    stringBuffer.append(word);
                }
            }
        }catch (JSONException je){
            je.printStackTrace();
        }

        return stringBuffer.toString();
    }
}
