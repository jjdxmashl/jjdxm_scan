package com.dou361.scan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.dou361.scan.camera.AutoFucesListener;
import com.dou361.scan.camera.CameraConfig;
import com.dou361.scan.camera.CameraManager;
import com.dou361.scan.qrcode.QRCodeCameraDecode;
import com.dou361.scan.qrcode.QRCodeDecode;
import com.dou361.scan.utils.BitmapUtils;
import com.dou361.scan.view.QRCodeFindView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * ========================================
 * <p>
 * 版 权：dou361.com 版权所有 （C） 2015
 * <p>
 * 作 者：陈冠明
 * <p>
 * 个人网站：http://www.dou361.com
 * <p>
 * 版 本：1.0
 * <p>
 * 创建日期：2016/6/29 17:41
 * <p>
 * 描 述：扫码的主要入口
 * <p>
 * <p>
 * 修订历史：
 * <p>
 * ========================================
 */
public class ScanHelper {


    private static final String TAG = ScanHelper.class.getSimpleName();
    private SurfaceView mSurfaceView;
    private QRCodeFindView mQRCodeFindView;
    private OnScanResultListener mOnScanResultListener;
    private CameraManager mCameraManager;
    private QRCodeCameraDecode mCameraDecode;
    private boolean isPrivew;
    private static final int REQUEST_CODE = 100;
    private Activity mActivity;
    private String photoPath;


    public ScanHelper(Activity activity, SurfaceView surfaceView, QRCodeFindView findView) {
        this.mActivity = activity;
        this.mSurfaceView = surfaceView;
        this.mQRCodeFindView = findView;
        mCameraManager = new CameraManager(surfaceView.getContext().getApplicationContext());
        mQRCodeFindView.setCamanerManager(mCameraManager);
    }

    public void setVibrate(boolean flag) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(mActivity);
        prefs.edit().putBoolean(CameraConfig.KEY_VIBRATE, flag).commit();
    }

    public void setSong(boolean flag) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(mActivity);
        prefs.edit().putBoolean(CameraConfig.KEY_PLAY_BEEP, flag).commit();
    }


    public void setScanResultListener(OnScanResultListener onScanResultListener) {
        this.mOnScanResultListener = onScanResultListener;
    }

    private AutoFucesListener mAutoFucesListener = new AutoFucesListener() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {

        }
    };

    /**
     * 处理预览
     */
    private final Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            CameraDecodeTask mCameraDecodeTask = new CameraDecodeTask();
            mCameraDecodeTask.execute(data);
        }
    };

    private SurfaceHolder.Callback mSurfaceViewCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            initCamera(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            isPrivew = false;
        }
    };


    /**
     * surfaceview 初始化完成，初始化相机
     *
     * @param surfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (mCameraManager.isOpen()) return;

        try {
            isPrivew = true;
            mCameraManager.openDevice(surfaceHolder);
            mCameraManager.requestPreview(mPreviewCallback);
            mCameraManager.startPreview();
            mCameraManager.setAutoFucesListener(mAutoFucesListener);

        } catch (IOException e) {
            e.printStackTrace();
            isPrivew = false;
        }


    }

    /**
     * 在activity 的onResume 中调用
     */
    public void onResume() {
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.addCallback(mSurfaceViewCallback);
    }

    /**
     * 在activity 的onPause 中调用
     */
    public void onPause() {
        isPrivew = false;
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.removeCallback(mSurfaceViewCallback);
        mCameraManager.stopPreview();
        mCameraManager.closeDriver();
    }

    /**
     * 打开关闭闪光灯
     */
    public void toggleFlashlight(boolean flashlight) {
        mCameraManager.toggleFlashlight(flashlight);
    }

    public void selectPhoto() {
        BitmapUtils.selectPhoto(mActivity, REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (resultCode == Activity.RESULT_OK) {
            final ProgressDialog progressDialog;
            switch (requestCode) {
                case REQUEST_CODE:
                    if (intent != null) {
                        photoPath = BitmapUtils.getAbsolutePathFromNoStandardUri(intent.getData());
                    }
                    progressDialog = new ProgressDialog(mActivity);
                    progressDialog.setMessage("正在扫描...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            boolean inner = false;
                            Bitmap img = null;
                            if (photoPath != null) {
                                img = BitmapUtils
                                        .getCompressedBitmap(photoPath);
                            }
                            if (img != null) {
                                QRCodeDecode.Builder builder = new QRCodeDecode.Builder();
                                String resultString = builder.build().decode(img);
                                if (!TextUtils.isEmpty(resultString) && mOnScanResultListener != null) {
                                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                                    img.compress(Bitmap.CompressFormat.JPEG, 50, out);
                                    mOnScanResultListener.onScanResult(resultString, out.toByteArray());
                                    inner = true;
                                }
                            }
                            progressDialog.dismiss();
                            if(!inner){
                                Toast.makeText(mActivity,"识别不出来啊！",Toast.LENGTH_LONG).show();
                            }

                        }
                    }).start();

                    break;

            }
        }
    }


    private class CameraDecodeTask extends AsyncTask<byte[], Void, QRCodeCameraDecode.CameraDecodeResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mCameraDecode == null) {
                mCameraDecode = new QRCodeCameraDecode(mCameraManager, mQRCodeFindView);
            }
        }

        @Override
        protected void onPostExecute(QRCodeCameraDecode.CameraDecodeResult result) {
            super.onPostExecute(result);
            if (result.getDecodeResult() == null) { // 未解析出来，重新解析
                if (isPrivew)
                    mCameraManager.requestPreview(mPreviewCallback);
            } else { //解析出来

                String resultString = result.getDecodeResult().getText();

                if (!TextUtils.isEmpty(resultString) && mOnScanResultListener != null) {

                    mOnScanResultListener.onScanResult(resultString, result.getDecodeByte());
                }
            }
        }

        @Override
        protected QRCodeCameraDecode.CameraDecodeResult doInBackground(byte[]... params) {

            return mCameraDecode.decode(params[0]);
        }
    }


    public interface OnScanResultListener {
        /**
         * 扫描结果的监听
         *
         * @param notNullResult 解析结果的字符串,不会为null
         * @param resultByte    解析结果的byte数组,
         */
        void onScanResult(String notNullResult, byte[] resultByte);
    }

}
