package com.bojkosoft.bojko108.testgpscam;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener, DeviceCompass.OnAzimuthChangedEventListener {
    // GPS Permissions
    private static final int REQUEST_PERMISSION = 1;
    private static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
    };

    private ImageView mCompassDial;
    private ImageView mCompassNiddle;
    private DeviceCompass deviceCompass;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                //((TextView) findViewById(R.id.textLatitude)).setText(bundle.get("latitude").toString());
                //((TextView) findViewById(R.id.textLongitude)).setText(bundle.get("longitude").toString());
                ((TextView) findViewById(R.id.textAltitude)).setText(bundle.get("altitude").toString());
                ((TextView) findViewById(R.id.textAccuracy)).setText(bundle.get("accuracy").toString());
            }
        }
    };


    @Override
    public void onAzimuthChanged(double azimuth) {
        ((TextView) findViewById(R.id.textAzimuth)).setText(Long.toString(Math.round(azimuth)));
        this.mCompassDial.setRotation((float) (360 - azimuth));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.checkPermissions();

        this.startGPS();

        this.mCompassDial = (ImageView) findViewById(R.id.compassDial);
        this.mCompassNiddle = (ImageView) findViewById(R.id.compassNiddle);

        this.deviceCompass = new DeviceCompass(this);
        this.deviceCompass.setOnOrientationChangedEventListener(this);

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
        intent.putExtra(DeviceLocation.LOCATION_INTERVAL, 10000);
        intent.putExtra(DeviceLocation.LOCATION_DISTANCE, 10f);
        //intent.putExtra(DeviceLocation.CREATE_NOTIFICATION,false);
        startService(intent);
    }

    private void stopGPS() {
        unregisterReceiver(receiver);
        stopService(new Intent(this, DeviceLocation.class));
    }

    private void startCamera() {
        //ErrorDialog.newInstance("asdsad").show(getFragmentManager(),"a");
        Intent camera = new Intent(getApplicationContext(), CameraPreviewActivity.class);
        startActivity(camera);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.deviceCompass.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.deviceCompass.stopListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.stopGPS();
    }

    private void checkPermissions() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // ask user for permissions
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS,
                    REQUEST_PERMISSION
            );
        }
    }
}
