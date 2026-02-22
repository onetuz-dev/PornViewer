package com.plovdev.pornviewer.pornimpl.porn365;

import com.plovdev.pornviewer.httpquering.PornChecker;

public class Porn365Checker implements PornChecker {
    @Override
    public boolean hasVideo() {
        return true;
    }

    @Override
    public boolean hasModels() {
        return true;
    }

    @Override
    public boolean hasCategories() {
        return true;
    }

    @Override
    public boolean hasComments() {
        return true;
    }

    @Override
    public boolean hasModelInfo() {
        return true;
    }

    @Override
    public boolean canSearch() {
        return true;
    }
}