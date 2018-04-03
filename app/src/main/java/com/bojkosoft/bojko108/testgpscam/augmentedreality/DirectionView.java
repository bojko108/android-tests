package com.bojkosoft.bojko108.testgpscam.augmentedreality;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class DirectionView extends RelativeLayout {

    private static final double CAMERA_FOV = Math.toRadians(90);
    private static final double CAMERA_DISTANCE = 0.01;

    private List<FaceClass> mFaces;

    public DirectionView(Context context) {
        super(context);
        this.initView();
    }

    public DirectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView();

        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Get the width measurement
        int widthSize = View.resolveSize(400, widthMeasureSpec);

        //Get the height measurement
        int heightSize = View.resolveSize(400, heightMeasureSpec);

        //MUST call this to store the measurements
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (FaceClass face : this.mFaces) {
            if (face.isVisible()) {
                canvas.drawPath(face.getFace(), face.getPaint());
            }
        }
    }

    private void initView() {
        this.mFaces = new ArrayList<FaceClass>();

        double[][] points = new double[][]{
                {-1, 1, 0},     // 0
                {1, 1, 0},    // 1
                {1, -1, 0},      // 2
                {-1, -1, 0},     // 3
                {-1, 1, 1},     // 4
                {1, 1, 1},    // 5
                {1, -1, 1},      // 6
                {-1, -1, 1}      // 7
        };

        Vector3D[] vertices;

        vertices = new Vector3D[]{
                new Vector3D(points[3]),
                new Vector3D(points[2]),
                new Vector3D(points[1]),
                new Vector3D(points[0])
        };
        this.mFaces.add(new FaceClass("BOTTOM", "#000000", vertices));

        vertices = new Vector3D[]{
                new Vector3D(points[1]),
                new Vector3D(points[2]),
                new Vector3D(points[6]),
                new Vector3D(points[5])
        };
        this.mFaces.add(new FaceClass("RIGHT", "#00ff00", vertices));

        vertices = new Vector3D[]{
                new Vector3D(points[0]),
                new Vector3D(points[4]),
                new Vector3D(points[7]),
                new Vector3D(points[3])
        };
        this.mFaces.add(new FaceClass("LEFT", "#ffff00", vertices));

        vertices = new Vector3D[]{
                new Vector3D(points[2]),
                new Vector3D(points[3]),
                new Vector3D(points[7]),
                new Vector3D(points[6])
        };
        this.mFaces.add(new FaceClass("BACK", "#00ffff", vertices));

        vertices = new Vector3D[]{
                new Vector3D(points[0]),
                new Vector3D(points[1]),
                new Vector3D(points[5]),
                new Vector3D(points[4])
        };
        this.mFaces.add(new FaceClass("FRONT", "#ff0000", vertices));

        vertices = new Vector3D[]{
                new Vector3D(points[4]),
                new Vector3D(points[5]),
                new Vector3D(points[6]),
                new Vector3D(points[7])
        };
        this.mFaces.add(new FaceClass("TOP", "#c0c0c0", vertices));
    }

    public void setNewRotation(float rX, float rY, float rZ) {
        for (FaceClass face : this.mFaces) {
            face.project(getWidth() / 2, getHeight() / 2, CAMERA_FOV, CAMERA_DISTANCE, rX, rY, rZ);
        }
        this.invalidate();
    }





    public void updateRotation(float currentDirection, float targetDirection, float tilt) {
        float rX = (tilt > 40 ? 40 : tilt) * -1;
        float rZ = this.calculateHorizontalRotation(currentDirection, targetDirection);

        for (FaceClass face : this.mFaces) {
            face.project(getWidth() / 2, getHeight() / 2, CAMERA_FOV, CAMERA_DISTANCE, rX, 0, rZ);
        }

        this.invalidate();
    }

    private float calculateHorizontalRotation(float currentDirection, float targetDirection) {
        float rotation;

        if (currentDirection >= targetDirection) {
            rotation = (360 + targetDirection) - currentDirection;
        } else {
            rotation = targetDirection - currentDirection;
        }

        if (rotation > 90 && rotation < 270) {
            rotation = (rotation > 180 ? 270 : 90);
        }

        return rotation;
    }
}