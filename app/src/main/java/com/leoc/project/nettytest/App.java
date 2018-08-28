package com.leoc.project.nettytest;

import android.app.Application;

/**
 * Created by Administrator on 2018/8/27.
 */

public class App extends Application{
    private static App me;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public static App me() {
        return me;
    }
}
