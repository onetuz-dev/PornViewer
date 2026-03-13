package com.plovdev.pornviewer.utility.files;

import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class FileUtils {
    public final static String PORN_VIEWER_SIGN = "b29a674cce9b3fff1010a658070c8933";
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
    @NotNull
    public static Path getPvDownloadsPath() {
        return Path.of(getPVBasePath() + PV_DOWNLOADS);
    }
    @NotNull
    public static String getPvSystemPath() {
        return getPVBasePath() + PV_SYSTEM;
    }
    @NotNull
    public static String getPvDbPath() {
        return getPVBasePath() + PV_DB_PATH;
    }
    @NotNull
    public static String replaceFileToHttpPath(String file) {
        file = file.substring(file.lastIndexOf("/"));
        boolean needDecrypt = file.endsWith("b29a674cce9b3fff1010a658070c8933");
        return String.format("http://127.0.0.1:3535/video?file=%s&needDecrypt=%s", URLEncoder.encode(file, StandardCharsets.UTF_8), needDecrypt);
    }
    @NotNull
    public static String getPVJDBCPathProtocol() {
        return "jdbc:sqlite:" + getPvSystemPath() + PV_DB_PATH;
    }
}