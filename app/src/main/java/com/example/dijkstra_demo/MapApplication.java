package com.example.dijkstra_demo;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by FJS0420 on 2016/1/31.
 */
public class MapApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        SDKInitializer.initialize(getApplicationContext());
            SDKInitializer.initialize(getApplicationContext());


    }
}
