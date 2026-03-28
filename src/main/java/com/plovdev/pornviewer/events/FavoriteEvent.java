package com.plovdev.pornviewer.events;

import com.plovdev.pornviewer.models.FavoriteVideo;

public interface FavoriteEvent {
    void favorite(FavoriteVideo card);
}