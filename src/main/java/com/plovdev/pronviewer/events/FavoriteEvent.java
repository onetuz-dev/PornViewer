package com.plovdev.pronviewer.events;

import com.plovdev.pronviewer.models.VideoCard;

public interface FavoriteEvent {
    void favorite(VideoCard card);
}