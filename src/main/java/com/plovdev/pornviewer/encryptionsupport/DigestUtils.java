package com.plovdev.pornviewer.encryptionsupport;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestUtils {
    private static final char[] HEX = "0123456789abcdef".toCharArray();

    public static String md5(String plain) {
        return processAlgorithm("MD5", plain);
    }

    public static String sha256(String plain) {
        return processAlgorithm("SHA-256", plain);
    }

    public static String processAlgorithm(String algorithm, String plain) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.update(plain.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(algorithm + " algorithm not found", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hex = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hex[i * 2] = HEX[v >>> 4];
            hex[i * 2 + 1] = HEX[v & 0x0F];
        }
        return new String(hex);
    }
}