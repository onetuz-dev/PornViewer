package com.plovdev.pornviewer.httpquering.defimpl;

import com.plovdev.pornviewer.events.listeners.EventListener;
import com.plovdev.pornviewer.events.listeners.FileDownloadingListener;
import com.plovdev.pornviewer.httpquering.PornHandler;
import com.plovdev.pornviewer.utility.files.EnvReader;
import com.plovdev.pornviewer.utility.files.FileUtils;
import com.plovdev.pornviewer.utility.security.CipherManager;
import com.plovdev.pornviewer.utility.security.VideoCipherrer;
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
    private static final CipherManager CM = new CipherManager(EnvReader.getEnv("VIDEO_PASSWORD"));

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
            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (Exception e) {
            log.error("Error to request porn: ", e);
        }
        return "";
    }

    @Override
    public void downloadPorn(String url, String filename) {
        log.info("Start loading file: {}", filename);
        EventListener.notifyListeners("START_DWONLOAD:" + filename);
        String encryptedFileName = CM.encrypt(filename) + FileUtils.PORN_VIEWER_SIGN;
        String fileOut = FileUtils.getPvDownloadsPath() + "/" + encryptedFileName;
        log.info("Write file to: {}", fileOut);

        VideoCipherrer cipher = new VideoCipherrer(EnvReader.getEnv("VIDEO_PASSWORD"));

        try (FileOutputStream file = new FileOutputStream(fileOut)) {
            // HTTP запрос
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .GET()
                    .build();

            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            try (InputStream in = response.body()) {
                long totalRead = 0;
                int read;
                byte[] buffer = new byte[131072];

                long videoSize = getVideoSize(url);
                FileDownloadingListener.notifyStartsListeners(videoSize);

                while ((read = in.read(buffer)) != -1) {
                    byte[] originalChunk = new byte[read];
                    System.arraycopy(buffer, 0, originalChunk, 0, read);
                    byte[] encryptedChunk = cipher.encrypt(originalChunk, totalRead);
                    file.write(encryptedChunk);
                    totalRead += read;
                    FileDownloadingListener.notifyProcessListeners(totalRead);
                }
                log.info("Download completed. Total bytes: {}, Encrypted file: {}", totalRead, fileOut);
            }
        } catch (Exception e) {
            log.error("Error loading file: ", e);
            FileDownloadingListener.notifyErrorListeners(e);
        }

        FileDownloadingListener.notifyEndListeners(fileOut);
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

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}
