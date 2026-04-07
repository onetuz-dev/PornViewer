package com.plovdev.pornviewer.databases;

import com.plovdev.pornviewer.utility.files.FileUtils;
import com.plovdev.pornviewer.utility.security.CipherManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

public class SecureDB {
    private static final Logger log = LoggerFactory.getLogger(SecureDB.class);

    public static void initDB() {
        try (Connection conn = initCipherer();
             Statement st = conn.createStatement()) {
            st.execute("SELECT count(*) FROM sqlite_master");
        } catch (Exception e) {
            log.error("Error to init db: ", e);
        }
    }

    public static synchronized Connection initCipherer() {
        try {
            Class.forName("org.sqlite.JDBC");
            String password = CipherManager.getPassword();
            String url = FileUtils.getPVJDBCPathProtocol();
            Properties props = new Properties();
            props.setProperty("cipher", "chacha20");
            props.setProperty("key", password);
            props.setProperty("temp_store", "MEMORY");

            return DriverManager.getConnection(url, props);
        } catch (Exception e) {
            log.error("Failed to initialize encrypted database", e);
            throw new RuntimeException(e);
        }
    }
}