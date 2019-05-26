package thienthn.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.util.Pair;
import org.apache.log4j.Logger;
import thienthn.algorithm.MixtureGaussianGenerator;

import java.util.ArrayList;

public class GaussianController implements EventHandler<ActionEvent> {
    private static final Logger LOGGER = Logger.getLogger(GaussianController.class);
    private TabPane rootLayout;
    private ScatterChart scGaussian;
    private Button btnGenerate;
    private static XYChart.Series<Double, Double> series1, series2, boundary;

    public GaussianController(TabPane rootLayout) {
        this.rootLayout = rootLayout;
        init();
    }

    private void init() {
        scGaussian = (ScatterChart) rootLayout.lookup("#scGaussian");

        btnGenerate = (Button) rootLayout.lookup("#btnGenerate");
        btnGenerate.setOnAction(this);
    }

    @Override
    public void handle(ActionEvent event) {

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                btnGenerate.setDisable(true);
                double variance = 1;
                int number = 100;
                double[] firstSetComponents = new double[]{-1, -0.75, -0.5, -0.25, 0};
                double[] secondSetComponents = new double[]{1, 0.75, 0.5, 0.25, 0};
                // first data set
                series1 = (XYChart.Series<Double, Double>) convertDatasToSeries(MixtureGaussianGenerator.getInstance()
                        .generateMixtureGaussianDataSet(firstSetComponents, variance, number));
                series1.setName("First Set");
                LOGGER.info("generated " + number + " datas for first set");

                // second data set
                series2 = (XYChart.Series<Double, Double>) convertDatasToSeries(MixtureGaussianGenerator.getInstance()
                        .generateMixtureGaussianDataSet(secondSetComponents, variance, number));
                series2.setName("Second Set");
                LOGGER.info("generated " + number + " datas for second set");

                boundary = (XYChart.Series<Double, Double>)  convertDatasToSeries(MixtureGaussianGenerator.getInstance().
                        generateBayesDecisionBoundary(firstSetComponents, secondSetComponents, variance));
                boundary.setName("Boundary");
                LOGGER.info("generated 100 data for second set");
                return null;
            }
        };
        task.setOnSucceeded(event1 -> {
            btnGenerate.setDisable(false);
            scGaussian.getData().clear();
            scGaussian.getData().add(series1);
            scGaussian.getData().add(series2);
            scGaussian.getData().add(boundary);
        });

        new Thread(task).start();

    }

    private XYChart.Series convertDatasToSeries(ArrayList<Pair> datas) {
        if(datas == null)
            return new XYChart.Series();
        XYChart.Series series = new XYChart.Series();
        for (Pair<Double, Double> point : datas) {
            series.getData().add(new XYChart.Data(point.getKey(), point.getValue()));
        }

        return series;
    }
}
