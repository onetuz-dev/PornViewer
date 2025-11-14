package com.plovdev.pronviewer;

import com.plovdev.pronviewer.databases.FavoriteVideos;
import com.plovdev.pronviewer.models.VideoCard;
import com.plovdev.pronviewer.pornimpl.porn365.DefRes;

public class Test {
    public static void main(String[] args) throws Exception {
        for (VideoCard card : FavoriteVideos.getAll()) {
            FavoriteVideos.update("url", card.getUrl().replace(DefRes.BASE4, DefRes.BASE5), card.getId());
        }
    }
}