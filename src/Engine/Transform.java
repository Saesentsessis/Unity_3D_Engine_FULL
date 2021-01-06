package Engine;

import Utility.Mathf;
import Utility.Matrix4x4;
import Utility.Quaternion;
import Utility.Vector3;

public class Transform extends MonoBehaviour {
    /** Object position in world space */
    public Vector3 position = new Vector3(0);
    /** Object rotation in world space */
    public Vector3 rotation = new Vector3(0);
    private Quaternion rotationQ;
    public final Quaternion Rotation() { return this.rotationQ; }
    /** Object rotation in local space */
    //private Quaternion localRotation;
    //public final Quaternion LocalRotation() { return this.localRotation; }
    /** Object scale in world space */
    public Vector3 scale = new Vector3(1);

    private Matrix4x4 rotationXMatrix = new Matrix4x4();
    private Matrix4x4 rotationYMatrix = new Matrix4x4();
    private Matrix4x4 rotationZMatrix = new Matrix4x4();
    private Matrix4x4 rotationMatrix = new Matrix4x4();
    public Matrix4x4 GetRotationMatrix() { return rotationMatrix; }
    public Matrix4x4 GetXRotationMatrix() { return rotationXMatrix; }
    public Matrix4x4 GetYRotationMatrix() { return rotationYMatrix; }
    public Matrix4x4 GetZRotationMatrix() { return rotationZMatrix; }

    public Transform() {
        transform = this;
        rotationQ = new Quaternion(rotation.x, rotation.y, rotation.z);
        //rotation = new Quaternion(0,0,0,1);
        //localRotation = new Quaternion(0,0,0,1);
        RecalculateRotationMatrix();
    }

    /** Translates object on given Vector values */
    public void Translate(Vector3 v) {
        position.Add(v);
    }

    /** Translates object on given x, y and z values */
    public void Translate(float x, float y, float z) {
        position.Add(x,y,z);
    }

    public void Translate(Vector3 v, Space tSpace) {
        Translate(v, tSpace, TransformMode.ADD);
    }

    public void Translate(Vector3 v, TransformMode tMode) {
        Translate(v, Space.WORLD, tMode);
    }

    public void Translate(Vector3 v, Space tSpace, TransformMode tMode) {
        switch (tMode) {
            case ADD: {
                switch (tSpace) {
                    case LOCAL: {

                    }
                    case WORLD: {
                        this.position.Add(v);
                    }
                }
            }
            case SET: {
                switch (tSpace) {
                    case LOCAL: {

                    }
                    case WORLD: {
                        this.position.Set(v);
                    }
                }
            }
        }
    }

    public void Rotate(Vector3 r) {
        Rotate(r, Space.WORLD);
    }

    public void Rotate(float x, float y) { Rotate(x,y,0); }

    public void Rotate(float x, float y, float z) {
        Rotate(new Vector3(x,y,z), TransformMode.ADD);
    }

    public void Rotate(float xAngle, float yAngle, float zAngle, Space relativeTo)
    {
        Rotate(new Vector3(xAngle, yAngle, zAngle), relativeTo);
    }

    public void Rotate(Vector3 r, Space rSpace) {
        Rotate(r, TransformMode.ADD, rSpace);
    }

    public void Rotate(Vector3 r, TransformMode rMode) {
        Rotate(r, rMode, Space.WORLD);
    }

    public void Rotate(Vector3 r, TransformMode rMode, Space rSpace) {
        //Quaternion eulerRot = new Quaternion(r.x, r.y, r.z);
        switch (rMode) {
            case ADD: {
                switch (rSpace) {
                    case LOCAL: {
                        //rotation.Mul(eulerRot);
                        break;
                    }
                    case WORLD: {
                        rotation.Add(r);
                        rotationQ = new Quaternion(rotation.x, rotation.y, rotation.z);
                        FixRotation();
                        //rotation.Mul(Quaternion.Inverse(rotation).Mul(eulerRot).Mul(rotation));
                        break;
                    }
                }
                break;
            }
            case SET: {
                switch (rSpace) {
                    case LOCAL: {
                        break;
                    }
                    case WORLD: {
                        //this.rotation = new Quaternion(r.x,r.y,r.z);
                        this.rotation.Set(r);
                        FixRotation();
                        break;
                    }
                }
                break;
            }
        }
        RecalculateRotationMatrix();
        //rotationMatrix.CalculateRotationMatrix(this);
    }

    private void FixRotation() {
        if (this.rotation.x > 360) this.rotation.x -= 360; else if (this.rotation.x < 0) this.rotation.x += 360;
        if (this.rotation.y > 360) this.rotation.y -= 360; else if (this.rotation.y < 0) this.rotation.y += 360;
        if (this.rotation.z > 360) this.rotation.z -= 360; else if (this.rotation.z < 0) this.rotation.z += 360;
    }

    private void RecalculateRotationMatrix() {
        Vector3 eulerRotation = rotation;//.ToEulerAngles();
        rotationXMatrix.CalculateXRotationMatrix(eulerRotation.x);
        rotationYMatrix.CalculateYRotationMatrix(eulerRotation.y);
        rotationZMatrix.CalculateZRotationMatrix(eulerRotation.z);
        rotationMatrix = Matrix4x4.MultiplyMatrices(Matrix4x4.MultiplyMatrices(rotationZMatrix, rotationXMatrix), rotationYMatrix);
    }

    /** LocalRotation mode used in some methods.<p> ADD - add given rotation to current.<p> SET - set current rotation to given. */
    public enum TransformMode {
        ADD,
        SET
    }

    /** LocalRotation space declares in which space rotation applies.<p> WORLD - rotate in global world space.<p> LOCAL - rotate in local parent object space */
    public enum Space {
        LOCAL,
        WORLD,
    }
}
