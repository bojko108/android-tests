package com.bojkosoft.bojko108.testgpscam;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Surface;
import android.view.WindowManager;

public class DeviceCompass implements SensorEventListener {

    public interface OnOrientationChangedEventListener {
        void onOrientationChanged(float azimuth, float pitch, float roll);
    }

    private Context mContext;

    // magnetic declination for BG can be set to +5 degrees
    private double mDeclination = 5.0;
    private double mAzimuthStep;
    private double mOldAzimuth;

    private SensorManager mSensorManager;
    private Sensor mMagnetometer;

    private OnOrientationChangedEventListener mOnOrientationChangedEventListener;

    public DeviceCompass(Context context) {
        this.mContext = context;
        this.mSensorManager = (SensorManager) this.mContext.getSystemService(Context.SENSOR_SERVICE);
        this.mMagnetometer = this.mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    public void setAzimuthStep(double azimuthStep) {
        if (azimuthStep < 0) azimuthStep = 0;
        this.mAzimuthStep = azimuthStep;
    }

    public void setDeclination(double declination) {
        this.mDeclination = declination;
    }

    public void startListening() {
        this.mSensorManager.registerListener(this, this.mMagnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void stopListening() {
        this.mSensorManager.unregisterListener(this);
    }

    public void setOnOrientationChangedEventListener(OnOrientationChangedEventListener eventListener) {
        this.mOnOrientationChangedEventListener = eventListener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] orientation = new float[3];
        float[] rotationMatrix = new float[16];
        float[] remappedMatrix = new float[16];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values.clone());

        WindowManager windowManager = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
        int rot = windowManager.getDefaultDisplay().getRotation();

        boolean isFlat = this.isDeviceFlat(event.values.clone());

        double azimuth = Double.NaN;

        if (!isFlat) {
            SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, remappedMatrix);
            SensorManager.getOrientation(remappedMatrix, orientation);

            azimuth = (Math.toDegrees(orientation[0]) + 360) % 360;
        }

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

        // orientation[0]: Azimuth
        //
        // Angle of rotation about the z axis.
        // This value represents the angle between the device's y axis
        // and the magnetic north pole. When facing north, this angle is 0,
        // when facing south, this angle is π. Likewise, when facing east,
        // this angle is π/2, and when facing west, this angle is -π/2.
        //
        // The range of values is -π to π.

        if (Double.isNaN(azimuth)) {
            azimuth = (Math.toDegrees(orientation[0]) + 360) % 360;
        }

        azimuth += this.mDeclination;

        if (Math.abs(this.mOldAzimuth - azimuth) > this.mAzimuthStep) {
            this.mOldAzimuth = azimuth;
            // broadcast orientation values
            if (this.mOnOrientationChangedEventListener != null) {
                this.mOnOrientationChangedEventListener.onOrientationChanged((float) azimuth, (float) Math.toDegrees(orientation[1]), (float) Math.toDegrees(orientation[2]));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private double mod(double a, double b) {
        return a % b;
    }

    private boolean isDeviceFlat(float[] values) {
        float z = values[2];
        float norm_of_g = (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + z * z);
        z = z / norm_of_g;
        int inclination = (int) Math.round(Math.toDegrees(Math.acos(z)));
        return (inclination < 30 || inclination > 150);
    }
}
