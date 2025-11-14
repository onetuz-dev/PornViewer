package com.plovdev.pronviewer.models;

public class ModelInfo {
    private String url;
    private String country;
    private String avatar;
    private int videos;
    private String rusName;
    private String engName;

    public ModelInfo() {}

    public ModelInfo(String url, String country, String avatar, int videos, String rusName, String engName) {
        this.url = url;
        this.country = country;
        this.avatar = avatar;
        this.videos = videos;
        this.rusName = rusName;
        this.engName = engName;
    }

    @Override
    public String toString() {
        return "ModelInfo{" +
                "url='" + url + '\'' +
                ", country='" + country + '\'' +
                ", avatar='" + avatar + '\'' +
                ", videos=" + videos +
                ", rusName='" + rusName + '\'' +
                ", engName='" + engName + '\'' +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getVideos() {
        return videos;
    }

    public void setVideos(int videos) {
        this.videos = videos;
    }

    public String getRusName() {
        return rusName;
    }

    public void setRusName(String rusName) {
        this.rusName = rusName;
    }

    public String getEngName() {
        return engName;
    }

    public void setEngName(String engName) {
        this.engName = engName;
    }
}