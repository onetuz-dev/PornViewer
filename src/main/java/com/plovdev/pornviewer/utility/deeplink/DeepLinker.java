package com.plovdev.pornviewer.utility.deeplink;

import com.plovdev.pornviewer.utility.LauncherHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URI;

public class DeepLinker {
    private static final Logger log = LoggerFactory.getLogger(DeepLinker.class);

    public static void init(LauncherHelper launcherHelper) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.setOpenURIHandler(event -> {
                    URI deeplink = event.getURI();
                    if (deeplink != null) {
                        launcherHelper.notifyDeepLink(deeplink);
                    }
                });
            }
        } catch (Exception e) {
            log.debug("Error to init deepleenk handler: {}", e.getMessage());
        }
    }
}