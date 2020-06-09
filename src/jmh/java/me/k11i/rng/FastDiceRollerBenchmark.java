package me.k11i.rng;

import org.openjdk.jmh.annotations.*;

import java.security.NoSuchAlgorithmException;
import java.util.Random;

@State(Scope.Benchmark)
public class FastDiceRollerBenchmark {
    @Param({"SECURE_RANDOM", "THREAD_LOCAL_RANDOM"})
    private RandomInstanceProvider randomProvider;

    private Random r;
    private FastDiceRoller fdr;
    private int n;

    @Setup
    public void setUp() throws NoSuchAlgorithmException {
        r = randomProvider.get();
        fdr = new FastDiceRoller();
        n = 2;
    }

    @Benchmark
    public int fastDiceRoller() {
        n <<= 1;
        if (n < 0) {
            n = 2;
        }
        return fdr.roll(r, n + 1);
    }

    @Benchmark
    public int javaUtilRandomNextIntN() {
        n <<= 1;
        if (n < 0) {
            n = 2;
        }
        return r.nextInt(n + 1);
    }
}
