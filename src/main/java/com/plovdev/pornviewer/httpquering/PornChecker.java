package com.plovdev.pornviewer.httpquering;

public interface PornChecker {
    boolean hasVideo();
    boolean hasModels();
    boolean hasCategories();
    boolean hasComments();
    boolean hasModelInfo();
    boolean canSearch();
}