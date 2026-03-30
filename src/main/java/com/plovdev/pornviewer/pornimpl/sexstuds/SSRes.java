package com.plovdev.pornviewer.pornimpl.sexstuds;

import com.plovdev.pornviewer.httpquering.Resourcer;

import java.util.List;

public class SSRes implements Resourcer {
    @Override
    public String baseUrl() {
        return "https://sex-studentki.live";
    }

    @Override
    public String searchUrl() {
        return "/search";
    }

    @Override
    public String modelsUrl() {
        return "/models";
    }

    @Override
    public List<String> getUrls() {
        return List.of();
    }

    @Override
    public String categories() {
        return "/categories";
    }

    @Override
    public String videoUrl() {
        return "/moovie";
    }

    @Override
    public String modelsSearchUrl() {
        return "/search/";
    }

    @Override
    public String modelUrl(String model) {
        return modelsUrl() + model;
    }

    @Override
    public String buildVideoUrlFromId(int id) {
        return baseUrl() + videoUrl() + id;
    }
}