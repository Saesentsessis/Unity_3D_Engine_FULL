package Engine;

import Utility.Vector3;

public class Screen {
    private static int width;
    public static int Width() { return width; }

    private static int height;
    public static int Height() { return height; }

    private static Vector3 lightDirection;
    public static Vector3 LightDirection() { return Screen.lightDirection; }

    private static float[] depthBuffer;
    public static float Depth(int width, int height) { return depthBuffer[Screen.width*height+width]; }
    public static void SetDepth(int width, int height, float depth) { depthBuffer[Screen.width*height+width] = depth; }

    public static void SetScreen(int width, int height) {
        if (width == Screen.width && height == Screen.height) return;
        Screen.width = width;
        Screen.height = height;
        if (Camera.main == null) return;
        Camera.main.SetAspectRatio(width,height);
        depthBuffer = new float[width*height];
    }
}
