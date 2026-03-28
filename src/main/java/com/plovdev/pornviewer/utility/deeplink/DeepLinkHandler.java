package com.plovdev.pornviewer.utility.deeplink;

import com.sun.glass.ui.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class DeepLinkHandler extends Application.EventHandler {
    private static final Logger log = LoggerFactory.getLogger(DeepLinkHandler.class);

    @Override
    public void handleOpenFilesAction(Application application, long l, String[] strings) {
        super.handleOpenFilesAction(application, l, strings);
        log.info("Opening: {}", Arrays.toString(strings));
        //TODO
    }
}