## Contents

* [How to work with Compass](#how-to-work-with-compass)
* [How to work with GPS](#how-to-work-with-gps)
* [How to work with Camera](#how-to-work-with-camera)
* [Add confirmation dialog](#add-confirmation-dialog)
* [Add error dialog](#add-error-dialog)

# How to work with Compass
1. Implement `SensorEventListener` in `MainActivity`
```java
private SensorManager mSensorManager;
private Sensor mMagnetometer;

@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    this.mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    this.mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
}
```
3. Register the event:
```java
@Override
protected void onResume() {
    super.onResume();
    this.mSensorManager.registerListener(this, this.mMagnetometer, SensorManager.SENSOR_DELAY_UI);
}

@Override
protected void onPause() {
    super.onPause();
    this.mSensorManager.unregisterListener(this);
}
```
2. Listen for changes to the sensor:
```java
@Override
public void onSensorChanged(SensorEvent event) {
    float[] orientation = new float[3];
    float[] rotationMatrix = new float[16];
    float[] remappedMatrix = new float[16];
    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values.clone());

    int rot = getWindowManager().getDefaultDisplay().getRotation();
    boolean isFlat = this.isDeviceFlat(event.values.clone());

    if (!isFlat) {
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
    // Angle of rotation about the z axis.
    // This value represents the angle between the device's y axis
    // and the magnetic north pole. When facing north, this angle is 0,
    // when facing south, this angle is π. Likewise, when facing east,
    // this angle is π/2, and when facing west, this angle is -π/2.
    //
    // The range of values is -π to π.

    double azimuth = Math.toDegrees(orientation[0]);
    if (azimuth < 0) azimuth = 360 - (azimuth * -1);
    ((TextView) findViewById(R.id.textAzimuth)).setText(Double.toString(azimuth));
}

// determines if the device position is flat or the user is looking through the display
private boolean isDeviceFlat(float[] values) {
    float z = values[2];
    float norm_of_g = (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + z * z);
    z = z / norm_of_g;
    int inclination = (int) Math.round(Math.toDegrees(Math.acos(z)));
    return (inclination < 30 || inclination > 150);
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
2. Add `CameraPreviewActivity` activity and `CameraFragment` fragment. You'll also need `layout/activity_camera` and `fragment_camera` layout files.
3. You can call camera activity from the main activity using:
```java
Intent camera = new Intent(getApplicationContext(), CameraPreviewActivity.class);
startActivity(camera);
```
4. Whatch out for `private static Size chooseOptimalSize(Size[], int, int, int, int, Size)`. In it we can define the max preview size according to screen size! If not used as a full screen activity, you must update `maxHeight` and `maxWidth`.
# Add confirmation dialog

# Add error dialog