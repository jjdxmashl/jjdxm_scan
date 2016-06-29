package com.dou361.scan;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;

/**
 * ========================================
 * <p>
 * 版 权：深圳市晶网电子科技有限公司 版权所有 （C） 2015
 * <p>
 * 作 者：陈冠明
 * <p>
 * 个人网站：http://www.dou361.com
 * <p>
 * 版 本：1.0
 * <p>
 * 创建日期：2015-10-6 下午3:21:22
 * <p>
 * 描 述：公共类
 * <p>
 * <p>
 * 修订历史：
 * <p>
 * ========================================
 */
public class CommonUtils {

    /**
     * 得到SD卡可使用容量
     * @return
     */
    public static long getAvailableStorage() {
        String storageDirectory= Environment.getExternalStorageDirectory().toString();
        try {
            StatFs stat = new StatFs(storageDirectory);
            long avaliableSize = ((long) stat.getAvailableBlocks() * (long) stat.getBlockSize());
            return avaliableSize;
        } catch (RuntimeException ex) {
            return 0;
        }
    }

    /**
     * 判断网络是否正常
     * @param context
     * @return
     */
    public static boolean isOnline(Context context) {
        try {
            ConnectivityManager cm=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni=cm.getActiveNetworkInfo();
            return ni!=null ? ni.isConnectedOrConnecting() : false;
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
