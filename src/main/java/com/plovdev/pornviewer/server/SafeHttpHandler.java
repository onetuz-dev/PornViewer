package com.plovdev.pornviewer.server;

import com.plovdev.pornviewer.encryptionsupport.videoparser.VideoMetadata;
import com.plovdev.pornviewer.encryptionsupport.videoparser.read.VideoReader;
import com.plovdev.pornviewer.utility.files.ServerPaths;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.plovdev.pornviewer.server.ContentUtils.checkFile;

public class SafeHttpHandler implements HttpHandler {
    private static final String HEAD = "HEAD";
    private static final String GET = "GET";
    private static final Logger log = LoggerFactory.getLogger(SafeHttpHandler.class);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Map<String, String> params = parseRequest(exchange.getRequestURI().getQuery());
        String token = params.get("token");
        if (token == null) {
            exchange.sendResponseHeaders(403, -1);
            return;
        }
        if (!token.equals(ServerPaths.getInstance().getToken())) {
            exchange.sendResponseHeaders(403, -1);
            return;
        }

        String method = exchange.getRequestMethod();
        log.info("Handling request. Method: {}", method);
        if (!checkMethod(method)) return;

        processRequest(params, exchange);
    }

    private void processRequest(Map<String, String> params, HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        if (method.equals(HEAD)) {
            executeHead(exchange, params);
        } else if (method.equals(GET)) {
            try {
                Headers headers = exchange.getRequestHeaders();
                File file = checkFile(exchange, params);
                if (headers != null) {
                    List<String> ranges = headers.get("Range");
                    if (ranges != null && !ranges.isEmpty()) {
                        String range = ranges.getFirst();
                        executeGet(exchange, parseChunk(range, file), params);
                    } else {
                        executeGet(exchange, parseChunk(String.valueOf(file.length()), file), params);
                    }
                } else {
                    executeGet(exchange, parseChunk(String.valueOf(file.length()), file), params);
                }
            } catch (Exception e) {
                log.error("Error to process get request: ", e);
            }
        }
    }

    private void executeHead(HttpExchange exchange, Map<String, String> params) {
        try {
            File file = checkFile(exchange, params);
            log.info("Sending head request. File: {}", file);

            long contentLength;
            try {
                VideoMetadata metadata = VideoReader.readMetadata(file);
                contentLength = file.length() - metadata.getTotalMetaSize();
            } catch (Exception e) {
                log.debug("No metadata found, sending full file");
                contentLength = file.length();
            }

            exchange.getResponseHeaders().set("Accept-Ranges", "bytes");
            exchange.getResponseHeaders().set("Content-Type", "video/mp4");
            exchange.getResponseHeaders().set("Content-Length", String.valueOf(contentLength));
            exchange.sendResponseHeaders(200, -1);
        } catch (Exception e) {
            log.error("Head processing error: ", e);
        }
    }

    private void executeGet(HttpExchange exchange, Chunk chunk, Map<String, String> params) {
        try {
            File file = checkFile(exchange, params);
            log.info("Process GET request. Chunk: {}", chunk);
            String needDecryptParam = params.get("needDecrypt");

            //ContentUtils.sendFileRange(exchange, chunk, file, needDecryptParam == null || Boolean.parseBoolean(needDecryptParam));
        } catch (Exception e) {
            log.error("GET processing error: ", e);
        }
    }

    private boolean checkMethod(String method) {
        return GET.equals(method) || HEAD.equals(method);
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

    private Chunk parseChunk(String range, File file) {
        if (range == null || !range.startsWith("bytes=")) {
            throw new IllegalArgumentException("Invalid range header format. Expected 'bytes=...'");
        }

        String rangeValue = range.substring(6); // Remove "bytes=" prefix

        int dashIndex = rangeValue.lastIndexOf("-");
        if (dashIndex == -1) {
            throw new IllegalArgumentException("Invalid range format. Missing hyphen separator.");
        }

        try {
            String startStr = rangeValue.substring(0, dashIndex);
            String endStr = rangeValue.substring(dashIndex + 1);

            long start = startStr.isEmpty() ? 0 : Long.parseLong(startStr);
            long end = getChunkEnd(file, endStr, start);

            return new Chunk(start, end);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric values in range: " + rangeValue, e);
        }
    }

    private long getChunkEnd(File file, String endStr, long start) {
        long end = endStr.isEmpty() ? file.length() - 1 : Long.parseLong(endStr);

        // Validate range bounds
        if (start < 0) {
            throw new IllegalArgumentException("Start position cannot be negative: " + start);
        }

        if (end >= file.length()) {
            end = file.length() - 1;
        }

        if (start > end) {
            throw new IllegalArgumentException(String.format("Invalid range: start (%d) > end (%d)", start, end));
        }
        return end;
    }
}