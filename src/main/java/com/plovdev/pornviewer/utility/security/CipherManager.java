package com.plovdev.pornviewer.utility.security;

import com.github.javakeyring.Keyring;
import com.plovdev.pornviewer.utility.files.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

public class CipherManager {
    private final Logger logger = LoggerFactory.getLogger(CipherManager.class);
    private static final String ALG = "AES/GCM/NoPadding";
    private final SecretKey key;
    private final GCMParameterSpec gcmSpec;

    public CipherManager(String password) {
        try {
            key = generateKeyFromPassword(password);

            // Генерируем IV из пароля
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            byte[] gcmIv = Arrays.copyOfRange(hash, 0, 12);
            gcmSpec = new GCMParameterSpec(128, gcmIv);
        } catch (Exception e) {
            logger.error("Ошибка инициализации: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize security service", e);
        }
    }

    public static String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return new BigInteger(1, bytes).toString(16);
    }

    public static String getPassword() {
        try (Keyring keyring = Keyring.create()) {
            return keyring.getPassword(FileUtils.PORN_VIEWER_SIGN, FileUtils.PORN_VIEWER_SIGN);
        } catch (Exception e) {
            logger.error("Error getting password from system: ", e);
        }
        return null;
    }

    private SecretKey generateKeyFromPassword(String password) throws Exception {
        byte[] salt = "fixed-salt".getBytes();
        int iterations = 65536;
        int keyLength = 256;

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    public byte[] encrypt(byte[] to) {
        try {
            Cipher cipher = Cipher.getInstance(ALG);
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);
            return cipher.doFinal(to);
        } catch (Exception e) {
            logger.error("Произошла ошибка шифрования байтов: {}", e.getMessage());
        }
        return to;
    }

    public String encrypt(String to) {
        try {
            Cipher cipher = Cipher.getInstance(ALG);
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);
            byte[] encrypted = cipher.doFinal(to.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encrypted);
        } catch (Exception e) {
            logger.error("Произошла ошибка шифрования строки: {}", e.getMessage());
        }
        return to;
    }

    public String decrypt(String from) {
        try {
            Cipher cipher = Cipher.getInstance(ALG);
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);
            byte[] decoded = Base64.getUrlDecoder().decode(from);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Произошла ошибка расшифровки строки: {}", e.getMessage());
        }
        return from;
    }

    public byte[] decrypt(byte[] from) {
        try {
            Cipher cipher = Cipher.getInstance(ALG);
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);
            return cipher.doFinal(from);
        } catch (Exception e) {
            logger.error("Произошла ошибка расшифровки байтов: {}", e.getMessage());
        }
        return from;
    }

    public static String md5(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }
}