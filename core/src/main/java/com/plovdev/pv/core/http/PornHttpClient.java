package com.plovdev.pv.core.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.CookieHandler;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class PornHttpClient {
    private static final Logger log = LoggerFactory.getLogger(PornHttpClient.class);
    private volatile static PornHttpClient pornHttpClient = null;

    private final ExecutorService asyncExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(60))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .version(HttpClient.Version.HTTP_2)
            .cookieHandler(CookieHandler.getDefault())
            .executor(asyncExecutor)
            .build();


    public static PornHttpClient getInstance() {
        if (pornHttpClient == null) {
            synchronized (PornHttpClient.class) {
                if (pornHttpClient == null) {
                    pornHttpClient = new PornHttpClient();
                }
            }
        }
        return pornHttpClient;
    }

    private PornHttpClient() {
    }

    public List<Future<String>> executeAsync(List<PornRequest> requests) {
        List<Future<String>> futures = new ArrayList<>();
        for (PornRequest request : requests) {
            futures.add(asyncExecutor.submit(() -> execute(request)));
        }
        return futures;
    }

    public String execute(PornRequest request) {
        String path = request.path();

        try {
            HttpRequest httpRequest = buildRequest(request);
            log.debug("Executing request: {}", path);
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (!isSuccessStatusCode(response.statusCode())) {
                log.warn("HTTP {} error for URL: {}, body: {}", response.statusCode(), path, response.body());
            }

            String body = response.body();
            log.debug("Response received from {}: {} bytes", path, body.length());
            return body;

        } catch (Exception e) {
            log.error("Error sending request to {}: {}", path, e.getMessage(), e);
        }
        return null;
    }

    //TODO: add download logic

    private HttpRequest buildRequest(PornRequest request) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                // Initial headers(must have)
                .uri(URI.create(request.path()))
                .header("Accept", "*/*")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36")
                .header("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                .header("Accept-Encoding", "gzip, deflate, br, zstd")
                .timeout(Duration.ofSeconds(60));
        Map<String, String> customHeaders = request.headers();
        for (String header : customHeaders.keySet()) {
            builder.header(header, customHeaders.get(header));
        }
        return builder.GET().build();
    }

    private boolean isSuccessStatusCode(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

    public void close() {
        asyncExecutor.close();
        httpClient.close();
    }
}