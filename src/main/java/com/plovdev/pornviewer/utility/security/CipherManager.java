package com.plovdev.pornviewer.utility.security;

import com.plovdev.pornviewer.utility.files.EnvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

public class CipherManager {
    private final static Logger logger = LoggerFactory.getLogger(CipherManager.class);
    private static final String ALG = "AES/CBC/PKCS5Padding";
    private static final String ALG_NP = "AES/CTR/NoPadding";
    private final SecretKey key;
    private final IvParameterSpec spec;

    public CipherManager(String password) {
        try {
            // Генерируем ключ из пароля
            key = generateKeyFromPassword(password);

            // Фиксированный IV на основе пароля (или генерируем и сохраняем)
            spec = generateIvFromPassword(password);

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
        return EnvReader.getEnv("VIDEO_PASSWORD");
    }

    private SecretKey generateKeyFromPassword(String password) throws Exception {
        byte[] salt = "fixed-salt".getBytes(); // или генерируйте случайный и сохраняйте
        int iterations = 65536;
        int keyLength = 256;

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    private IvParameterSpec generateIvFromPassword(String password) throws Exception {
        // Генерируем IV на основе пароля
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        byte[] iv = Arrays.copyOfRange(hash, 0, 16);
        return new IvParameterSpec(iv);
    }

    public byte[] encrypt(byte[] to) {
        try {
            Cipher cipher = Cipher.getInstance(ALG_NP);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            return cipher.doFinal(to);
        } catch (Exception e) {
            logger.error("Произошла ошибка шифрования байтов: {}", e.getMessage());
        }
        return to;
    }

    public String encrypt(String to) {
        try {
            Cipher cipher = Cipher.getInstance(ALG);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);

            byte[] encrypted = cipher.doFinal(to.getBytes());
            return Base64.getUrlEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            logger.error("Произошла ошибка шифрования коючей: {}", e.getMessage());
        }
        return to;
    }

    public String decrypt(String from) {
        try {
            Cipher cipher = Cipher.getInstance(ALG);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);

            byte[] decoded = Base64.getUrlDecoder().decode(from);
            byte[] decrypted = cipher.doFinal(decoded);

            return new String(decrypted);
        } catch (Exception e) {
            logger.error("Произошла ошибка расшифровки ключей: {}", e.getMessage());
        }
        return from;
    }

    public byte[] decrypt(byte[] from) {
        try {
            Cipher cipher = Cipher.getInstance(ALG_NP);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            return cipher.doFinal(from);
        } catch (Exception e) {
            logger.error("Произошла ошибка расшифровки байтов: {}", e.getMessage());
        }
        return from;
    }
}