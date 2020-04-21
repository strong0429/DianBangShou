package com.xingdhl.www.storehelper.webservice;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Created by Strong on 17/11/1.
 *
 */

public abstract class HttpRunnable implements Runnable{
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_MULTI = "multipart/form-data";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

    public static final int HTTP_UNIQUE_ERROR = 701;
    public static final int HTTP_REQUIRED_ERROR = 702;
    public static final int HTTP_DISACCORD_ERROR = 704;
    public static final int HTTP_NETWORK_ERR = 1001;
    public static final int HTTP_TIMEOUT_ERR = 1002;
    public static final int HTTP_SERVER_ERR = 1003;
    public static final int HTTP_API_ERR = 1004;
    public static final int HTTP_RESP_ERROR = 1005;
    public static final int HTTP_UNKNOWN_ERR = 1010;

    private final String TAG = "XDB_DebugInfo";
    private final String PREFIX = "--";
    private final String LINE_END = "\r\n";

    private String mBoundary;
    private String mUrl;        //URL;
    private int mHttpCode;       //http请求执行结果；

    private int mConnectTimeout;
    private int mReadTimeout;

    HttpRunnable(String url){
        initClass(url, 5000, 10000);
    }

    HttpRunnable(String url, int readTimeout, int connectTimeout){
        initClass(url, readTimeout, connectTimeout);
    }

    private void initClass(String url, int readTimeout, int connectTimeout){
        mUrl = url;
        mReadTimeout = readTimeout;
        mConnectTimeout = connectTimeout;
        mBoundary = "----!@#$%^&**&^%$#@!" + System.currentTimeMillis();
    }

    int getHttpCode() {
        return mHttpCode;
    }

    void setConnectTimeout(int connectTimeout) {
        mConnectTimeout = connectTimeout;
    }

    void setReadTimeout(int readTimeout) {
        mReadTimeout = readTimeout;
    }

    void addPathParams(Object... path){
        mUrl = String.format(mUrl, path);
    }

    void addQueryParams(String key, String value) {
        mUrl = Uri.parse(mUrl).buildUpon().appendQueryParameter(key, value).build().toString();
    }

    private void writeRequestData(Map<?, ?> dataMap, OutputStream outputStream, String contentType) throws IOException{
        if(dataMap == null || dataMap.isEmpty() || outputStream == null)
            return;
        /*
        if(contentType.equals(CONTENT_TYPE_JSON)){
            JSONObject jsonObject;
            jsonObject = new JSONObject(dataMap);
            outputStream.write(jsonObject.toString().getBytes());
            return;
        }
        */
        StringBuilder dataString = new StringBuilder(1024);

        if(contentType.equals(CONTENT_TYPE_FORM)) {
            for (Map.Entry<?, ?> dataEntry : dataMap.entrySet()) {
                dataString.append(dataEntry.getKey())
                        .append("=")
                        .append(dataEntry.getValue().toString())
                        .append("&");
            }
            dataString.deleteCharAt(dataString.length() - 1);
            outputStream.write(dataString.toString().getBytes());
            return;
        }

        if(contentType.equals(CONTENT_TYPE_MULTI)){
            for(Map.Entry<?, ?> dataEntry : dataMap.entrySet()){
                dataString.append(PREFIX).append(mBoundary).append(LINE_END);
                if(dataEntry.getValue() instanceof File){
                    dataString.append("Content-Disposition: form-data; name=\"")
                            .append(dataEntry.getValue()).append("\"; filename=\"")
                            .append(((File) dataEntry.getValue()).getName())
                            .append("\"").append(LINE_END)
                            .append("Content-Type: application/octet-stream")
                            .append(LINE_END).append(LINE_END);
                    outputStream.write(dataString.toString().getBytes());
                    dataString.delete(0, dataString.length());

                    int bytesRead;
                    byte[] bufRead = new byte[2048];
                    BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream((File)dataEntry.getValue()));
                    while((bytesRead = inputStream.read(bufRead)) != -1) {
                        outputStream.write(bufRead, 0, bytesRead);
                    }
                    outputStream.write(LINE_END.getBytes());
                }else {
                    dataString.append(String.format("Content-Disposition: form-data; name=\"%s\"", dataEntry.getKey()));
                    dataString.append(LINE_END).append(LINE_END);
                    dataString.append(dataEntry.getValue().toString()).append(LINE_END);
                }
            }
            dataString.append(PREFIX).append(mBoundary).append(PREFIX).append(LINE_END);
            outputStream.write(dataString.toString().getBytes());
        }
    }

    void addFormParams(String name, Object value){
    }

    private byte[] makeJsonData(byte[] jsonData, String name, Object values) {
        if(name == null || name.length() == 0 || values == null)
            return jsonData;

        try {
            JSONObject jsonObject;
            if(jsonData == null)
                jsonObject = new JSONObject();
            else {
                jsonObject = new JSONObject(new String(jsonData));
            }
            jsonObject.accumulate(name, values);
            return jsonObject.toString().getBytes(StandardCharsets.UTF_8);
        } catch (JSONException je) {
            Log.d(TAG, "addJsonParams() called. ", je);
            return jsonData;
        }
    }

    void addJsonParams(String name, Object values) {
    }
    /*
    void httpDownload(BufferedOutputStream bufOutputStream) {
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
                mHttpCode = _HTTP_UNKNOWN_ERR;
                return;
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
                mHttpCode = _HTTP_TIMEOUT_ERR;
            }
        } finally {
            connection.disconnect();
        }
    }

    byte[] httpUpload(BufferedInputStream bufInputStream, Long bufLength) {
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
                mHttpCode = _HTTP_UNKNOWN_ERR;
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
                mHttpCode = _HTTP_TIMEOUT_ERR;
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
    */
    byte[] httpRequest(String mode, String contentType){
        return null;
    }

    byte[] httpRequest(String token){
        return httpRequest(null, "GET", CONTENT_TYPE_JSON, token);
    }

    byte[] httpRequest(Map<?,?> dataRequest, String token){
        return httpRequest(dataRequest, "POST", CONTENT_TYPE_FORM, token);
    }

    byte[] httpRequest(Map<?,?> dataRequest, String contentType, String token){
        return httpRequest(dataRequest, "POST", contentType, token);
    }

    byte[] httpRequest(Map<?,?> dataRequest, String method, String contentType, String token) {
        HttpURLConnection connection = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        //自定义的错误信息，统一错误处理方式；
        byte[] respData = makeJsonData(null, "field", "non_field_errors");

        try {
            URL url = new URL(mUrl);
            connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(mConnectTimeout);
            connection.setReadTimeout(mReadTimeout);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            if(method.equals("GET"))
                connection.setDoOutput(false);
            connection.setUseCaches(false); //不使用缓存；
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestMethod(method);
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Connection", "keep-alive");// 维持长连接
            //在头中添加Token；
            if(token != null)
                connection.setRequestProperty("Authorization", token);
            if(contentType.equals(CONTENT_TYPE_MULTI)) {
                String content_type = contentType + "; boundary=" + mBoundary;
                connection.setRequestProperty("Content-Type", content_type);
            }else {
                connection.setRequestProperty("Content-Type", contentType);
            }

            connection.connect();
            if(!contentType.equals(CONTENT_TYPE_JSON)) {
                OutputStream outputStream = connection.getOutputStream();
                writeRequestData(dataRequest, outputStream, contentType);
                outputStream.flush();
                outputStream.close();
            }

            InputStream in;
            mHttpCode = connection.getResponseCode();
            if(mHttpCode == -1) {   //未知原因的错误
                Log.d(TAG, String.format("URL_%s--->%s", method, url.toString()));
                Log.d(TAG, String.format("URL_%s--->Http code: %d", method, mHttpCode));
                //mHttpCode = _HTTP_UNKNOWN_ERR;
                respData = makeJsonData(respData, "code", HTTP_UNKNOWN_ERR);
                respData = makeJsonData(respData, "message", "未知原因错误。");
                return respData;
            }else if(mHttpCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                //mHttpCode = _HTTP_SERVER_ERR;
                respData = makeJsonData(null, "code", HTTP_SERVER_ERR);
                respData = makeJsonData(respData, "message", "服务器内部错误。");
                return respData;
            }else if(mHttpCode == HttpURLConnection.HTTP_NOT_FOUND){
                //mHttpCode = _HTTP_API_ERR;
                respData = makeJsonData(respData, "code", HTTP_API_ERR);
                respData = makeJsonData(respData, "message", "API接口错误。");
                return respData;
            }else if( mHttpCode == HttpURLConnection.HTTP_OK)
                in = connection.getInputStream();
            else
                in = connection.getErrorStream();

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
                mHttpCode = HTTP_NETWORK_ERR;
                respData = makeJsonData(respData, "code", HTTP_NETWORK_ERR);
                respData = makeJsonData(respData, "message", "网络连接不可用。");
            }else if(err_msg.contains(("timed out"))) {
                mHttpCode = HTTP_TIMEOUT_ERR;
                respData = makeJsonData(respData, "code", HTTP_TIMEOUT_ERR);
                respData = makeJsonData(respData, "message", "服务器响应超时。");
            }else {
                mHttpCode = HTTP_UNKNOWN_ERR;
                respData = makeJsonData(respData, "code", HTTP_UNKNOWN_ERR);
                respData = makeJsonData(respData, "message", "未知原因错误。");
            }
            return respData;
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
