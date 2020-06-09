package me.k11i.rng;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public enum RandomInstanceProvider {
    SECURE_RANDOM {
        @Override
        Random get() throws NoSuchAlgorithmException {
            return SecureRandom.getInstance("DRBG");
        }
    },
    THREAD_LOCAL_RANDOM {
        @Override
        Random get() throws NoSuchAlgorithmException {
            return ThreadLocalRandom.current();
        }
    };

    abstract Random get() throws NoSuchAlgorithmException;
}
