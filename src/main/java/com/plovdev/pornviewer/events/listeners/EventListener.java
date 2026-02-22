package com.plovdev.pornviewer.events.listeners;

import com.plovdev.pornviewer.events.Event;

import java.util.ArrayList;

public class EventListener {
    private static final ArrayList<Event> events = new ArrayList<>();
    private EventListener() {}

    public static void addListener(Event e) {
        events.add(e);
    }
    public static void removeListener(Event e) {
        events.remove(e);
    }

    public static void notifyListeners(String type) {
        for (Event e : events) e.onEvent(type);
    }
}