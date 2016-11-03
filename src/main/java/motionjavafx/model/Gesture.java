package motionjavafx.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lena on 10/24/16.
 */
public class Gesture {
    private int id;
    private String name;
    private List<HandGesture> handGestures;

    public Gesture() {
        handGestures = new ArrayList<>();
    }

    public Gesture(int id, String name, List<HandGesture> handGestures) {
        this.id = id;
        this.name = name;
        this.handGestures = handGestures;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Gesture gesture = (Gesture) o;

        return name.equals(gesture.name);
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
