import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import thienthn.algorithm.PCAEngine;
import thienthn.core.common.ConfigurationManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
}
