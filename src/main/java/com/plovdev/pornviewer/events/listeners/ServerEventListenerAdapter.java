package com.plovdev.pornviewer.events.listeners;

import java.net.InetSocketAddress;
import java.net.URI;

public abstract class ServerEventListenerAdapter implements ServerEventListener {
    @Override
    public void onServerStarted() {

    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onAdressAlreadyInUse(InetSocketAddress address) {

    }

    @Override
    public void onServerStopped() {

    }

    @Override
    public void onServerRequested(URI path) {

    }
}