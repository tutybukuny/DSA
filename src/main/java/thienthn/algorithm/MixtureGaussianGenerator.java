package thienthn.algorithm;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class MixtureGaussianGenerator {
    private static MixtureGaussianGenerator instance = null;

    public static MixtureGaussianGenerator getInstance() {
        if (instance == null)
            instance = new MixtureGaussianGenerator();
        return instance;
    }

    /**
     * generate next Gaussian point
     *
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
            x = mean + x * s * variance;
            y = mean + y * s * variance;
        } while (x > mean + variance || x < mean - variance || y > mean + variance || y < mean - variance);

        return new Pair<>(x, y);
    }

    /**
     * generate a Gaussian data set
     *
     * @param mean
     * @param variance
     * @param number
     * @return
     */
    public ArrayList<Pair> generateGaussianDataSet(double mean, double variance, int number) {
        ArrayList<Pair> dataSet = new ArrayList<>();
        for (int i = 0; i < number; ++i) {
            dataSet.add(generateNextGaussianData(mean, variance));
        }

        return dataSet;
    }

    /**
     * generate mixture Gaussian data set
     *
     * @param means
     * @param variance
     * @param number
     * @return
     */
    public ArrayList<Pair> generateMixtureGaussianDataSet(double[] means, double variance, int number) {
        ArrayList<Pair> dataSet = new ArrayList<>();

        for (int i = 0; i < number; i++) {
            // I set k component before in code and assume that they are equal in priority so I just use random integer to define which component will be used
            double mean = means[ThreadLocalRandom.current().nextInt(0, means.length - 1)];
            Pair<Double, Double> point = generateNextGaussianData(mean, variance);
            dataSet.add(point);
        }

        return dataSet;
    }

    /**
     * generate Bayes decision boundary
     * the last 0.25 of the formula to calculate log1 and log2 is the probability of a mean, each k1 and k2 has 5 mean and they have equal probability so it is 0.25
     * @param k1
     * @param k2
     * @param variance
     * @return
     */
    public ArrayList<Pair> generateBayesDecisionBoundary(double[] k1, double[] k2, double variance) {
        ArrayList<Pair> points = new ArrayList<>();

        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        for(int i = 0; i < k1.length; i++) {
            if(k1[i] + variance > max)
                max = k1[i] + variance;
            if(k2[i] + variance > max)
                max = k2[i] + variance;
            if(k1[i] - variance < min)
                min = k1[i] - variance;
            if(k2[i] - variance < min)
                min = k2[i] - variance;
        }

        for (double x = min; x <= max; x += 0.001) {
            double log1 = 0;
            double log2 = 0;
            for (int i = 0; i < k1.length; i++) {
                log1 += 1.0 / (Math.sqrt(2 * Math.PI) * variance) * Math.exp(-Math.pow(x - k1[i], 2) / (2 * variance * variance)) * 0.25;
                log2 += 1.0 / (Math.sqrt(2 * Math.PI) * variance) * Math.exp(-Math.pow(x - k2[i], 2) / (2 * variance * variance)) * 0.25;
            }

            double y = Math.log(log1) - Math.log(log2);

            points.add(new Pair(x, y));
        }

        return points;
    }
}
