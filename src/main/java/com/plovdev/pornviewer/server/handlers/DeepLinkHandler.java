package com.plovdev.pornviewer.server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.plovdev.pornviewer.events.listeners.DeepLinkListener;
import com.plovdev.pornviewer.gui.MainMenu;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeepLinkHandler implements HttpHandler {
    private static final Logger log = LoggerFactory.getLogger(DeepLinkHandler.class);
    private static final Gson GSON = new Gson();
    private final List<URI> pendingLinks = new ArrayList<>();
    private boolean isMainAppStarted = false;

    public DeepLinkHandler() {
        MainMenu.setStartListener(() -> {
            isMainAppStarted = true;
            for (URI deeplink : pendingLinks) {
                DeepLinkListener.notifyListener(deeplink.getHost(), deeplink);
            }
        });
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Map<String, String> params = parseRequest(exchange.getRequestURI().getQuery());
        String token = params.get("token");
        if (token == null) {
            exchange.sendResponseHeaders(403, -1);
            return;
        }

        try {
            String method = exchange.getRequestMethod();
            if (!"POST".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                return;
            }

            byte[] bodyBytes;
            try (InputStream is = exchange.getRequestBody()) {
                bodyBytes = is.readAllBytes();
            }

            String body = new String(bodyBytes, StandardCharsets.UTF_8).trim();
            if (body.isEmpty()) {
                sendResponse(exchange, 400, "Empty body");
                return;
            }

            URI deeplink = URI.create(body);
            log.info("Accepted DL: {}", deeplink);

            // 2. Используем синхронизацию для списка
            synchronized (pendingLinks) {
                if (isMainAppStarted) {
                    // Важно: notifyListener должен внутри себя делать Platform.runLater!
                    DeepLinkListener.notifyListener(deeplink.getHost(), deeplink);
                } else {
                    pendingLinks.add(deeplink);
                }
            }

            // 3. Успешный ответ
            sendResponse(exchange, 200, formJsonInfo());

        } catch (Exception e) {
            log.error("Error handling deeplink", e);
            sendResponse(exchange, 500, "Internal Server Error");
        } finally {
            exchange.close();
        }
    }

    private void sendResponse(HttpExchange exchange, int rCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(rCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private Map<String, String> parseRequest(String request) {
        Map<String, String> params = new HashMap<>();
        String[] strings = request.split("&");

        for (String param : strings) {
            int firstEq = param.indexOf("=");
            if (firstEq > 0) {
                String name = param.substring(0, firstEq);
                String value = param.substring(firstEq + 1);
                params.put(name, value);
            }
        }
        return params;
    }

    private String formJsonInfo() {
        JsonObject info = new JsonObject();
        info.addProperty("notified", true);
        return GSON.toJson(info);
    }
}