package thienthn.algorithm;

import javafx.util.Pair;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class MixtureGaussianGenerator {
    private static MixtureGaussianGenerator instance = null;

    public static MixtureGaussianGenerator getInstance() {
        if(instance == null)
            instance = new MixtureGaussianGenerator();
        return instance;
    }

    /**
     * generate next Gaussian point
     * @param mean
     * @param variance
     * @return
     */
    public Pair<Double, Double> generateNextGaussianData(double mean, double variance) {
        double x, y, s;
        do {
            do {
                x = 2 * ThreadLocalRandom.current().nextDouble(0, 1) - 1;
                y = 2 * ThreadLocalRandom.current().nextDouble(0, 1) - 1;
                s = x * x + y * y;
            } while (s >= 1 || s == 0);

            s = Math.sqrt(-2 * Math.log(s) / s);
            x = mean +  x * s * variance;
            y = mean + y * s * variance;
        } while (x > mean + variance || x < mean - variance || y > mean + variance || y < mean - variance);

        return new Pair<>(x, y);
    }

    /**
     * generate a Gaussian data set
     * @param mean
     * @param variance
     * @param number
     * @return
     */
    public ArrayList<Pair> generateGaussianDataSet(double mean, double variance, int number) {
        ArrayList<Pair> dataSet = new ArrayList<>();
        for(int i = 0; i < number ; ++i) {
            dataSet.add(generateNextGaussianData(mean, variance));
        }

        return dataSet;
    }

    /**
     * generate mixture Gaussian data set
     * @param means
     * @param variance
     * @param number
     * @return
     */
    public ArrayList<Pair> generateMixtureGaussianDataSet(double[] means, double variance, int number) {
        ArrayList<Pair> dataSet = new ArrayList<>();

        for(int i = 0; i < number; i++) {
            // I set k component before in code and assume that they are equal in priority so I just use random integer to define which component will be used
            double mean = means[ThreadLocalRandom.current().nextInt(0, means.length - 1)];
            Pair<Double, Double> point = generateNextGaussianData(mean, variance);
            dataSet.add(point);
        }

        return dataSet;
    }
}
