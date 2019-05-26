package thienthn.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.util.Pair;
import org.apache.log4j.Logger;
import thienthn.algorithm.MixtureGaussianGenerator;

import java.util.ArrayList;

public class GaussianController implements EventHandler<ActionEvent> {
    private static final Logger LOGGER = Logger.getLogger(GaussianController.class);
    private TabPane rootLayout;
    private ScatterChart scGaussian;
    private Button btnGenerate;

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
        scGaussian.getData().clear();

        // first data set
        XYChart.Series<Double, Double> series1 = (XYChart.Series<Double, Double>) convertDatasToSeries(MixtureGaussianGenerator.getInstance()
                .generateMixtureGaussianDataSet(new double[]{-1, -0.75, -0.5, -0.25, 0}, 1, 100));
        series1.setName("First Set");
        scGaussian.getData().add(series1);
        LOGGER.info("generated 100 data for first set");

        // second data set
        XYChart.Series<Double, Double> series2 = (XYChart.Series<Double, Double>) convertDatasToSeries(MixtureGaussianGenerator.getInstance()
                .generateMixtureGaussianDataSet(new double[]{1, 0.75, 0.5, 0.25, 0}, 1, 100));
        series2.setName("Second Set");
        scGaussian.getData().add(series2);
        LOGGER.info("generated 100 data for second set");
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
