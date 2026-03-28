package com.plovdev.pornviewer.utility;

import com.google.gson.Gson;

public class JSONSerializer {
    private static final Gson GSON = new Gson();
    public static String serialize(Object o) {
        return GSON.toJson(o);
    }
    public static <V> V deserialize(String json, Class<V> type) {
        return GSON.fromJson(json, type);
    }
}