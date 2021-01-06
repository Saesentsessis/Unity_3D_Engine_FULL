package Engine;

import Primitives.*;
import Utility.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import sample.Controller;

import java.util.Arrays;
import java.util.Vector;

public class MeshRenderer extends MonoBehaviour {
    private Mesh currentMesh;
    public void SetMesh(Mesh ref) { this.currentMesh = ref; RecalculateMesh(); } public Mesh Mesh() { return this.currentMesh; }

    public Color currentColor = Color.GRAY;
    public Color wireframeColor = Color.BLACK;

    private Vector3[] translatedVerts;
    private Vector3[] projectedVerts;
    public Vector3[] ProjectedVerts() { return this.projectedVerts; }
    private Vector3[] triangleNormals;
    private boolean[] isProjected;
    public boolean IsProjected(int index) { if (index < 0 || index >= isProjected.length) return false; return isProjected[index]; }

    public MeshRenderer() {
        Controller.Instance.drawables.add(this);
    }

    public void Draw(Camera cam, Controller.DrawMode drawMode) {
        //Random rand = new Random();
        if (currentMesh == null) return;
        Arrays.fill(isProjected, false);
        Matrix4x4 cameraProjMatrix = cam.GetProjectionMatrix();
        Vector3 screenSize = new Vector3((float) Controller.Instance.canvas.getWidth(), (float) Controller.Instance.canvas.getHeight());

        GraphicsContext gc = Controller.Instance.canvas.getGraphicsContext2D();
        switch (drawMode) {
            case Wireframe: { gc.setLineWidth(0.5); gc.setStroke(wireframeColor); break; }
            case Solid: { gc.setLineWidth(1); gc.setFill(currentColor); break; }
        }

        float maxSqrDistance = cam.FarPlane()*cam.FarPlane(), minSqrDistance = cam.NearPlane()*cam.NearPlane();
        int i = -1;
        Float distance = 0.f;
        for (Triangle tri : currentMesh.tris) {
            i++;
            if (IsTriangleClipped(i, maxSqrDistance, minSqrDistance, cam, drawMode, distance)) continue;
            //gc.setStroke(new Color(rand.nextFloat()%1, rand.nextFloat()%1, rand.nextFloat()%1, 1f));

            Vector3 center = Utility.CentralPoint(new Vector3[] {translatedVerts[tri.First()], translatedVerts[tri.Second()], translatedVerts[tri.Third()]});
            float dist = Vector3.Distance(Vector3.zero, center);

            ProjectTriangle(tri, cameraProjMatrix, screenSize);

            int first = tri.First(), second = tri.Second(), third = tri.Third();

            switch (drawMode) {
                case Wireframe:
                {
                    if (NeedToDrawEdge(first,second,screenSize))
                        gc.strokeLine(projectedVerts[first].x, projectedVerts[first].y, projectedVerts[second].x, projectedVerts[second].y);
                    if (NeedToDrawEdge(second,third,screenSize))
                        gc.strokeLine(projectedVerts[second].x, projectedVerts[second].y, projectedVerts[third].x, projectedVerts[third].y);
                    if (NeedToDrawEdge(third,first,screenSize))
                        gc.strokeLine(projectedVerts[third].x, projectedVerts[third].y, projectedVerts[first].x, projectedVerts[first].y);
                    break;
                }
                case Solid:
                {
                    if (NeedToDrawPolygon(projectedVerts[first], projectedVerts[second], projectedVerts[third], screenSize)) {
                        float dotProduct = Vector3.DotProd(Controller.LightDirection, triangleNormals[i]) * 0.8f;
                        Color fill;
                        if (dotProduct < 0) {
                            dotProduct *= -1; dotProduct = 1-dotProduct; dotProduct += 0.2; if (dotProduct > 1) dotProduct = 1;
                            fill = new Color(currentColor.getRed() * dotProduct, currentColor.getGreen() * dotProduct, currentColor.getBlue() * dotProduct, 1);
                        }
                        else {
                            double red = dotProduct + currentColor.getRed() * (.99f - dotProduct),
                                    green = dotProduct + currentColor.getGreen() * (.99f - dotProduct),
                                    blue = dotProduct * 0.7f + currentColor.getBlue() * (.99f - dotProduct * 0.7f);
                            fill = new Color(red, green, blue, 1);
                        }
                        GL.FillTriangle(new Triangle(-1, -1, -1, projectedVerts[first], projectedVerts[second], projectedVerts[third]), dist, fill);
                        /*gc.setStroke(gc.getFill());
                        //GL.FillTriangle(Mathf.RoundToInt(projectedVerts[first].x), Mathf.RoundToInt(projectedVerts[first].y),
                        //        Mathf.RoundToInt(projectedVerts[second].x), Mathf.RoundToInt(projectedVerts[second].y),
                        //        Mathf.RoundToInt(projectedVerts[third].x), Mathf.RoundToInt(projectedVerts[third].y), (Color)gc.getFill(), gc);
                        double[] xS = new double[] {
                                projectedVerts[first].x, //+ (projectedVerts[first].x > projectedVerts[second].x && projectedVerts[first].x > projectedVerts[third].x ? 1 : 0) + (projectedVerts[first].x < projectedVerts[second].x && projectedVerts[first].x < projectedVerts[third].x ? -1 : 0),
                                projectedVerts[second].x, //+ (projectedVerts[second].x > projectedVerts[first].x && projectedVerts[second].x > projectedVerts[third].x ? 1 : 0) + (projectedVerts[second].x < projectedVerts[first].x && projectedVerts[second].x < projectedVerts[third].x ? -1 : 0),
                                projectedVerts[third].x};
                        double[] yS = new double[] {
                                projectedVerts[first].y, //+ (projectedVerts[first].y > projectedVerts[second].y && projectedVerts[first].y > projectedVerts[third].y ? 1 : 0) + (projectedVerts[first].y < projectedVerts[second].y && projectedVerts[first].y < projectedVerts[third].y ? -1 : 0),
                                projectedVerts[second].y, //+ (projectedVerts[second].y > projectedVerts[first].y && projectedVerts[second].y > projectedVerts[third].y ? 1 : 0) + (projectedVerts[second].y < projectedVerts[first].y && projectedVerts[second].y < projectedVerts[third].y ? -1 : 0),
                                projectedVerts[third].y};
                        gc.fillPolygon(xS, yS, 3 //+ (projectedVerts[third].x > projectedVerts[first].x && projectedVerts[third].x > projectedVerts[second].x ? 1 : 0) + (projectedVerts[third].x < projectedVerts[first].x && projectedVerts[third].x < projectedVerts[second].x ? -1 : 0)
                                 //+ (projectedVerts[third].y > projectedVerts[first].y && projectedVerts[third].y > projectedVerts[second].y ? 1 : 0) + (projectedVerts[third].y < projectedVerts[first].y && projectedVerts[third].y < projectedVerts[second].y ? -1 : 0)
                        );
                        gc.strokePolygon(xS,yS,3);*/
                        //gc.strokeLine(xS[0], yS[0], xS[1], yS[1]);
                        //gc.strokeLine(xS[1], yS[1], xS[2], yS[2]);
                        //gc.strokeLine(xS[2], yS[2], xS[0], yS[0]);
                    }
                    break;
                }
            }
        }
    }

    //region PROJECTION
    private Vector3 ProjectVertex(Vector3 rawVertex, Camera cam, Matrix4x4 projectionMatrix, Vector3 screenSize) {
        Vector3 projectedVertex = new Vector3();
        Vector3 vertTranslated = Vector3.Mul(rawVertex, transform.scale).Add(transform.position).Sub(cam.transform.position);

        projectionMatrix.MultiplyMatrixVector(vertTranslated, projectedVertex);

        projectedVertex.Add(1f,1f,1f);
        projectedVertex.Mul(0.5f);
        projectedVertex.Mul(screenSize);

        projectedVertex.y -= Screen.Height(); projectedVertex.y *=-1;

        return projectedVertex;
    }

    private Vector3 ProjectVertex(int index, Matrix4x4 projectionMatrix, Vector3 screenSize) {
        Vector3 projectedVertex = new Vector3();

        projectionMatrix.MultiplyMatrixVector(translatedVerts[index], projectedVertex);

        projectedVertex.Add(1f,1f,1f);
        projectedVertex.Mul(0.5f);
        projectedVertex.Mul(screenSize);

        projectedVertex.y -= Screen.Height(); projectedVertex.y *=-1;

        return projectedVertex;
    }

    private void ProjectTriangle(Triangle rawTriangle, Camera cam, Matrix4x4 projectionMatrix, Vector3 screenSize) {
        int first = rawTriangle.First(), second = rawTriangle.Second(), third = rawTriangle.Third();

        if (!isProjected[first]) {
            projectedVerts[first] = ProjectVertex(currentMesh.verts.get(first), cam, projectionMatrix, screenSize);
            isProjected[first] = true;
        }
        if (!isProjected[second]) {
            projectedVerts[second] = ProjectVertex(currentMesh.verts.get(second), cam, projectionMatrix, screenSize);
            isProjected[second] = true;
        }
        if (!isProjected[third]) {
            projectedVerts[third] = ProjectVertex(currentMesh.verts.get(third), cam, projectionMatrix, screenSize);
            isProjected[third] = true;
        }
    }

    private void ProjectTriangle(Triangle rawTriangle, Matrix4x4 projectionMatrix, Vector3 screenSize) {
        int first = rawTriangle.First(), second = rawTriangle.Second(), third = rawTriangle.Third();

        if (!isProjected[first]) {
            projectedVerts[first] = ProjectVertex(first, projectionMatrix, screenSize);
            isProjected[first] = true;
        }
        if (!isProjected[second]) {
            projectedVerts[second] = ProjectVertex(second, projectionMatrix, screenSize);
            isProjected[second] = true;
        }
        if (!isProjected[third]) {
            projectedVerts[third] = ProjectVertex(third, projectionMatrix, screenSize);
            isProjected[third] = true;
        }
    }

    private Vector3 TranslateVertex(int index, Camera cam) {
        //region NO ROTATION
        /*translatedVerts[index] = Vector3.Mul(transform.scale, currentMesh.verts.get(index));
        translatedVerts[index].Add(transform.position).Sub(cam.transform.position);
        return translatedVerts[index];
        */
        //endregion
        //Vector3 vert = Vector3.Mul(transform.scale, currentMesh.verts.get(index));
        //transform.GetRotationMatrix().MultiplyMatrixVector(currentMesh.verts.get(index), translatedVerts[index]);
        //translatedVerts[index].Mul(transform.scale).Add(transform.position).Sub(cam.transform.position);
        //Vector3 buff = new Vector3(translatedVerts[index]);
        //cam.transform.GetRotationMatrix().MultiplyMatrixVector(buff, translatedVerts[index]);
        transform.GetRotationMatrix().MultiplyMatrixVector(currentMesh.verts.get(index), translatedVerts[index]);
        translatedVerts[index].Mul(transform.scale).Add(transform.position).Sub(cam.transform.position);
        cam.transform.GetRotationMatrix().MultiplyMatrixVector(new Vector3(translatedVerts[index]), translatedVerts[index]);
        return translatedVerts[index];
    }
    //endregion

    private void RecalculateMesh() {
        projectedVerts = new Vector3[currentMesh.verts.size()*2];
        translatedVerts = new Vector3[currentMesh.verts.size()];
        triangleNormals = new Vector3[currentMesh.tris.size()*2];
        for (int i = 0; i < translatedVerts.length; i++)
            translatedVerts[i] = new Vector3();
        isProjected = new boolean[projectedVerts.length];
    }

    //region OPTIMIZATION
    private boolean NeedToDrawEdge(int first, int second, Vector3 screenSize) {
        return !((projectedVerts[first].x > screenSize.x &&
                   projectedVerts[second].x > screenSize.x) ||
                  (projectedVerts[first].y > screenSize.y &&
                   projectedVerts[second].y > screenSize.y) ||
                  (projectedVerts[first].x < 0 &&
                   projectedVerts[second].x < 0) ||
                  (projectedVerts[first].y < 0 &&
                   projectedVerts[second].y < 0)
        );
    }

    private boolean NeedToDrawPolygon(Vector3 first, Vector3 second, Vector3 third, Vector3 screenSize) {
        return !((first.x > screenSize.x && second.x > screenSize.x && third.x > screenSize.x) ||
                 (first.y > screenSize.y && second.y > screenSize.y && third.y > screenSize.y) ||
                 (first.x < 0 && second.x < 0 && third.x < 0) ||
                 (first.y < 0 && second.y < 0 && third.y < 0)
        );
    }

    private boolean IsTriangleClipped(int index, float maxSqrDistance, float minSqrDistance, Camera cam, Controller.DrawMode drawMode, Float dist) {
        Triangle tri = currentMesh.tris.get(index);
        TranslateVertex(tri.First(), cam);
        float first = Vector3.SqrDistance(Vector3.zero, translatedVerts[tri.First()]);
        if (first > maxSqrDistance || first < minSqrDistance) return true;

        TranslateVertex(tri.Second(), cam);
        float second = Vector3.SqrDistance(Vector3.zero, translatedVerts[tri.Second()]);
        if (second > maxSqrDistance || second < minSqrDistance) return true;

        TranslateVertex(tri.Third(), cam);
        float third = Vector3.SqrDistance(Vector3.zero, translatedVerts[tri.Third()]);
        if (third > maxSqrDistance || third < minSqrDistance) return true;

        if (drawMode == Controller.DrawMode.Wireframe) return false;

        Vector3 line1 = Vector3.Sub(translatedVerts[tri.Second()], translatedVerts[tri.First()]);
        Vector3 line2 = Vector3.Sub(translatedVerts[tri.Third()], translatedVerts[tri.First()]);

        triangleNormals[index] = Vector3.CrossProd(line1, line2).Normalise();

        if (transform.scale.x >= 0 && transform.scale.y >= 0 && transform.scale.z >= 0) {
            return Vector3.DotProd(translatedVerts[tri.First()], triangleNormals[index]) >= 0.0f;
        }
        triangleNormals[index].Mul(-1f);
        return Vector3.DotProd(translatedVerts[tri.First()], triangleNormals[index]) >= 0.0f;
    }
    //endregion
}
