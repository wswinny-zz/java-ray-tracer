package utils;

import glm.vec._4.Vec4;

public class Constants
{
    public static final int POINT = 1;
    public static final int VEC   = 0;

    public static final int MAX_RECURSIVE_DEPTH = 5;
    public static final double INFINITY = 1e9;

    public static final double CANCER_DELTA = 0.0001;
    public static final double BLINN_EXPONENT_MULTIPLIER = 4.0;
    public static final double AIR_REFRACTIVE_INDEX = 1.0002926;

    public static final double SS_WEIGHT_CENTER = 0.6;
    public static final double SS_WEIGHT_CORNER = 0.1;

    public static final int NUM_SAMPLES = 5;
    public static final int FOCAL_DISTANCE = 1;

    public static double aperatureRadius = FOCAL_DISTANCE / 256.0;

    public static final Vec4 CAMERA = new Vec4(0.0, 0.0, Constants.FOCAL_DISTANCE, Constants.POINT);
}
