package com.plovdev.pornviewer.httpquering;

import java.net.http.HttpRequest;

/**
 * Интерфейс, предоставляющий методы, для запроса к контент-сайту
 */
public interface PornHandler {
    String requestPorn(String url);
    void downloadPorn(String url, String out);
    void setRandomHeaders(HttpRequest request);
    String getNextLink(String html);
}