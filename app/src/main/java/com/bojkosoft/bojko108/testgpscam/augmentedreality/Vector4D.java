package com.bojkosoft.bojko108.testgpscam.augmentedreality;

/**
 *
 */
public class Vector4D {
    /**
     * A test vector in Right-handed Cartesian coordinate system.
     * Can be used for example if we need to calculate the dot product
     * with camera vector, but we don't actually have a camera.
     */
    public static final Vector4D RIGHT_HANDED = new Vector4D(0, 0, 1, 0);
    /**
     * A test vector in Left-handed Cartesian coordinate system.
     * Can be used for example if we need to calculate the dot product
     * with camera vector, but we don't actually have a camera.
     */
    public static final Vector4D LEFT_HANDED = new Vector4D(0, 0, -1, 0);

    public static final Vector4D PLUS_X = new Vector4D(1, 0, 0, 0);
    public static final Vector4D PLUS_Y = new Vector4D(0, 1, 0, 0);
    public static final Vector4D PLUS_Z = new Vector4D(0, 0, 1, 0);
    public static final Vector4D MINUS_X = new Vector4D(-1, 0, 0, 0);
    public static final Vector4D MINUS_Y = new Vector4D(0, -1, 0, 0);
    public static final Vector4D MINUS_Z = new Vector4D(0, 0, -1, 0);

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
     * W component
     */
    public double w;

    /**
     * Construct a new 3D Vector with X = Y = Z = W = 0
     */
    public Vector4D() {
        x = y = z = w = 0;
    }

    /**
     * Construct a new 4D Vector
     *
     * @param x - X component of the vector
     * @param y - Y component of the vector
     * @param z - Z component of the vector
     * @param w - W component of the vector
     */
    public Vector4D(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Construct a new 4D Vector
     *
     * @param coords - coordinates array
     */
    public Vector4D(double[] coords) {
        this(coords[0], coords[1], coords[2], coords[3]);
    }

    /**
     * sum of two vectors
     *
     * @param toVector - vector to add to
     * @return assembled vector
     */
    public Vector4D addTo(Vector4D toVector) {
        return new Vector4D(toVector.x + this.x, toVector.y + this.y, toVector.z + this.z, toVector.w + this.w);
    }

    /**
     * multiplies two vectors
     *
     * @param second vector
     * @return resulting vector
     */
    public Vector4D multiply(Vector4D second) {
        return new Vector4D(second.x * this.x, second.y * this.y, second.z * this.z, second.w * this.w);
    }

    /**
     * get the angle between two vectors
     *
     * @param toVector - second vector
     * @return angle in radians
     */
    public float angle(Vector4D toVector) {
        Vector4D a = this.normalize();
        Vector4D b = toVector.normalize();

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
    public Vector4D cross(Vector4D rightVector) {
        double x = this.y * rightVector.z - this.z * rightVector.y;
        double y = this.z * rightVector.x - this.x * rightVector.z;
        double z = this.x * rightVector.y - this.y * rightVector.x;
        double w = this.x * rightVector.y - this.y * rightVector.x;

        return new Vector4D(x, y, z, w);
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
    public Vector4D subtractFrom(Vector4D fromVector) {
        return new Vector4D(fromVector.x - this.x, fromVector.y - this.y, fromVector.z - this.z, fromVector.w - this.w);
    }

    /**
     * scale the vector
     *
     * @param factor - scale factor
     * @return scaled vector
     */
    public Vector4D scale(float factor) {
        return new Vector4D(this.x * factor, this.y * factor, this.z * factor, this.w * factor);
    }

    /**
     * inverse vector components
     *
     * @return inverted vector
     */
    public Vector4D inverse() {
        return new Vector4D(1.0f / this.x, 1.0f / this.y, 1.0f / this.z, 1.0f / this.w);
    }

    /**
     * divide the vector
     *
     * @param factor - division factor
     * @return divided vector
     */
    public Vector4D divideBy(float factor) {
        return new Vector4D(this.x / factor, this.y / factor, this.z / factor, this.w / factor);
    }

    /**
     * calculate vector direction
     *
     * @return vector direction
     */
    public Vector4D direction() {
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
    public float dot(Vector4D that) {
        float sum = 0.0f;

        sum += (this.x * that.x);
        sum += (this.y * that.y);
        sum += (this.z * that.z);
        sum += (this.w * that.w);

        return sum;
    }

    /**
     * Calculate normalized vector. Vector magnitude
     * must be greater than 0.
     *
     * @return normalized vector
     */
    public Vector4D normalize() {
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
    public float distanceTo(Vector4D toVector) {
        return this.subtractFrom(toVector).magnitude();
    }
}