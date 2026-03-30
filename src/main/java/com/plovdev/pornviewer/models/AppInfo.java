package com.plovdev.pornviewer.models;

import com.google.gson.annotations.SerializedName;

public class AppInfo {
    @SerializedName("version")
    private String version;

    public AppInfo(String version) {
        this.version = version;
    }

    public AppInfo() {
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}