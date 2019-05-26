import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import thienthn.algorithm.PCAEngine;
import thienthn.algorithm.WordPreprocessor;
import thienthn.core.common.ConfigurationManager;
import thienthn.core.common.WordSegment;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PCAEngineTest {
    static PCAEngine engine;

    @BeforeClass
    public static void beforeClass() {
        engine = new PCAEngine();
    }

    @Test
    public void testLoadModelMethod() {
        File model = new File(ConfigurationManager.MODEL_PATH);
        File tempModel = new File(ConfigurationManager.MODEL_PATH + "x");
        model.renameTo(tempModel);
        boolean pass = false;
        try {
            engine.loadModel();
        } catch (IOException e) {
            pass = true;
        } catch (ClassNotFoundException e) {
            pass = false;
            e.printStackTrace();
        }

        Assert.assertTrue(pass);

        tempModel.renameTo(model);
        pass = true;
        try {
            engine.loadModel();
        } catch (IOException e) {
            pass = false;
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            pass = false;
            e.printStackTrace();
        }

        Assert.assertTrue(pass);
        Assert.assertTrue(engine.isTrained());
    }

    @Test
    public void testFindForQueriesFileMethod() {
        boolean throwException = false;
        try {
            engine.loadModel();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            engine.find("", "");
        } catch (IOException e) {
            throwException = true;
        }

        Assert.assertTrue(throwException);

        try {
            engine.find("src/main/resources/100_query.txt", "results/test/");
            File testPath = new File("results/test");
            Assert.assertTrue(testPath.exists());
            Assert.assertEquals(100, testPath.list().length);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testFindMethod() {
        try {
            engine.loadModel();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        ArrayList<String> results = engine.find("dong ho");
        Assert.assertEquals(5, results.size());
    }

    @Test
    public void testTrainMethod() {
        String dataPath = "src/main/not_existed_file.txt";
        Assert.assertFalse(engine.train(dataPath));
        dataPath = "src/main/resources/product_names.txt";
        Assert.assertTrue(engine.train(dataPath));
    }

    @Test
    public void testCalculateTfIdfMethod() {
        Class<?>[] methodArgumentTypes = new Class[]{WordSegment.class, String.class, ArrayList.class, double.class};
        Method method = null;
        try {
            method = engine.getClass().getDeclaredMethod("calculateTfIdf", methodArgumentTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Assert.fail();
        }
        method.setAccessible(true);
        WordSegment wordSegment = new WordSegment(0, "test");
        wordSegment.addDocumentIndex(0, false);
        wordSegment.addDocumentIndex(1, false);
        wordSegment.addDocumentIndex(2, false);
        wordSegment.addDocumentIndex(3, false);
        String sentence = "I test my function";
        ArrayList<String> words = WordPreprocessor.getInstance().getWords(sentence);
        double tfidf = 0;

        try {
            tfidf = (double) method.invoke(engine, wordSegment, "test", words, 40.0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Assert.fail();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Assert.fail();
        }
        Assert.assertEquals(0.519, tfidf, 0.001);
    }

    @Test
    public void testCalculateVectorsDistanceMethod() {
        Class<?>[] methodArgumentTypes = new Class[]{Map.class, HashMap.class, HashMap.class};
        Method method = null;
        try {
            method = engine.getClass().getDeclaredMethod("calculateVectorsDistance", methodArgumentTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Assert.fail();
        }
        method.setAccessible(true);
        Map<String, WordSegment> dic = new HashMap<>();
        dic.put("one", new WordSegment(0, "one"));
        dic.put("two", new WordSegment(0, "two"));
        dic.put("three", new WordSegment(0, "three"));
        dic.put("four", new WordSegment(0, "four"));
        HashMap<String, Double> vector1 = new HashMap<>();
        vector1.put("one", 3.0);
        vector1.put("two", 1.0);
        HashMap<String, Double> vector2 = new HashMap<>();
        vector2.put("three", Math.sqrt(2));
        vector2.put("two", 6.0);
        double distance = 0;
        try {
            distance = (double) method.invoke(engine, dic, vector1, vector2);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Assert.fail();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Assert.fail();
        }
        Assert.assertEquals(6, distance, 0.001);
    }
}
