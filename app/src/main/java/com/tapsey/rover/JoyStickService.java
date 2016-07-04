package com.tapsey.rover;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.view.Surface;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOService;

/**
 * Created by tapsey on 29/06/2016.
 */
public class JoyStickService extends IOIOService {

    int rotation;
    double tiltX;
    double tiltY;
    double tiltZ;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private SensorEventListener sListener;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int r = super.onStartCommand(intent, flags, startId);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sListener = new Acc();
        mSensorManager.registerListener(sListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);

        rotation = intent.getIntExtra("rotation", 0);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (intent != null && intent.getAction() != null
                && intent.getAction().equals("stop")) {
            // User clicked the notification. Need to stop the service.
            nm.cancel(0);
            stopSelf();
        } else {
            // Service starting. Create a notification.
            Notification notification = null;
            NotificationCompat.Builder bBuilder = new NotificationCompat.Builder(this);

            bBuilder.setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(new BitmapFactory().decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setTicker("Rover in JoyStick Mode")
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle("Rover in JoyStick Mode")
                    .setContentText("Tilt device to control")
                    .setContentInfo("Click to exit")
                    .setContentIntent(PendingIntent.getService(this,
                            0, new Intent("stop", null, this, this.getClass()), 0));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                notification = bBuilder.build();
            } else {

                notification = bBuilder.getNotification();
            }
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            nm.notify(0, notification);

        }

        return r;
    }

    @Override
    protected IOIOLooper createIOIOLooper() {

        return new JoyStickLooper();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class JoyStickLooper extends BaseIOIOLooper {

        private DigitalOutput forwardPin_;
        private DigitalOutput backwardPin_;
        private DigitalOutput leftPin_;
        private DigitalOutput rightPin_;
        private DigitalOutput led_;

        @Override
        protected void setup() throws ConnectionLostException,
                InterruptedException {

            led_ = ioio_.openDigitalOutput(IOIO.LED_PIN);
            led_.write(false);
            forwardPin_ = ioio_.openDigitalOutput(22, false);
            backwardPin_ = ioio_.openDigitalOutput(23, false);
            leftPin_ = ioio_.openDigitalOutput(24, false);
            rightPin_ = ioio_.openDigitalOutput(25, false);
        }

        @Override
        public void loop() throws ConnectionLostException,
                InterruptedException {
            if (tiltY > -0.5) forwardPin_.write(true);
            else forwardPin_.write(false);
            if (tiltY < -0.9) backwardPin_.write(true);
            else backwardPin_.write(false);
            if (tiltX < -0.4) leftPin_.write(true);
            else leftPin_.write(false);
            if (tiltX > 0.4) rightPin_.write(true);
            else rightPin_.write(false);

            Thread.sleep(100);

        }

        @Override
        public void disconnected() {


        }
    }

    class Acc implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {


            float x = 0;
            float y = 0;
            float z = 0;


            if (rotation == Surface.ROTATION_0) {
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
            } else if (rotation == Surface.ROTATION_90) {
                z = event.values[2];
                y = event.values[0];
                x = -event.values[1];

            } else if (rotation == Surface.ROTATION_180) {
                x = -event.values[0];
                y = -event.values[1];
                z = event.values[2];

            } else if (rotation == Surface.ROTATION_270) {
                z = event.values[2];
                y = -event.values[0];
                x = event.values[1];

            }

            double accX = -x / SensorManager.GRAVITY_EARTH;
            double accY = -y / SensorManager.GRAVITY_EARTH;
            double accZ = z / SensorManager.GRAVITY_EARTH;
            double totAcc = Math.sqrt((accX * accX) + (accY * accY) + (accZ * accZ));
            tiltX = Math.asin(accX / totAcc);
            tiltY = Math.asin(accY / totAcc);
            tiltZ = Math.asin(accZ / totAcc);// all values in radians

//
//            Log.d("rover"," tilt x is " + tiltX);
//            Log.d("rover"," tilt y is " + tiltY);
//            Log.d("rover"," tilt z is " + tiltZ);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {


        }
    }
}
