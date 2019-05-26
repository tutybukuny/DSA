import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;
import thienthn.algorithm.MixtureGaussianGenerator;

import java.util.ArrayList;

public class MixtureGaussianGeneratorTest {
    @Test
    public void testGenerateMixtureGaussianDataSetMethod() {
        ArrayList<Pair> datas = MixtureGaussianGenerator.getInstance()
                .generateMixtureGaussianDataSet(new double[]{-1, -0.75, -0.5, -0.25, 0}, 1, 100);

        Assert.assertEquals(100, datas.size());
        for(Pair data : datas)
        {
            double key = (double) data.getKey();
            double value = (double) data.getValue();
            Assert.assertTrue(key >= -2 && key <= 1);
            Assert.assertTrue(value >= -2 && value <= 1);
        }
    }

    @Test
    public void testGenerateNextGaussianDataMethod() {
        Pair<Double, Double> point = MixtureGaussianGenerator.getInstance().generateNextGaussianData(150, 0.5);
        Assert.assertTrue(point.getKey() >= 149.5 && point.getKey() <= 150.5);
        Assert.assertTrue(point.getValue() >= 149.5 && point.getValue() <= 150.5);
    }
}
