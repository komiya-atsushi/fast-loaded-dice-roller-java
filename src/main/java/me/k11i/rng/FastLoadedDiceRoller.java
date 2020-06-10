package me.k11i.rng;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Implementation of the Fast Loaded Dice Roller.
 *
 * Saad, Feras A., et al.
 * "The Fast Loaded Dice Roller: A Near-Optimal Exact Sampler for Discrete Probability Distributions."
 * arXiv preprint arXiv:2003.03830 (2020).
 */
public class FastLoadedDiceRoller {
    private final int n;
    private final int[] h;
    private final int[][] H;

    private int count;
    private long randomBits;

    public FastLoadedDiceRoller(int[] weights) {
        n = weights.length;
        int[] a = Arrays.copyOf(weights, n + 1);
        long m = IntStream.of(a).asLongStream().sum();
        int k = calculateK(m);
        a[n] = (int) ((1 << k) - m);

        h = new int[k];
        H = new int[n + 1][k];

        for (int j = 0; j < k; j++) {
            int d = 0;
            for (int i = 0; i <= n; i++) {
                int w = (a[i] >>> ((k - 1) - j)) & 1;
                h[j] += w;
                if (w == 1) {
                    H[d][j] = i;
                    d++;
                }
            }
        }
    }

    static int calculateK(long totalWeight) {
        int numLeadingZeros = Long.numberOfLeadingZeros(totalWeight);
        long highestOneBit = totalWeight & (Long.MIN_VALUE >>> numLeadingZeros);
        if (totalWeight == highestOneBit) {
            return 64 - numLeadingZeros;
        } else {
            return 64 - numLeadingZeros + 1;
        }
    }

    public int roll(Random r) {
        int d = 0;
        int c = 0;
        while (true) {
            d = 2 * d + (1 - flip(r));
            if (d < h[c]) {
                if (H[d][c] < n) {
                    return H[d][c];
                } else {
                    d = c = 0;
                }
            } else {
                d = d - h[c];
                c++;
            }
        }
    }

    private int flip(Random r) {
        if (count == 0) {
            count = 64;
            randomBits = r.nextLong();
        }

        int result = (int)(randomBits & 1);
        randomBits >>>= 1;
        count--;
        return result;
    }
}
