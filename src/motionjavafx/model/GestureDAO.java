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
        String selectStmt = "SELECT g.name, g.id, a.value, a.angletype, hg.IsRightHand FROM Gesture g, Angle a, HandGesture hg where g.id = hg.gestureid and hg.id = a.handgestureid";

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


        while (rs.next()) {
            String name = rs.getString("NAME");
            if (!gestureMap.containsKey(name)) {
                Gesture gesture = new Gesture();
                gesture.setId(rs.getInt("ID"));
                gesture.setName(name);
                gestureList.add(gesture);
            }
            Gesture gesture = gestureMap.get(name);


        }
        //return gestureList (ObservableList of Gestures)
        return gestureList;
    }

    public static void main(String[] args) {
        try {
            insertGesture(new Gesture(2, "'--", Arrays.asList(new HandGesture())));
        } catch (SQLException e) {
            e.getErrorCode();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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
