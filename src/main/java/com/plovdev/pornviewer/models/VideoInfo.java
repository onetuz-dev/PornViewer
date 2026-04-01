package com.plovdev.pornviewer.models;

import java.util.List;
import java.util.Map;

public class VideoInfo {
    private List<Comment> comments;

    private int views;
    private String rating;
    private Map<String, String> urls;
    private Map<String, String> timeCodes;
    private Map<String, String> tags;
    private Map<String, String> models;
    private Map<String, String> categories;
    private String duration;
    private String pic;
    private int id;
    private String description;
    private String title;
    private String url;

    public VideoInfo() {

    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
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

    @Override
    public String toString() {
        return "VideoInfo{" +
                "rating='" + rating + '\'' +
                ", views=" + views +
                ", duration='" + duration + '\'' +
                ", urls=" + urls +
                ", categories=" + categories +
                ", models=" + models +
                ", tags=" + tags +
                ", timeCodes=" + timeCodes +
                ", comments=" + comments +
                ", pic='" + pic + '\'' +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}