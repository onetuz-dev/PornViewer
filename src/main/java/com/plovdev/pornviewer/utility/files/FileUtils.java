package com.plovdev.pornviewer.utility.files;

import com.plovdev.pornviewer.utility.security.CipherManager;
import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class FileUtils {
    public final static String PORN_VIEWER_SIGN = "b29a674cce9b3fff1010a658070c8933";
    public final static String PV_BASE_PATH = System.getProperty("user.home") + "/PornViewer/";
    public final static String PV_DOWNLOADS = "downloads/";
    public final static String PV_SYSTEM = "system/";
    public final static String PV_DB_PATH = CipherManager.md5("pornviewer.db");

    public static String getPVBasePath() {
        return PV_BASE_PATH;
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
        return getPvSystemPath() + PV_DB_PATH;
    }
    @NotNull
    public static String replaceFileToHttpPath(String file) {
        file = file.substring(file.lastIndexOf("/"));
        boolean needDecrypt = file.endsWith("b29a674cce9b3fff1010a658070c8933");
        return String.format("http://127.0.0.1:3535/video?file=%s&needDecrypt=%s", URLEncoder.encode(file, StandardCharsets.UTF_8), needDecrypt);
    }
    @NotNull
    public static String getPVJDBCPathProtocol() {
        return "jdbc:sqlite:" + getPvDbPath();
    }
}