package com.plovdev.pornviewer.events;

import com.plovdev.pornviewer.models.VideoCard;

public interface FavoriteEvent {
    void favorite(VideoCard card);
}