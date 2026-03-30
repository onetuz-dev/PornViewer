package com.plovdev.pornviewer.events.listeners;

import com.plovdev.pornviewer.events.ClickEvent;
import com.plovdev.pornviewer.models.ModelInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class ClickListener {
    private static final ArrayList<ClickEvent> events = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(ClickListener.class);

    private ClickListener() {}

    public static void addListener(ClickEvent e) {
        events.add(e);
    }
    public static void removeListener(ClickEvent e) {
        events.remove(e);
    }

    public static void notifyListeners(ModelInfo info) {
        for (ClickEvent e : events) {
            log.info("Уведомляем слушателя модели: {}", info.getUrl());
            e.onClick(info);
        }
    }
}