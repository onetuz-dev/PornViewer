package com.plovdev.pornviewer;

import com.plovdev.pornviewer.gui.MainMenu;
import com.plovdev.pornviewer.server.SafeHttpServer;
import com.plovdev.pornviewer.utility.deeplink.DeepLinker;
import com.plovdev.pornviewer.utility.files.FileUtils;
import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Launcher {
    private static final Logger log = LoggerFactory.getLogger(Launcher.class);

    static {
        System.setProperty("sun.net.httpserver.maxReqTime", "600");
        System.setProperty("sun.net.httpserver.maxRspTime", "600");
    }

    public static void main(String[] args) throws Exception {
        try {
            if (Taskbar.isTaskbarSupported()) {
                Taskbar taskbar = Taskbar.getTaskbar();
                taskbar.setIconImage(ImageIO.read(Objects.requireNonNull(Launcher.class.getResourceAsStream("/com/plovdev/pornviewer/pv-logo.png"))));
            }
        } catch (Exception e) {
            log.error("Error setup image icon: ", e);
        }

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
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        log.info("Launching pv...");
        DeepLinker.init();
        Application.launch(MainMenu.class, args);
    }
}