package com.plovdev.pornviewer;

import com.plovdev.pornviewer.databases.SecureDB;
import com.plovdev.pornviewer.encryptsupport.CipherEngineUtils;
import com.plovdev.pornviewer.encryptsupport.CryptoEngine;
import com.plovdev.pornviewer.utility.files.EnvReader;
import com.plovdev.pornviewer.utility.security.CipherManager;
import com.plovdev.pornviewer.utility.security.VideoCipherrer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

public class Test {
    private static final Logger log = LoggerFactory.getLogger(Test.class);
    private static final VideoCipherrer cipher = new VideoCipherrer(CipherManager.getPassword());

    public static void main(String[] args) throws Exception {
        String testdata = "Hello, world!";

        String password = EnvReader.getEnv("VIDEO_PASSWORD"); // test password
        byte[] nonce = new byte[8];
        CipherEngineUtils.createRandomPassword(nonce);

        CryptoEngine engine = new CryptoEngine(Cipher.ENCRYPT_MODE, password.toCharArray(), nonce);
        byte[] enc = engine.processChunk(0, testdata.getBytes(StandardCharsets.UTF_8));
        log.info("Enc: {}", new String(enc));

        engine.setMode(Cipher.DECRYPT_MODE);
        byte[] dec = engine.processChunk(0, enc);
        log.info("Dec: {}", new String(dec));
    }

    public static void encryptDatabase(String newPassword) {
        try {
            Connection conn = SecureDB.initCipherer();
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA rekey = '" + newPassword + "'");
                System.out.println("✅ База успешно зашифрована");
            }
            conn.close();
        } catch (Exception e) {
            System.err.println("❌ Ошибка при шифровании: " + e.getMessage());
        }
    }
}