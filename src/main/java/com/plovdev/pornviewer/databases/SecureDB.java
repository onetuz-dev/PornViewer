package com.plovdev.pornviewer.databases;

import com.plovdev.pornviewer.utility.files.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

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
            return DriverManager.getConnection(FileUtils.getPVJDBCPathProtocol());
        } catch (Exception e) {
            log.error("Failed to initialize encrypted database", e);
            throw new RuntimeException(e);
        }
    }
}