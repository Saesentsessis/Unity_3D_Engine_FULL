package Engine;

import java.util.ArrayList;
import java.util.List;

public abstract class MonoBehaviour {
    private List<Object> classes = new ArrayList<>();
    /** Core component of current entity */
    public GameObject gameObject;
    /** Transform component of current entity */
    public Transform transform;

    public <T> T AddComponent(Class<T> type) {
        for (java.lang.Object cl:classes) {
            if (!cl.getClass().getTypeName().equals(type.getTypeName())) continue;
            if (type.getTypeName().equals(Transform.class.getTypeName())) {System.out.println("Can't add another Transform component on this Entity"); return null;}
            if (type.getTypeName().equals(Object.class.getTypeName())) {System.out.println("Can't add another Object component on this Entity"); return null;}
        }
        try {
            java.lang.Object o = type.newInstance();
            classes.add(o);
            MonoBehaviour m = (MonoBehaviour) o;
            m.gameObject = gameObject;
            m.transform = transform;
            return (T) o;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /** Get class of given type attached to this entity. If no such class attached - return null */
    public <T> T GetComponent(Class<T> type) {
        for (java.lang.Object c : classes) {
            if (c.getClass().getTypeName().equals(type.getTypeName())) return (T) c;
            //System.out.println("Found!");
        }
        //System.out.println("Not found!");
        return null;
    }

    public <T> boolean RemoveComponent(T ref) {
        return false;
    }

    public void Update() {

    }
}