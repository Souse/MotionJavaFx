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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Angle angle = (Angle) o;

        if (Float.compare(angle.value, value) != 0) return false;
        return angleType == angle.angleType;

    }

    @Override
    public int hashCode() {
        int result = (value != +0.0f ? Float.floatToIntBits(value) : 0);
        result = 31 * result + angleType.hashCode();
        return result;
    }

    public enum AngleType {
        FTOF(1), FTOB(2), BTOW(3);
        private int number;


        AngleType(int number) {
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
