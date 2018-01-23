package com.bojkosoft.bojko108.testgpscam;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

public class DeviceLocation extends Service implements LocationListener {

    public static final String DEVICE_LOCATION = "com.bojkosoft.devicelocation";

    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 0f;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        this.initializeLocationManager();
        //startForeground(108, this.createNotification(intent));
        return START_STICKY;
    }

    private void initializeLocationManager() {
        if (this.mLocationManager == null) {
            this.mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            this.mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, this.LOCATION_INTERVAL, this.LOCATION_DISTANCE, this);
            this.mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, this.LOCATION_INTERVAL, this.LOCATION_DISTANCE, this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mLocationManager.removeUpdates(this);
        //stopForeground(true);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.broadcastDeviceLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void broadcastDeviceLocation(Location location) {
        Intent intent = new Intent(DEVICE_LOCATION);
        intent.putExtra("latitude", location.getLatitude());
        intent.putExtra("longitude", location.getLongitude());
        intent.putExtra("altitude", location.getAltitude());
        intent.putExtra("accuracy", location.getAccuracy());
        sendBroadcast(intent);
    }

    private Notification createNotification(Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setOngoing(true)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setStyle(new Notification.InboxStyle()
                        .addLine("listening for GPS ...")
                        .setBigContentTitle(getResources().getString(R.string.app_name)))
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setContentIntent(pendingIntent);

        return builder.build();
    }
}
