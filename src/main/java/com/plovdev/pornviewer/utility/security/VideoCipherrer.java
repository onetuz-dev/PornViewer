package com.plovdev.pornviewer.utility.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;

public class VideoCipherrer {
    private static final String ALG = "AES/CTR/NoPadding";
    private static final int BLOCK_SIZE = 16;
    private static final Logger log = LoggerFactory.getLogger(VideoCipherrer.class);

    private final SecretKeySpec key;
    private final byte[] iv;

    public VideoCipherrer(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = Arrays.copyOfRange(digest.digest(password.getBytes()), 0, 32);
            this.key = new SecretKeySpec(keyBytes, "AES");

            byte[] hash = digest.digest((password + "_iv").getBytes());
            this.iv = Arrays.copyOfRange(hash, 0, 16);

        } catch (Exception e) {
            throw new RuntimeException("Failed to init VideoCipherrer", e);
        }
    }

    /**
     * Расшифровывает массив байтов с любой позиции
     */
    public byte[] decrypt(byte[] bytes, long position) {
        return process(bytes, position, Cipher.DECRYPT_MODE);
    }

    /**
     * Расшифровывает часть массива байтов с любой позиции
     */
    public void decrypt(byte[] bytes, int offset, int length, long position) {
        byte[] data = Arrays.copyOfRange(bytes, offset, offset + length);
        process(data, position, Cipher.DECRYPT_MODE);
    }

    /**
     * Шифрование массива байтов с любой позиции
     */
    public byte[] encrypt(byte[] bytes, long position) {
        return process(bytes, position, Cipher.ENCRYPT_MODE);
    }

    private byte[] process(byte[] bytes, long position, int mode) {
        try {
            int skip = (int) (position % BLOCK_SIZE);
            long alignedPos = position - skip;

            Cipher cipher = createCipher(mode, alignedPos);
            byte[] input = new byte[bytes.length + skip];
            System.arraycopy(bytes, 0, input, skip, bytes.length);

            byte[] output = cipher.doFinal(input);
            return Arrays.copyOfRange(output, skip, output.length);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Cipher createCipher(int mode, long position) throws Exception {
        long blockIndex = position / BLOCK_SIZE;
        byte[] currentIv = iv.clone();
        for (int i = 0; i < 8; i++) {
            currentIv[15 - i] = (byte) (blockIndex >>> (i * 8));
        }

        Cipher cipher = Cipher.getInstance(ALG);
        cipher.init(mode, key, new IvParameterSpec(currentIv));
        return cipher;
    }
}