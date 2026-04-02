package com.plovdev.pornviewer.utility.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.ChaCha20ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;

public class VideoCipherrer {
    private static final String ALG = "ChaCha20";
    private static final int BLOCK_SIZE = 16; // ChaCha20 block size for counter
    private static final Logger log = LoggerFactory.getLogger(VideoCipherrer.class);

    private final SecretKeySpec key;
    private final byte[] iv;

    public VideoCipherrer(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = Arrays.copyOfRange(digest.digest(password.getBytes()), 0, 32);
            this.key = new SecretKeySpec(keyBytes, "ChaCha20"); // Исправлено!

            byte[] hash = digest.digest((password + "_iv").getBytes());
            this.iv = Arrays.copyOfRange(hash, 0, 12); // ChaCha20 требует 12 байт IV
        } catch (Exception e) {
            throw new RuntimeException("Failed to init VideoCipherrer", e);
        }
    }

    public byte[] decrypt(byte[] bytes, long position) {
        return process(bytes, position, Cipher.DECRYPT_MODE);
    }

    public void decrypt(byte[] bytes, int offset, int length, long position) {
        byte[] data = Arrays.copyOfRange(bytes, offset, offset + length);
        process(data, position, Cipher.DECRYPT_MODE);
        System.arraycopy(data, 0, bytes, offset, length);
    }

    public byte[] encrypt(byte[] bytes, long position) {
        return process(bytes, position, Cipher.ENCRYPT_MODE);
    }

    private byte[] process(byte[] bytes, long position, int mode) {
        try {
            int skip = (int) (position % BLOCK_SIZE);
            long counter = position / BLOCK_SIZE;

            Cipher cipher = createCipher(mode, counter);

            byte[] input = new byte[bytes.length + skip];
            System.arraycopy(bytes, 0, input, skip, bytes.length);

            byte[] output = cipher.doFinal(input);
            return Arrays.copyOfRange(output, skip, output.length);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Cipher createCipher(int mode, long counter) throws Exception {
        Cipher cipher = Cipher.getInstance(ALG);

        // Используем ChaCha20ParameterSpec вместо IvParameterSpec
        ChaCha20ParameterSpec spec = new ChaCha20ParameterSpec(iv, (int) counter);
        cipher.init(mode, key, spec);

        return cipher;
    }
}