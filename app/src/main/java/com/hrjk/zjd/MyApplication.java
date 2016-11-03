package com.hrjk.zjd;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;

/**
 * Created by 15137 on 2016/10/28.
 */

public class MyApplication extends Application {
    /**
     *  实时定位的经纬度信息。
     */
    public static LatLng sLatLng;

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }

    public void setsLatLng(LatLng sLatLng) {
        this.sLatLng = sLatLng;
    }
    public LatLng getsLatLng() {
        return sLatLng;
    }
}
