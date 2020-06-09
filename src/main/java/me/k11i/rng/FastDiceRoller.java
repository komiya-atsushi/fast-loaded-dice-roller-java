package me.k11i.rng;

import java.util.Random;

/**
 * Implementation of the Fast Dice Roller.
 * <p>
 * Lumbroso, Jérémie.
 * "Optimal discrete uniform generation from coin flips, and applications."
 * arXiv preprint arXiv:1304.1916 (2013).
 */
public class FastDiceRoller {
    private int count;
    private long randomBits;

    public int roll(Random r, int n) {
        long v = 1;
        long c = 0;

        while (true) {
            v <<= 1;
            c = (c << 1) | flip(r);

            if (v >= n) {
                if (c < n) {
                    return (int) c;
                } else {
                    v -= n;
                    c -= n;
                }
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
