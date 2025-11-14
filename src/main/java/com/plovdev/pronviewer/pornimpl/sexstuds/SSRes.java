package com.plovdev.pronviewer.pornimpl.sexstuds;

import com.plovdev.pronviewer.httpquering.Resourcer;

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
}