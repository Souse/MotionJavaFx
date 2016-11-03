package motionjavafx.model;

import com.leapmotion.leap.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lena on 10/23/16.
 */
public class HandGesture {

    private List<Vector> fingerTips = new ArrayList<>();
    private List<Vector> fingerBases = new ArrayList <>();
    private List<Angle> angles = new ArrayList<>();
    private boolean rightHand = false;
    private int id;

    public HandGesture() {
    }

    public HandGesture(List<Vector> fingerTips, List<Vector> fingerBases, Vector wrist, boolean rightHand) {
        this.fingerTips = fingerTips;
        this.fingerBases = fingerBases;
        this.rightHand = rightHand;


        calcFingerTipToBaseAngles(fingerTips, fingerBases);

        calcFingerTipToTipAngles(fingerTips);

        calcAngleProxToMetacarp(this.fingerBases.get(2), wrist);
    }

    private void calcFingerTipToTipAngles(List<Vector> fingerTips) {
        for (int i = 0; i < 5; i++){
            for (int j = i+1;j < 5; j++) {
                final float angleValue = calcAngle(fingerTips.get(i), fingerTips.get(j));
                Angle angle = new Angle();
                angle.setAngleType(Angle.AngleType.FTOF);
                angle.setValue(angleValue);
                this.angles.add(angle);
            }
        }
    }

    private void calcFingerTipToBaseAngles(List<Vector> fingerTips, List<Vector> fingerBases) {
        for (int i = 0; i < 5; i++)
        {
            final float angleValue = calcAngle(fingerTips.get(i), fingerBases.get(i));
            Angle angle = new Angle();
            angle.setAngleType(Angle.AngleType.FTOB);
            angle.setValue(angleValue);
            this.angles.add(angle);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private float calcAngle(Vector pointA, Vector pointB) {
        return pointA.angleTo(pointB);
    }

    public boolean isRightHand() {
        return rightHand;
    }

    public void setIsRightHand(boolean rightHand) {
        this.rightHand = rightHand;
    }

    private void calcAngleProxToMetacarp (Vector prox, Vector wrist){
        final float angleValue = calcAngle(prox, wrist);
        Angle angle = new Angle();
        angle.setAngleType(Angle.AngleType.BTOW);
        angle.setValue(angleValue);
        this.angles.add(angle);
    }


    public List<Angle> getAngles() {
        return angles;
    }


}
