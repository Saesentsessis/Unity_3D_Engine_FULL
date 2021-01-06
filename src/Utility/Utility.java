package Utility;

import Primitives.Mesh;
import Primitives.Triangle;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Vector;

public class Utility {
    public static Vector3 ParseVertex(String rawData) {
        String[] data = rawData.split(" ");
        if (data.length < 3) return null;
        int index = data[0].equals("v") ? 1 : 0;
        return new Vector3(
                Float.parseFloat(data[index]),
                Float.parseFloat(data[index+1]),
                Float.parseFloat(data[index+2])
                );
    }

    /*public static Triangle ParseTriangle(String rawData) {
        String[] data = rawData.split(" ");
        if (data.length < 3) return null;
        int index = data[0].equals("v") ? 1 : 0;
        return new Triangle(
                Integer.parseInt()
        );
    }*/

    public static Mesh LoadMesh(String path) {
        String meshName = "NewMesh";

        String[] rawData = new String[0];
        try {
            rawData = Files.readAllLines(Paths.get(path)).toArray(new String[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<Vector3> verts = new ArrayList<>(8);
        ArrayList<Triangle> tris = new ArrayList<>(12);
        for (String rawDataPart: rawData) {
            switch (rawDataPart.charAt(0)) {
                case 'v':
                {
                    verts.add(ParseVertex(rawDataPart));
                    break;
                }
                case 'f':
                {
                    String[] data = rawDataPart.split(" ");
                    int[] indexes = new int[] {Integer.parseInt(data[1])-1, Integer.parseInt(data[2])-1, Integer.parseInt(data[3])-1};
                    tris.add(new Triangle(
                            indexes[0], indexes[1], indexes[2],
                            verts.get(Integer.parseInt(data[1])-1),
                            verts.get(Integer.parseInt(data[2])-1),
                            verts.get(Integer.parseInt(data[3])-1)
                    ));
                    break;
                }
                case 'o':
                {
                    try {
                        if (rawDataPart.split(" ")[1].split("_").length > 1)
                        meshName = rawDataPart.split(" ")[1].split("_")[1];
                        else meshName = rawDataPart.split(" ")[1];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        return new Mesh(meshName, verts, tris);
    }

    public static boolean PointInsideTriangle(Vector3 a, Vector3 b, Vector3 c, Vector2 point) {
        if (b.y > c.y) {
            Vector3 buff = b;
            b = c;
            c = buff;
        }
        if (a.y > c.y) {
            Vector3 buff = a;
            a = c;
            c = buff;
        }
        if (b.y > c.y) {
            Vector3 buff = b;
            b = c;
            c = buff;
        }

        float as_x = point.x-a.x;
        float as_y = point.y-a.y;

        boolean s_ab = (b.x-a.x)*as_y-(b.y-a.y)*as_x > 0;

        if ((c.x-a.x)*as_y-(c.y-a.y)*as_x > 0 == s_ab) return false;

        return (c.x - b.x) * (point.y - b.y) - (c.y - b.y) * (point.x - b.x) > 0 == s_ab;

        //Works better than first but not good enough
        /*float s = a.y * c.x - a.x * c.y + (c.y - a.y) * point.x + (a.x - c.x) * point.y;
        float t = a.x * b.y - a.y * b.x + (a.y - b.y) * point.x + (b.x - a.x) * point.y;

        if ((s < 0) != (t < 0))
            return false;

        float A = -b.y * c.x + a.y * (c.x - b.x) + a.x * (b.y - c.y) + b.x * c.y;

        return A < 0 ?
                (s <= 0 && s + t >= A) :
                (s >= 0 && s + t <= A);*/

        // +- works
        /*double x3 = c.x, y3 = c.y;
        double y23, x32, y31, x13;
        double det, minD, maxD;

        y23 = b.y - c.y;
        x32 = c.x - b.x;
        y31 = c.y - a.y;
        x13 = a.x - c.x;
        det = y23 * x13 - x32 * y31;
        minD = Math.min(det, 0);
        maxD = Math.max(det, 0);

        double dx = point.x - x3;
        double dy = point.y - y3;
        double _a = y23 * dx + x32 * dy;
        if (_a < minD || _a > maxD)
            return false;
        double _b = y31 * dx + x13 * dy;
        if (_b < minD || _b > maxD)
            return false;
        double _c = det - _a - _b;
        if (_c < minD || _c > maxD)
            return false;
        return true;*/
    }

    public static float[] CalculateBoundaries(Vector3[] points) {
        float minX = points[0].x, maxX = points[0].x, minY = points[0].y, maxY = points[0].y;
        for (Vector3 point : points) {
            if (point == null) continue;
            if (point.x < minX) minX = point.x;
            if (point.x > maxX) maxX = point.x;
            if (point.y < minY) minY = point.y;
            if (point.y > maxY) maxY = point.y;
        }
        return new float[] { minX, maxX, minY, maxY };
    }

    public static Vector3 CentralPoint(Vector3[] points) {
        float x = 0, y = 0, z = 0, mult = 1f/points.length;
        for (Vector3 point : points) {
            x += point.x;
            y += point.y;
            z += point.z;
        }
        return new Vector3(x*mult, y*mult, z*mult);
    }
}
