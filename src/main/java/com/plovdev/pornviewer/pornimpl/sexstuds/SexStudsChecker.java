package com.plovdev.pornviewer.pornimpl.sexstuds;

import com.plovdev.pornviewer.httpquering.PornChecker;

public class SexStudsChecker implements PornChecker {
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
        return false;
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