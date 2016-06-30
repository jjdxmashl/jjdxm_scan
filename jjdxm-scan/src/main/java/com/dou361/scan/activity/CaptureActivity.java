package com.dou361.scan.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.dou361.scan.ScanHelper;
import com.dou361.scan.utils.BeepManager;
import com.dou361.scan.utils.ResourceUtils;
import com.dou361.scan.view.FinderViewStyle;


public class CaptureActivity extends Activity implements ScanHelper.OnScanResultListener, View.OnClickListener {

    private SurfaceView mSurfaceView;
    private FinderViewStyle mFinderView;
    private ScanHelper mScanHelper;
    private ImageView capture_flashlight;
    private ImageView capture_scan_photo;
    private Context mContext;
    private BeepManager beepManager;
    private boolean flashlight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = this;
        setContentView(ResourceUtils.getResourceIdByName(mContext, "layout", "jjdxm_scan_activity_capture"));

        mFinderView = (FinderViewStyle) findViewById(ResourceUtils.getResourceIdByName(mContext, "id", "capture_viewfinder_view"));
        mSurfaceView = (SurfaceView) findViewById(ResourceUtils.getResourceIdByName(mContext, "id", "sufaceview"));
        capture_flashlight = (ImageView) findViewById(ResourceUtils.getResourceIdByName(mContext, "id", "capture_flashlight"));
        capture_scan_photo = (ImageView) findViewById(ResourceUtils.getResourceIdByName(mContext, "id", "capture_scan_photo"));
        capture_flashlight.setOnClickListener(this);
        capture_scan_photo.setOnClickListener(this);

        mScanHelper = new ScanHelper(this, mSurfaceView, mFinderView);
        mScanHelper.setScanResultListener(this);
        beepManager = new BeepManager(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mScanHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScanHelper.onPause();
    }

    @Override
    public void onScanResult(String notNullResult, byte[] resultBytes) {
        Intent intent = new Intent();
        intent.putExtra("result", notNullResult);
        intent.putExtra("resultByte", resultBytes);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == ResourceUtils.getResourceIdByName(mContext, "id", "capture_scan_photo")) {// 图片识别
            mScanHelper.selectPhoto();
        } else if (v.getId() == ResourceUtils.getResourceIdByName(mContext, "id", "capture_flashlight")) {
            flashlight = !flashlight;
            mScanHelper.toggleFlashlight(flashlight);
        }
    }
}
