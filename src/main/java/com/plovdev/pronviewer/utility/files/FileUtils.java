package com.plovdev.pronviewer.utility.files;

import java.nio.file.Path;

public class FileUtils {
    public final static String PV_BASE_PATH_WIN = System.getProperty("user.home") + "/PornViewer/";
    public final static String PV_BASE_PATH_MAC = "/Users/mac/PornViewer/";
    public final static String PV_BASE_PATH_UNIX = "/usr/PornViewer/";
    public final static String PV_DOWNLOADS = "downloads/";
    public final static String PV_SYSTEM = "system/";
    public final static String PV_DB_PATH = "pornviewer.db";

    public static String getPVBasePath() {
        String platform = System.getProperty("os.name").toLowerCase().replace(" ", "");
        if (platform.contains("mac")) {
            return PV_BASE_PATH_MAC;
        } else if (platform.contains("win")) {
            return PV_BASE_PATH_WIN;
        } else {
            return PV_BASE_PATH_UNIX;
        }
    }
    public static Path getPvDownloadsPath() {
        return Path.of(getPVBasePath() + PV_DOWNLOADS);
    }
    public static String getPvSystemPath() {
        return getPVBasePath() + PV_SYSTEM;
    }
    public static String getPvDbPath() {
        return getPVBasePath() + PV_DB_PATH;
    }
    public static String getPVJDBCPathProtocol() {
        return "jdbc:sqlite:" + getPvSystemPath() + PV_DB_PATH;
    }
}