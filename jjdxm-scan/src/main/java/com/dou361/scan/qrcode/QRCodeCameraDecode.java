package com.dou361.scan.qrcode;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.HybridBinarizer;
import com.dou361.scan.camera.CameraManager;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.Vector;

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
 * 描 述：照相机预览解码
 * <p>
 * <p>
 * 修订历史：
 * <p>
 * ========================================
 */
public class QRCodeCameraDecode {

    private static final String TAG = QRCodeCameraDecode.class.getSimpleName();

    private  Hashtable<DecodeHintType, Object> hints;
    private CameraManager mCameraManger;
    private MultiFormatReader multiFormatReader;

    public QRCodeCameraDecode(CameraManager cameraManager,ResultPointCallback resultPointCallback){
        this.mCameraManger = cameraManager;
        hints = new Hashtable<>(3);

        Vector<BarcodeFormat> decodeFormats = new Vector<>();
        decodeFormats.addAll(QRCodeDecodeFormat.ONE_D_FORMATS);
        decodeFormats.addAll(QRCodeDecodeFormat.QR_CODE_FORMATS);
        decodeFormats.addAll(QRCodeDecodeFormat.DATA_MATRIX_FORMATS);
        decodeFormats.addAll(QRCodeDecodeFormat.PRODUCT_FORMATS);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        if (resultPointCallback != null) {
            hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
        }

        multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(hints);
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
     * reuse the same reader objects from one decode to the next.
     *
     * @param data   The YUV preview frame.
     */
    public CameraDecodeResult decode(byte[] data) {
        int width = mCameraManger.getCameraConfig().getCameraResolution().x;
        int height = mCameraManger.getCameraConfig().getCameraResolution().y;
        Result rawResult = null;

        // 这里需要将获取的data翻转一下，因为相机默认拿的的横屏的数据
        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++)
                rotatedData[x * height + height - y - 1] = data[x + y * width];
        }
        int tmp = width; // Here we are swapping, that's the difference to #11
        width = height;
        height = tmp;

        PlanarYUVLuminanceSource source = mCameraManger.buildLuminanceSource(rotatedData, width, height);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            rawResult = multiFormatReader.decodeWithState(bitmap);
        } catch (ReaderException re) {
            // continue
        } finally {
            multiFormatReader.reset();
        }
        Log.e(TAG, "decode:" + rawResult);
        CameraDecodeResult cameraDecodeResult = new CameraDecodeResult();
        cameraDecodeResult.setDecodeResult(rawResult);

        if (rawResult != null){
            cameraDecodeResult.setDecodeByte(bundleThumbnail(source));
        }
        return cameraDecodeResult;
    }


    private static byte[] bundleThumbnail(PlanarYUVLuminanceSource source) {
        int[] pixels = source.renderThumbnail();
        int width = source.getThumbnailWidth();
        int height = source.getThumbnailHeight();
        Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
        return  out.toByteArray();
    }

    public static class CameraDecodeResult{
        private Result decodeResult;
        private byte[] decodeByte;

        public Result getDecodeResult() {
            return decodeResult;
        }

        public void setDecodeResult(Result decodeResult) {
            this.decodeResult = decodeResult;
        }

        public byte[] getDecodeByte() {
            return decodeByte;
        }

        public void setDecodeByte(byte[] decodeByte) {
            this.decodeByte = decodeByte;
        }
    }

}
