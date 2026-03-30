package com.plovdev.pornviewer.utility.sharing;

public class ShareParameter {
    private String name;
    private String value;

    public ShareParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public ShareParameter() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return name + "=" + value;
    }
}