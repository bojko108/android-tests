package com.bojkosoft.bojko108.testgpscam.augmentedreality;

public class ProjectionMatrices {
    /**
     * Create a new matrix for projecting into perspective projection
     *
     * @param verticalFOV - camera's viewing angle - example: from 45 to 90 degrees
     * @param aspect      - aspect ration - the width / height of the screen represented by a single number
     * @param near        - distance from camera lens to near clipping plane
     * @param far         - distance from camera lens to far clipping plane
     * @return perspective projection matrix
     */
    public static float[][] Perspective(float verticalFOV, float aspect, float near, float far) {
        float f = 1.0f / ((float) Math.tan(Math.toRadians(verticalFOV) / 2));
        float zp = far + near;
        float zm = far - near;

        return new float[][]{
                {f / aspect, 0f, 0f, 0f},
                {0f, f, 0f, 0f},
                {0f, 0f, ((-1 * zp) / zm), (-1 * ((2 * far * near) / zm))},
                {0f, 0f, -1f, 0f}
        };
    }
}
