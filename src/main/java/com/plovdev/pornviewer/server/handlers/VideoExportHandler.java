package com.plovdev.pornviewer.server.handlers;

import com.google.gson.JsonObject;
import com.plovdev.pornviewer.encryptionsupport.videoparser.videomodel.VideoHeader;
import com.plovdev.pornviewer.server.utils.ContentUtils;
import com.plovdev.pornviewer.server.utils.VideoRequestSet;
import com.plovdev.pornviewer.utility.json.JSONSerializer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class VideoExportHandler implements HttpHandler {
    private static final Logger log = LoggerFactory.getLogger(VideoExportHandler.class);

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
                exchange.sendResponseHeaders(405, -1);
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

            File file = new File(body);
            try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
                VideoRequestSet set = SafeHttpHandler.getCachedOrCreateSet(file);
                ContentUtils.sendDecryptedRange(file, set.getEncryptedVideo(), 0, calculateContentLength(set, file, exchange), os, set);
                sendResponse(exchange, 200, formJsonInfo());
            } catch (Exception e) {
                sendResponse(exchange, 500, "Internal Server Error");
                log.error("Error export video: ", e);
            }

        } catch (Exception e) {
            log.error("Error handling deeplink", e);
            sendResponse(exchange, 500, "Internal Server Error");
        } finally {
            exchange.close();
        }
    }

    private long calculateContentLength(VideoRequestSet set, File file, HttpExchange exchange) throws IOException {
        long end = set.getEncryptedVideo().getVideoHeader().plainVideoSize();
        long realStart = VideoHeader.HEADER_SIZE;
        long encVideoLength = set.getEncryptedVideo().getVideoHeader().encVideoSize();

        if (end >= encVideoLength) {
            end = encVideoLength - 1;
        }
        long realEnd = realStart + end;
        if (realStart >= file.length() || realStart >= (realStart + encVideoLength)) {
            exchange.sendResponseHeaders(416, -1);
            return 0;
        }

        if (realEnd >= file.length()) {
            realEnd = file.length() - 1;
        }

        return realEnd - realStart + 1;
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
        info.addProperty("exported", true);
        return JSONSerializer.GSON.toJson(info);
    }

    private boolean checkMethod(String method) {
        return "POST".equals(method);
    }
}