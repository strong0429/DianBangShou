package com.xingdhl.www.storehelper.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.xingdhl.www.storehelper.R;

import com.google.zxing.Result;
import com.xingdhl.www.zxinglibrary.camera.CameraManager;
import com.xingdhl.www.zxinglibrary.decode.DecodeHandler;
import com.xingdhl.www.zxinglibrary.decode.DecodeThread;
import com.xingdhl.www.zxinglibrary.utils.BeepManager;
import com.xingdhl.www.zxinglibrary.utils.CaptureActivityHandler;
import com.xingdhl.www.zxinglibrary.utils.InactivityTimer;
import java.io.IOException;

/**
 * Created by Leeyc on 2018/3/13.
 *
 */

public abstract class ScanBarcodeUtil implements SurfaceHolder.Callback, DecodeHandler.Callbacks{
    private static final String TAG = "SCAN_BARCODE";

    private SurfaceView scanPreview;
    private RelativeLayout scanCropView;
    private ImageView scanLine;

    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;

    private TranslateAnimation animation;

    private Rect mCropRect;
    private boolean isFlashOpen;
    private boolean isHasSurface;

    public ScanBarcodeUtil(Activity activity, SurfaceView scanPreview, RelativeLayout scanCropView, ImageView scanLine){
        this.scanLine = scanLine;
        this.scanPreview = scanPreview;
        this.scanCropView = scanCropView;

        beepManager = new BeepManager(activity);
        inactivityTimer = new InactivityTimer(activity);
        animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
                0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
                0.0f, Animation.RELATIVE_TO_PARENT, 0.9f);
        animation.setDuration(2000);    //动画2秒循环一次；
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.REVERSE);
        scanLine.startAnimation(animation);

        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //申请相机使用权限
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA }, 111);//自定义的code
        }

        mCropRect = null;
        isHasSurface = false;
    }

    public void openFlash(){
        if(cameraManager == null || !cameraManager.isOpen())
            return;

        isFlashOpen = true;
        Camera camera = cameraManager.getCamera();
        Camera.Parameters parameters = camera.getParameters();
        if (parameters.getFlashMode().equals("torch")) {
            return;
        } else {
            parameters.setFlashMode("torch");
        }
        camera.setParameters(parameters);
    }

    public void beep(){
        inactivityTimer.onActivity();
        beepManager.playBeepSoundAndVibrate();
    }

    public void startScan(int delayMS){
        cameraManager.startPreview();
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
        scanLine.startAnimation(animation);

        if(isFlashOpen){
            Camera camera = cameraManager.getCamera();
            Camera.Parameters parameters = camera.getParameters();
            if (parameters.getFlashMode().equals("torch")) {
                return;
            } else {
                parameters.setFlashMode("torch");
            }
            camera.setParameters(parameters);
        }
    }

    public void closeFlash(){
        if(cameraManager == null || !cameraManager.isOpen())
            return;

        isFlashOpen = false;
        Camera camera = cameraManager.getCamera();
        Camera.Parameters parameters = camera.getParameters();
        if (parameters.getFlashMode().equals("off")) {
            return;
        } else {
            parameters.setFlashMode("off");
        }
        camera.setParameters(parameters);
    }

    public void stopScan(){
        cameraManager.stopPreview();
        scanLine.clearAnimation();

        if(isFlashOpen){
            Camera camera = cameraManager.getCamera();
            Camera.Parameters parameters = camera.getParameters();
            if (parameters.getFlashMode().equals("off")) {
                return;
            } else {
                parameters.setFlashMode("off");
            }
            camera.setParameters(parameters);
        }
    }

    public void onDestroy(){
        inactivityTimer.shutdown();
    }

    public void onPause(){
        //该函数必须在Activaty的 onPause 回调函数中调用；
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        cameraManager.closeDriver();
        if (!isHasSurface) {
            scanPreview.getHolder().removeCallback(this);
        }
    }

    public void onResume(Context context){
        //该函数必须在Activaty的 onResume 回调函数中调用；
        // CameraManager must be initialized here, not in onCreate(). This is necessary because
        // we don't want to open the camera driver and measure the screen size if we're going to
        // show the help on first launch. That led to bugs where the scanning rectangle was the
        // wrong size and partially off screen.
        cameraManager = new CameraManager(context);
        handler = null;

        if (isHasSurface) {
            // The activity was paused but not stopped, so the surface still
            // exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(scanPreview.getHolder());
        } else {
            // Install the callback and wait for surfaceCreated() to init the
            // camera.
            scanPreview.getHolder().addCallback(this);
        }

        inactivityTimer.onResume();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            Log.w(TAG, "No SurfaceHolder provided");
            return;
            //throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a
            // RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager, DecodeThread.ALL_MODE);
            }

            initCrop();
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
        }
    }

    private void initCrop() {
        int cameraWidth = cameraManager.getCameraResolution().y;
        int cameraHeight = cameraManager.getCameraResolution().x;

        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);
        int cropLeft = location[0];
        int cropTop = location[1]; // - getStatusBarHeight();
        /** 减去外面窗口的相对位置 */
        //scanContainer.getLocationInWindow(location);
        scanPreview.getLocationInWindow(location);
        cropLeft -= location[0];
        cropTop -= location[1];

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        /** 获取布局容器的宽高 */
        int containerWidth = scanPreview.getWidth();//scanContainer.getWidth();
        int containerHeight = scanPreview.getHeight();//scanContainer.getHeight();

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /** 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    @Override
    public abstract void handleDecode(Result rawResult, Bundle bundle);

    @Override
    public CaptureActivityHandler getHandler() {
        return handler;
    }

    @Override
    public CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    public Rect getCropRect() {
        return mCropRect;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        cameraManager.setFocusOnRect(mCropRect, scanPreview.getWidth(), scanPreview.getHeight());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!isHasSurface) {
            isHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isHasSurface = false;
    }
}
