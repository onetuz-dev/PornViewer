package com.plovdev.pornviewer;

import com.plovdev.pornviewer.databases.FavoriteVideos;
import com.plovdev.pornviewer.databases.UserPreferences;
import com.plovdev.pornviewer.gui.MainMenu;
import com.plovdev.pornviewer.server.SafeHttpServer;
import com.plovdev.pornviewer.utility.files.FileUtils;
import javafx.application.Application;

import java.nio.file.Files;
import java.nio.file.Path;

public class Launcher {
    static {
        System.setProperty("sun.net.httpserver.maxReqTime", "600");
        System.setProperty("sun.net.httpserver.maxRspTime", "600");
    }
    public static void main(String[] args) throws Exception {
        System.out.println("Start app");
        SafeHttpServer server = SafeHttpServer.getInstance();
        server.startServer();

        try {
            Path downloadsPath = FileUtils.getPvDownloadsPath();
            if (!Files.exists(downloadsPath)) {
                Files.createDirectories(downloadsPath);
            }
            Path systemPath = Path.of(FileUtils.getPvSystemPath());
            if (!Files.exists(systemPath)) {
                Files.createDirectories(systemPath);
            }
            Path dbFile = Path.of(FileUtils.getPvDbPath());
            if (!Files.exists(dbFile)) {
                Files.createFile(dbFile);
            }
            FavoriteVideos.createTable();
            UserPreferences.createTable();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        Application.launch(MainMenu.class, args);
    }
}