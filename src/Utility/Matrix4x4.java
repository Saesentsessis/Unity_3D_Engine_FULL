package Utility;

import Engine.Transform;

public class Matrix4x4 {
    public float[][] mat = new float[4][4];

    public Matrix4x4() {}

    public Matrix4x4(Matrix4x4 m1, Matrix4x4 m2) {
        for (int c = 0; c < 4; c++)
            for (int r = 0; r < 4; r++)
                this.mat[r][c] = m1.mat[r][0] * m2.mat[0][c] + m1.mat[r][1] * m2.mat[1][c] + m1.mat[r][2] * m2.mat[2][c] + m1.mat[r][3] * m2.mat[3][c];
    }

    public static Matrix4x4 MultiplyMatrices(Matrix4x4 m1, Matrix4x4 m2) {
        Matrix4x4 m3 = new Matrix4x4();
        for (int c = 0; c < 4; c++)
            for (int r = 0; r < 4; r++)
                m3.mat[r][c] = m1.mat[r][0] * m2.mat[0][c] + m1.mat[r][1] * m2.mat[1][c] + m1.mat[r][2] * m2.mat[2][c] + m1.mat[r][3] * m2.mat[3][c];
        return m3;
    }

    public void CalculateXRotationMatrix(float angle) {
        float cos = Mathf.Cos(angle * Mathf.Deg2Rad);
        float sin = Mathf.Sin(angle * Mathf.Deg2Rad);

        this.mat[0][0] = 1f;
        this.mat[1][1] = cos;
        this.mat[2][1] = sin;
        this.mat[1][2] = -sin;
        this.mat[2][2] = cos;
        this.mat[3][3] = 1f;
    }

    public void CalculateYRotationMatrix(float angle) {
        float cos = Mathf.Cos(angle * Mathf.Deg2Rad);
        float sin = Mathf.Sin(angle * Mathf.Deg2Rad);

        this.mat[0][0] = cos;
        this.mat[1][1] = 1f;
        this.mat[2][0] = -sin;
        this.mat[0][2] = sin;
        this.mat[2][2] = cos;
        this.mat[3][3] = 1f;
    }

    public void CalculateZRotationMatrix(float angle) {
        float cos = Mathf.Cos(angle * Mathf.Deg2Rad);
        float sin = Mathf.Sin(angle * Mathf.Deg2Rad);

        this.mat[0][0] = cos;
        this.mat[0][1] = -sin;
        this.mat[1][0] = sin;
        this.mat[1][1] = cos;
        this.mat[2][2] = 1f;
        this.mat[3][3] = 1f;
    }

    /*public void CalculateRotationMatrix(Vector3 rotation) {
        float cosX = (float)Math.cos(Math.toRadians(rotation.x)), sinX = (float)Math.sin(Math.toRadians(rotation.x));
        float cosY = (float)Math.cos(Math.toRadians(rotation.y)), sinY = (float)Math.sin(Math.toRadians(rotation.y));
        float cosZ = (float)Math.cos(Math.toRadians(rotation.z)), sinZ = (float)Math.sin(Math.toRadians(rotation.z));
        Matrix4x4 rotZ = new Matrix4x4();
        Matrix4x4 rotX = new Matrix4x4();
        Matrix4x4 rotY = new Matrix4x4();

        rotZ.mat[0][0] =
        rotZ.mat[3][3] = 1;
    }*/

    public void MultiplyMatrixVector(Vector3 in, Vector3 out) {
        out.x = in.x * mat[0][0] + in.y * mat[1][0] + in.z * mat[2][0] + mat[3][0];
        out.y = in.x * mat[0][1] + in.y * mat[1][1] + in.z * mat[2][1] + mat[3][1];
        out.z = in.x * mat[0][2] + in.y * mat[1][2] + in.z * mat[2][2] + mat[3][2];
        float w = in.x * mat[0][3] + in.y * mat[1][3] + in.z * mat[2][3] + mat[3][3];

        if (w != 0.0f) {
            out.x /= w;
            out.y /= w;
            out.z /= w;
        }
    }

    /*public void CalculateRotationMatrix(Transform transform) {
        float cosX = (float)Math.cos(Math.toRadians(transform.Rotation().x)), sinX = (float)Math.sin(Math.toRadians(transform.Rotation().x));
        float cosY = (float)Math.cos(Math.toRadians(transform.Rotation().y)), sinY = (float)Math.sin(Math.toRadians(transform.Rotation().y));
        float cosZ = (float)Math.cos(Math.toRadians(transform.Rotation().z)), sinZ = (float)Math.sin(Math.toRadians(transform.Rotation().z));
        this.mat[0][0] = 1 + cosY + cosZ;
        this.mat[0][1] = -sinZ;
        this.mat[0][2] = sinY;
        this.mat[1][0] = sinZ;
        this.mat[1][1] = cosX + 1 + cosZ;
        this.mat[1][2] = -sinX;
        this.mat[2][0] = -sinY;
        this.mat[2][1] = sinX;
        this.mat[2][2] = cosX + cosY + 1;
        this.mat[3][3] = 3;
    }*/

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("Matrix{\n");
        for (int x = 0; x < mat.length; x++) {
            for (int y = 0; y < mat[x].length; y++)
                out.append(mat[x][y]).append(' ');
            out.append('\n');
        }
        out.append('}');
        return out.toString();
    }
}
