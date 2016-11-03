package motionjavafx;

import com.leapmotion.leap.*;
import javafx.application.Platform;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;

/**
 * Created by lena on 11/3/16.
 */
class UserInterfaceListener extends Listener {

    private HandModel leftHand, rightHand;

    public UserInterfaceListener(HandModel leftHand, HandModel rightHand) {
        this.leftHand = leftHand;
        this.rightHand = rightHand;
    }

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
