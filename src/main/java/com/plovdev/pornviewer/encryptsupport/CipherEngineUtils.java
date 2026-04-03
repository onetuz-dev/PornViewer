package com.plovdev.pornviewer.encryptsupport;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class CipherEngineUtils {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static final int CHACHA20_NONCE_LENGTH = 12;
    public static final int BASE_NONCE_LENGTH = 8;
    public static final int COUNTER_NONCE_LENGTH = 4;

    public static SecretKeySpec createSecretKeySpecFromPassword(byte[] password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return new SecretKeySpec(Arrays.copyOfRange(digest.digest(password), 0, 32), "ChaCha20");
    }

    public static IvParameterSpec createParameterSpecFromBaseNonce(int counter, byte[] baseNonce) {
        if (baseNonce.length != BASE_NONCE_LENGTH) {
            throw new IllegalArgumentException("Illegal base nonce length! Make sure that nonce length is 8 byte!");
        }

        byte[] fullNonce = new byte[CHACHA20_NONCE_LENGTH];
        byte[] counterNonce = LoadersUtils.intToBytes(counter);
        System.arraycopy(baseNonce, 0, fullNonce, 0, baseNonce.length);
        System.arraycopy(counterNonce, 0, fullNonce, BASE_NONCE_LENGTH, counterNonce.length);

        return createParameterSpecFromNonce(fullNonce);
    }

    public static IvParameterSpec createParameterSpecFromNonce(byte[] nonce) {
        if (nonce.length != CHACHA20_NONCE_LENGTH) {
            throw new IllegalArgumentException("Illegal nonce length! Make sure that nonce length is 12 byte!");
        }
        return new IvParameterSpec(nonce);
    }

    public static void createRandomPassword(byte[] toWrite) {
        SECURE_RANDOM.nextBytes(toWrite);
    }
}