package com.tapsey.rover;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOService;

/**
 * Created by tapsey on 23/06/2016.
 */
public class RoverServiceLooper  extends IOIOService {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    protected IOIOLooper createIOIOLooper() {
        return super.createIOIOLooper();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
