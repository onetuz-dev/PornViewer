package com.plovdev.pornviewer.events.listeners;

import com.plovdev.pornviewer.events.FavoriteEvent;
import com.plovdev.pornviewer.models.VideoCard;

import java.util.ArrayList;

public class FavoriteListener {
    private static final ArrayList<FavoriteEvent> events = new ArrayList<>();
    private FavoriteListener() {}

    public static void addListener(FavoriteEvent e) {
        events.add(e);
    }
    public static void removeListener(FavoriteEvent e) {
        events.remove(e);
    }

    public static void notifyListeners(VideoCard card) {
        for (FavoriteEvent e : events) e.favorite(card);
    }
}
