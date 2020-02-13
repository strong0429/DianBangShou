package com.xingdhl.www.storehelper.webservice;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Created by Strong on 18/1/28.
 *
 */

public class UpdateService extends Service {
    /** 安卓系统下载类 **/
    DownloadManager mManager;
    /** 接收下载完的广播 **/
    DownloadCompleteReceiver mReceiver;

    public UpdateService(){
    }


    /** 初始化下载器 **/
    private void initDownLoadManager() {
        mManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        mReceiver = new DownloadCompleteReceiver();

        //设置下载地址
        Uri parse = Uri.parse(WebServiceAPIs.URL_GET_APP);
        DownloadManager.Request down = new DownloadManager.Request(parse);

        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);

        // 下载时，通知栏显示进度；
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        }

        // 显示下载界面
        down.setVisibleInDownloadsUi(true);

        // 设置下载后文件存放的位置
        down.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "dianbangshou.apk");

        // 将下载请求放入队列
        mManager.enqueue(down);

        //注册下载广播
        registerReceiver(mReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initDownLoadManager();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if(mReceiver != null){
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 接受下载完成后的intent
    class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //判断是否下载完成的广播
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                //获取下载的文件id
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                //自动安装apk
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    //Uri uriForDownloadedFile = mManager.getUriForDownloadedFile(downId);
                    Cursor cursor = mManager.query(new DownloadManager.Query().setFilterById(downId));
                    if(cursor != null){
                        cursor.moveToFirst();
                        int fileNameIdx = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                        String fileName = cursor.getString(fileNameIdx);
                        cursor.close();
                        installApkNew(Uri.parse(fileName).getLastPathSegment());
                    }
                }

                //停止服务并关闭广播
                UpdateService.this.stopSelf();
            }
        }

        //安装apk
        protected void installApkNew(String fileName) {
            Uri uri;
            String pathFile = Environment.getExternalStorageDirectory() + "/" +
                    Environment.DIRECTORY_DOWNLOADS + "/" + fileName;
            //创建Intent，执行动作ACTION_VIEW；
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    uri = FileProvider.getUriForFile(UpdateService.this,
                            "com.xingdhl.www.storehelper.fileprovider", new File(pathFile));
                }catch (RuntimeException re){
                    re.printStackTrace();
                    return;
                }
            }else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                uri = Uri.parse(pathFile);
            }
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            try {
                startActivity(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
            //android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}
