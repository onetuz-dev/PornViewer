package com.plovdev.pronviewer.httpquering;

public interface PornChecker {
    boolean hasVideo();
    boolean hasModels();
    boolean hasCategories();
    boolean hasComments();
    boolean hasModelInfo();
    boolean canSearch();
}