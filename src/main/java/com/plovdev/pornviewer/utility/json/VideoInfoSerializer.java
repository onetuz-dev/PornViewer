package com.plovdev.pornviewer.utility.json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.plovdev.pornviewer.models.VideoInfo;

public class VideoInfoSerializer {
    private static final Gson GSON = new Gson();

    public static String serializeInfo(VideoInfo info) {
        JsonObject infoObject = new JsonObject();

        return GSON.toJson(infoObject);
    }
}