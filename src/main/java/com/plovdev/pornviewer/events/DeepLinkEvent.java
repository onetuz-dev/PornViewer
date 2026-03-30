package com.plovdev.pornviewer.events;

import com.plovdev.pornviewer.utility.deeplink.Deeplink;

public interface DeepLinkEvent {
    void onDeepLink(Deeplink link);
}