package motionjavafx;

import com.leapmotion.leap.*;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.stage.Stage;
import motionjavafx.model.Angle;
import motionjavafx.model.Gesture;
import motionjavafx.model.GestureDAO;
import motionjavafx.model.HandGesture;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MotionJavaFx extends Application {

    final Group root = new Group();
    final Xform axisGroup = new Xform();
    final Xform world = new Xform();
    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final Xform cameraXform = new Xform();
    final Xform cameraXform2 = new Xform();
    final Xform cameraXform3 = new Xform();
    final HandModel leftHand = new HandModel();
    final HandModel rightHand = new HandModel();
    private static final double CAMERA_INITIAL_DISTANCE = -500;
    private static final double CAMERA_INITIAL_X_ANGLE = 70.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;
    private static final double AXIS_LENGTH = 250.0;
    private static final double CONTROL_MULTIPLIER = 0.1;
    private static final double SHIFT_MULTIPLIER = 10.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 2.0;
    private static final double TRACK_SPEED = 0.3;
    public TextField gestureNameField;
    public TextField errorField;
    public TextField outputField;
    public TextField confidentialityField;

    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;
    private Controller controller;
    private UserInterfaceListener listener;
    private ObservableList<Gesture> allGestures;

    private void buildCamera() {
        root.getChildren().add(cameraXform);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(180.0);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
    }

    private void buildAxes() {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        final Box xAxis = new Box(AXIS_LENGTH, 1, 1);
        final Box yAxis = new Box(1, AXIS_LENGTH, 1);
        final Box zAxis = new Box(1, 1, AXIS_LENGTH);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        axisGroup.setVisible(false);
        world.getChildren().addAll(axisGroup);
    }/*
 * Copyright (c) 2013, 2014 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

//
// The handleMouse() method is used in the MotionJavaFx application to
// handle the different 3D camera views.
// This method is used in the Getting Started with JavaFX 3D Graphics tutorial.
//

    private void handleMouse(SubScene scene) {

        scene.setOnMousePressed(me -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });
        scene.setOnMouseDragged(me -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseDeltaX = (mousePosX - mouseOldX);
            mouseDeltaY = (mousePosY - mouseOldY);
            double modifierFactor = 1.0;

            double modifier = 1.0;

            if (me.isControlDown()) {
                modifier = CONTROL_MULTIPLIER;
            }
            if (me.isShiftDown()) {
                modifier = SHIFT_MULTIPLIER;
            }
            if (me.isPrimaryButtonDown()) {
                cameraXform.ry.setAngle(cameraXform.ry.getAngle() -
                        mouseDeltaX * modifierFactor * modifier * ROTATION_SPEED);  //
                cameraXform.rx.setAngle(cameraXform.rx.getAngle() +
                        mouseDeltaY * modifierFactor * modifier * ROTATION_SPEED);  // -
            } else if (me.isMiddleButtonDown()) {
                cameraXform2.t.setX(cameraXform2.t.getX() +
                        mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED);  // -
                cameraXform2.t.setY(cameraXform2.t.getY() +
                        mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);  // -
            }
        }); // setOnMouseDragged
        scene.setOnScroll(se -> {
            double modifier = 1.0;
            if (se.isControlDown()) {
                modifier = CONTROL_MULTIPLIER;
            }
            if (se.isShiftDown()) {
                modifier = SHIFT_MULTIPLIER;
            }
            double z = camera.getTranslateZ();
            double newZ = z + MOUSE_SPEED * se.getDeltaY() * modifier;
            camera.setTranslateZ(newZ);
        });
    } //handleMouse

    //
// The handleKeyboard() method is used in the MotionJavaFx application to
// handle the different 3D camera views.
// This method is used in the Getting Started with JavaFX 3D Graphics tutorial.
//

    private void handleKeyboard(SubScene scene) {

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case Z:
                    cameraXform2.t.setX(0.0);
                    cameraXform2.t.setY(0.0);
                    cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
                    cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
                    break;
                case X:
                    axisGroup.setVisible(!axisGroup.isVisible());
                    break;
            } // switch
        });  // setOnKeyPressed
    }  //  handleKeyboard()

    @Override
    public void start(Stage primaryStage) {
        buildCamera();
        buildAxes();
        //buildMolecule();
        controller = new Controller();
        Pane myPane = null;
        try {
            myPane = FXMLLoader.load(getClass().getResource
                    ("sample.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        world.getChildren().add(leftHand);
        world.getChildren().add(rightHand);
        // Create a Box
        root.getChildren().add(world);
        SubScene scene = new SubScene(root, 1024, 768);
        scene.setFill(Color.GREY);
        handleKeyboard(scene);
        handleMouse(scene);
        primaryStage.setTitle("LeapMotion Zeichensprache");
        Scene myScene = new Scene(myPane);
        primaryStage.setScene(myScene);
        ((BorderPane) myPane).setCenter(scene);
        primaryStage.setScene(myScene);
        primaryStage.show();
        gestureNameField = (TextField) myPane.lookup("#gestureNameField");
        errorField = (TextField) myPane.lookup("#errorField");
        confidentialityField = (TextField) myPane.lookup("#confidentialityField");
        outputField = (TextField) myPane.lookup("#outputField");
        scene.setCamera(camera);
        Thread t = new Thread() {
            @Override
            public void run() {
                listener = new UserInterfaceListener(leftHand, rightHand);

                // Have the sample listener receive events from the controller
                controller.addListener(listener);
            }

        };
        t.start();
        try {

            allGestures = GestureDAO.getAllGestures();
        } catch (Exception e) {
            throw new IllegalStateException("Error while retrieving Data from DB, " + e);
        }

        Thread worker = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new IllegalStateException("Error while sleeping: " + e);
                    }
                    //controller = new Controller();
                    Frame frame = controller.frame();
                    Gesture gesture = getGestureFromFrame(frame);
                    gesture.getHandGestures();
                    Map<Gesture, Double> confidentialityMap = new HashMap<>();
                    try {

                        for (Gesture gestureFromDb : allGestures) {
                            confidentialityMap.put(gestureFromDb, calcConfidentiality(gesture, gestureFromDb));
                        }
                        final Map.Entry<Gesture, Double> gestureDoubleEntry = confidentialityMap.entrySet().stream().max((o1, o2) -> o1.getValue().compareTo(o2.getValue())).get();
                        outputField.setText(gestureDoubleEntry.getKey().getName());
                        confidentialityField.setText(gestureDoubleEntry.getValue()+"");
                    } catch (Exception e) {
                        throw new IllegalStateException("Error while retrieving Data from DB, " + e);
                    }
                }
            }

            private Double calcConfidentiality(Gesture gesture, Gesture gestureFromDb) {
                double totalFailure = 0;
                for (HandGesture handGesture : gesture.getHandGestures()) {
                    final boolean rightHand = handGesture.isRightHand();
                    final List<HandGesture> matchingHandGestures = gestureFromDb.getHandGestures().stream().
                            filter(handGesture1 -> handGesture1.isRightHand() == rightHand)
                            .collect(Collectors.toList());
                    double minFailure = Double.MAX_VALUE;
                    for (HandGesture matchingHandGesture : matchingHandGestures) {
                        double failures = calcFailure(handGesture,matchingHandGesture);
                        if (failures < minFailure) {
                            minFailure = failures;
                        }
                    }
                    totalFailure+=minFailure;
                }

                return 100-totalFailure;
            }

            private double calcFailure(HandGesture handGesture, HandGesture matchingHandGesture) {
                double failures = 0;

                for (int i = 0; i < handGesture.getAngles().size(); i++) {
                    Angle a = handGesture.getAngles().get(i);
                    Angle b = matchingHandGesture.getAngles().get(i);
                    failures += Math.pow(a.getValue() - b.getValue(), 2);
                }

                return failures;
            }

        };
        worker.start();


        // Remove the sample listener when done
        //controller.removeListener(listener);
        //primaryStage.close();
    }

    private Gesture getGestureFromFrame(Frame frame) {
        Gesture gesture = new Gesture();
        gesture.setName(gestureNameField.getText());
        for (Hand hand : frame.hands()) {
            List<Vector> fingerTips = new ArrayList<Vector>();
            List<Vector> fingerBases = new ArrayList<Vector>();
            for (Finger finger : hand.fingers()) {
                fingerTips.add(finger.tipPosition());
                fingerBases.add(finger.bone(Bone.Type.TYPE_PROXIMAL).center());
            }
            HandGesture handGesture = new HandGesture(fingerTips, fingerBases, hand.arm().wristPosition(), hand.isRight());
            gesture.getHandGestures().add(handGesture);
        }
        return gesture;
    }

    public void saveGesture(Event event) {
        controller = new Controller();
        Frame frame = controller.frame();
        Gesture gesture = getGestureFromFrame(frame);
        try {
            GestureDAO.insertGesture(gesture);
        } catch (Exception e) {
            errorField.setVisible(true);
            errorField.setText(e.getMessage());
        }
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX
     * application. main() serves only as fallback in case the
     * application can not be launched through deployment artifacts,
     * e.g., in IDEs with limited FX support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}