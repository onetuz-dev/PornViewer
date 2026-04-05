package com.plovdev.pornviewer.httpquering;

import java.io.InputStream;

public interface PornRequestProvider {
    String executeGet(PornRequest request);
    byte[] executeRaw(PornRequest request);
    String executePost(PornRequest request);
    InputStream requestStream(PornRequest request);
    long checkContentLength(PornRequest request);
}