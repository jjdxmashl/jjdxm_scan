package com.dou361.scan.camera;

import android.hardware.Camera;

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
 * 描 述：自动对焦监听
 * <p>
 * <p>
 * 修订历史：
 * <p>
 * ========================================
 */
public interface AutoFucesListener {

    public void onAutoFocus(boolean success, Camera camera);
}
