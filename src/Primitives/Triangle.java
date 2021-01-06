package Primitives;

import Utility.Vector3;

public class Triangle {
    private int[] indexes;
    private Vector3[] verts;
    public int First() {return indexes[0];}
    public int Second() {return indexes[1];}
    public int Third() {return indexes[2];}
    public Vector3 VFirst() {return verts[0];}
    public Vector3 VSecond() {return verts[1];}
    public Vector3 VThird() {return verts[2];}

    public Triangle(int first, int second, int third, Vector3 vFirst, Vector3 vSecond, Vector3 vThird) {
        indexes = new int[] {first, second, third};
        verts = new Vector3[] {vFirst, vSecond, vThird};
    }

    public Triangle(Triangle ref) {this.indexes = new int[] {ref.First(), ref.Second(), ref.Third()}; this.verts = new Vector3[] {ref.VFirst(), ref.VSecond(), ref.VThird()}; }

    public Triangle(Triangle ref, Mesh newMesh) { this.indexes = new int[] {ref.First(), ref.Second(), ref.Third()}; this.verts = new Vector3[] {newMesh.verts.get(ref.First()), newMesh.verts.get(ref.Second()), newMesh.verts.get(ref.Third())}; }

    public Triangle(int first, Vector3 vFirst, int second, Vector3 vSecond, int third, Vector3 vThird) {
        indexes = new int[] {first, second, third};
        verts = new Vector3[] {vFirst, vSecond, vThird};
    }

    public static Vector3 IntersectPlane(Vector3 plane_p, Vector3 plane_n, Vector3 lineStart, Vector3 lineEnd) {
        plane_n.Normalise();
        float plane_d = -Vector3.DotProd(plane_n, plane_p);
        float ad = Vector3.DotProd(lineStart, plane_n);
        float bd = Vector3.DotProd(lineEnd, plane_n);
        float t = (-plane_d - ad) / (bd - ad);
        Vector3 lineStartToEnd = Vector3.Sub(lineEnd, lineStart);
        lineStartToEnd.Mul(t);
        return Vector3.Add(lineStart, lineStartToEnd);
    }

    public static Triangle[] ClipAgainstPlane(Vector3 plane_p, Vector3 plane_n, Triangle in_tri1) {
        plane_n.Normalise();

        Vector3[] insidePts = new Vector3[3]; int insPtsCount = 0;
        Vector3[] outsidePts = new Vector3[3]; int outPtsCount = 0;

        float d0 = DistanceBetweenPlane(in_tri1.VFirst(), plane_n);
        float d1 = DistanceBetweenPlane(in_tri1.VSecond(), plane_n);
        float d2 = DistanceBetweenPlane(in_tri1.VThird(), plane_n);

        if (d0 >= 0) { insidePts[insPtsCount++] = in_tri1.VFirst(); }
        else { outsidePts[outPtsCount++] = in_tri1.VFirst(); }
        if (d1 >= 0) { insidePts[insPtsCount++] = in_tri1.VSecond(); }
        else { outsidePts[outPtsCount++] = in_tri1.VSecond(); }
        if (d2 >= 0) { insidePts[insPtsCount++] = in_tri1.VThird(); }
        else { outsidePts[outPtsCount] = in_tri1.VThird();
        }

        if (insPtsCount == 0) return null;
        else if (insPtsCount == 3) { return new Triangle[]{in_tri1}; }
        else if (insPtsCount == 1) {
            return new Triangle[] {
                    new Triangle(in_tri1.First(), in_tri1.VFirst(),
                            in_tri1.Second(), Triangle.IntersectPlane(plane_p, plane_n, insidePts[0], outsidePts[0]),
                            in_tri1.Third(), Triangle.IntersectPlane(plane_p, plane_n, outsidePts[0], outsidePts[1])
                    )};
        }
        else {
            Vector3 nPoint = Triangle.IntersectPlane(plane_p, plane_n, insidePts[0], outsidePts[0]);
            return new Triangle[] {
                    new Triangle(in_tri1.First(), insidePts[0], in_tri1.Second(), insidePts[1],
                            in_tri1.Third(), nPoint),
                    new Triangle(in_tri1.Second(), insidePts[1], in_tri1.Third(), nPoint,
                            -1, Triangle.IntersectPlane(plane_p, plane_n, insidePts[1], outsidePts[0]))
            };
        }
    }

    private static float DistanceBetweenPlane(Vector3 plane_p, Vector3 plane_n) {
        plane_p.Normalise();
        return (plane_n.x * plane_p.x + plane_n.y + plane_p.y + plane_n.z + plane_p.z - Vector3.DotProd(plane_n, plane_p));
    }
}
