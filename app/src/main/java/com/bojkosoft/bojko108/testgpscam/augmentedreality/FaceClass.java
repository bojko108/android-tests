package com.bojkosoft.bojko108.testgpscam.augmentedreality;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FaceClass {
    private String mName;
    private Paint mPaint;
    private Path mPath;
    private List<Vector3D> mVertices;
    private List<Vector3D> mProjectedVertices;

    private boolean mIsVisible;

    public FaceClass(String name, String color) {
        this(name, color, new Vector3D[1]);
    }

    public FaceClass(String name, String color, Vector3D[] vertices) {
        this.mName = name;

        this.mPaint = new Paint();
        this.mPaint.setColor(Color.parseColor(color));
        this.mPaint.setStyle(Paint.Style.FILL);

        this.mPath = new Path();

        this.mProjectedVertices = new ArrayList<Vector3D>();
        this.mVertices = this.parseVertices(vertices);
    }

    public List<Vector3D> getVertices() {
        return this.mVertices;
    }

    public void setVertices(Vector3D[] vertices) {
        this.mVertices = this.parseVertices(vertices);
    }

    public void project(int xOrigin, int yOrigin, double fov, double distance, float rX, float rY, float rZ) {
        this.mProjectedVertices.clear();
        for (int i = 0; i < this.mVertices.size(); i++) {
            Vector3D projectedVertex = this.mVertices.get(i).rotateZ(rZ).rotateX(rX).rotateY(rY);
            projectedVertex = projectedVertex.project(xOrigin, yOrigin, fov, distance);

            this.mProjectedVertices.add(projectedVertex);
        }
        this.mIsVisible = this.calculateFaceVisibility(this.mProjectedVertices);
    }

    public boolean isVisible() {
        return this.mIsVisible;
    }

    public Paint getPaint() {
        return this.mPaint;
    }

    public Path getFace() {
        if (this.mProjectedVertices.size() > 1) {
            this.mPath.reset();
            this.mPath.moveTo((float) this.mProjectedVertices.get(0).x, (float) this.mProjectedVertices.get(0).y);

            for (int i = 1; i < this.mProjectedVertices.size(); i++) {
                this.mPath.lineTo((float) this.mProjectedVertices.get(i).x, (float) this.mProjectedVertices.get(i).y);
            }

            this.mPath.close();
        }

        return this.mPath;
    }

    /**
     * Take the normal vector of that face, and check if it faces towards or away from the camera.
     * Calculate the dot product of the normal and the view vector of your camera. Depending on
     * the sign of the dot product, the polygon is facing towards or away from the camera.
     * If you don't actually have a camera at this point, substitute a vector into the screen
     * (i.e. [0, 0, 1] or [0, 0, -1] depending on your axis system).
     *
     * @param face - face to check
     * @return face visibility
     */
    private boolean calculateFaceVisibility(List<Vector3D> face) {
        boolean visible = true;

        if (face.size() > 2) {
            // U = v1 - v0
            Vector3D U = face.get(0).subtractFrom(face.get(1));
            // V = v2 - v0
            Vector3D V = face.get(0).subtractFrom(face.get(2));
            Vector3D normal = U.cross(V).normalize();
            visible = normal.dot(Vector3D.LEFT_HANDED) >= 0f;
        }

        return visible;
    }

    private List<Vector3D> parseVertices(Vector3D[] vertices) {
        List<Vector3D> result = new ArrayList<Vector3D>();

        result.addAll(Arrays.asList(vertices));

        return result;
    }
}
