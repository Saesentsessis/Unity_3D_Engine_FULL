package Engine;

import Utility.Vector3;
import sample.Controller;

import java.util.ArrayList;
import java.util.List;

public class Selection {
    public static ArrayList<Transform> selectedObjects = new ArrayList<>(5);

    private static ArrayList<Vector3> lastPositions = new ArrayList<>(5);
    private static ArrayList<Vector3> lastRotations = new ArrayList<>(5);
    private static ArrayList<Vector3> lastScales = new ArrayList<>(5);

    public static void Select(Transform ref) {
        if (Remove(ref)) { return; }
        selectedObjects.add(ref);
        lastPositions.add(new Vector3(ref.position));
        lastRotations.add(new Vector3(ref.rotation));
        lastScales.add(new Vector3(ref.scale));
    }

    public static boolean Remove(Transform ref) {
        int index = selectedObjects.indexOf(ref);
        if (index < 0) return false;
        lastPositions.remove(index);
        lastRotations.remove(index);
        lastScales.remove(index);
        return selectedObjects.removeIf(t -> t == ref);
    }

    public static void ChangeAction() {
        int i = 0;
        for (Transform t : selectedObjects) {
            t.position.Set(lastPositions.get(i));
            t.rotation.Set(lastRotations.get(i));
            t.scale.Set(lastScales.get(i));
            i++;
        }
    }

    public static void ApplyAction() {
        int i = 0;
        for (Transform t : selectedObjects) {
            lastPositions.get(i).Set(t.position);
            lastRotations.get(i).Set(t.rotation);
            lastScales.get(i).Set(t.scale);
            i++;
        }
    }

    public static void DeselectAll() {
        selectedObjects.clear();
        lastPositions.clear();
        lastRotations.clear();
        lastScales.clear();
    }
}
