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
    private List<Float> anglesTipToBase = new ArrayList<>();
    private List<Float> anglesTipToTip = new ArrayList<>();
    private float angleProxToMetacarp;

    public HandGesture(List<Vector> fingerTips, List<Vector> fingerBases, Vector Metacarp) {
        this.fingerTips = fingerTips;
        this.fingerBases = fingerBases;

        calcFingerTipToBaseAngles(fingerTips, fingerBases);

        calcFingerTipToTipAngles(fingerTips);

        calcAngleProxToMetacarp(this.fingerBases.get(2), Metacarp);
    }

    private void calcFingerTipToTipAngles(List<Vector> fingerTips) {
        for (int i = 0; i < 5; i++){
            for (int j = i+1;j < 5; j++) {
                this.anglesTipToTip.add(calcAngle(fingerTips.get(i), fingerTips.get(j)));
            }
        }
    }

    private void calcFingerTipToBaseAngles(List<Vector> fingerTips, List<Vector> fingerBases) {
        for (int i = 0; i < 5; i++)
        {
            this.anglesTipToBase.add(calcAngle(fingerTips.get(i), fingerBases.get(i)));
        }
    }

    private float calcAngle(Vector pointA, Vector pointB) {
        return pointA.angleTo(pointB);
    }

    private void calcAngleProxToMetacarp (Vector Prox, Vector Metacarp){
        this.angleProxToMetacarp = calcAngle(Prox, Metacarp);
    }

    public List<Float> getanglesTipToBase() {
        return anglesTipToBase;
    }

    public List<Float> getAnglesTipToTip() {
        return anglesTipToTip;
    }


}
