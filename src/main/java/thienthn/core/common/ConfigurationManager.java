package thienthn.core.common;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {
    final static Logger LOGGER = Logger.getLogger(ConfigurationManager.class);

    public static String MODEL_PATH = "models/searchModel.model";

    public static void loadAllConfigurations() throws IOException {
        InputStream inputStream = new FileInputStream("app.config");
        Properties properties = new Properties();
        properties.load(inputStream);

        MODEL_PATH = properties.getProperty("model_path", "models/searchModel.model");

        LOGGER.info("Configurations of engine loaded!");
        LOGGER.info("MODEL_PATH:\t" + MODEL_PATH);
        System.out.println();
    }
}
