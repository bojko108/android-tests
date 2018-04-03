## Contents

* [How to work with Compass](#how-to-work-with-compass)
* [How to work with GPS](#how-to-work-with-gps)
* [How to work with Camera](#how-to-work-with-camera)
* [Add confirmation dialog](#add-confirmation-dialog)
* [Add error dialog](#add-error-dialog)
* [Select directory](#select-directory)
* [Add line in view](#add-line-in-view)
* [Custom view](#custom-view)
* [Vector 3D](#vector3d)

# How to work with Compass
1. Add `DeviceCompass` class to the activity. The default value for `azimuthStep` is 0 degrees. If set, the azimuth (and other orientation angles) will be returned only if the change is bigger than the azimuth step:
```java
private DeviceCompass deviceCompass;
...
// pass Context
this.deviceCompass = new DeviceCompass(this);
// you can set the azimuth step in degrees:
this.deviceCompass.setAzimuthStep(5);
```
2. Implement `DeviceCompass.OnOrientationChangedEventListener` listener in the activity:
```java
this.deviceCompass.setOnOrientationChangedEventListener(this);
...
@Override
public void onOrientationChanged(float azimuth, float pitch, float roll) {
    // display azimuth value
    ((TextView) findViewById(R.id.textAzimuth)).setText(Long.toString(Math.round(azimuth)));
    
    // rotate image view
    this.mCompassDial.setRotation((float) (360 - azimuth));

    // pitch * -1 - if used in Android.graphics.Camera
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
    intent.putExtra(DeviceLocation.LOCATION_INTERVAL, 10000);   // wait atleast 10 seconds
    intent.putExtra(DeviceLocation.LOCATION_DISTANCE, 50f);     // call on distance changed with 50 meters
    startService(intent);
}

private void stopGPS() {
    unregisterReceiver(receiver);
    stopService(new Intent(this, DeviceLocation.class));
}
```
4. You can set some of the parameters when starting the service:
- minimum location interval (in miliseconds), default is `10000`;
- minimum location distance (in meters), default is `0f`;
- to create a notification or not, default is `true`.
```java
Intent intent = new Intent(getApplicationContext(), DeviceLocation.class);
intent.putExtra(DeviceLocation.LOCATION_INTERVAL, 1000);   // 1 second
intent.putExtra(DeviceLocation.LOCATION_DISTANCE, 50f);    // 50 meters
intent.putExtra(DeviceLocation.CREATE_NOTIFICATION, false); // do not create a notification
startService(intent);
```
5. Add `BroadcastReceiver` to `MainActivity`. In `onReceive()` you can get device location information: `latitude`, `longitude`, `altitude` and `accuracy`. 
```java
private BroadcastReceiver receiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            double latitude = (double) bundle.get(DeviceLocation.EXTRA_LATITUDE);
            double longitude = (double) bundle.get(DeviceLocation.EXTRA_LONGITUDE);
            double altitude = (double) bundle.get(DeviceLocation.EXTRA_ALTITUDE);
            float accuracy = (float) bundle.get(DeviceLocation.EXTRA_ACCURACY);       
        }
    }
};
```
6. Available navigation data:
- `DeviceLocation.EXTRA_LATITUDE` - geographic latitude (WGS84);
- `DeviceLocation.EXTRA_LONGITUDE` - geographic longitude (WGS84);
- `DeviceLocation.EXTRA_ALTITUDE` - GPS altitude in meters;
- `DeviceLocation.EXTRA_ACCURACY` - position accuracy in meters;
- `DeviceLocation.EXTRA_TIME` - UTC time of this fix, in milliseconds since January 1, 1970
- `DeviceLocation.EXTRA_DECLINATION` - get magnetic declination for this location in degrees.

7. Check GPS permissions:
```java
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
```

# How to work with Camera
1. Add permissions to android manifest:
```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera2.full" />
```
2. Add `CameraPreviewActivity` activity, `CameraFragment` fragment and `AutoFitTextureView` class. You'll also need `layout/activity_camera` and `layout/fragment_camera` xml files.
- you can add only `CameraFragment` and just add a fragment in your activity:
```xml
<fragment android:name="PACKAGE.NAME.CameraFragment"
    android:id="@+id/cameraPreview"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```
```java
@Override
protected void onResume() {
    super.onResume();

    if (this.mCameraFragment == null) {
        this.mCameraFragment = (CameraFragment) getFragmentManager().findFragmentById(R.id.cameraPreview);
        
        // work with camera fragment
    }
}
```
3. Check camera permissions:
```java
private static final int REQUEST_PERMISSION = 1;

private void checkCameraPermissions() {
    int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

    if (permission != PackageManager.PERMISSION_GRANTED) {
        // ask user for permissions
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_PERMISSION
        );
    }
}
```
4. Using the camera preview:
- you can call camera from the main activity using:
```java
Intent camera = new Intent(getApplicationContext(), CameraPreviewActivity.class);
startActivity(camera);
```
5. Whatch out for `private static Size chooseOptimalSize(Size[], int, int, int, int, Size)`. In it we can define the max preview size according to screen size! If not used as a full screen activity, you must update `maxHeight` and `maxWidth`.
6. To calculate horizontal and vertical field of view you can use:
```java
private float getHorizontalFieldOfView(CameraCharacteristics info) {
    SizeF sensorSize = info.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
    float[] focalLengths = info.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);

    if (focalLengths != null && focalLengths.length > 0) {
        return (float) (2.0f * Math.atan(sensorSize.getWidth() / (2.0f * focalLengths[0])));
    }

    return 1.1f;
}

private float getVerticalFieldOfView(CameraCharacteristics info) {
    SizeF sensorSize = info.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
    float[] focalLengths = info.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);

    if (focalLengths != null && focalLengths.length > 0) {
        return (float) (2.0f * Math.atan(sensorSize.getHeight() / (2.0f * focalLengths[0])));
    }

    return 1.1f;
}

...

int orientation = getResources().getConfiguration().orientation;
if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
    this.mVerticalFOV = Math.toDegrees(this.getVerticalFieldOfView(characteristics));
    this.mHorizontalFOV = Math.toDegrees(this.getHorizontalFieldOfView(characteristics));
} else {
    this.mVerticalFOV = Math.toDegrees(this.getHorizontalFieldOfView(characteristics));
    this.mHorizontalFOV = Math.toDegrees(this.getVerticalFieldOfView(characteristics));
}
```
# Add confirmation dialog

# Add alert dialog
1. Add class:
```java
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

public class DialogAlert {
    public static Dialog create(Activity activity, String message, String title) {
        title = (title != null ? title : "Alert");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message)
                .setTitle(title).setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }
}
```
2. Use it:
```java
DialogAlert.create(this, "Message", "Title").show();
```
# Select directory
```java
private void chooseDirectory() {
    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
    intent.addCategory(Intent.CATEGORY_DEFAULT);
    if (intent.resolveActivity(getPackageManager()) != null) {
        startActivityForResult(intent, REQUEST_CODE);
    }
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
        Uri uri = data.getData();   // get selected directory URI
    }
}
```

# Add line in view
- For a verical line:
```xml
<View
    android:id="@+id/targetLine"
    android:layout_width="1dp"
    android:layout_height="fill_parent"
    android:layout_marginBottom="50dp"
    android:background="@color/primaryColor"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent" />
```
- For a horizontal line:
```xml
<View
    android:id="@+id/targetLine"
    android:layout_width="fill_parent"
    android:layout_height="1dp"
    android:layout_marginBottom="50dp"
    android:background="@color/primaryColor"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent" />
```


# Custom view
1. Create a custom view class - `DirectionView`.
2. Add view to layout:
```xml
<PACKAGE.NAME.DirectionView
    android:id="@+id/directionView"
    android:layout_width="255dp"
    android:layout_height="259dp"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintLeft_toLeftOf="parent"
    app:layout_constraintRight_toRightOf="parent"
    app:layout_constraintTop_toTopOf="parent" />
```
or programmatically:
```java
// this - context
DirectionView view = new DirectionView(this);
container.addView(view);
```
3. The test view - `DirectionView` is only an arrow object, pointing to a location. You can
update it by calling:
```java
directionView = (TargetView) findViewById(R.id.directionView);
// current direction - can be set to phone view direction in degrees
// targetDirection - is the bearuing to the target - the desired rotation of DirectionView
// tilt - phone pitch angle * -1
directionView.updateRotation(currentDirection, targetDirection, tilt);
```

# Vector3D
- http://www.euclideanspace.com/maths/algebra/vectors/applications/normals/index.htm
- https://introcs.cs.princeton.edu/java/33design/Vector.java.html

# Perspective???
https://stackoverflow.com/questions/701504/perspective-projection-help-a-noob/701978#701978
Here's a very general answer. Say the camera's at (`Xc`, `Yc`, `Zc`) and the point you want to project is P = (`X`, `Y`, `Z`). The distance from the camera to the 2D plane onto which you are projecting is `F` (so the equation of the plane is `Z - Zc = F`). The 2D coordinates of P projected onto the plane are (`X'`, `Y'`).
Then, very simply:
```
X' = ((X - Xc) * (F/Z)) + Xc
Y' = ((Y - Yc) * (F/Z)) + Yc
```
If your camera is the origin, then this simplifies to:
```
X' = X * (F/Z)
Y' = Y * (F/Z)
```


When the phone tilt is changed then calculate the camera position:
```
camera.rotateX(tilt)        // or tilt * -1?
```