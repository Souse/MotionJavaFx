package motionjavafx.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import motionjavafx.util.DBUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lena on 10/24/16.
 */
public class GestureDAO {

    public static ObservableList<Gesture> getAllGestures() throws SQLException, ClassNotFoundException {
        //Declare a SELECT statement
        String selectStmt = "SELECT g.name, g.id, a.value, a.angletype, hg.IsRightHand, hg.id FROM Gesture g, Angle a, HandGesture hg where g.id = hg.gestureid and hg.id = a.handgestureid";

        //Execute SELECT statement
        try {
            //Get ResultSet from dbExecuteQuery method
            ResultSet resultSetGestures = DBUtil.dbExecuteQuery(selectStmt);

            //Send ResultSet to the getGestureList method and get Gesture object
            ObservableList<Gesture> gestureList = getGestureList(resultSetGestures);

            //Return Gesture object
            return gestureList;
        } catch (SQLException e) {
            System.out.println("SQL select operation has been failed: " + e);
            //Return exception
            throw e;
        }
    }

    //Select * from Gestures operation
    private static ObservableList<Gesture> getGestureList(ResultSet rs) throws SQLException, ClassNotFoundException {
        //Declare a observable List which comprises of Gesture objects
        ObservableList<Gesture> gestureList = FXCollections.observableArrayList();
        Map<String, Gesture> gestureMap = new HashMap<>();
        Map<Integer, HandGesture> handGestureMap = new HashMap<>();

        while (rs.next()) {
            String name = rs.getString(1);
            if (!gestureMap.containsKey(name)) {
                Gesture gesture = new Gesture();
                gesture.setId(rs.getInt(2));
                gesture.setName(name);
                gestureList.add(gesture);
                gestureMap.put(name,gesture);
            }
            Gesture gesture = gestureMap.get(name);
            final int hgId = rs.getInt(6);
            if (!handGestureMap.containsKey(hgId)) {
                HandGesture handGesture = new HandGesture();
                handGesture.setId(hgId);
                handGesture.setIsRightHand(rs.getBoolean(5));
                handGestureMap.put(hgId,handGesture);
            }
            HandGesture handGesture = handGestureMap.get(hgId);
            if (!gesture.getHandGestures().contains(handGesture)){
                gesture.getHandGestures().add(handGesture);
            }
            Angle angle = new Angle();
            angle.setValue(rs.getFloat(3));
            angle.setAngleType(Angle.AngleType.fromInt(rs.getInt(4)));
            handGesture.getAngles().add(angle);

        }
        //return gestureList (ObservableList of Gestures)
        return gestureList;
    }

    public static void main(String[] args) {
        try {
            setup();
            final ObservableList<Gesture> allGestures = getAllGestures();
            final Gesture gesture = allGestures.get(0);
            if (gesture.getHandGestures().size()!= 2)  {
                System.out.println("ERROR missing handgesture");
            } else {
                final HandGesture handGesture1 = gesture.getHandGestures().get(0);
                if (handGesture1.getAngles().size()!=2)  {
                    System.out.println( "ERROR missing angle in hg 1");
                }
                final HandGesture handGesture2 = gesture.getHandGestures().get(1);
                if (handGesture2.getAngles().size()!=2)  {
                    System.out.println( "ERROR missing angle in hg 2");
                }
            }

        } catch (SQLException e) {
            e.getErrorCode();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void setup() throws SQLException, ClassNotFoundException {
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
        insertGesture(new Gesture(2, "test", Arrays.asList(handGesture, handGesture2)));
    }

    //*************************************
    //INSERT an Gesture
    //*************************************
    public static void insertGesture(Gesture gesture) throws SQLException, ClassNotFoundException {
        //Declare a DELETE statement

        final int maxGestureId = getMaxidFromTable("gesture");
        int maxHandGestureId = getMaxidFromTable("handgesture");
        int maxAngleId = getMaxidFromTable("angle");
        final String insertStatement = "INSERT INTO gesture (id,name) values (?,?)";
        final String insert = "INSERT INTO handgesture (id,gestureid,isrighthand) values (?,?,?)";
        final String insertangle = "INSERT INTO angle (angletype,handgestureid,id,value) values (?,?,?,?)";

        final PreparedStatement preparedStatement = DBUtil.createPreparedStatement(insertStatement);
        preparedStatement.setInt(1, maxGestureId);
        preparedStatement.setString(2, gesture.getName());
        DBUtil.executePreparedStatementUpdate(preparedStatement);


        for (HandGesture handGesture : gesture.getHandGestures()) {
            final PreparedStatement preparedStatement2 = DBUtil.createPreparedStatement(insert);
            preparedStatement2.setInt(1, maxHandGestureId++);
            preparedStatement2.setInt(2, maxGestureId);
            preparedStatement2.setShort(3, (short) (handGesture.isRightHand() ? 1 : 0));
            DBUtil.executePreparedStatementUpdate(preparedStatement2);

            for (Angle angle : handGesture.getAngles()) {
                final PreparedStatement preparedStatement3 = DBUtil.createPreparedStatement(insertangle);
                preparedStatement3.setInt(1, angle.getAngleType().getNumber());
                preparedStatement3.setInt(2, maxHandGestureId - 1);
                preparedStatement3.setInt(3, maxAngleId++);
                preparedStatement3.setFloat(4, angle.getValue());
                DBUtil.executePreparedStatementUpdate(preparedStatement3);
            }

        }


    }

    private static int getMaxidFromTable(String tablename) throws SQLException, ClassNotFoundException {
        final ResultSet resultSet = DBUtil.dbExecuteQuery("select ifnull(max(id),0) from " + tablename);
        if (resultSet.next()) {
            return resultSet.getInt(1) + 1;
        }
        throw new IllegalStateException("Error when executing query");
    }
}
