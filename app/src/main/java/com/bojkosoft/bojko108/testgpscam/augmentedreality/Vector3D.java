package com.bojkosoft.bojko108.testgpscam.augmentedreality;

/**
 *
 */
public class Vector3D {
    /**
     * A test vector in Right-handed Cartesian coordinate system.
     * Can be used for example if we need to calculate the dot product
     * with camera vector, but we don't actually have a camera.
     */
    public static final Vector3D RIGHT_HANDED = new Vector3D(0, 0, 1);
    /**
     * A test vector in Left-handed Cartesian coordinate system.
     * Can be used for example if we need to calculate the dot product
     * with camera vector, but we don't actually have a camera.
     */
    public static final Vector3D LEFT_HANDED = new Vector3D(0, 0, -1);

    public static final Vector3D PLUS_X = new Vector3D(1, 0, 0);
    public static final Vector3D PLUS_Y = new Vector3D(0, 1, 0);
    public static final Vector3D PLUS_Z = new Vector3D(0, 0, 1);
    public static final Vector3D MINUS_X = new Vector3D(-1, 0, 0);
    public static final Vector3D MINUS_Y = new Vector3D(0, -1, 0);
    public static final Vector3D MINUS_Z = new Vector3D(0, 0, -1);

    /**
     * X component
     */
    public double x;
    /**
     * Y component
     */
    public double y;
    /**
     * Z component
     */
    public double z;

    /**
     * Construct a new 3D Vector with X = Y = Z = 0
     */
    public Vector3D() {
        x = y = z = 0;
    }

    /**
     * Construct a new 3D Vector
     *
     * @param x - X component of the vector
     * @param y - Y component of the vector
     * @param z - Z component of the vector
     */
    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Construct a new 3D Vector
     *
     * @param coords - coordinates array
     */
    public Vector3D(double[] coords) {
        this(coords[0], coords[1], coords[2]);
    }

    /**
     * rotate the vector around X component (axis)
     *
     * @param angle in degrees
     * @return rotated vector
     */
    public Vector3D rotateX(double angle) {
        double rad, cosa, sina, yn, zn;

        rad = angle * Math.PI / 180;
        cosa = Math.cos(rad);
        sina = Math.sin(rad);
        yn = this.y * cosa - this.z * sina;
        zn = this.y * sina + this.z * cosa;

        return new Vector3D(this.x, yn, zn);
    }

    /**
     * rotate the vector around Y component (axis)
     *
     * @param angle in degrees
     * @return rotated vector
     */
    public Vector3D rotateY(double angle) {
        double rad, cosa, sina, xn, zn;

        rad = angle * Math.PI / 180;
        cosa = Math.cos(rad);
        sina = Math.sin(rad);
        zn = this.z * cosa - this.x * sina;
        xn = this.z * sina + this.x * cosa;

        return new Vector3D(xn, this.y, zn);
    }

    /**
     * rotate the vector around Z component (axis)
     *
     * @param angle in degrees
     * @return rotated vector
     */
    public Vector3D rotateZ(double angle) {
        double rad, cosa, sina, xn, yn;

        rad = angle * Math.PI / 180;
        cosa = Math.cos(rad);
        sina = Math.sin(rad);
        xn = this.x * cosa - this.y * sina;
        yn = this.x * sina + this.y * cosa;

        return new Vector3D(xn, yn, this.z);
    }

    /**
     * TODO: need to check what projection is used... and change it to perspective
     * project this vector
     *
     * @param xOrigin
     * @param yOrigin
     * @param fov          - camera field of view
     * @param viewDistance - view distance
     * @return projected vector
     */
    public Vector3D project(int xOrigin, int yOrigin, double fov, double viewDistance) {
        double factor, xn, yn, ow;

        factor = fov / (viewDistance + this.z);
        xn = this.x * factor + xOrigin;
        yn = this.y * factor + yOrigin;

        //factor = fov / viewDistance;
        //xn = this.x * factor + xOrigin;
        //yn = this.y * factor + yOrigin;

        return new Vector3D(xn, yn, this.z);
    }

    /**
     * project this vector
     *
     * @param matrix - perspective projection matrix
     * @return projected vector
     */
    public Vector3D project(float[] matrix) {
        return this;
    }


    /**
     * sum of two vectors
     *
     * @param toVector - vector to add to
     * @return assembled vector
     */
    public Vector3D addTo(Vector3D toVector) {
        return new Vector3D(toVector.x + this.x, toVector.y + this.y, toVector.z + this.z);
    }

    /**
     * multiplies two vectors
     *
     * @param second vector
     * @return resulting vector
     */
    public Vector3D multiply(Vector3D second) {
        return new Vector3D(second.x * this.x, second.y * this.y, second.z * this.z);
    }

    /**
     * get the angle between two vectors
     *
     * @param toVector - second vector
     * @return angle in radians
     */
    public float angle(Vector3D toVector) {
        Vector3D a = this.normalize();
        Vector3D b = toVector.normalize();

        float cosine = a.dot(b);
        if (cosine > 1.0f) {
            return 0f;
        } else {
            return (float) Math.acos(cosine);
        }
    }

    /**
     * Calculate the cross-product. THIS is the left vector.
     * returnValue.x = left.y * right.z - left.z * right.y;
     * returnValue.y = left.z * right.x - left.x * right.z;
     * returnValue.z = left.x * right.y - left.y * right.x;
     *
     * @param rightVector - right vector
     * @return cross-product
     */
    public Vector3D cross(Vector3D rightVector) {
        double x = this.y * rightVector.z - this.z * rightVector.y;
        double y = this.z * rightVector.x - this.x * rightVector.z;
        double z = this.x * rightVector.y - this.y * rightVector.x;

        return new Vector3D(x, y, z);
    }

    /**
     * calculate magnitude (length)
     * magnitude = Math.sqrt(x*x + y*y + z*z)
     *
     * @return magnitude (length)
     */
    public float magnitude() {
        return (float) Math.sqrt(this.dot(this));
    }

    /**
     * subtract this vector from fromVector
     *
     * @param fromVector - vector to subtract from
     * @return subtracted vector
     */
    public Vector3D subtractFrom(Vector3D fromVector) {
        return new Vector3D(fromVector.x - this.x, fromVector.y - this.y, fromVector.z - this.z);
    }

    /**
     * scale the vector
     *
     * @param factor - scale factor
     * @return scaled vector
     */
    public Vector3D scale(float factor) {
        return new Vector3D(this.x * factor, this.y * factor, this.z * factor);
    }

    /**
     * inverse vector components
     *
     * @return inverted vector
     */
    public Vector3D inverse() {
        return new Vector3D(1.0f / this.x, 1.0f / this.y, 1.0f / this.z);
    }

    /**
     * divide the vector
     *
     * @param factor - division factor
     * @return divided vector
     */
    public Vector3D divideBy(float factor) {
        return new Vector3D(this.x / factor, this.y / factor, this.z / factor);
    }

    /**
     * calculate vector direction
     *
     * @return vector direction
     */
    public Vector3D direction() {
        if (this.magnitude() == 0.0f)
            throw new ArithmeticException("zero-vector has no direction");
        return this.scale(1.0f / this.magnitude());
    }

    /**
     * return inner product with that Vector
     * dot = x*x + y*y + z*z
     *
     * @param that - Vector3D
     * @return inner product
     */
    public float dot(Vector3D that) {
        float sum = 0.0f;

        sum += (this.x * that.x);
        sum += (this.y * that.y);
        sum += (this.z * that.z);

        return sum;
    }

    /**
     * Calculate normalized vector. Vector magnitude
     * must be greater than 0.
     *
     * @return normalized vector
     */
    public Vector3D normalize() {
        float magnitude = this.magnitude();
        if (this.magnitude() == 0.0f)
            throw new ArithmeticException("zero-vector has no direction");
        return this.divideBy(magnitude);
    }

    /**
     * Calculate the Euclidean distance between this and toVector
     *
     * @param toVector - vector to calculate distance to
     * @return distance
     */
    public float distanceTo(Vector3D toVector) {
        return this.subtractFrom(toVector).magnitude();
    }
}


/*
   http://www.homer.com.au/webdoc/geometry/polygonorder.htm

   Example and test program for testing whether a polygon is convex or concave. For MICROSOFT WINDOWS, contributed by G. Adam Stanislav.

   Return whether a polygon in 2D is concave or convex
   return 0 for incomputables eg: colinear points
          CONVEX == 1
          CONCAVE == -1
   It is assumed that the polygon is simple
   (does not intersect itself or have holes)

int Convex(XY *p,int n)
{
int i,j,k;
int flag = 0;
double z;

if (n < 3)
return(0);

for (i=0;i<n;i++) {
j = (i + 1) % n;
k = (i + 2) % n;
z  = (p[j].x - p[i].x) * (p[k].y - p[j].y);
z -= (p[j].y - p[i].y) * (p[k].x - p[j].x);
if (z < 0)
flag |= 1;
else if (z > 0)
flag |= 2;
if (flag == 3)
return(CONCAVE);
}
if (flag != 0)
return(CONVEX);
else
return(0);
}
*/