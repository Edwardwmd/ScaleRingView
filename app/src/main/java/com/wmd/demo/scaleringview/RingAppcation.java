package com.wmd.demo.scaleringview;

import android.app.Application;
import android.content.Context;

import com.zhy.autolayout.config.AutoLayoutConifg;

/**
 * 时间：2017/07/28/22：07
 * 作者：吴明德
 * 邮箱：1732141816@qq.com
 * 作用：xxxx
 * 声明：版权归作者所有
 */

public class RingAppcation extends Application {
    public static Context mC;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mC=RingAppcation.this;
        //屏幕适配
        AutoLayoutConifg.getInstance().useDeviceSize();

    }
    public static Context getAppcation(){
        return mC;
    }
}
