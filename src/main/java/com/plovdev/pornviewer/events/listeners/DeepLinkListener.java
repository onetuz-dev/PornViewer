package com.plovdev.pornviewer.events.listeners;

import com.plovdev.pornviewer.events.DeepLinkEvent;
import com.plovdev.pornviewer.utility.deeplink.Deeplink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class DeepLinkListener {
    private static final Map<String, List<DeepLinkEvent>> listeners = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(DeepLinkListener.class);

    private DeepLinkListener() {
        throw new UnsupportedOperationException();
    }

    public static void addListener(String host, DeepLinkEvent event) {
        Objects.requireNonNull(host);
        Objects.requireNonNull(event);
        listeners.computeIfAbsent(host, s -> new ArrayList<>()).add(event);
    }
    public static void removeListener(String host, DeepLinkEvent event) {
        Objects.requireNonNull(host);
        Objects.requireNonNull(event);
        listeners.computeIfAbsent(host, s -> new ArrayList<>()).remove(event);
    }

    public static void notifyListener(String host, URI link) {
        List<DeepLinkEvent> events = listeners.computeIfAbsent(host, h -> new ArrayList<>());
        for (DeepLinkEvent event : events) {
            try {
                event.onDeepLink(new Deeplink(link));
            } catch (Exception e) {
                log.error("Deeplink processing error: ", e);
            }
        }
    }
}