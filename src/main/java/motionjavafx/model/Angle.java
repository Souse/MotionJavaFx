package motionjavafx.model;

/**
 * Created by lena on 10/25/16.
 */
public class Angle {
    private float value;
    private AngleType angleType;

    public Angle() {
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public AngleType getAngleType() {
        return angleType;
    }

    public void setAngleType(AngleType angleType) {
        this.angleType = angleType;
    }

    public static enum AngleType {
        FTOF(1), FTOB(2), BTOW(3);
        private int number;


        private AngleType(int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }

        public static AngleType fromInt(int anInt) {
            for (AngleType angleType : AngleType.values()) {
                if (angleType.getNumber() == anInt) {
                    return angleType;
                }
            }

            throw new IllegalStateException("Invalid value for angleType: "+anInt);
        }
    }

}
