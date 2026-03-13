package com.plovdev.pv.adapter;

import java.util.List;

public interface PornViewerAdapter {
    Resourcer getAdapterResourcer();
    PornParser getAdapterParser();
    List<PornParseAction> getSupportedActions();
}