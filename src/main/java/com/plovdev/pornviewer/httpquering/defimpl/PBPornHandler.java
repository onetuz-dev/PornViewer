package com.plovdev.pornviewer.httpquering.defimpl;

import com.plovdev.pornviewer.events.listeners.EventListener;
import com.plovdev.pornviewer.models.VideoInfo;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class PBPornHandler {
    private static final Logger log = LoggerFactory.getLogger(PBPornHandler.class);

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .version(HttpClient.Version.HTTP_2)
//            .proxy(new ProxySelector() {
//                @Override
//                public List<Proxy> select(URI uri) {
//                    return List.of(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("85.198.98.179", 1080)));
//                }
//
//                @Override
//                public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
//                    log.error("Proxy connection error: ", ioe);
//                }
//            })
            .build();

    public PBPornHandler() {
    }

    public String requestPorn(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (Exception e) {
            log.error("Error to request porn: ", e);
        }
        return "";
    }

    public byte[] getBytes(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .GET()
                    .build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (Exception e) {
            log.error("Error to request porn: ", e);
        }
        return null;
    }

    public String executePost(String url, String body) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (Exception e) {
            log.error("Error to post query: ", e);
        }
        return "";
    }

    public void downloadPorn(String url, String filename, VideoInfo info) {
        log.info("Start loading file: {}", filename);
        EventListener.notifyListeners("START_DWONLOAD:" + filename);
        long videoSize = getVideoSize(url);

        PornDownloader downloader = new PornDownloader(client, URI.create(url), filename);
        downloader.startDownload(videoSize, info, getBytes(info.getPic()));
    }

    public long getVideoSize(String url) {
        try (HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .version(HttpClient.Version.HTTP_2)
                .build()) {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .HEAD()
                    .build();

            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() == 200) {
                return response.headers().firstValue("Content-Length").map(Long::parseLong).orElse(0L);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }
    public String getNextLink(String html) {
        Document document = Jsoup.parse(html);
        Elements elements = document.select("div.navigation");


        StringProperty link = new SimpleStringProperty();
        elements.forEach(e -> {
            Element a = e.selectFirst("a");
            if (a != null) link.set(a.attr("abs:href"));
            else link.set(null);
        });

        return link.get();
    }
}
