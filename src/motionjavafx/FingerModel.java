package motionjavafx;

import com.leapmotion.leap.Bone;
import javafx.scene.Group;
import javafx.scene.paint.Material;
import javafx.scene.shape.Sphere;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Howl on 10/8/2016.
 */
public class FingerModel extends Group {
    private Map<Bone.Type,Sphere> bones = new HashMap<>();
    private Sphere fingerTip = new Sphere(5);

    public FingerModel() {
        for (Bone.Type type : Bone.Type.values()) {
            final Sphere sphere = new Sphere(5);
            this.getChildren().add(sphere);
            bones.put(type, sphere);
        }
        this.getChildren().add(fingerTip);
    }

    public Sphere getFingerTip() {
        return fingerTip;
    }

    public Sphere getBoneByType(Bone.Type type) {
        return bones.get(type);
    }

    public void setFingerTipMaterial(Material material) {
        fingerTip.setMaterial(material);
    }
}
