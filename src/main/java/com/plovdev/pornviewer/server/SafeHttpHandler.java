package com.plovdev.pornviewer.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.plovdev.pornviewer.server.ContentUtils.checkFile;

public class SafeHttpHandler implements HttpHandler {
    private static final String HEAD = "HEAD";
    private static final String GET = "GET";
    private static final Logger log = LoggerFactory.getLogger(SafeHttpHandler.class);

    @Override
    public void handle(HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        log.info("Handling request. Method: {}", method);
        if (!checkMethod(method)) return;

        processRequest(exchange);
    }

    private void processRequest(HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        Map<String, String> params = parseRequest(exchange.getRequestURI().getQuery());
        if (method.equals(HEAD)) {
            executeHead(exchange, params);
        } else if (method.equals(GET)) {
            try {
                String range = exchange.getRequestHeaders().get("Range").getFirst();
                if (range == null) {
                    throw new NullPointerException("Range is null");
                }
                executeGet(exchange, parseChunk(range), params);
            } catch (Exception e) {
                log.error("Error to process get request: ", e);
            }
        }
    }

    private void executeHead(HttpExchange exchange, Map<String, String> params) {
        try {
            File file = checkFile(exchange, params);
            log.info("Sending head request. File: {}", file);

            exchange.getResponseHeaders().set("Accept-Ranges", "bytes");
            exchange.getResponseHeaders().set("Content-Type", "video/mp4");
            exchange.getResponseHeaders().set("Content-Length", String.valueOf(file.length()));
            exchange.sendResponseHeaders(200, -1);
        } catch (Exception e) {
            log.error("Head processing error: ", e);
        }
    }

    private void executeGet(HttpExchange exchange, Chunk chunk, Map<String, String> params) {
        try {
            File file = checkFile(exchange, params);
            log.info("Process GET request. Chunk: {}", chunk);
            ContentUtils.sendFileRange(exchange, chunk, file, Boolean.parseBoolean(params.get("needDecrypt")));
        } catch (Exception e) {
            log.error("GET processing error: ", e);
        }
    }

    private boolean checkMethod(String method) {
        return "GET".equals(method) || "HEAD".equals(method);
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

    private Chunk parseChunk(String range) {
        range = range.substring(6); // starts with bytes=

        long start = Integer.parseInt(range.substring(0, range.lastIndexOf("-")));
        long end = Integer.parseInt(range.substring(range.lastIndexOf("-") + 1));
        return new Chunk(start, end);
    }
}