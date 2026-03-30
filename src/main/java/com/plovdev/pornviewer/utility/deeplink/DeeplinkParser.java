package com.plovdev.pornviewer.utility.deeplink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class DeeplinkParser {
    private static final Logger log = LoggerFactory.getLogger(DeeplinkParser.class);

    public static void parseDeeplink(URI uri) {
        String link = uri.toString();
        if (!(link.startsWith("pv://") || link.startsWith("pornviewer://"))) {
            log.warn("Not PV Deeplink: {}", link);
            return;
        }

    }
}