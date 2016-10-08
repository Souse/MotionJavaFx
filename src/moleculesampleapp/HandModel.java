package moleculesampleapp;

import com.leapmotion.leap.Finger;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Howl on 10/8/2016.
 */
public class HandModel extends Group {
    private Map<Finger.Type,FingerModel> fingers = new HashMap<>();
    private Map<Finger.Type,Material> fingerMaterials = new HashMap<>();

    public HandModel() {
        for (Finger.Type type : Finger.Type.values()) {
            final FingerModel fingerModel = new FingerModel();
            this.getChildren().add(fingerModel);
            fingers.put(type, fingerModel);
        }
        initMaterials();
        for (Map.Entry<Finger.Type, FingerModel> fingerModelEntry : fingers.entrySet()) {
            final Finger.Type fingerType = fingerModelEntry.getKey();
            final FingerModel fingerModel = fingerModelEntry.getValue();
            fingerModel.setFingerTipMaterial(fingerMaterials.get(fingerType));
        }

    }

    public FingerModel getFingerByType(Finger.Type type) {
        return fingers.get(type);
    }

    private void initMaterials() {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial yellowMaterial = new PhongMaterial();
        yellowMaterial.setDiffuseColor(Color.YELLOW);
        yellowMaterial.setSpecularColor(Color.LIGHTYELLOW);

        final PhongMaterial skyblueMaterial = new PhongMaterial();
        skyblueMaterial.setDiffuseColor(Color.SKYBLUE);
        skyblueMaterial.setSpecularColor(Color.LIGHTSKYBLUE);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        fingerMaterials.put(Finger.Type.TYPE_THUMB,redMaterial);
        fingerMaterials.put(Finger.Type.TYPE_INDEX,greenMaterial);
        fingerMaterials.put(Finger.Type.TYPE_MIDDLE,blueMaterial);
        fingerMaterials.put(Finger.Type.TYPE_RING,yellowMaterial);
        fingerMaterials.put(Finger.Type.TYPE_PINKY,skyblueMaterial);
    }
}
