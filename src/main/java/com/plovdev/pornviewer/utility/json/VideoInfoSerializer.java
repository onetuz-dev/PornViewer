package com.plovdev.pornviewer.utility.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.plovdev.pornviewer.models.DownloadedVideoInfo;
import com.plovdev.pornviewer.models.VideoInfo;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class VideoInfoSerializer {

    public static String serializeInfo(VideoInfo info) {
        return serializeInfo(info.getTitle(), info.getDescription(), info.getUrl(), info.getDuration(), info.getTags(), info.getTimeCodes());
    }

    public static String serializeInfo(String title, String description, String url, Duration duration, Map<String, String> tags, Map<String, String> timecodes) {
        JsonObject infoObject = new JsonObject();
        infoObject.addProperty("title", title);
        infoObject.addProperty("description", description);
        infoObject.addProperty("url", url);
        infoObject.addProperty("duration", duration.toString());

        JsonArray tagsArray = new JsonArray();
        for (String tag : tags.keySet()) {
            tagsArray.add(tag);
        }
        infoObject.add("tags", tagsArray);

        JsonArray timecodesArray = new JsonArray();
        for (String text : timecodes.keySet()) {
            JsonObject timecode = new JsonObject();
            timecode.addProperty("time", timecodes.get(text));
            timecode.addProperty("text", text);
        }
        infoObject.add("timecodes", timecodesArray);

        return JSONSerializer.GSON.toJson(infoObject);
    }

    public static String serializeInfo(String title, String description, String url, Duration duration, List<String> tags, List<DownloadedVideoInfo.Timecode> timecodes) {
        JsonObject infoObject = new JsonObject();
        infoObject.addProperty("title", title);
        infoObject.addProperty("description", description);
        infoObject.addProperty("url", url);
        infoObject.addProperty("duration", duration.toString());

        JsonArray tagsArray = new JsonArray();
        for (String tag : tags) {
            tagsArray.add(tag);
        }
        infoObject.add("tags", tagsArray);

        JsonArray timecodesArray = new JsonArray();
        for (DownloadedVideoInfo.Timecode timecode : timecodes) {
            JsonObject timecodeObj = new JsonObject();
            timecodeObj.addProperty("time", timecode.getTime().toString());
            timecodeObj.addProperty("text", timecode.getText());
        }
        infoObject.add("timecodes", timecodesArray);

        return JSONSerializer.GSON.toJson(infoObject);
    }

    public static DownloadedVideoInfo deserializeInfo(String json) {
        return JSONSerializer.GSON.fromJson(json, DownloadedVideoInfo.class);
    }
}