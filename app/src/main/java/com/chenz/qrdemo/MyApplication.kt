package com.chenz.qrdemo

import android.app.Application

import com.lib_zxing.activity.ZXingLibrary

/**
 * description: <一句话功能简述>
 *
 * @author Chenz
 * @date 2018/2/22 0022
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ZXingLibrary.initDisplayOpinion(this)//初始化ZXing
    }
}
