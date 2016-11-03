package motionjavafx;

import com.leapmotion.leap.*;
import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import motionjavafx.model.Gesture;
import motionjavafx.model.GestureDAO;
import motionjavafx.model.HandGesture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;
    private Controller controller;
    private SampleListener listener;

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
        primaryStage.setTitle("Molecule Sample Application");
        Scene myScene = new Scene(myPane);
        primaryStage.setScene(myScene);
        ((BorderPane) myPane).setCenter(scene);
        primaryStage.setScene(myScene);
        primaryStage.show();
        gestureNameField = (TextField) myPane.lookup("#gestureNameField");
        errorField = (TextField) myPane.lookup("#errorField");
        scene.setCamera(camera);
        Thread t = new Thread() {
            @Override
            public void run() {
                listener = new SampleListener();

                // Have the sample listener receive events from the controller
                controller.addListener(listener);
            }

        };

        t.start();

        // Remove the sample listener when done
        //controller.removeListener(listener);
        //primaryStage.close();
    }

    public void saveGesture(Event event) {
        controller = new Controller();
        Frame frame = controller.frame();
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
        try {
            GestureDAO.insertGesture(gesture);
        } catch (Exception e) {
            errorField.setVisible(true);
            errorField.setText(e.getMessage());
        }
    }

    class SampleListener extends Listener {
        public void onInit(Controller controller) {
            System.out.println("Initialized");
        }

        public void onConnect(Controller controller) {
            System.out.println("Connected");
        }

        public void onDisconnect(Controller controller) {
            //Note: not dispatched when running in a debugger.
            System.out.println("Disconnected");
        }

        public void onExit(Controller controller) {
            System.out.println("Exited");
        }

        public void onFrame(Controller controller) {
            // Get the most recent frame and report some basic information
            Frame frame = controller.frame();

            //Get hands
            for (Hand hand : frame.hands()) {


                // Get the hand's normal vector and direction
                Vector origin = hand.palmPosition();


                // Get arm bone
                Arm arm = hand.arm();

                // Get fingers
                for (Finger finger : hand.fingers()) {

                    final HandModel handModel;
                    if (hand.isLeft()) {
                        handModel = leftHand;
                    } else {
                        handModel = rightHand;
                    }
                    final FingerModel fingerModel = handModel.getFingerByType(finger.type());
                    final Sphere fingerTip = fingerModel.getFingerTip();
                    final Vector tipPosition = finger.tipPosition();
                    //System.out.println("x: "+tipPosition.getX() + " y: "+tipPosition.getY());
                    moveSphereToVector(fingerTip, tipPosition);
                    //Get Bones
                    for (Bone.Type boneType : Bone.Type.values()) {
                        Bone bone = finger.bone(boneType);
                        final Sphere boneSphere = fingerModel.getBoneByType(bone.type());
                        moveSphereToVector(boneSphere, bone.center());
                    }
                }
            }
            try {
                Thread.sleep(50); //60 FPS
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }

        private void moveSphereToVector(Sphere fingerTip, Vector tipPosition) {
            final Translate translate = calcTranslation(fingerTip, tipPosition);
            /*if (translate.determinant() < 1){
                return;
            } */
            Platform.runLater(() -> {
                fingerTip.getTransforms().add(translate);
            });
            fingerTip.setTranslateX(fingerTip.getTranslateX() + translate.getTx());
            fingerTip.setTranslateY(fingerTip.getTranslateY() + translate.getTy());
            fingerTip.setTranslateZ(fingerTip.getTranslateZ() + translate.getTz());
        }

        private Translate calcTranslation(Sphere fingerSphere, Vector tipPosition) {
            final double deltaX = tipPosition.getX() / 5 - fingerSphere.getTranslateX();
            final double deltaY = tipPosition.getY() / 5 - fingerSphere.getTranslateY();
            final double deltaZ = tipPosition.getZ() / 5 - fingerSphere.getTranslateZ();
            return new Translate(deltaX, deltaY, deltaZ);
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