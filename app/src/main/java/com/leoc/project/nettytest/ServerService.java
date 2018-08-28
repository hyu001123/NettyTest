package com.leoc.project.nettytest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2018/8/27.
 */

public class ServerService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ImServer.getInstance().init(2222);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        ImServer.getInstance().shutDown();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
