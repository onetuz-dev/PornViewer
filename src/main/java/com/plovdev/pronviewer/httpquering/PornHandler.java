package com.plovdev.pronviewer.httpquering;

import java.net.http.HttpRequest;

/**
 * Интерфейс, предоставляющий методы, для запроса к контент-сайту
 */
public interface PornHandler {
    // 47.79.95.136:1122 - прокси на заметку

    String requestPorn(String url);
    void downloadPorn(String url, String out);
    void setRandomHeaders(HttpRequest request);
    String getNextLink(String html);
}