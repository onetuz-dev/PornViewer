package com.plovdev.pornviewer.databases;

import com.plovdev.pornviewer.utility.files.EnvReader;
import com.plovdev.pornviewer.utility.files.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class SecureDB {
    private static final Logger log = LoggerFactory.getLogger(SecureDB.class);

    public static Connection initCipherer() {
        try {
            Class.forName("org.sqlite.JDBC");
            String password = EnvReader.getEnv("VIDEO_PASSWORD");
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