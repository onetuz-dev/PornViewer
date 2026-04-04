package com.plovdev.pornviewer.utility.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.plovdev.pornviewer.models.VideoInfo;

import java.util.Map;

public class VideoInfoSerializer {
    private static final Gson GSON = new Gson();

    public static String serializeInfo(VideoInfo info) {
        JsonObject infoObject = new JsonObject();
        infoObject.addProperty("title", info.getTitle());
        infoObject.addProperty("description", info.getDescription());
        infoObject.addProperty("url", info.getUrl());
        infoObject.addProperty("duration", info.getDuration().toString());

        JsonArray tags = new JsonArray();
        for (String tag : info.getTags().keySet()) {
            tags.add(tag);
        }
        infoObject.add("tags", tags);

        JsonArray timecodes = new JsonArray();
        Map<String, String> timecodesMap = info.getTimeCodes();
        for (String text : timecodesMap.keySet()) {
            JsonObject timecode = new JsonObject();
            timecode.addProperty("time", timecodesMap.get(text));
            timecode.addProperty("text", text);
        }
        infoObject.add("timecodes", timecodes);

        return GSON.toJson(infoObject);
    }
}