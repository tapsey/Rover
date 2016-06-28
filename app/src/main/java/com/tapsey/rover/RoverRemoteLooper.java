package com.tapsey.rover;

import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;

/**
 * Created by tapsey on 23/06/2016.
 */
public class RoverRemoteLooper extends BaseIOIOLooper {

    @Override
    protected void setup() throws ConnectionLostException, InterruptedException {
        super.setup();
    }

    @Override
    public void loop() throws ConnectionLostException, InterruptedException {
        super.loop();
    }

    @Override
    public void disconnected() {
        super.disconnected();
    }

    @Override
    public void incompatible() {
        super.incompatible();
    }
}
