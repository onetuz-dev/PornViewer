package com.plovdev.pornviewer;

import com.plovdev.pornviewer.databases.FavoriteVideos;
import com.plovdev.pornviewer.models.VideoCard;
import com.plovdev.pornviewer.pornimpl.porn365.DefRes;

public class Test {
    public static void main(String[] args) throws Exception {
        for (VideoCard card : FavoriteVideos.getAll()) {
            FavoriteVideos.update("url", card.getUrl().replace(DefRes.BASE5, DefRes.BASE6), card.getId());
        }
    }
}