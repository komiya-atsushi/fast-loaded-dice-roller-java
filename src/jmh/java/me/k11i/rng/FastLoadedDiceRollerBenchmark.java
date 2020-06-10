package me.k11i.rng;

import org.openjdk.jmh.annotations.*;

import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.stream.IntStream;

@State(Scope.Benchmark)
public class FastLoadedDiceRollerBenchmark {
    @Param({"SECURE_RANDOM", "THREAD_LOCAL_RANDOM"})
    private RandomInstanceProvider randomProvider;

    @Param({"17", "8193"})
    private int numWeights;

    private Random r;
    private FastLoadedDiceRoller fldr;
    private AliasMethod aliasMethod;

    @Setup
    public void setUp() throws NoSuchAlgorithmException {
        r = randomProvider.get();
        int[] weights = IntStream.iterate(1, i -> i + 1).limit(numWeights).toArray();
        fldr = new FastLoadedDiceRoller(weights);
        aliasMethod = new AliasMethod(weights);
    }

    @Benchmark
    public int fastLoadedDiceRoller() {
        return fldr.roll(r);
    }

    @Benchmark
    public int aliasMethod() {
        return aliasMethod.roll(r);
    }
}
