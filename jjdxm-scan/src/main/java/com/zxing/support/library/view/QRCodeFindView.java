package com.zxing.support.library.view;

import com.google.zxing.ResultPointCallback;
import com.zxing.support.library.camera.CameraManager;

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
 * 描 述：此接口主要是给findView 设置CameraManager ，来获取识别二维码的位置
 * <p>
 * <p>
 * 修订历史：
 * <p>
 * ========================================
 */
public interface QRCodeFindView extends ResultPointCallback {

    void setCamanerManager(CameraManager manager);
}
