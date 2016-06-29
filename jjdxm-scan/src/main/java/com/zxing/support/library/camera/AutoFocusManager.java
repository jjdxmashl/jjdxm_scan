package com.zxing.support.library.camera;

import android.hardware.Camera;
import android.os.Handler;
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
 * 创建日期：2016/6/29 17:40
 * <p>
 * 描 述：自动对焦管理
 * <p>
 * <p>
 * 修订历史：
 * <p>
 * ========================================
 */
public class AutoFocusManager implements Camera.AutoFocusCallback{


    private static final long AUTO_FOCUS_TIMES = 1500L;
    private Camera mCamera;
    private AutoFucesListener mAutoFucesListener;
    private Handler mHandler = new Handler();
    private boolean isAutoFucesIng = false;


    /**
     * 开始自动对焦
     * @param autoFucesListener
     */
    public void start(AutoFucesListener autoFucesListener) {
        mAutoFucesListener = autoFucesListener;
        if (mCamera != null && !isAutoFucesIng){
            mCamera.autoFocus(this);
            isAutoFucesIng = true;
        }
    }


    /**
     * 停止对焦
     */
    public void stop() {
        isAutoFucesIng = false;
        mCamera = null;
        mHandler.removeCallbacks(mAutoRunnable);
    }

    public void setCamera(Camera camera) {
        this.mCamera = camera;
    }



    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (mAutoFucesListener != null){
            mAutoFucesListener.onAutoFocus(success, camera);
        }
        if (isAutoFucesIng){
            mHandler.postDelayed(mAutoRunnable,AUTO_FOCUS_TIMES);
        }
    }

    private Runnable mAutoRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCamera != null){
                mCamera.autoFocus(AutoFocusManager.this);
            }
        }
    };
}
