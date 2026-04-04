package com.plovdev.pornviewer.utility.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.time.Duration;

public class JSONSerializer {
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(Duration.class, new DurationTypeAdapter()).create();

    public static String serialize(Object o) {
        return GSON.toJson(o);
    }

    public static <V> V deserialize(String json, Class<V> type) {
        return GSON.fromJson(json, type);
    }

    public static String serializeObject(Object o) {
        JsonObject infoObject = new JsonObject();
        parseAnnotations(o.getClass(), infoObject);
        return GSON.toJson(infoObject);
    }

    private static void parseAnnotations(Class<?> cls, JsonObject object) {
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(SerializeJson.class)) {
                field.setAccessible(true);
                SerializeJson serializeJson = field.getAnnotation(SerializeJson.class);
                String name = field.getName();
                if (serializeJson.name() != null && !serializeJson.name().isEmpty()) {
                    name = serializeJson.name();
                }
                Class<?> type = field.getType();
                try {
                    object.addProperty(name, field.get(type).toString());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}