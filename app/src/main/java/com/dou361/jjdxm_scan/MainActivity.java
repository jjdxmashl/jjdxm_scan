package com.dou361.jjdxm_scan;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dou361.scan.activity.CaptureActivity;
import com.dou361.scan.qrcode.QRCodeEncode;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int ACTIVITY_RESULT_SCAN = 1;

    private Button btn_scan;
    private Button mEnCodeButton;
    private EditText mInputText;
    private TextView mResultTextView;
    private ImageView mQRCodeImage;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        btn_scan = (Button) findViewById(R.id.btn_scan);
        mEnCodeButton = (Button) findViewById(R.id.encode);
        mInputText = (EditText) findViewById(R.id.input);
        mResultTextView = (TextView) findViewById(R.id.result);
        mQRCodeImage = (ImageView) findViewById(R.id.qrcode);

        btn_scan.setOnClickListener(this);
        mEnCodeButton.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.encode) {
            String input = mInputText.getText().toString();
            if (TextUtils.isEmpty(input)) {
                Toast.makeText(this, "请输入要生成二维码的字符串", Toast.LENGTH_SHORT).show();
            } else {
                QRCodeEncode.Builder builder = new QRCodeEncode.Builder();
                builder.setBackgroundColor(0xffffff)
                        .setOutputBitmapHeight(800)
                        .setOutputBitmapWidth(800)
                        .setOutputBitmapPadding(10);
                mQRCodeImage.setImageBitmap(builder.build().encode(input));
            }
        } else if (v.getId() == R.id.btn_scan) {
            if (Dexter.isRequestOngoing()) {
                return;
            }
            Dexter.checkPermissions(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    for (PermissionGrantedResponse response : report.getGrantedPermissionResponses()) {
                        showPermissionGranted(response.getPermissionName());
                    }

                    for (PermissionDeniedResponse response : report.getDeniedPermissionResponses()) {
                        showPermissionDenied(response.getPermissionName(), response.isPermanentlyDenied());
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    showPermissionRationale(token);
                }
            }, Manifest.permission.FLASHLIGHT, Manifest.permission.CAMERA);
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void showPermissionRationale(final PermissionToken token) {
        new AlertDialog.Builder(this).setTitle("We need this permission")
                .setMessage("This permission is needed for doing some fancy stuff so please, allow it!")
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.cancelPermissionRequest();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.continuePermissionRequest();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        token.cancelPermissionRequest();
                    }
                })
                .show();
    }

    private void showPermissionDenied(String permissionName, boolean permanentlyDenied) {

    }

    private void showPermissionGranted(String permissionName) {
        if (Manifest.permission.CAMERA.equals(permissionName)) {
            Intent intent2 = new Intent(this, CaptureActivity.class);
            startActivityForResult(intent2, ACTIVITY_RESULT_SCAN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_RESULT_SCAN) {
            if (data != null) {
                String result = data.getStringExtra("result");
                mResultTextView.setText(result);
                Bitmap barcode = null;
                byte[] compressedBitmap = data.getByteArrayExtra("resultByte");
                if (compressedBitmap != null) {
                    barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
                    barcode = barcode.copy(Bitmap.Config.RGB_565, true);
                    mQRCodeImage.setImageBitmap(barcode);
                }

            } else {
                mResultTextView.setText("没有结果！！！");
            }
        }
    }
}
