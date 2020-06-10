package me.k11i.rng;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class FastLoadedDiceRollerTest {
    static Stream<Arguments> testCalculateK() {
        return Stream.of(
                arguments(1, 1),
                arguments(2, 2),
                arguments(3, 3),
                arguments(4, 3),
                arguments(5, 4),
                arguments(7, 4),
                arguments(8, 4),
                arguments(9, 5)
        );
    }

    @ParameterizedTest
    @MethodSource
    void testCalculateK(int input, int expected) {
        assertThat(FastLoadedDiceRoller.calculateK(input)).isEqualTo(expected);
    }

    static Stream<Arguments> testRollStatistically() {
        return Stream.of(
                new int[]{1, 2, 3, 4, 5},
                new int[]{1, 4, 9, 16, 25},
                new int[]{1, 1},
                new int[]{1024, 64, 1})
                .map(w -> (Object) w).map(Arguments::arguments);
    }

    @ParameterizedTest
    @MethodSource
    void testRollStatistically(int[] weights) {
        final int numFirstLevelTrials = 100000;
        final int numSecondLevelTrials = 100;

        int sum = IntStream.of(weights).sum();
        double[] expected = IntStream.of(weights).asDoubleStream().map(w -> w / sum).toArray();
        long[] occurrences = new long[weights.length];

        ChiSquareTest chiSqTest = new ChiSquareTest();
        double[] pValues = new double[numSecondLevelTrials];

        for (int s = 0; s < numSecondLevelTrials; s++) {
            var sut = new FastLoadedDiceRoller(weights);
            Arrays.fill(occurrences, 0);

            for (int f = 0; f < numFirstLevelTrials; f++) {
                int result = sut.roll(ThreadLocalRandom.current());
                occurrences[result]++;
            }

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
}
