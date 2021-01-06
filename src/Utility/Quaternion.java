package Utility;

import static Utility.Mathf.*;
import static java.lang.Math.*;

public class Quaternion {
    private float x;
    private float y;
    private float z;
    private float w;

    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Quaternion(float rotationX, float rotationY, float rotationZ) {
        /*rotationX *= Deg2Rad;
        rotationY *= Deg2Rad;
        rotationZ *= Deg2Rad;
        double cy = cos(rotationX * 0.5);
        double sy = sin(rotationX * 0.5);
        double cr = cos(rotationZ * 0.5);
        double sr = sin(rotationZ * 0.5);
        double cp = cos(rotationY * 0.5);
        double sp = sin(rotationY * 0.5);

        this.w = (float)(cy * cr * cp + sy * sr * sp);
        this.x = (float)(cy * sr * cp - sy * cr * sp);
        this.y = (float)(cy * cr * sp + sy * sr * cp);
        this.z = (float)(sy * cr * cp - cy * sr * sp);
        */
        rotationX *= Mathf.Deg2Rad * 0.5f;
        rotationY *= Mathf.Deg2Rad * 0.5f;
        rotationZ *= Mathf.Deg2Rad * 0.5f;

        double c1 = Math.cos(rotationX);
        double c2 = Math.cos(rotationY);
        double c3 = Math.cos(rotationZ);
        double s1 = Math.sin(rotationX);
        double s2 = Math.sin(rotationY);
        double s3 = Math.sin(rotationZ);

        this.x = (float) ((s1 * c2 * c3) + (c1 * s2 * s3));
        this.y = (float) ((c1 * s2 * c3) - (s1 * c2 * s3));
        this.z = (float) ((c1 * c2 * s3) + (s1 * s2 * c3));
        this.w = (float) ((c1 * c2 * c3) - (s1 * s2 * s3));
    }

    public float get(int id) {
        switch (id) {
            case 0: {return x;}
            case 1: {return y;}
            case 2: {return z;}
            case 3: {return w;}
            default: return -1;
        }
    }

    public void set(int id, float val) {
        switch (id) {
            case 0: {this.x = val; break;}
            case 1: {this.y = val; break;}
            case 2: {this.z = val; break;}
            case 3: {this.w = val; break;}
        }
    }

    public Quaternion Add(float v) {
        this.x += v;
        this.y += v;
        this.z += v;
        this.w += v;
        return this;
    }

    public Quaternion Sub(float v) {
        this.x -= v;
        this.y -= v;
        this.z -= v;
        this.w -= v;
        return this;
    }

    public Quaternion Mul(float v) {
        this.x *= v;
        this.y *= v;
        this.z *= v;
        this.w *= v;
        return this;
    }

    public Quaternion Div(float v) {
        this.x /= v;
        this.y /= v;
        this.z /= v;
        this.w /= v;
        return this;
    }

    // Special quaternions.

    // z = 0*i + 0*j + 0*k + 0
    static Quaternion Zero()
    {
        return new Quaternion(0, 0, 0, 0);
    }

    // i = 1*i + 0*j + 0*k + 0
    static Quaternion I()
    {
        return new Quaternion(1, 0, 0, 0);
    }

    // j = 0*i + 1*j + 0*k + 0
    static Quaternion J()
    {
        return new Quaternion(0, 1, 0, 0);
    }

    // k = 0*i + 0*j + 1*k + 0
    static Quaternion K()
    {
        return new Quaternion(0, 0, 1, 0);
    }

    // 1 = 0*i + 0*j + 0*k + 1
    static Quaternion Identity()
    {
        return new Quaternion(0, 0, 0, 1);
    }

    public static Quaternion Mul(Quaternion lhs, Quaternion rhs) {
        return new Quaternion(
                (float) ((double) lhs.w * (double) rhs.x + (double) lhs.x * (double) rhs.w + (double) lhs.y * (double) rhs.z - (double) lhs.z * (double) rhs.y),
                (float) ((double) lhs.w * (double) rhs.y + (double) lhs.y * (double) rhs.w + (double) lhs.z * (double) rhs.x - (double) lhs.x * (double) rhs.z),
                (float) ((double) lhs.w * (double) rhs.z + (double) lhs.z * (double) rhs.w + (double) lhs.x * (double) rhs.y - (double) lhs.y * (double) rhs.x),
                (float) ((double) lhs.w * (double) rhs.w - (double) lhs.x * (double) rhs.x - (double) lhs.y * (double) rhs.y - (double) lhs.z * (double) rhs.z)
        );
    }

    public Quaternion Mul(Quaternion b) {
        Quaternion a = this;
        /*float x = this.x, y = this.y, z = this.z, w = this.w;
        this.x = w * b.w - x * b.x - y * b.y - z * b.z;
        this.y = w * b.x + x * b.w + y * b.z - z * b.y;
        this.z = w * b.y + y * b.w + z * b.x - x * b.z;
        this.w = w * b.z + z * b.w + x * b.y - y * b.x;*/

        this.x = (float)((double) a.w * (double) b.x + (double) a.x * (double) b.w + (double) a.y * (double) b.z - (double) a.z * (double) b.y);
        this.y = (float) ((double) a.w * (double) b.y + (double) a.y * (double) b.w + (double) a.z * (double) b.x - (double) a.x * (double) b.z);
        this.z = (float) ((double) a.w * (double) b.z + (double) a.z * (double) b.w + (double) a.x * (double) b.y - (double) a.y * (double) b.x);
        this.w = (float) ((double) a.w * (double) b.w - (double) a.x * (double) b.x - (double) a.y * (double) b.y - (double) a.z * (double) b.z);

        return this;
    }

    /*public static Quaternion Euler(float x, float y, float z)
    {
        return null;
        //return Quaternion.Internal_FromEulerRad(new Vector3(x, y, z) * ((float) Math.PI / 180f));
    }*/

    /*public Vector3 ToEulerAngles(Vector3 vec)
    {
        float norm = x*x+y*y+z*z+w*w;
        if (norm > 0)
        {
            float invNorm = 1 / norm;
            float qX = -x * invNorm, qY = -y * invNorm, qZ = -z * invNorm, qW = w * invNorm;
            float twoX = 2 * qX; float twoY = 2 * qY; float twoZ = 2 * qZ;
            float twoWX = twoX * qW; float twoWY = twoY * qW; float twoWZ = twoZ * qW;
            float twoXX = twoX * qX; float twoXY = twoY * qX; float twoXZ = twoZ * qX;
            float twoYY = twoY * qY; float twoYZ = twoZ * qY; float twoZZ = twoZ * qZ;
            return new Vector3(
                    vec.x * (1 - (twoYY + twoZZ)) + vec.y * (twoXY - twoWZ) + vec.z * (twoXZ + twoWY),
                    vec.x * (twoXY + twoWZ) + vec.y * (1 - (twoXX + twoZZ)) + vec.z * (twoYZ - twoWX),
                    vec.x * (twoXZ - twoWY) + vec.y * (twoYZ + twoWX) + vec.z * (1 - (twoXX + twoYY)));
        }
        return new Vector3(0);
    }*/

    public Vector3 ToEulerAngles() {
        /*Vector3 out = new Vector3();
        double sinr_cosp = +2.0 * (w * x + y * z);
        double cosr_cosp = +1.0 - 2.0 * (x * x + y * y);
        out.x = (float)atan2(sinr_cosp, cosr_cosp);

        // Pitch (y-axis rotation)
        double sinp = +2.0 * (w * y - z * x);
        if (abs(sinp) >= 1)
            out.y = (float)Math.copySign(Math.PI / 2, sinp); // use 90 degrees if out of range
        else
            out.y = (float)asin(sinp);

        // Yaw (z-axis rotation)
        double siny_cosp = +2.0 * (w * z + x * y);
        double cosy_cosp = +1.0 - 2.0 * (y * y + z * z);
        out.z = (float)atan2(siny_cosp, cosy_cosp);
        return out.Mul(Rad2Deg);*/
        Quaternion q = this;

        Vector3 eulerAngles = new Vector3();

        final float SINGULARITY_THRESHOLD = 0.4999995f;

        float sqw = q.w * q.w;
        float sqx = q.x * q.x;
        float sqy = q.y * q.y;
        float sqz = q.z * q.z;
        float unit = sqx + sqy + sqz + sqw;
        float singularityTest = (q.x * q.z) + (q.w * q.y);

        if (singularityTest > SINGULARITY_THRESHOLD * unit) {
            eulerAngles.z = (float) (2 * Math.atan2(q.x, q.w));
            eulerAngles.y = (float) (Math.PI / 2);
            eulerAngles.x = 0;
        } else if (singularityTest < -SINGULARITY_THRESHOLD * unit) {
            eulerAngles.z = (float) (-2 * Math.atan2(q.x, q.w));
            eulerAngles.y = -(float) (Math.PI / 2);
            eulerAngles.x = 0;
        } else {
            eulerAngles.z = (float) Math.atan2(2 * ((q.w * q.z) - (q.x * q.y)), sqw + sqx - sqy - sqz);
            eulerAngles.y = (float) Math.asin(2 * singularityTest / unit);
            eulerAngles.x = (float) Math.atan2(2 * ((q.w * q.x) - (q.y * q.z)), sqw - sqx - sqy + sqz);
        }

        return eulerAngles.Mul(Rad2Deg);
    }

    public Vector3 AxisX()
    {
        double twoY = 2 * y; double twoZ = 2 * z;
        double twoWY = twoY * w; double twoWZ = twoZ * w;
        double twoXY = twoY * x; double twoXZ = twoZ * x;
        double twoYY = twoY * y; double twoZZ = twoZ * z;
        return new Vector3(1f - (float)(twoYY + twoZZ), (float)(twoXY + twoWZ), (float)(twoXZ - twoWY));
    }

    public Vector3 AxisY()
    {
        double twoX = (double)2 * x; double twoY = (double)2 * y; double twoZ = (double)2 * z;
        double twoWX = twoX * w; double twoWZ = twoZ * w; double twoXX = twoX * x;
        double twoXY = twoY * x; double twoYZ = twoZ * y; double twoZZ = twoZ * z;
        return new Vector3((float)(twoXY - twoWZ), 1f - (float)(twoXX + twoZZ), (float)(twoYZ + twoWX));
    }

    public Vector3 AxisZ()
    {
        double twoX = (double)2 * x; double twoY = (double)2 * y; double twoZ = (double)2 * z;
        double twoWX = twoX * w; double twoWY = twoY * w; double twoXX = twoX * x;
        double twoXZ = twoZ * x; double twoYY = twoY * y; double twoYZ = twoZ * y;
        return new Vector3((float)(twoXZ + twoWY), (float)(twoYZ - twoWX), 1f - (float)(twoXX + twoYY));
    }

    public Vector3[] AllAxis() {
            double twoX = 2 * x; double twoY = 2 * y; double twoZ = 2 * z;
            double twoWX = twoX * w; double twoWY = twoY * w; double twoWZ = twoZ * w;
            double twoXX = twoX * x; double twoXY = twoY * x; double twoXZ = twoZ * x;
            double twoYY = twoY * y; double twoYZ = twoZ * y; double twoZZ = twoZ * z;
            return new Vector3[] {
                    new Vector3(1 - (float)(twoYY + twoZZ), (float)(twoXY + twoWZ), (float)(twoXZ - twoWY)),
                    new Vector3((float)(twoXY - twoWZ), 1 - (float)(twoXX + twoZZ), (float)(twoYZ + twoWX)),
                    new Vector3((float)(twoXZ + twoWY), (float)(twoYZ - twoWX), 1 - (float)(twoXX + twoYY))
            };
    }

    public static Quaternion FromEulerAngles(Vector3 eulerAngles)
    {
        return new Quaternion(eulerAngles.x, eulerAngles.y, eulerAngles.z);
    }

    public Vector3 Rotate(Vector3 v) {
        Vector3 output = new Vector3();

        double ww = w * w;
        double xx = x * x;
        double yy = y * y;
        double zz = z * z;
        double wx = w * x;
        double wy = w * y;
        double wz = w * z;
        double xy = x * y;
        double xz = x * z;
        double yz = y * z;

        // Formula from http://www.euclideanspace.com/maths/algebra/realNormedAlgebra/quaternions/transforms/index.htm
        // p2.x = w*w*p1.x + 2*y*w*p1.z - 2*z*w*p1.y + x*x*p1.x + 2*y*x*p1.y + 2*z*x*p1.z - z*z*p1.x - y*y*p1.x;
        // p2.y = 2*x*y*p1.x + y*y*p1.y + 2*z*y*p1.z + 2*w*z*p1.x - z*z*p1.y + w*w*p1.y - 2*x*w*p1.z - x*x*p1.y;
        // p2.z = 2*x*z*p1.x + 2*y*z*p1.y + z*z*p1.z - 2*w*y*p1.x - y*y*p1.z + 2*w*x*p1.y - x*x*p1.z + w*w*p1.z;

        output.x = (float)(ww*v.x + 2*wy*v.z - 2*wz*v.y +
                xx*v.x + 2*xy*v.y + 2*xz*v.z -
                zz*v.x - yy*v.x);
        output.y = (float)(2*xy*v.x + yy*v.y + 2*yz*v.z +
                2*wz*v.x - zz*v.y + ww*v.y -
                2*wx*v.z - xx*v.y);
        output.z = (float)(2*xz*v.x + 2*yz*v.y + zz*v.z -
                2*wy*v.x - yy*v.z + 2*wx*v.y -
                xx*v.z + ww*v.z);
        return output.Mul(Rad2Deg);
    }

    /*public static Quaternion FromAxisAngle(Vector3 axis, float angle)
    {
        if (axis.LengthSquared == 0.0f)
        {
            return Identity;
        }

        var result = Identity;

        angle *= 0.5f;
        axis.Normalize();
        result.Xyz = axis * MathF.Sin(angle);
        result.w = MathF.Cos(angle);

        return Normalize(result);
    }*/

    public static Quaternion Inverse(Quaternion q)
    {
        float sqrLen = Dot(q, q);
        if (sqrLen <= 0)
        {
            return Quaternion.Identity();
        }
        return Conjugate(q).Div(sqrLen);
    }

    // The conjugate of q = (x,y,z,w) is conj(q) = (-x,-y,-z,w).
    public static Quaternion Conjugate(Quaternion q)
    {
        return new Quaternion(-q.get(0), -q.get(1), -q.get(2), +q.get(3));
    }

    public static float Dot(Quaternion q0, Quaternion q1)
    {
        float dot = q0.get(0) * q1.get(0);
        for (int i = 1; i < 4; ++i)
        {
            dot += q0.get(i) * q1.get(i);
        }
        return dot;
    }

    @Override
    public String toString() {
        return "Quaternion{ " + x + " " + y + " " + z + " " + w + " }";
    }
}

/*package Utility;

// A quaternion is of the form
//   q = x * i + y * j + z * k + w * 1 = x * i + y * j + z * k + w
// where w, x, y, and z are real numbers.  The scalar and vector parts are
//   Vector(q) = x * i + y * j + z * k
//   Scalar(q) = w
//   q = Vector(q) + Scalar(q)
// I assume that you are familiar with the arithmetic and algebraic properties
// of quaternions.  See
// https://www.geometrictools.com/Documentation/Quaternions.pdf
public class Quaternion {
    // The quaternions are of the form q = x*i + y*j + z*k + w.  In tuple
    // form, q = (x,y,z,w).

    private float x;
    private float y;
    private float z;
    private float w;

    // Construction.  The default constructor does not initialize the
    // members.
    public Quaternion() {}

    public Quaternion(float x, float y, float z, float w)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Quaternion(float rotationX, float rotationY, float rotationZ)
    {
        rotationX *= Mathf.Deg2Rad;
        rotationY *= Mathf.Deg2Rad;
        rotationZ *= Mathf.Deg2Rad;
        rotationX *= 0.5f;
        rotationY *= 0.5f;
        rotationZ *= 0.5f;

        double c1 = Math.cos(rotationX);
        double c2 = Math.cos(rotationY);
        double c3 = Math.cos(rotationZ);
        double s1 = Math.sin(rotationX);
        double s2 = Math.sin(rotationY);
        double s3 = Math.sin(rotationZ);

        this.w = (float)((c1 * c2 * c3) - (s1 * s2 * s3));
        this.x = (float)((s1 * c2 * c3) + (c1 * s2 * s3));
        this.y = (float)((c1 * s2 * c3) - (s1 * c2 * s3));
        this.z = (float)((c1 * c2 * s3) + (s1 * s2 * c3));
    }

    // Special quaternions.

    // z = 0*i + 0*j + 0*k + 0
    static Quaternion Zero()
    {
        return new Quaternion(0, 0, 0, 0);
    }

    // i = 1*i + 0*j + 0*k + 0
    static Quaternion I()
    {
        return new Quaternion(1, 0, 0, 0);
    }

    // j = 0*i + 1*j + 0*k + 0
    static Quaternion J()
    {
        return new Quaternion(0, 1, 0, 0);
    }

    // k = 0*i + 0*j + 1*k + 0
    static Quaternion K()
    {
        return new Quaternion(0, 0, 1, 0);
    }

    // 1 = 0*i + 0*j + 0*k + 1
    static Quaternion Identity()
    {
        return new Quaternion(0, 0, 0, 1);
    }

    public float get(int id) {
        switch (id) {
            case 0: {return x;}
            case 1: {return y;}
            case 2: {return z;}
            case 3: {return w;}
            default: return -1;
        }
    }

    public void set(int id, float val) {
        switch (id) {
            case 0: {this.x = val; break;}
            case 1: {this.y = val; break;}
            case 2: {this.z = val; break;}
            case 3: {this.w = val; break;}
        }
    }

    public Quaternion Add(float v) {
        this.x += v;
        this.y += v;
        this.z += v;
        this.w += v;
        return this;
    }

    public Quaternion Sub(float v) {
        this.x -= v;
        this.y -= v;
        this.z -= v;
        this.w -= v;
        return this;
    }

    public Quaternion Mul(float v) {
        this.x *= v;
        this.y *= v;
        this.z *= v;
        this.w *= v;
        return this;
    }

    public Quaternion Div(float v) {
        this.x /= v;
        this.y /= v;
        this.z /= v;
        this.w /= v;
        return this;
    }

    public static float Dot(Quaternion q0, Quaternion q1)
    {
        float dot = q0.get(0) * q1.get(0);
        for (int i = 1; i < 4; ++i)
        {
            dot += q0.get(i) * q1.get(i);
        }
        return dot;
    }

    public static float Length(Quaternion q)
    {
        return Mathf.Sqrt(Dot(q, q));
    }

    float Normalize(Quaternion q)
    {
        float length = Length(q);
        if (length > 0)
        {
            q.Div(length);
        }
        else
        {
            for (int i = 0; i < 4; ++i)
            {
                q.set(i, 0);
            }
        }
        return length;
    }

    public Quaternion Mul(Quaternion q1)
    {
        Quaternion q0 = this;
        // (x0*i + y0*j + z0*k + w0)*(x1*i + y1*j + z1*k + w1)
        // =
        // i*(+x0*w1 + y0*z1 - z0*y1 + w0*x1) +
        // j*(-x0*z1 + y0*w1 + z0*x1 + w0*y1) +
        // k*(+x0*y1 - y0*x1 + z0*w1 + w0*z1) +
        // 1*(-x0*x1 - y0*y1 - z0*z1 + w0*w1)

        //return new Quaternion(
        this.x = +q0.get(0) * q1.get(3) + q0.get(1) * q1.get(2) - q0.get(2) * q1.get(1) + q0.get(3) * q1.get(0);
        this.y = -q0.get(0) * q1.get(2) + q0.get(1) * q1.get(3) + q0.get(2) * q1.get(0) + q0.get(3) * q1.get(1);
        this.z = +q0.get(0) * q1.get(1) - q0.get(1) * q1.get(0) + q0.get(2) * q1.get(3) + q0.get(3) * q1.get(2);
        this.w = -q0.get(0) * q1.get(0) - q0.get(1) * q1.get(1) - q0.get(2) * q1.get(2) + q0.get(3) * q1.get(3);
        //);
        return this;
    }

    // Multiplication of quaternions.  This operation is not generally
    // commutative; that is, q0*q1 and q1*q0 are not usually the same value.
    // (x0*i + y0*j + z0*k + w0)*(x1*i + y1*j + z1*k + w1)
    // =
    // i*(+x0*w1 + y0*z1 - z0*y1 + w0*x1) +
    // j*(-x0*z1 + y0*w1 + z0*x1 + w0*y1) +
    // k*(+x0*y1 - y0*x1 + z0*w1 + w0*z1) +
    // 1*(-x0*x1 - y0*y1 - z0*z1 + w0*w1)
    public static Quaternion Mul(Quaternion q0, Quaternion q1)
    {
        // (x0*i + y0*j + z0*k + w0)*(x1*i + y1*j + z1*k + w1)
        // =
        // i*(+x0*w1 + y0*z1 - z0*y1 + w0*x1) +
        // j*(-x0*z1 + y0*w1 + z0*x1 + w0*y1) +
        // k*(+x0*y1 - y0*x1 + z0*w1 + w0*z1) +
        // 1*(-x0*x1 - y0*y1 - z0*z1 + w0*w1)

        return new Quaternion(
                +q0.get(0) * q1.get(3) + q0.get(1) * q1.get(2) - q0.get(2) * q1.get(1) + q0.get(3) * q1.get(0),
                -q0.get(0) * q1.get(2) + q0.get(1) * q1.get(3) + q0.get(2) * q1.get(0) + q0.get(3) * q1.get(1),
                +q0.get(0) * q1.get(1) - q0.get(1) * q1.get(0) + q0.get(2) * q1.get(3) + q0.get(3) * q1.get(2),
                -q0.get(0) * q1.get(0) - q0.get(1) * q1.get(1) - q0.get(2) * q1.get(2) + q0.get(3) * q1.get(3)
        );
    }

    // For a nonzero quaternion q = (x,y,z,w), inv(q) = (-x,-y,-z,w)/|q|^2, where
    // |q| is the length of the quaternion.  When q is zero, the function returns
    // zero, which is considered to be an improbable case.
    public static Quaternion Inverse(Quaternion q)
    {
        float sqrLen = Dot(q, q);
        if (sqrLen > 0)
        {
            return Conjugate(q).Div(sqrLen);
        }
        else
        {
            return Quaternion.Zero();
        }
    }

    // The conjugate of q = (x,y,z,w) is conj(q) = (-x,-y,-z,w).
    public static Quaternion Conjugate(Quaternion q)
    {
        return new Quaternion(-q.get(0), -q.get(1), -q.get(2), +q.get(3));
    }

    // Rotate a vector using quaternion multiplication.  The input quaternion must
    // be unit length.
    public static Vector3 Rotate(Quaternion q, Vector3 v)
    {
        Quaternion input = new Quaternion(v.x, v.y, v.z, 0);
        Quaternion.Mul(q, input);
        Quaternion output = Mul(q,Conjugate(q));
        return new Vector3( output.get(0), output.get(1), output.get(2));
    }
}*/

