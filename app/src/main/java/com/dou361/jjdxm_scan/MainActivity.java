package com.dou361.jjdxm_scan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zxing.support.library.qrcode.QRCodeEncode;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int ACTIVITY_RESULT_SCAN = 1;

    private Button btn_scan1;
    private Button btn_scan2;
    private Button mEnCodeButton;
    private EditText mInputText;
    private TextView mResultTextView;
    private ImageView mQRCodeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_scan1 = (Button) findViewById(R.id.btn_scan1);
        btn_scan2 = (Button) findViewById(R.id.btn_scan2);
        mEnCodeButton = (Button) findViewById(R.id.encode);
        mInputText = (EditText) findViewById(R.id.input);
        mResultTextView = (TextView) findViewById(R.id.result);
        mQRCodeImage = (ImageView) findViewById(R.id.qrcode);

        btn_scan1.setOnClickListener(this);
        btn_scan2.setOnClickListener(this);
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
        } else if (v.getId() == R.id.btn_scan1) {
            Intent intent = new Intent(this, ScanActivityStyle1.class);
            startActivityForResult(intent, ACTIVITY_RESULT_SCAN);
        } else if (v.getId() == R.id.btn_scan2) {
            Intent intent2 = new Intent(this, ScanActivityStyle2.class);
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
