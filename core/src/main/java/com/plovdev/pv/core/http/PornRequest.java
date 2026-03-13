package com.plovdev.pv.core.http;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public record PornRequest(String path, Map<String, String> headers) {
    public static PornRequest ofPath(String path) {
        return new PornRequest(path, Map.of());
    }

    public PornRequest {
        Objects.requireNonNull(path);
        Objects.requireNonNull(headers);

        headers = Map.copyOf(headers);
    }

    @Override
    public Map<String, String> headers() {
        return Map.copyOf(headers);
    }

    @NotNull
    @Override
    public String toString() {
        return String.format("[Request]: Path: %s", path());
    }
}