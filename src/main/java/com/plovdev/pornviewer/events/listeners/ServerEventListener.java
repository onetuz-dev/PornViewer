package com.plovdev.pornviewer.events.listeners;

import java.net.InetSocketAddress;
import java.net.URI;

public interface ServerEventListener {
    void onServerStarted();
    void onError(Exception e);
    void onAdressAlreadyInUse(InetSocketAddress address);
    void onServerStopped();
    void onServerRequested(URI path);
}