package com.plovdev.pornviewer.events.listeners;

import com.plovdev.pornviewer.events.ClickEvent;
import com.plovdev.pornviewer.models.ModelInfo;

import java.util.ArrayList;

public class ClickListener {
    private final ArrayList<ClickEvent> events = new ArrayList<>();
    public ClickListener() {}

    public void addListener(ClickEvent e) {
        events.add(e);
    }
    public void removeListener(ClickEvent e) {
        events.remove(e);
    }

    public void notifyListeners(ModelInfo info) {
        for (ClickEvent e : events) e.onClick(info);
    }
}