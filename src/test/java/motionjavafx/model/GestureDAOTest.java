package motionjavafx.model;

import javafx.collections.ObservableList;
import motionjavafx.util.DBUtil;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by lena on 11/3/16.
 */
public class GestureDAOTest {

    private Gesture gesture;

    @Before
    public void setUp() throws Exception {
        DBUtil.connStr = "jdbc:sqlite:/home/lena/programming/LeapMotionDB/dbTest";
        DBUtil.dbExecuteUpdate("delete from angle");
        DBUtil.dbExecuteUpdate("delete from handGesture");
        DBUtil.dbExecuteUpdate("delete from gesture");
        final HandGesture handGesture = new HandGesture();
        handGesture.setIsRightHand(true);
        Angle angle = new Angle();
        angle.setAngleType(Angle.AngleType.BTOW);
        angle.setValue(3.123f);
        handGesture.getAngles().add(angle);
        angle = new Angle();
        angle.setAngleType(Angle.AngleType.FTOF);
        angle.setValue(6.3245f);
        handGesture.getAngles().add(angle);
        final HandGesture handGesture2 = new HandGesture();
        handGesture2.setIsRightHand(false);
        angle = new Angle();
        angle.setAngleType(Angle.AngleType.BTOW);
        angle.setValue(5.123f);
        handGesture2.getAngles().add(angle);
        angle = new Angle();
        angle.setAngleType(Angle.AngleType.FTOF);
        angle.setValue(4.3245f);
        handGesture2.getAngles().add(angle);
        gesture = new Gesture(2, "test", Arrays.asList(handGesture,handGesture2));
        GestureDAO.insertGesture(gesture);//, handGesture2)));
    }


    @Test
    public void testGetAllGestures() throws Exception {
        try {
            final ObservableList<Gesture> allGestures = GestureDAO.getAllGestures();
            final Gesture gesture = allGestures.get(0);


            assertEquals(gesture.getHandGestures().size(),2);
            assertArrayEquals(gesture.getHandGestures().toArray(),this.gesture.getHandGestures().toArray());

        } catch (SQLException e) {
            e.getErrorCode();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}