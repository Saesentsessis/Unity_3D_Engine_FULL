package sample;

import Engine.*;
import Primitives.Mesh;
import Primitives.Triangle;
import Utility.*;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.animation.*;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Controller {
    public static Controller Instance;
    private static Vector3 LightDir = new Vector3(-0.25f, 0.5f, -1f);
    public static Vector3 LightDirection = new Vector3(-.05f,0f,-1f);

    public static Scene scene;
    public static List<MonoBehaviour> entities = new ArrayList<>(9);
    public List<MeshRenderer> drawables = new ArrayList<>(3);

    public Canvas canvas;
    public AnchorPane anchorPane;

    private Vector3 deltaTransform = new Vector3();
    private Vector2 lastMousePosition;

    private CurrentAxis currentAxis = CurrentAxis.All;
    private CurrentMode currentMode = CurrentMode.Viewport;
    private CurrentAction currentAction = CurrentAction.None;
    private DrawMode currentDrawMode = DrawMode.Wireframe;
    public DrawMode DrawMode() { return currentDrawMode; }

    private boolean selectedObject = false;
    private boolean selectedInEdit = false;

    //private List<KeyCode> lastFramePressedKeys = new ArrayList<>(3);
    private List<KeyCode> pressedKeys = new ArrayList<>(3);

    private float debugTextSize = 11f;
    private Transform MovableObj;
    float delta = 0.05f;

    @FXML
    public void initialize() {
        Instance = this;

        Screen.SetScreen((int)canvas.getWidth(), (int)canvas.getHeight());

        Camera.main = Instantiate(new Vector3(0,0,-10f), new Vector3(), new Vector3(1), "MainCamera").AddComponent(Camera.class);
        Camera.main.SetFarPlane(25f);
        System.out.println(Camera.main.GetProjectionMatrix());
        GameObject cubeL = Instantiate("CubeL");
        GameObject cubeR = Instantiate("CubeR");
        GameObject monkey = Instantiate("Monkey");
        GameObject pencil = Instantiate("Pencil");
        GameObject torus = Instantiate("Torus");
        cubeL.transform.position.Add(new Vector3(-5f,0,0));
        cubeR.transform.position.Add(new Vector3(5f,4.5f,0));
        cubeR.transform.Rotate(45f,0f,0f);
        MeshRenderer cubeRendererL = cubeL.AddComponent(MeshRenderer.class);
        MeshRenderer cubeRendererR = cubeR.AddComponent(MeshRenderer.class);
        MeshRenderer monkeyRenderer = monkey.AddComponent(MeshRenderer.class);
        MeshRenderer pencilRenderer = pencil.AddComponent(MeshRenderer.class);
        MeshRenderer torusRenderer = torus.AddComponent(MeshRenderer.class);

        cubeRendererL.SetMesh( Utility.LoadMesh("Assets/Cube.obj") );
        cubeRendererL.wireframeColor = Color.GREEN;
        cubeRendererL.currentColor = Color.DARKGREEN;

        cubeRendererR.SetMesh( new Mesh(cubeRendererL.Mesh()) );
        cubeRendererR.wireframeColor = Color.LIGHTCORAL;
        cubeRendererR.currentColor = Color.CORAL;

        monkeyRenderer.SetMesh( Utility.LoadMesh("Assets/Monkey.obj") );

        pencilRenderer.SetMesh( Utility.LoadMesh("Assets/Pencil.obj") );
        pencilRenderer.wireframeColor = Color.BLUE;
        pencilRenderer.currentColor = Color.DARKSLATEBLUE;

        monkeyRenderer.transform.scale.Mul(new Vector3(2f,2f,-2f));
        //monkeyRenderer.transform.Rotate(0f,180f,0f);
        pencilRenderer.transform.Translate(-2f, -2f, -2f);
        pencilRenderer.transform.scale.Mul(0.25f);

        torusRenderer.SetMesh( Utility.LoadMesh("Assets/Torus.obj") );
        torus.transform.Translate(0f, 15f, 25f);
        torus.transform.scale.Mul(4f);
        MovableObj = cubeR.transform;

        //Quaternion q = new Quaternion(0,10,0);
        //System.out.println(q.toString());
        //System.out.println(q.ToEulerAngles().toString());
        //Quaternion q2 = new Quaternion(10,0,0);
        //System.out.println(q2.toString());
        //System.out.println(q2.ToEulerAngles().toString());
        //Quaternion q3 = Quaternion.Mul(q,q2);
        //System.out.println(q3.toString());
        //System.out.println(q3.ToEulerAngles().toString());

        /*for (int i = 0; i < 20; i++) {
            q3 = Quaternion.Mul(q3, q2);
            System.out.println(q3.toString());
            System.out.println(q3.ToEulerAngles().toString());
        }*/

        //Camera.main.SetClearMode(Camera.ClearMode.Gradient);
        //Camera.main.gradientColor = new Color(.2f,.2f,.2f,1f);

        //GameObject hundredThousandPoly = Instantiate("HighPoly");
        //MeshRenderer htpRend = hundredThousandPoly.AddComponent(MeshRenderer.class);
        //htpRend.SetMesh(Utility.LoadMesh("Assets/MediumPoly.obj"));
        //htpRend.wireframeColor = Color.YELLOW;
        //htpRend.currentColor = Color.LIGHTGOLDENRODYELLOW;
        //hundredThousandPoly.transform.Translate(new Vector3(2f,3f,10f));

        //MovableObj = hundredThousandPoly.transform;

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16.0), e -> EngineUpdate()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void Start() {
        lastMousePosition = new Vector2((float)scene.getX(), (float)scene.getY());

        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());

        scene.setOnKeyPressed(this::HandleTapInput);
        scene.setOnKeyReleased(this::HandleReleaseInput);
        scene.setOnMousePressed(this::HandleMouseTap);
        scene.setOnMouseMoved(this::HandleMouseMove);
    }

    //region Engine -> User_Input
    private void HandleTapInput(KeyEvent e) {
        pressedKeys.add(e.getCode());
        switch (currentAction) {
            case None: {
                if (NoneStateInput(e)) break;
                break;
            }
            case FreeCam:
            {
                if (SwitchDrawMode(e)) break;
                if (e.getCode() == KeyCode.ESCAPE) {
                    currentAction = CurrentAction.None;
                    currentAxis = CurrentAxis.All;
                }
                break;
            }
            case Rotating:
            case Scaling:
            case Dragging:
            {
                if (SwitchAction(e)) break;
                if (SwitchAxis(e)) break;
                if (SwitchDrawMode(e)) break;
                switch (e.getCode()) {
                    case TAB:
                    {
                        if (currentMode == CurrentMode.Viewport) currentMode = CurrentMode.Edit;
                        else currentMode = CurrentMode.Viewport;
                        currentAxis = CurrentAxis.All;
                        break;
                    }
                    case ESCAPE:
                    {
                        currentAxis = CurrentAxis.All;
                        currentAction = CurrentAction.None;
                    }
                }
                break;
            }
        }
    }

    private void HandleHoldInput() {
        if (pressedKeys.contains(KeyCode.RIGHT)) {
            Camera.main.transform.Rotate(0f,1f,0f);
        }
        if (pressedKeys.contains(KeyCode.UP)) {
            Camera.main.transform.Rotate(1f,0f,0f);
        }
        if (pressedKeys.contains(KeyCode.LEFT)) {
            Camera.main.transform.Rotate(0f,0f,1f);
        }
        if (currentAction == CurrentAction.FreeCam) {
            if (FreeCamInput()) return;
        }
    }

    private void HandleReleaseInput(KeyEvent e) {
        pressedKeys.removeIf(k -> k == e.getCode());//.remove(e.getCode());
    }

    private void HandleMouseTap(MouseEvent e) {
        switch (currentAction) {
            case None:
            case FreeCam:
            {
                if (e.isSecondaryButtonDown()) {
                    if (TrySelect(e.isShiftDown())) break;
                }
                break;
            }
            case Rotating:
            case Scaling:
            case Dragging:
            {
                Selection.ApplyAction();
                currentAction = CurrentAction.None;
                break;
            }
        }
    }

    private void HandleMouseMove(MouseEvent e) {
        Vector2 currentMousePos = new Vector2((float)e.getSceneX(), (float)e.getSceneY());
        if (currentAction == CurrentAction.FreeCam) {
            Vector2 deltaMousePos = Vector2.Sub(currentMousePos, lastMousePosition);
            //float cRX = Camera.main.transform.Rotation().x, cRY = Camera.main.transform.Rotation().y;
            //float rotZ = (Mathf.Sin(cRX * deltaMousePos.x) - Mathf.Cos(cRY * deltaMousePos.y));
            //(Camera.main.transform.Rotation().x - Camera.main.transform.Rotation().y)/(0.01f+Camera.main.transform.Rotation().x+Camera.main.transform.Rotation().y)*(deltaMousePos.y-deltaMousePos.x);
            //Camera.main.transform.Rotation().z = (cRX*cRY + cRY*cRX) / ((cRY+cRX) * (cRY+cRX));
            Camera.main.transform.Rotate(new Vector3(/*deltaMousePos.y*0.2f*/0f, deltaMousePos.x * 0.2f, 0f));
        }
        lastMousePosition.x = currentMousePos.x; lastMousePosition.y = currentMousePos.y;
    }

    private boolean SwitchAxis(KeyEvent e) {
        switch (e.getCode()) {
            case X: {
                if (e.isShiftDown()) {
                    if (currentAxis == CurrentAxis.YZ) currentAxis = CurrentAxis.All;
                    else currentAxis = CurrentAxis.YZ;
                    return true;
                }
                if (currentAxis == CurrentAxis.X) currentAxis = CurrentAxis.All;
                else currentAxis = CurrentAxis.X;
                return true;
            }
            case Y: {
                if (e.isShiftDown()) {
                    if (currentAxis == CurrentAxis.XZ) currentAxis = CurrentAxis.All;
                    else currentAxis = CurrentAxis.XZ;
                    return true;
                }
                if (currentAxis == CurrentAxis.Y) currentAxis = CurrentAxis.All;
                else currentAxis = CurrentAxis.Y;
                return true;
            }
            case Z: {
                if (e.isShiftDown()) {
                    if (currentAxis == CurrentAxis.XY) currentAxis = CurrentAxis.All;
                    else currentAxis = CurrentAxis.XY;
                    return true;
                }
                if (currentAxis == CurrentAxis.Z) currentAxis = CurrentAxis.All;
                else currentAxis = CurrentAxis.Z;
                return true;
            }
        }
        return false;
    }

    private boolean NoneStateInput(KeyEvent e) {
        if (SwitchDrawMode(e)) return true;
        if (SwitchAction(e)) return true;
        switch (e.getCode()) {
            case TAB:
            {
                if (currentMode == CurrentMode.Viewport) currentMode = CurrentMode.Edit;
                else currentMode = CurrentMode.Viewport;
                currentAxis = CurrentAxis.All;
                return true;
            }
            case F:
            {
                currentAction = CurrentAction.FreeCam;
                currentAxis = CurrentAxis.All;
                return true;
            }
            case DELETE:
            {
                ShowDeleteWindow();
                return true;
            }
        }
        return false;
    }

    private boolean SwitchDrawMode(KeyEvent e) {
        switch (e.getCode()) {
            case Z:
            {
                currentDrawMode = currentDrawMode == DrawMode.Wireframe ? DrawMode.Solid : DrawMode.Wireframe;
                return true;
            }
            case T:
            {
                currentDrawMode = currentDrawMode == DrawMode.Textured ? DrawMode.Solid : DrawMode.Textured;
                return true;
            }
        }
        return false;
    }

    private boolean SwitchAction(KeyEvent e) {
        if (!selectedObject) return false;
        switch (e.getCode()) {
            case G: {
                return Grab();
            }
            case R: {
                return Rotate();
            }
            case S: {
                return Scale();
            }
        }
        return false;
    }

    private boolean FreeCamInput() {
        boolean found = false;
        float deltaMove = 0.1f;
        Vector3[] dirs = Camera.main.transform.Rotation().AllAxis();
        Vector3 dirX = dirs[0];
        Vector3 dirY = dirs[1];
        Vector3 dirZ = dirs[2];
        if (pressedKeys.contains(KeyCode.SHIFT)) deltaMove *= 0.2f;
        deltaTransform.Set(0,0,0);
        if (pressedKeys.contains(KeyCode.A)) {
            deltaTransform.x -= deltaMove;
            found = true;
        }
        if (pressedKeys.contains(KeyCode.D)) {
            deltaTransform.x += deltaMove;
            found = true;
        }
        if (pressedKeys.contains(KeyCode.W)) {
            deltaTransform.z += deltaMove;
            found = true;
        }
        if (pressedKeys.contains(KeyCode.S)) {
            deltaTransform.z -= deltaMove;
            found = true;
        }
        if (pressedKeys.contains(KeyCode.SPACE)) {
            deltaTransform.y += deltaMove;
            found = true;
        }
        if (pressedKeys.contains(KeyCode.CONTROL)) {
            deltaTransform.y -= deltaMove;
            found = true;
        }
        deltaTransform.Set(
                (dirX.x * deltaTransform.x + dirY.x * deltaTransform.y + dirZ.x * deltaTransform.z),
                (dirX.y * deltaTransform.x + dirY.y * deltaTransform.y + dirZ.y * deltaTransform.z),
                (dirX.z * deltaTransform.x + dirY.z * deltaTransform.y + dirZ.z * deltaTransform.z)
        );
        deltaTransform.Div(3f);
        Camera.main.transform.Translate(deltaTransform);
        return found;
    }

    private boolean TrySelect(boolean isShiftDown) {
        for (MonoBehaviour mb : entities) {
            if (mb.gameObject == Camera.main.gameObject) continue;
            MeshRenderer mr = mb.GetComponent(MeshRenderer.class);
            if (mr == null) {
                Vector3 translatedPos = new Vector3(mb.transform.position).Sub(Camera.main.transform.position);
                Camera.main.transform.GetRotationMatrix().MultiplyMatrixVector(new Vector3(translatedPos), translatedPos);
                Camera.main.GetProjectionMatrix().MultiplyMatrixVector(new Vector3(translatedPos), translatedPos);

                translatedPos.Add(1f,1f,1f);
                translatedPos.Mul(0.5f);
                translatedPos.Mul(new Vector3(Screen.Width(), Screen.Height()));

                translatedPos.y -= Screen.Height(); translatedPos.y *=-1;

                translatedPos.z = 0;

                if (Vector3.SqrDistance(translatedPos, lastMousePosition.ToVector3()) < 81) {
                    if (!isShiftDown) Selection.DeselectAll();
                    Selection.Select(mb.transform);
                    return true;
                }
            }
            else {
                Vector3[] verts = mr.ProjectedVerts();
                //Vector3[] visible = new Vector3[verts.length];
                for (int i = 0; i < verts.length; i+=3) {
                    if (!mr.IsProjected(i) || !mr.IsProjected(i+1) || !mr.IsProjected(i+2)) continue;
                    //else visible[i] = verts[i];
                    /*if (Utility.PointInsideTriangle(verts[i], verts[i+1], verts[i+2], lastMousePosition)) {
                        if (!isShiftDown) Selection.DeselectAll();
                        Selection.Select(mr.transform);
                        return true;
                    }*/
                }
                float[] boundaries = Utility.CalculateBoundaries(verts);
                System.out.println(lastMousePosition.x + " : " + lastMousePosition.y);
                System.out.println(mb.gameObject.name+":\n    "+(int)boundaries[3]+"    \n"+(int)boundaries[0]+"    "+(int)boundaries[1]+"\n    "+(int)boundaries[2]);
                if (lastMousePosition.x > boundaries[0] && lastMousePosition.x < boundaries[1] && lastMousePosition.y > boundaries[2] && lastMousePosition.y < boundaries[3]) {
                    if (!isShiftDown) Selection.DeselectAll();
                    Selection.Select(mr.transform);
                    return true;
                }
            }
        }

        if (!isShiftDown) Selection.DeselectAll();
        return false;
    }
    //endregion

    private void ShowDeleteWindow() {
        
    }

    private void EngineUpdate() {
        //region Core
        Screen.SetScreen((int)canvas.getWidth(), (int)canvas.getHeight());
        Camera.main.transform.GetRotationMatrix().MultiplyMatrixVector(LightDir, LightDirection);
        //endregion

        //region PlayerInput
        HandleHoldInput();
        //endregion

        //region PhysicsUpdate

        //endregion

        //region Update
        //if ((MovableObj.transform.position.y < -4 && delta < 0) || (MovableObj.transform.position.y > 4 && delta > 0)) delta *= -1f;
        //MovableObj.transform.position.y += delta;
        MovableObj.transform.Rotate(new Vector3(0f, x ? 0.5f : 0f, !x ? 0.5f : 0f));
        //counter++;
        if (counter > 45) {counter = -45; x = !x;}
        //endregion

        //region Graphics
        Draw(Camera.main);
        //endregion
    }

    private byte counter = -45;
    private boolean x = true;

    //region Engine -> DRAWING
    public void Draw(Camera cam) {
        if (cam == null)
            if (Camera.main == null) return;
            else cam = Camera.main;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        ClearFlags(cam, gc);

        for (MeshRenderer mr : drawables)
            mr.Draw(cam, currentDrawMode);

        GL.DrawTris(gc);

        gc.setStroke(Color.BLACK);
        gc.setFill(Color.YELLOW);
        Vector3 screenSize = new Vector3(Screen.Width(), Screen.Height());
        for (Transform t : Selection.selectedObjects) {
            Vector3 projected = new Vector3(t.position).Sub(Camera.main.transform.position);
            Camera.main.transform.GetRotationMatrix().MultiplyMatrixVector(new Vector3(projected), projected);
            Camera.main.GetProjectionMatrix().MultiplyMatrixVector(new Vector3(projected), projected);

            projected.Add(1f,1f,1f);
            projected.Mul(0.5f);
            projected.Mul(screenSize);

            projected.y -= Screen.Height(); projected.y *=-1;

            gc.strokeOval(projected.x-2.5, projected.y -2.5, 5, 5);
            gc.fillOval(projected.x-2, projected.y-2, 4, 4);
        }

        gc.setLineWidth(1f);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFont(new Font("Arial",debugTextSize));
        gc.setStroke(Color.BLACK);
        gc.strokeText("Axis: "+currentAxis.name(), 0,debugTextSize);
        gc.strokeText("Mode: "+currentMode.name(), 0, debugTextSize*2);
        gc.strokeText("Action: "+currentAction.name(), 0, debugTextSize*3);
        gc.strokeText("Draw mode: "+currentDrawMode.name(), 0, debugTextSize*4);
        gc.strokeText("MousePosition: "+lastMousePosition.toString(), 0, 100);
        gc.strokeText("CameraPosition: "+Camera.main.transform.position.toTable(), 0, 100+debugTextSize);
        gc.strokeText("CameraRotation: "+Camera.main.transform.rotation.toTable(), 0, 100+debugTextSize*6);
        //gc.strokeText("CameraRotation: "+Camera.main.transform.Rotation().ToEulerAngles().toTable(), 0, 100+debugTextSize*6);
        gc.strokeText("MovableRotation: "+MovableObj.rotation.toTable(), 0, 100+debugTextSize*12);
        gc.strokeText("SelectedObjects:", 0, 100+debugTextSize*17);
        for (int i = 0; i < Selection.selectedObjects.size(); i++) {
            gc.strokeText("    "+Selection.selectedObjects.get(i).gameObject.name, 0, 100+debugTextSize*(18+i));
        }
        //gc.strokeText("MovableLocalRotation: "+MovableObj.LocalRotation().ToEulerAngles().toTable(), 0, 100+debugTextSize*18);
        //gc.strokeText("Right: "+Camera.main.transform.Rotation().AxisX().toTable(), 0, 100+debugTextSize*24);
        //gc.strokeText("Up: "+Camera.main.transform.Rotation().AxisY().toTable(), 0, 100+debugTextSize*30);
        //gc.strokeText("Forward: "+Camera.main.transform.Rotation().AxisZ().toTable(), 0, 100+debugTextSize*36);
    }

    private void ClearFlags(Camera cam, GraphicsContext gc) {
        switch (cam.ClearMode()) {
            case SolidColor:
            {
                gc.setFill(cam.clearColor);
                gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
                break;
            }
            case Gradient:
            {
                // DONT USE!!!
                int stepSize = 32;
                int steps = (int)(canvas.getHeight()/stepSize);
                Color buffered = new Color(cam.clearColor.getRed(), cam.clearColor.getGreen(), cam.clearColor.getBlue(), 1f);
                float[] delta = new float[] {(float)(cam.gradientColor.getRed() - cam.clearColor.getRed())/steps, (float)(cam.gradientColor.getGreen() - cam.clearColor.getGreen())/steps, (float)(cam.gradientColor.getBlue() - cam.clearColor.getBlue())/steps};
                for (int i = 0; i < steps; i++) {
                    gc.setFill(buffered);
                    gc.fillRect(0,stepSize*i, canvas.getWidth(), stepSize*(i+1));
                    buffered = new Color(buffered.getRed()+delta[0], buffered.getGreen()+delta[1], buffered.getBlue()+delta[2],1f);
                }
                break;
            }
        }
    }
    //endregion

    //region Engine -> Affine_Transforms
    private boolean Grab() {
        currentAxis = CurrentAxis.All;
        currentAction = CurrentAction.Dragging;
        return true;
    }

    private boolean Rotate() {
        currentAxis = CurrentAxis.All;
        currentAction = CurrentAction.Rotating;
        return true;
    }

    private boolean Scale() {
        currentAxis = CurrentAxis.All;
        currentAction = CurrentAction.Scaling;
        return true;
    }
    //endregion

    //region STATIC Engine -> Instantiate|Destroy
    public static GameObject Instantiate() {
        return Instantiate("Object");
    }

    public static GameObject Instantiate(String name) {
        return Instantiate(new Vector3(), new Vector3(), new Vector3(1), name);
    }

    public static GameObject Instantiate(Vector3 position) {
        return Instantiate(position, new Vector3());
    }

    public static GameObject Instantiate(Vector3 position, Vector3 rotation) {
        return Instantiate(position, rotation, new Vector3(1));
    }

    public static GameObject Instantiate(Vector3 position, Vector3 rotation, Vector3 scale) {
        return Instantiate(position,rotation,scale,"Object");
    }

    public static GameObject Instantiate(Vector3 position, Vector3 rotation, Vector3 scale, String name) {
        GameObject obj = new GameObject(name);
        obj.transform.position = position; obj.transform.Rotate(rotation, Transform.TransformMode.SET); obj.transform.scale = scale;
        entities.add(obj);
        return obj;
    }

    public static boolean Destroy(Class<MonoBehaviour> instance) {
        return false;
    }

    public static boolean DestroyImmediate(Class<MonoBehaviour> instance) {
        return true;
    }
    //endregion

    //region ENUMS
    private enum CurrentAxis {
        All,
        X,
        Y,
        Z,
        XY,
        XZ,
        YZ,
    }

    private enum CurrentAction {
        None,
        Dragging,
        Scaling,
        Rotating,
        FreeCam,
    }

    private enum CurrentMode {
        Viewport,
        Edit
    }

    public enum DrawMode {
        Solid,
        Wireframe,
        Textured,
    }
    //endregion
}
