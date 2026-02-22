package com.plovdev.pornviewer.models;

import javafx.scene.layout.Pane;

public abstract class PornCard extends Pane {
    protected int id;
    protected String title;
    protected String url;
    protected String pic;

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

    public int getCardId() {
        return id;
    }

    public void setCardId(int id) {
        this.id = id;
    }

    public abstract Pane display();

    @Override
    public String toString() {
        return "PornCard{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", pic='" + pic + '\'' +
                '}';
    }
}