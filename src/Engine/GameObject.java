package Engine;

public class GameObject extends MonoBehaviour {
    public String name;

    public GameObject(String name) {
        this.name = name;
        gameObject = this;
        transform = new Transform();
        transform.gameObject = this;
    }
}
