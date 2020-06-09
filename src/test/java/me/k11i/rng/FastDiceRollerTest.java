package me.k11i.rng;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.DoubleStream;

import static org.assertj.core.api.Assertions.assertThat;

class FastDiceRollerTest {
    @ParameterizedTest
    @ValueSource(ints = {2, 15, 255, 1024, 32769})
    void testStatistically(int n) {

        final int numFirstLevelTrials = 100000;
        final int numSecondLevelTrials = 100;

        ChiSquareTest chiSqTest = new ChiSquareTest();
        double[] expected = DoubleStream.generate(() -> 1.0 / n).limit(n).toArray();
        long[] occurrences = new long[n];

        double[] pValues = new double[numSecondLevelTrials];

        // compute p-values numSecondLevelTrials times
        for (int s = 0; s < numSecondLevelTrials; s++) {
            var sut = new FastDiceRoller();
            Arrays.fill(occurrences, 0);

            for (int f = 0; f < numFirstLevelTrials; f++) {
                int result = sut.roll(ThreadLocalRandom.current(), n);
                occurrences[result]++;
            }

            // use Chi-square test to compute p-value
            double p = chiSqTest.chiSquareTest(expected, occurrences);
            System.out.printf("[%d] p = %f%n", s + 1, p);

            pValues[s] = p;
        }

        // test uniformity of p-values
        KolmogorovSmirnovTest ksTest = new KolmogorovSmirnovTest();
        double secondP = ksTest.kolmogorovSmirnovTest(new UniformRealDistribution(0, 1.0), pValues);

        System.out.printf("Second level p = %f%n", secondP);
        assertThat(secondP).isGreaterThanOrEqualTo(0.001);
    }


    @ParameterizedTest
    @ValueSource(ints = {1, 2147483647 - 1, 2147483647})
    void testBoundaryValues(int n) {
        var sut = new FastDiceRoller();

        for (int i = 0; i < 100; i++) {
            sut.roll(ThreadLocalRandom.current(), n);
        }
    }
}
