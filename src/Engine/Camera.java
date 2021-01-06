package Engine;

import Utility.Mathf;
import Utility.Matrix4x4;
import Utility.Vector3;
import javafx.scene.paint.Color;

public class Camera extends MonoBehaviour {
    public static Camera main;

    private float nearPlane = 0.5f;
    public void SetNearPlane(float nearPlane) { this.nearPlane = nearPlane; RecalculateProjectionMatrix(); }
    public float NearPlane() { return this.nearPlane; }

    private float farPlane = 100f;
    public void SetFarPlane(float farPlane) { this.farPlane = farPlane; RecalculateProjectionMatrix(); }
    public float FarPlane() { return this.farPlane; }

    private float fieldOfView = 90f;
    public void SetFOV(float fov) {
        this.fieldOfView = fov;
        this.fieldOfViewRadians = 1.0f / Mathf.Tan(fieldOfView * 0.5f * Mathf.Deg2Rad);
        RecalculateProjectionMatrix();
    }
    public float FOV() { return this.fieldOfView; }

    private float aspectRatio;
    public void SetAspectRatio(int width, int height) {
        aspectRatio = (float)height/width;
        fieldOfViewRadians = 1.0f / Mathf.Tan(fieldOfView * 0.5f * Mathf.Deg2Rad);
        if (projectionMatrix != null) RecalculateProjectionMatrix();
    }
    private float fieldOfViewRadians;

    private ClearMode clearMode = ClearMode.SolidColor;
    public void SetClearMode(ClearMode cMode) { this.clearMode = cMode; } public ClearMode ClearMode() { return this.clearMode; }

    public Color clearColor = Color.DARKGRAY, gradientColor = Color.GREY;

    private Matrix4x4 projectionMatrix;
    public Matrix4x4 GetProjectionMatrix() {
        return projectionMatrix;
    }

    public Camera() {
        SetAspectRatio(Screen.Width(), Screen.Height());
        projectionMatrix = new Matrix4x4();
        RecalculateProjectionMatrix();
    }

    public Vector3 ScreenToWorldPoint(Vector3 screenPoint) {
        return null;
    }

    public Vector3 WorldToScreenPoint(Vector3 worldPoint) {
        return null;
    }

    private void RecalculateProjectionMatrix() {
        projectionMatrix.mat[0][0] = aspectRatio / fieldOfViewRadians;
        projectionMatrix.mat[1][1] = fieldOfViewRadians;
        projectionMatrix.mat[2][2] = farPlane / (farPlane - nearPlane);
        projectionMatrix.mat[3][2] = (-farPlane * nearPlane) / (farPlane - nearPlane);
        projectionMatrix.mat[2][3] = 1.0f;
        projectionMatrix.mat[3][3] = 0.0f;
    }

    public enum ClearMode {
        None,
        SolidColor,
        Gradient,
    }
}
