package Utility;

import Primitives.Triangle;

public class Vector3 {
    public float x, y, z;

    public static Vector3 zero = new Vector3(0,0,0);

    public Vector3() { this.x = 0; this.y = 0; this.z = 0; }
    public Vector3(float v) { this.x = v; this.y = v; this.z = v; }
    public Vector3(float x, float y) { this.x = x; this.y = y; this.z = 0; }
    public Vector3 (float x, float y, float z) { this.x = x; this.y = y; this.z = z; }
    public Vector3(Vector3 vec) { this.x = vec.x; this.y = vec.y; this.z = vec.z; }

    public static Vector3 Add(Vector3 first, Vector3 second) {
        return new Vector3(first.x + second.x, first.y + second.y, first.z + second.z);
    }

    public static Vector3 Sub(Vector3 first, Vector3 second) {
        return new Vector3(first.x - second.x, first.y - second.y, first.z - second.z);
    }

    public static Vector3 Mul(Vector3 first, Vector3 second) {
        return new Vector3(first.x * second.x, first.y * second.y, first.z * second.z);
    }

    public static Vector3 Div(Vector3 first, Vector3 second) {
        return new Vector3(
                (second.x == 0 || first.x == 0) ? 0 : first.x/second.x,
                (second.y == 0 || first.y == 0) ? 0 : first.y/second.y,
                (second.z == 0 || first.z == 0) ? 0 : first.z/second.z
        );
    }

    public Vector3 Set(float x, float y) { this.x = x; this.y = y; return this; }

    public Vector3 Set(float x, float y, float z) { this.x = x; this.y = y; this.z = z; return this; }

    public Vector3 Set(Vector3 vec) { this.x = vec.x; this.y = vec.y; this.z = vec.z; return this; }

    public Vector3 Add(Vector3 vec) { this.x += vec.x; this.y += vec.y; this.z += vec.z; return this; }

    public Vector3 Add(float x, float y, float z) { this.x += x; this.y += y; this.z += z; return this; }

    public Vector3 Sub(Vector3 vec) { this.x -= vec.x; this.y -= vec.y; this.z -= vec.z; return this; }

    public Vector3 Mul(Vector3 vec) { this.x *= vec.x; this.y *= vec.y; this.z *= vec.z; return this; }

    public Vector3 Mul(float val) { this.x *= val; this.y *= val; this.z *= val; return this; }

    public Vector3 Div(Vector3 vec) {
        this.x /= vec.x == 0 ? 1 : vec.x;
        this.y /= vec.y == 0 ? 1 : vec.y;
        this.z /= vec.z == 0 ? 1 : vec.z;
        return this;
    }

    public Vector3 Div(float val) { if (val == 0) return this; this.x /= val; this.y /= val; this.z /= val; return this; }

    public Vector3 Normalise() {
        float divider = Mathf.Sqrt(this.x*this.x + this.y*this.y + this.z*this.z);
        if (divider == 0) divider = 1;
        this.x /= divider; this.y /= divider; this.z /= divider;
        return this;
    }

    public Vector3 Absolute() {
        this.x = this.x < 0 ? -this.x : this.x;
        this.y = this.y < 0 ? -this.y : this.y;
        this.z = this.z < 0 ? -this.z : this.z;
        return this;
    }

    public Vector2 ToVector2() {
        return new Vector2(this.x, this.y);
    }

    public static float SqrDistance(Vector3 first, Vector3 second) {
        return (Math.abs(second.x - first.x) * Math.abs(second.x - first.x) + Math.abs(second.y - first.y) * Math.abs(second.y - first.y) + Math.abs(second.z - first.z) * Math.abs(second.z - first.z));
    }

    public static float Distance(Vector3 first, Vector3 second) {
        return Mathf.Sqrt(SqrDistance(first, second));
    }

    public static Vector3 CrossProd(Vector3 first, Vector3 second) {
        return new Vector3(
                first.y * second.z - first.z * second.y,
                first.z * second.x - first.x * second.z,
                first.x * second.y - first.y * second.x
        );
    }

    public static float DotProd(Vector3 first, Vector3 second) {
        return first.x * second.x + first.y * second.y + first.z * second.z;
    }

    @Override
    public String toString() {
        return "{ " + x +" : " + y + " : " + z + " }";
    }

    public String toTable() {
        return "\n  X: " + x + "\n  Y: " + y + "\n  Z: " + z;
    }
}
