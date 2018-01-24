package com.bojkosoft.bojko108.testgpscam;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
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

import java.text.DecimalFormat;

public class DeviceLocation extends Service implements LocationListener {

    public static final String DEVICE_LOCATION = "com.bojkosoft.devicelocation";
    public static final String LOCATION_INTERVAL = "location_interval";
    public static final String LOCATION_DISTANCE = "location_distance";
    public static final String CREATE_NOTIFICATION = "create_notification";

    private LocationManager mLocationManager = null;
    private int mLocationInterval = 1000;
    private float mLocationDistance = 0f;

    private static final int NOTIFICATION_ID = 1886;
    private NotificationManager mNotificationManager;
    private Notification.Builder mNotificationBuilder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent != null) {
            this.mLocationInterval = intent.getIntExtra(LOCATION_INTERVAL, this.mLocationInterval);
            this.mLocationDistance = intent.getFloatExtra(LOCATION_DISTANCE, this.mLocationDistance);

            if (intent.getBooleanExtra(CREATE_NOTIFICATION, true)) {
                this.initializeNotification();
                startForeground(NOTIFICATION_ID, this.mNotificationBuilder.build());
            }
        }

        this.initializeLocationManager();

        return START_STICKY;
    }

    private void initializeNotification() {
        this.mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        this.mNotificationBuilder = this.createNotification("GPS", null);
    }

    private void initializeLocationManager() {
        if (this.mLocationManager == null) {
            this.mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            this.mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, this.mLocationInterval, this.mLocationDistance, this);
            this.mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, this.mLocationInterval, this.mLocationDistance, this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mLocationManager.removeUpdates(this);
        stopForeground(true);
    }

    @Override
    public void onLocationChanged(Location location) {
        DecimalFormat f = new DecimalFormat("##.000000");
        this.updateNotification("lat: " + f.format(location.getLatitude()) + "; lon: " + f.format(location.getLongitude()));
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

    private void updateNotification(String text) {
        if (this.mNotificationBuilder != null) {
            this.mNotificationBuilder.setContentText(text);

            this.mNotificationManager.notify(NOTIFICATION_ID, this.mNotificationBuilder.build());
        }
    }

    private Notification.Builder createNotification(String title, String text) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(title)
                .setContentText(text);

        return builder;
    }
}
