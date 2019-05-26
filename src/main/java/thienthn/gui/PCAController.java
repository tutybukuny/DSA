package thienthn.gui;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import thienthn.algorithm.PCAEngine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PCAController implements EventHandler<ActionEvent> {
    private static final Logger LOGGER = Logger.getLogger(PCAController.class);
    private Stage primaryStage;
    private TabPane rootLayout;
    private Button btnSearch, btnQuery, btnQueryFileBrowse, btnTrainingData, btnResultFolder, btnTrain;
    private TextField tfQuery, tfQueryFile, tfResultFolder, tfTrainingData;
    private TextArea taResult;
    private PCAEngine engine;
    private FileChooser fileChooser;
    private DirectoryChooser directoryChooser;

    public PCAController(Stage primaryStage, TabPane rootLayout) {
        this.primaryStage = primaryStage;
        this.rootLayout = rootLayout;
        fileChooser = new FileChooser();
        directoryChooser = new DirectoryChooser();
        engine = new PCAEngine();
        try {
            engine.loadModel();
        } catch (IOException e) {
            LOGGER.error(e);
        } catch (ClassNotFoundException e) {
            LOGGER.error(e);
        }
        init();
    }

    private void init() {
        btnSearch = (Button) rootLayout.lookup("#btnSearch");
        btnQuery = (Button) rootLayout.lookup("#btnQuery");
        btnQueryFileBrowse = (Button) rootLayout.lookup("#btnQueryFileBrowse");
        btnTrainingData = (Button) rootLayout.lookup("#btnTrainingData");
        btnResultFolder = (Button) rootLayout.lookup("#btnResultFolder");
        btnTrain = (Button) rootLayout.lookup("#btnTrain");

        btnSearch.setOnAction(this);
        btnQuery.setOnAction(this);
        btnQueryFileBrowse.setOnAction(this);
        btnTrainingData.setOnAction(this);
        btnResultFolder.setOnAction(this);
        btnTrain.setOnAction(this);

        taResult = (TextArea) rootLayout.lookup("#taResult");
        tfQuery = (TextField) rootLayout.lookup("#tfQuery");
        tfQueryFile = (TextField) rootLayout.lookup("#tfQueryFile");
        tfTrainingData = (TextField) rootLayout.lookup("#tfTrainingData");
        tfResultFolder = (TextField) rootLayout.lookup("#tfResultFolder");

    }

    @Override
    public void handle(ActionEvent event) {
        if (event.getSource() == btnSearch) {
            startQuerySearch();
        } else if (event.getSource() == btnQuery) {
            startQueryFile();
        } else if (event.getSource() == btnTrain) {
            startTraining();
        } else {
            Button button = null;
            TextField textField = null;
            if (event.getSource() == btnTrainingData) {
                button = btnTrainingData;
                textField = tfTrainingData;
            } else if (event.getSource() == btnQueryFileBrowse) {
                button = btnQueryFileBrowse;
                textField = tfQueryFile;
            } else if (event.getSource() == btnResultFolder) {
                button = btnResultFolder;
                textField = tfResultFolder;
            }

            File file;
            if (button == btnResultFolder) {
                file = directoryChooser.showDialog(primaryStage);
            } else {
                file = fileChooser.showOpenDialog(primaryStage);
            }

            if (file != null && file.exists()) {
                textField.setText(file.getAbsolutePath());
            }
        }
    }

    private void startTraining() {
        if (tfTrainingData.getText().isEmpty()) {
            Util.showAlert("An exception occurred!", "Training data path is empty!", Alert.AlertType.ERROR);
            return;
        }

        Task task = new Task<Void>() {
            @Override
            public Void call() {
                btnTrainingData.setDisable(true);
                tfTrainingData.setDisable(true);
                btnTrainingData.setDisable(true);
                engine.train(tfTrainingData.getText());
                return null;
            }
        };
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                btnTrainingData.setDisable(false);
                tfTrainingData.setDisable(false);
                btnTrainingData.setDisable(false);
                if (engine.isTrained())
                    Util.showAlert("Your engine has been trained!", "Success", Alert.AlertType.INFORMATION);
                else
                    Util.showAlert("Your engine has been trained!", "Your engine has been trained!", Alert.AlertType.ERROR);
            }
        });
        new Thread(task).start();
    }

    private void startQueryFile() {
        if (tfQueryFile.getText().isEmpty()) {
            Util.showAlert("An exception occurred!", "Query file path is empty!", Alert.AlertType.ERROR);
            return;
        }
        if (tfResultFolder.getText().isEmpty()) {
            Util.showAlert("An exception occurred!", "Result folder path is empty!", Alert.AlertType.ERROR);
            return;
        }

        if (!engine.isTrained()) {
            Util.showAlert("An error occurred!", "engine is not trained", Alert.AlertType.ERROR);
            return;
        }

        Task task = new Task<Void>() {
            @Override
            public Void call() {
                btnQuery.setDisable(true);
                btnQueryFileBrowse.setDisable(true);
                btnResultFolder.setDisable(true);
                try {
                    engine.find(tfQueryFile.getText(), tfResultFolder.getText());
                } catch (IOException e) {
                    Util.showAlert("An exception occurred!", e.getMessage(), Alert.AlertType.ERROR);
                }

                return null;
            }
        };
        task.setOnSucceeded(event -> {
            btnQuery.setDisable(false);
            btnQueryFileBrowse.setDisable(false);
            btnResultFolder.setDisable(false);
            Util.showAlert("Query file was excused!", "You can check the results now", Alert.AlertType.INFORMATION);
        });
        new Thread(task).start();
    }

    private void startQuerySearch() {
        if (!engine.isTrained()) {
            return;
        }
        if (tfQuery.getText().isEmpty())
            return;
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                taResult.setText("");
                tfQuery.setDisable(true);
                btnSearch.setDisable(true);
                String textResult = "";
                ArrayList<String> results = engine.find(tfQuery.getText());
                for (String result : results) {
                    textResult += result + "\r\n";
                }
                taResult.setText(textResult);
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            tfQuery.setDisable(false);
            btnSearch.setDisable(false);
        });
        new Thread(task).start();
    }
}
