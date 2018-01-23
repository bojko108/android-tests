package com.bojkosoft.bojko108.testgpscam;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener, SensorEventListener {
    // GPS Permissions
    private static final int REQUEST_GPS = 1;
    private static String[] PERMISSIONS_GPS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
    };

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                ((TextView) findViewById(R.id.textLatitude)).setText(bundle.get("latitude").toString());
                ((TextView) findViewById(R.id.textLongitude)).setText(bundle.get("longitude").toString());
                ((TextView) findViewById(R.id.textAltitude)).setText(bundle.get("altitude").toString());
                ((TextView) findViewById(R.id.textAccuracy)).setText(bundle.get("accuracy").toString());
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.checkPermissions();

        this.mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        this.mCompassDial = (ImageView) findViewById(R.id.compassDial);
        this.mCompassNiddle = (ImageView) findViewById(R.id.compassNiddle);

        findViewById(R.id.buttonCamera).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonCamera:
                startCamera();
                break;
        }
    }

    private void startGPS() {
        registerReceiver(receiver, new IntentFilter(DeviceLocation.DEVICE_LOCATION));
        Intent intent = new Intent(getApplicationContext(), DeviceLocation.class);
        startService(intent);
    }

    private void stopGPS() {
        unregisterReceiver(receiver);
        stopService(new Intent(this, DeviceLocation.class));
    }

    private void startCamera() {
        Intent camera = new Intent(getApplicationContext(), CameraPreviewActivity.class);
        startActivity(camera);
        //ErrorDialog.newInstance("error !!!").show(getFragmentManager., "wtf");
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.startGPS();
        this.mSensorManager.registerListener(this, this.mMagnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mSensorManager.unregisterListener(this);
        this.stopGPS();
    }

    private void checkPermissions() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // ask user for permissions
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_GPS,
                    REQUEST_GPS
            );
        }
    }

    private SensorManager mSensorManager;
    private Sensor mMagnetometer;
    private ImageView mCompassDial;
    private ImageView mCompassNiddle;

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] orientation = new float[3];
        float[] rotationMatrix = new float[16];
        float[] remappedMatrix = new float[16];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values.clone());

        int rot = getWindowManager().getDefaultDisplay().getRotation();

        if (!this.isDeviceFlat(event.values.clone())) {
            SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, remappedMatrix);
            SensorManager.getOrientation(remappedMatrix, orientation);
        } else {
            switch (rot) {
                case Surface.ROTATION_0:
                    // No orientation change, use default coordinate system
                    SensorManager.getOrientation(rotationMatrix, orientation);
                    break;
                case Surface.ROTATION_90:
                    SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, remappedMatrix);
                    SensorManager.getOrientation(remappedMatrix, orientation);
                    break;
                case Surface.ROTATION_180:
                    SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y, remappedMatrix);
                    SensorManager.getOrientation(remappedMatrix, orientation);
                    break;
                case Surface.ROTATION_270:
                    // tilt to right
                    SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X, remappedMatrix);
                    SensorManager.getOrientation(remappedMatrix, orientation);
                    break;
                default:
                    SensorManager.getOrientation(rotationMatrix, orientation);
                    break;
            }
        }

        // orientation[0]: Azimuth
        //
        // Angle of rotation about the -z axis.
        // This value represents the angle between the device's y axis
        // and the magnetic north pole. When facing north, this angle is 0,
        // when facing south, this angle is π. Likewise, when facing east,
        // this angle is π/2, and when facing west, this angle is -π/2.
        //
        // The range of values is -π to π.

        double azimuth = Math.toDegrees(orientation[0]);
        if (azimuth < 0) azimuth = 360 - (azimuth * -1);
        ((TextView) findViewById(R.id.textAzimuth)).setText(Long.toString(Math.round(azimuth)));

        this.rotateImageView((float) azimuth);
    }

    private void rotateImageView(float rotate) {
        this.mCompassDial.setRotation(360 - rotate);
        //this.mCompassNiddle.setRotation(360 - rotate);
    }

    private boolean isDeviceFlat(float[] values) {
        float z = values[2];
        float norm_of_g = (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + z * z);
        z = z / norm_of_g;
        int inclination = (int) Math.round(Math.toDegrees(Math.acos(z)));
        return (inclination < 30 || inclination > 150);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
