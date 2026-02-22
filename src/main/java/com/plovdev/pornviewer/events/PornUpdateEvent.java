package com.plovdev.pornviewer.events;

public interface PornUpdateEvent {
    void onPageUpdate(String url, int type);
}