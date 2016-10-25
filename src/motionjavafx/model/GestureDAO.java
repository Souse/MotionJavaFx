package motionjavafx.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import motionjavafx.util.DBUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lena on 10/24/16.
 */
public class GestureDAO {

    public static ObservableList<Gesture> getAllGestures() throws SQLException, ClassNotFoundException {
        //Declare a SELECT statement
        String selectStmt = "SELECT g.name, g.id, a.value, hg.IsRightHand FROM Gestures g, Angles a, HandGestures hg where g.id = hg.gestureid and hg.id = a.handgestureid";

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
        Map<String,Gesture> gestureMap = new HashMap<>();


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

    //*************************************
    //INSERT an Gesture
    //*************************************
    public static void insertEmp (String name, String lastname, String email) throws SQLException, ClassNotFoundException {
        //Declare a DELETE statement
        String updateStmt =
                "BEGIN\n" +
                        "INSERT INTO Gestures\n" +
                        "(Gesture_ID, FIRST_NAME, LAST_NAME, EMAIL, HIRE_DATE, JOB_ID)\n" +
                        "VALUES\n" +
                        "(sequence_Gesture.nextval, '"+name+"', '"+lastname+"','"+email+"', SYSDATE, 'IT_PROG');\n" +
                        "END;";

        //Execute DELETE operation
        try {
            DBUtil.dbExecuteUpdate(updateStmt);
        } catch (SQLException e) {
            System.out.print("Error occurred while DELETE Operation: " + e);
            throw e;
        }
    }
}
