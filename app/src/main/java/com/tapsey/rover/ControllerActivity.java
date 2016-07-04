package com.tapsey.rover;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

public class ControllerActivity extends IOIOActivity implements CompoundButton.OnCheckedChangeListener {


    TextView consoleTv;
    ImageButton forwardBtn, backwardBtn, rightBtn, leftBtn;
    SwitchCompat modeTogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        consoleTv = (TextView) findViewById(R.id.console_tv);
        forwardBtn = (ImageButton) findViewById(R.id.forward_button);
        backwardBtn = (ImageButton) findViewById(R.id.backward_button);
        leftBtn = (ImageButton) findViewById(R.id.left_button);
        rightBtn = (ImageButton) findViewById(R.id.right_button);
        modeTogle = (SwitchCompat) findViewById(R.id.mode_switch);


        modeTogle.setOnCheckedChangeListener(this);
    }

    @Override
    protected IOIOLooper createIOIOLooper() {
        return new ControllerLooper();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (true == isChecked) {

            // enter joystick mode

            Intent intent = new Intent(this, JoyStickService.class);
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            intent.putExtra("rotation", rotation);
            startService(intent);

            forwardBtn.setEnabled(false);
            backwardBtn.setEnabled(false);
            leftBtn.setEnabled(false);
            rightBtn.setEnabled(false);

            consoleTv.setText("JoyStick mode!");

            Toast.makeText(this, "Entering JoyStick Mode..", Toast.LENGTH_LONG).show();

            // finish me launch link in browser
            finish();

            //sugest wifi conection wen not conected
            //etc


        } else {

            // enter controller mode
            forwardBtn.setEnabled(true);
            backwardBtn.setEnabled(true);
            leftBtn.setEnabled(true);
            rightBtn.setEnabled(true);
            consoleTv.setText("Controller mode!");

        }

    }

    private void toast(final String message) {
        final Context context = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showOnTv(final String message) {
        final Context context = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                consoleTv.setText(message);
            }
        });
    }

    class ControllerLooper extends BaseIOIOLooper {

        private DigitalOutput forwardPin_;
        private DigitalOutput backwardPin_;
        private DigitalOutput leftPin_;
        private DigitalOutput rightPin_;

        @Override
        protected void setup() throws ConnectionLostException, InterruptedException {

            String message = String.format(
                    "IOIOLib: %s\n" +
                            "Application firmware: %s\n" +
                            "Bootloader firmware: %s\n" +
                            "Hardware: %s",
                    "CONNECTED!!!",
                    ioio_.getImplVersion(IOIO.VersionType.IOIOLIB_VER),
                    ioio_.getImplVersion(IOIO.VersionType.APP_FIRMWARE_VER),
                    ioio_.getImplVersion(IOIO.VersionType.BOOTLOADER_VER),
                    ioio_.getImplVersion(IOIO.VersionType.HARDWARE_VER));


            showOnTv("Connected");
            forwardPin_ = ioio_.openDigitalOutput(22, false);
            backwardPin_ = ioio_.openDigitalOutput(23, false);
            leftPin_ = ioio_.openDigitalOutput(24, false);
            rightPin_ = ioio_.openDigitalOutput(25, false);

        }

        @Override
        public void loop() throws ConnectionLostException, InterruptedException {

            if (forwardBtn.isPressed()) forwardPin_.write(true);
            else forwardPin_.write(false);
            if (backwardBtn.isPressed()) backwardPin_.write(true);
            else backwardPin_.write(false);
            if (leftBtn.isPressed()) leftPin_.write(true);
            else leftPin_.write(false);
            if (rightBtn.isPressed()) rightPin_.write(true);
            else rightPin_.write(false);

        }

        @Override
        public void disconnected() {

            toast("Controller Disconnected");
            showOnTv("Controller Disconnected");
        }

        @Override
        public void incompatible() {

            String message = String.format(
                    "IOIOLib: %s\n" +
                            "Application firmware: %s\n" +
                            "Bootloader firmware: %s\n" +
                            "Hardware: %s",
                    "Incompatible firmware version!",
                    ioio_.getImplVersion(IOIO.VersionType.IOIOLIB_VER),
                    ioio_.getImplVersion(IOIO.VersionType.APP_FIRMWARE_VER),
                    ioio_.getImplVersion(IOIO.VersionType.BOOTLOADER_VER),
                    ioio_.getImplVersion(IOIO.VersionType.HARDWARE_VER));

            showOnTv(message);

        }
    }
}
