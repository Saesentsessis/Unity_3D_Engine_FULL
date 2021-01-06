package Primitives;

import Utility.Vector3;

import java.util.Collection;
import java.util.Vector;

public class Mesh {
    private String meshName;
    public String MeshName() { return this.meshName; }

    public Vector<Vector3> verts;
    public Vector<Triangle> tris;

    public Mesh(String meshName) {
        this.meshName = meshName;
        verts = new Vector<>(8);
        tris = new Vector<>(12);
    }

    public Mesh(Mesh ref) {
        if (ref != null) {
            this.verts = new Vector<>(ref.verts.size());
            for (Vector3 vert : ref.verts)
                this.verts.add(new Vector3(vert));
            this.tris = new Vector<>(ref.tris.size());
            for (Triangle tri : ref.tris)
                this.tris.add(new Triangle(tri, this));
            this.meshName = ref.MeshName();
        }
    }

    public Mesh(String meshName, Collection<Vector3> verts, Collection<Triangle> tris) {
        this.meshName = meshName;
        this.verts = new Vector<>(verts.size());
        this.tris = new Vector<>(tris.size());
        int i = 0;
        for (Vector3 v : verts) {
            this.verts.add(i, v);
            i++;
        }
        i = 0;
        for (Triangle t : tris) {
            this.tris.add(i, t);
            i++;
        }
    }
}
