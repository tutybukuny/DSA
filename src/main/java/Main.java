import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import thienthn.core.common.ConfigurationManager;
import thienthn.gui.GaussianController;
import thienthn.gui.PCAController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main extends Application {
    static {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        System.setProperty("current.date.time", dateFormat.format(new Date()));
    }

    private Logger LOGGER = Logger.getLogger(ConfigurationManager.class);

    private Stage primaryStage;
    private TabPane rootLayout;

    private GaussianController gaussianController;
    private PCAController pcaController;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Small Search Engine");
        try {
            ConfigurationManager.loadAllConfigurations();
        } catch (IOException e) {
            LOGGER.error("failed to load configuration!", e);
        }
        initRootLayout();
    }

    private void initRootLayout() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("Main.fxml"));
        try {
            rootLayout = (TabPane) loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            gaussianController = new GaussianController(rootLayout);
            pcaController = new PCAController(primaryStage, rootLayout);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
