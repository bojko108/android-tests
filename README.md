## Contents

* [How to work with Compass](#how-to-work-with-compass)
* [How to work with GPS](#how-to-work-with-gps)
* [How to work with Camera](#how-to-work-with-camera)
* [Add confirmation dialog](#add-confirmation-dialog)
* [Add error dialog](#add-error-dialog)

# How to work with Compass
1. Add `DeviceCompass` class to the activity:
```java
private DeviceCompass deviceCompass;
...
// pass Context
this.deviceCompass = new DeviceCompass(this);
```
2. Implement `DeviceCompass.OnAzimuthChangedEventListener` listener in the activity:
```java
this.deviceCompass.setOnAzimuthChangedEventListener(this);
...
@Override
public void onAzimuthChanged(double azimuth) {
    // display azimuth value
    ((TextView) findViewById(R.id.textAzimuth)).setText(Long.toString(Math.round(azimuth)));
    
    // rotate image view
    this.mCompassDial.setRotation((float) (360 - azimuth));
}
```
3. Attach/detach to process. While in background we no longger need to listen for changes to the azimuth value:
```java
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
```

# How to work with GPS
1. Add permissions to android manifest:
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```
2. Add `DeviceLocation` class
3. Add `startGPS()` and `stopGPS()` methods:
```java
private void startGPS() {
    registerReceiver(receiver, new IntentFilter(DeviceLocation.DEVICE_LOCATION));
    Intent intent = new Intent(getApplicationContext(), DeviceLocation.class);
    startService(intent);
}

private void stopGPS() {
    unregisterReceiver(receiver);
    stopService(new Intent(this, DeviceLocation.class));
}
```
4. Add `BroadcastReceiver` to `MainActivity`. In `onReceive()` you can get device location information: `latitude`, `longitude`, `altitude` and `accuracy`. 
```java
private BroadcastReceiver receiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            double latitude = (double) bundle.get("latitude");
            double longitude = (double) bundle.get("longitude");
            double altitude = (double) bundle.get("altitude");
            float accuracy = (float) bundle.get("accuracy");            
        }
    }
};
```
# How to work with Camera
1. Add permissions to android manifest:
```xml
<uses-permission android:name="android.permission.CAMERA" />

<uses-feature android:name="android.hardware.camera2.full" />
```
2. Add `CameraPreviewActivity` activity and `CameraFragment` fragment. You'll also need `layout/activity_camera` and `layout/fragment_camera` xml files.
3. You can call camera activity from the main activity using:
```java
Intent camera = new Intent(getApplicationContext(), CameraPreviewActivity.class);
startActivity(camera);
```
4. Whatch out for `private static Size chooseOptimalSize(Size[], int, int, int, int, Size)`. In it we can define the max preview size according to screen size! If not used as a full screen activity, you must update `maxHeight` and `maxWidth`.
# Add confirmation dialog

# Add error dialog