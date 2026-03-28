package com.plovdev.pornviewer.models;


import com.google.gson.annotations.SerializedName;

public class FavoriteVideoInfo {
    @SerializedName("videoId")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("url")
    private String url;

    @SerializedName("picture")
    private String pic;

    @SerializedName("duration")
    private String duration;

    @SerializedName("views")
    private int views;

    @SerializedName("rating")
    private String rating;

    @SerializedName("videoInfo")
    private VideoInfo info;

    @SerializedName("favorie")
    private boolean isFavorite;

    @SerializedName("group")
    private String group;

    public FavoriteVideoInfo(int id, String title, String url, String pic, String duration, int views, String rating, VideoInfo info, boolean isFavorite, String group) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.pic = pic;
        this.duration = duration;
        this.views = views;
        this.rating = rating;
        this.info = info;
        this.isFavorite = isFavorite;
        this.group = group;
    }

    public FavoriteVideoInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public VideoInfo getInfo() {
        return info;
    }

    public void setInfo(VideoInfo info) {
        this.info = info;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}