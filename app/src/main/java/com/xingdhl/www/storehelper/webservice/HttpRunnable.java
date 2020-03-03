package com.xingdhl.www.storehelper.webservice;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Strong on 17/11/1.
 *
 */

public abstract class HttpRunnable implements Runnable{
    private final String TAG = "XDB_DebugInfo";

    //private static final int _HTTP_OK = 0;
    private static final int _HTTP_NETWORK_ERR = 600;
    private static final int _HTTP_SERVER_ERR = 601;
    private static final int _HTTP_API_ERR = 602;

    private String mUrl;        //URL;
    private String mToken;
    private byte[] mUrlData;    //URL post参数；
    private int mHttpCode;       //http请求执行结果；

    private int mConnectTimeout;
    private int mReadTimeout;

    public HttpRunnable(String url){
        mUrl = url;
        mToken = null;
        mUrlData = null;
        mReadTimeout = 5000;
        mConnectTimeout = 15000;
    }

    public HttpRunnable(String url, String token){
        mUrl = url;
        mToken = token;
        mUrlData = null;
        mReadTimeout = 5000;
        mConnectTimeout = 15000;
    }

    public HttpRunnable(String url, String token, int readTimeout, int connectTimeout){
        mUrl = url;
        mToken = token;
        mUrlData = null;
        mReadTimeout = readTimeout;
        mConnectTimeout = connectTimeout;
    }

    public int getHttpCode() {
        return mHttpCode;
    }

    public void setConnectTimeout(int connectTimeout) {
        mConnectTimeout = connectTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        mReadTimeout = readTimeout;
    }

    public void setToken(String token){
        mToken = token;
    }

    public HttpRunnable addPathParams(Object... path){
        mUrl = String.format(mUrl, path);
        return this;
    }

    public HttpRunnable addQueryParams(String key, String value) {
        mUrl = Uri.parse(mUrl).buildUpon().appendQueryParameter(key, value).build().toString();
        return  this;
    }

    public HttpRunnable addFormParams(String name, Object value){
        String urlData;

        if(name == null || name.length() == 0 || value == null)
            return this;

        if(mUrlData == null) {
            urlData = name + "=" + value.toString();
        } else{
            urlData = new String(mUrlData);
            urlData = urlData + "&" + name + "=" + value.toString();
        }
        try {
            mUrlData = urlData.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return this;
    }

    public HttpRunnable addJsonParams(String name, Object values) {
        String jsonString;
        JSONObject jsonObject;

        if(name == null || name.length() == 0 || values == null)
            return this;

        try {
            if(mUrlData == null)
                jsonObject = new JSONObject();
            else {
                jsonString = new String(mUrlData);
                jsonObject = new JSONObject(jsonString);
            }
            jsonObject.accumulate(name, values);
            jsonString = jsonObject.toString();
            mUrlData = jsonString.getBytes("UTF-8");
        } catch (JSONException je) {
            Log.d(TAG, "addJsonParams() called. ", je);
            return this;
        } catch (UnsupportedEncodingException ue) {
            Log.d(TAG, "addJsonParams( ) called. ", ue);
            return this;
        }

        return this;
    }

    public HttpRunnable addJsonParams(byte[] data) {
        if (data == null) return this;

        if(mUrlData == null){
            mUrlData = data;
            return this;
        }

        byte[] byteData = new byte[mUrlData.length + data.length];

        System.arraycopy(mUrlData,0, byteData, 0, mUrlData.length);
        System.arraycopy(data, 0, byteData, mUrlData.length, data.length);
        mUrlData = byteData;

        return this;
    }

    public byte[] httpDownload(BufferedOutputStream bufOutputStream) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(mUrl);
            connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(mConnectTimeout);
            connection.setReadTimeout(mReadTimeout);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(false);

            connection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            connection.setUseCaches(false); //不使用缓存；

            //在头中添加Token；
            if(mToken != null)
                connection.setRequestProperty("Authorization", mToken);

            connection.connect();
            mHttpCode = connection.getResponseCode();
            if(mHttpCode != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, String.format("URL_Post--->%s", url.toString()));
                Log.d(TAG, String.format("URL_Post--->Http code: %d", mHttpCode));
                mHttpCode = _HTTP_API_ERR;
                return null;
            }

            int bytesRead;
            //long fileLength = 0;
            byte[] bufRead = new byte[2048];
            InputStream in = connection.getInputStream();
            while ((bytesRead = in.read(bufRead)) > 0) {
                bufOutputStream.write(bufRead, 0, bytesRead);
                //fileLength += bytesRead;
            }
            bufOutputStream.flush();
        } catch (IOException ioe) {
            String err_msg = ioe.getMessage();
            Log.d(TAG, mUrl + ": with " + err_msg);
            if(err_msg.contains("unreachable")) {
                mHttpCode = _HTTP_NETWORK_ERR;
            }
            else{
                mHttpCode = _HTTP_SERVER_ERR;
            }
        } finally {
            connection.disconnect();
        }
        return null;
    }

    public byte[] httpUpload(BufferedInputStream bufInputStream, Long bufLength) {
        HttpURLConnection connection = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            URL url = new URL(mUrl);
            connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(mConnectTimeout);
            connection.setReadTimeout(mReadTimeout);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Type", "application/x-jpg");
            connection.setRequestProperty("Content-Length", bufLength.toString());
            connection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            connection.setUseCaches(false); //不使用缓存；

            //在头中添加Token；
            if(mToken != null)
                connection.setRequestProperty("Authorization", mToken);

            connection.connect();
            OutputStream outputStream = connection.getOutputStream();

            int bytesRead;
            byte[] bufRead = new byte[2048];
            while((bytesRead = bufInputStream.read(bufRead)) != -1) {
                outputStream.write(bufRead, 0, bytesRead);
            }
            outputStream.flush();
            outputStream.close();

            mHttpCode = connection.getResponseCode();
            if(mHttpCode != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, String.format("URL_Post--->%s", url.toString()));
                Log.d(TAG, String.format("URL_Post--->Http code: %d", mHttpCode));
                mHttpCode = _HTTP_API_ERR;
                return null;
            }

            InputStream in = connection.getInputStream();
            while ((bytesRead = in.read(bufRead)) > 0) {
                out.write(bufRead, 0, bytesRead);
            }
            out.close();
        } catch (IOException ioe) {
            String err_msg = ioe.getMessage();
            Log.d(TAG, mUrl + ": with " + err_msg);
            if(err_msg.contains("unreachable")) {
                mHttpCode = _HTTP_NETWORK_ERR;
            }
            else{
                mHttpCode = _HTTP_SERVER_ERR;
            }
            return null;
        } finally {
            try {
                connection.disconnect();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        //mHttpCode = _HTTP_OK;
        return out.toByteArray();
    }

    public byte[] httpRequest(String mode, String contentType) {
        HttpURLConnection connection = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            URL url = new URL(mUrl);
            connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(mConnectTimeout);
            connection.setReadTimeout(mReadTimeout);
            connection.setRequestMethod(mode);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            if(mode.equals("GET"))
                connection.setDoOutput(false);
            connection.setRequestProperty("Content-Type", contentType);
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            connection.setUseCaches(false); //不使用缓存；

            //在头中添加Token；
            if(mToken != null)
                connection.setRequestProperty("Authorization", mToken);

            connection.connect();

            if(mUrlData != null) {
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(mUrlData);
                outputStream.flush();
                outputStream.close();
            }

            mHttpCode = connection.getResponseCode();
            if(mHttpCode != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, String.format("URL_%s--->%s", mode, url.toString()));
                if(mUrlData != null)
                    Log.d(TAG, String.format("URL_%s--->Http data: %s", mode, new String(mUrlData)));
                Log.d(TAG, String.format("URL_%s--->Http code: %d", mode, mHttpCode));
                mHttpCode = _HTTP_API_ERR;
                return null;
            }

            InputStream in = connection.getInputStream();

            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
        } catch (IOException ioe) {
            String err_msg = ioe.getMessage();
            Log.d(TAG, mUrl + ": with " + err_msg);
            if(err_msg.contains("unreachable")) {
                mHttpCode = _HTTP_NETWORK_ERR;
            }
            else{
                mHttpCode = _HTTP_SERVER_ERR;
            }
            return null;
        } finally {
            try {
                connection.disconnect();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return out.toByteArray();
    }
}
