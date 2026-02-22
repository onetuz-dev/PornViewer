package com.plovdev.pornviewer.events.listeners;

import com.plovdev.pornviewer.events.PornUpdateEvent;

import java.util.ArrayList;

public class PornUpdateListener {
    private static final ArrayList<PornUpdateEvent> events = new ArrayList<>();
    private PornUpdateListener() {}

    public static void addListener(PornUpdateEvent e) {
        events.add(e);
    }
    public static void removeListener(PornUpdateEvent e) {
        events.remove(e);
    }

    public static void notifyListeners(String url, int type) {
        for (PornUpdateEvent e : events) e.onPageUpdate(url, type);
    }
}