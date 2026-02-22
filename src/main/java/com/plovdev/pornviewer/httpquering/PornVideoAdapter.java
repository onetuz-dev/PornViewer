package com.plovdev.pornviewer.httpquering;

public interface PornVideoAdapter {
    PornParser getParser();
    PornChecker getChecker();
    Resourcer getResourcer();
}