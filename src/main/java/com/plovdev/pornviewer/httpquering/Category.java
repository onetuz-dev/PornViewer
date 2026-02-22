package com.plovdev.pornviewer.httpquering;

import java.util.Objects;

public class Category {
    private String url;
    private String name;

    public Category() {
    }

    public Category(String name, String url) {
        this.url = url;
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("Category: %s", name);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(url, category.url) && Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, name);
    }
}