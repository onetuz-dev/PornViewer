package com.plovdev.pornviewer.httpquering.defimpl;

import com.plovdev.pornviewer.events.listeners.EventListener;
import com.plovdev.pornviewer.events.listeners.FileDownloadingListener;
import com.plovdev.pornviewer.httpquering.PornHandler;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class PBPornHandler implements PornHandler {
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

    @Override
    public String requestPorn(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.statusCode() + " - code from requester");
            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "";
    }

    @Override
    public void downloadPorn(String url, String out) {
        EventListener.notifyListeners("START_DWONLOAD:" + out.substring(out.lastIndexOf('/') + 1));
        try (FileOutputStream file = new FileOutputStream(out)) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .GET()
                    .build();

            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            try (InputStream in = response.body()) {
                long totalRead = 0;
                int chunk;
                byte[] bytes = new byte[8192];

                long videoSize = getVideoSize(url);
                double size = (double) videoSize / 1_048_576.0;
                FileDownloadingListener.notifyStartsListeners(videoSize);

                while ((chunk = in.read(bytes)) != -1) {
                    file.write(bytes, 0, chunk);
                    totalRead += chunk;
                    FileDownloadingListener.notifyProcessListeners(totalRead);
                }
            }
        } catch (Exception e) {
            FileDownloadingListener.notifyErrorListeners(e);
        }
        FileDownloadingListener.notifyEndListeners(out);
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

    @Override
    public void setRandomHeaders(HttpRequest request) {

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
