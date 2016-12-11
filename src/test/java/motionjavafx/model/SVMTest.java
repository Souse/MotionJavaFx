package motionjavafx.model;

import javafx.collections.ObservableList;
import org.apache.commons.collections.FastArrayList;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.classification.OneVsRest;
import org.apache.spark.ml.classification.OneVsRestModel;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.linalg.*;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;
import org.apache.spark.sql.types.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lena on 12/6/16.
 */
public class SVMTest {

    public static final int DIMENSIONS = 16;

    @Before
    public void setUp() {

    }

    @Test
    public void testSpark() throws IOException, SQLException, ClassNotFoundException {

        SparkSession spark = SparkSession
                .builder()
                .master("local")
                .appName("JavaOneVsRestExample")
                .getOrCreate();

        // $example on$
        // load data file.
        final ObservableList<Gesture> allGestures = GestureDAO.getAllGestures();
        StructType st = new StructType();
        st=st.add("label", DataTypes.IntegerType);
        st=st.add("features",new VectorUDT());
        //org.apache.spark.ml.linalg.VectorUDT
        List<Row> rows = new ArrayList<>();
        for (Gesture gesture : allGestures) {
            HandGesture hg = gesture.getHandGestures().stream().filter(handGesture -> handGesture.isRightHand()).findFirst().get();
            List<Object> data = new FastArrayList();
            data.add(gesture.getId());
            final List<Angle> angles = hg.getAngles();
            Map<Integer,Double> map = new HashMap<>();
            for (int i = 0; i < angles.size(); i++) {
                map.put(i,(double)angles.get(i).getValue());
            }
            int [] keys = new int[16];
            double [] values = new double[16];
            final Object[] keysObj = map.keySet().toArray();
            final Object[] valuesObj = map.values().toArray();
            for (int i = 0; i < 16; i++) {
                keys[i] = (int) keysObj[i];
                values[i] = (double) valuesObj[i];
            }

            org.apache.spark.ml.linalg.Vector v = new SparseVector(16,keys,values);
            data.add(v);

            rows.add(new GenericRowWithSchema(data.toArray(),st));
        }


        final Dataset<Row> inputData = spark.createDataFrame(rows, st);
       // Dataset<Row> inputData = spark.read().format("libsvm")
        //        .load("/home/lena/IdeaProjects/MotionJavaFx/sample_multiclass_classification_data.txt");

        // generate the train/test split.
        Dataset<Row> test;

        final Row testRow = inputData.collectAsList().get(15);
        SparseVector sv = (SparseVector) testRow.get(1);
        Object [] listO = new Object[1];
        listO[0] = sv;
        StructType st2 = new StructType().add("features",new VectorUDT());
        Row newR = new GenericRowWithSchema(listO,st2);

        test = spark.createDataFrame(Arrays.asList(newR),st2);

        // configure the base classifier.
        LogisticRegression classifier = new LogisticRegression()
                .setMaxIter(10)
                .setTol(1E-6)
                .setFitIntercept(true);

        // instantiate the One Vs Rest Classifier.
        OneVsRest ovr = new OneVsRest().setClassifier(classifier);

        // train the multiclass model.
        OneVsRestModel ovrModel = ovr.fit(inputData);

        // score the model on test data.
        Dataset<Row> predictions = ovrModel.transform(test)
                .select("prediction");
        // obtain evaluator.
        //MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
        //        .setMetricName("accuracy");

        // compute the classification error on test data.
        // double accuracy = evaluator.evaluate(predictions);
        System.out.println("Predicted = " + predictions.collectAsList().get(0));
ogg        System.out.println("Actual = "+testRow.get(0));
        // $example off$

        spark.stop();
    }
}
/*SparseVector sv = (SparseVector) tmp[0].collectAsList().get(0).get(1);
Object [] listO = new Object[1];
listO[0] = sv;
StructType st = new StructType().add("features",new VectorUDT());
Row newR = new GenericRowWithSchema(listO,st);

test = spark.createDataFrame(Arrays.asList(newR),st); */