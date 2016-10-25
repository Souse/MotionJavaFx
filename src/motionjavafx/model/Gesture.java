package motionjavafx.model;

import java.util.List;

/**
 * Created by lena on 10/24/16.
 */
public class Gesture {
    private int id;
    private String name;
    private List<HandGesture> handGestures;

    public Gesture() {
    }

    public Gesture(int id, String name, List<HandGesture> handGestures) {
        this.id = id;
        this.name = name;
        this.handGestures = handGestures;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<HandGesture> getHandGestures() {
        return handGestures;
    }

    public void setHandGestures(List<HandGesture> handGestures) {
        this.handGestures = handGestures;
    }
}
