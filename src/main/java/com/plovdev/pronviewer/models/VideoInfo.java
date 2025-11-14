package com.plovdev.pronviewer.models;

import java.util.List;
import java.util.Map;

public class VideoInfo {
    private Map<String, String> urls;
    private Map<String, String> categories;
    private Map<String, String> models;
    private String description;
    private Map<String, String> tags;
    private Map<String, String> timeCodes;
    private List<Comment> comments;
    private String title;
    private int id;
    private String url;

    public VideoInfo() {

    }

    @Override
    public String toString() {
        return "VideoInfo{" +
                "urls=" + urls +
                ", categories=" + categories +
                ", models=" + models +
                ", description='" + description + '\'' +
                ", tags=" + tags +
                ", timeCodes=" + timeCodes +
                ", comments=" + comments +
                '}';
    }


    public Map<String, String> getCategories() {
        return categories;
    }

    public void setCategories(Map<String, String> categories) {
        this.categories = categories;
    }

    public Map<String, String> getUrls() {
        return urls;
    }

    public void setUrls(Map<String, String> urls) {
        this.urls = urls;
    }

    public Map<String, String> getModels() {
        return models;
    }

    public void setModels(Map<String, String> models) {
        this.models = models;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    public Map<String, String> getTimeCodes() {
        return timeCodes;
    }

    public void setTimeCodes(Map<String, String> timeCodes) {
        this.timeCodes = timeCodes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}