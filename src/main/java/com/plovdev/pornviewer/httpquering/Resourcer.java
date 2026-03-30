package com.plovdev.pornviewer.httpquering;

import java.util.List;

public interface Resourcer {
    //https://sex-studentki.live/hq-porn
    String baseUrl();
    String searchUrl();
    String modelsSearchUrl();
    String modelsUrl();
    String modelUrl(String model);
    List<String> getUrls();
    String categories();
    String videoUrl();
    String buildVideoUrlFromId(int id);
}