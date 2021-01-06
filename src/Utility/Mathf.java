package Utility;

public class Mathf {

    public static final float Infinity = Float.POSITIVE_INFINITY;

    public static final float NegativeInfinity = Float.NEGATIVE_INFINITY;

    public static final float PI = 3.141593f;

    public static final float Deg2Rad = 0.01745329f;

    public static final float Rad2Deg = 57.29578f;

    public static int RoundToInt(double v) {
        if (v == (int)v) return (int)v;
        return (int)v+1;
    }

    public static float Sin(float angleInRad) {
        return (float) Math.sin(angleInRad);
    }

    public static float Cos(float angleInRad) {
        return (float) Math.cos(angleInRad);
    }

    public static float Tan(float angleInRad) {
        return (float) Math.tan(angleInRad);
    }

    public static float Asin(float angleInRad)
    {
        return (float) Math.asin(angleInRad);
    }

    public static float Acos(float angleInRad)
    {
        return (float) Math.acos(angleInRad);
    }

    public static float Atan(float angleInRad)
    {
        return (float) Math.atan(angleInRad);
    }

    public static float Atan2(float y, float x)
    {
        return (float) Math.atan2(y, x);
    }

    public static float Sqrt(float val)
    {
        return (float) Math.sqrt(val);
    }

    public static float Pow(float val, float power)
    {
        return (float) Math.pow(val, power);
    }
}
