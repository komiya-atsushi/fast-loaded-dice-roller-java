package me.k11i.rng;

import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class AliasMethod {
    private final int n;
    private final double[] prob;
    private final int[] alias;

    AliasMethod(int[] weights) {
        this(IntStream.of(weights).asDoubleStream().toArray());
    }

    AliasMethod(double[] weights) {
        n = weights.length;
        prob = new double[n];
        alias = new int[n];

        double totalWeight = DoubleStream.of(weights).sum();
        double[] p = DoubleStream.of(weights).map(w -> w / totalWeight).toArray();
        double mean = 1.0 / n;

        int[] work = new int[n];
        int s = 0, l = n;

        for (int i = 0; i < n; i++) {
            work[p[i] > mean ? --l : s++] = i;
        }

        while (s != 0 && l != n) {
            int si = work[--s], li = work[l++];

            prob[si] = n * p[si];
            alias[si] = li;

            p[li] += (p[si] - mean);
            if (p[li] > mean) {
                work[--l] = li;
            } else {
                work[s++] = li;
            }
        }
        while (s > 0) {
            prob[work[--s]] = 1;
        }
        while (l < n) {
            prob[work[l++]] = 1;
        }
    }

    int roll(Random r) {
        int i = r.nextInt(n);
        if (r.nextDouble() < prob[i]) {
            return i;
        } else {
            return alias[i];
        }
    }
}
