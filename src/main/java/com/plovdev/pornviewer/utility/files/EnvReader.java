package com.plovdev.pornviewer.utility.files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class EnvReader {
    private static final Properties properties = new Properties();
    private static final Logger log = LoggerFactory.getLogger(EnvReader.class);

    static {
        try {
            properties.load(EnvReader.class.getResourceAsStream("/.env"));
        } catch (Exception e) {
            log.error("Error to load properties, using fallback. ", e);
            properties.put("VERSION", "PornViewer-2026w6");
        }
    }
    public static String getEnv(String path) {
        return properties.getProperty(path);
    }
}