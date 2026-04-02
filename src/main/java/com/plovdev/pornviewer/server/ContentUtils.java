package com.plovdev.pornviewer.server;

import com.plovdev.pornviewer.encryptsupport.videoparser.VideoMetadata;
import com.plovdev.pornviewer.encryptsupport.videoparser.read.VideoReader;
import com.plovdev.pornviewer.utility.files.EnvReader;
import com.plovdev.pornviewer.utility.files.FileUtils;
import com.plovdev.pornviewer.utility.security.VideoCipherrer;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ContentUtils {
    private static final Logger log = LoggerFactory.getLogger(ContentUtils.class);
    private static final VideoCipherrer VD = new VideoCipherrer(EnvReader.getEnv("VIDEO_PASSWORD"));
    private static final int BUFFER_SIZE = 8192;
    private static final int HEADER_SIZE = 40; // 32 байта MD5 + 8 байт videoSize

    public static void sendFileRange(HttpExchange exchange, Chunk chunk, File file, boolean needDecrypt) throws Exception {
        long start = chunk.getStart();
        long end = chunk.getEnd();

        long metadataSize = 0;
        VideoMetadata metadata = null;
        try {
            metadata = VideoReader.readMetadata(file);
        } catch (Exception e) {
            log.debug("No metadata found");
        }

        // 2. Видео начинается после 40-байтового заголовка
        long videoStart = HEADER_SIZE;
        long videoLength = (metadata != null) ? metadata.getVideoSize() : (file.length() - HEADER_SIZE);

        if (end >= videoLength) {
            end = videoLength - 1;
        }

        // 3. Корректируем позиции для отправки клиенту
        long realStart = videoStart + start;
        long realEnd = videoStart + end;

        log.info("Chunk requesting: start: {}, end: {}, realStart: {}, realEnd: {}, videoStart: {}, videoLength: {}, metadataSize: {}", start, end, realStart, realEnd, videoStart, videoLength, metadataSize);
        if (realStart >= file.length() || realStart >= (videoStart + videoLength)) {
            exchange.sendResponseHeaders(416, -1);
            return;
        }

        if (realEnd >= file.length()) {
            realEnd = file.length() - 1;
        }

        long contentLength = realEnd - realStart + 1;

        log.info("Sending range: client {}-{}, real {}-{}, videoLength={}, metadataSize={}",
                start, end, realStart, realEnd, videoLength, metadataSize);

        exchange.getResponseHeaders().set("Content-Type", "video/mp4");
        exchange.getResponseHeaders().set("Accept-Ranges", "bytes");
        exchange.getResponseHeaders().set("Connection", "keep-alive");
        exchange.getResponseHeaders().set("Keep-Alive", "timeout=600");
        exchange.getResponseHeaders().set("Content-Range",
                String.format("bytes %d-%d/%d", start, end, videoLength));
        exchange.sendResponseHeaders(206, contentLength);

        try (OutputStream os = exchange.getResponseBody()) {
            if (needDecrypt) {
                sendDecryptedRange(file, realStart, contentLength, os);
            } else {
                sendPlainRange(file, realStart, contentLength, os);
            }
        }
    }

    private static void sendPlainRange(File file, long start, long length, OutputStream os) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            raf.seek(start);
            byte[] buffer = new byte[BUFFER_SIZE];
            long remaining = length;
            int bytesRead;

            while (remaining > 0 && (bytesRead = raf.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
                os.write(buffer, 0, bytesRead);
                remaining -= bytesRead;
            }
            os.flush();
        }
    }

    private static void sendDecryptedRange(File file, long startInFile, long length, OutputStream os) throws Exception {
        // ВАЖНО: Вычисляем позицию ОТНОСИТЕЛЬНО начала контента (после 40 байт)
        long relativePos = startInFile - HEADER_SIZE;

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            int skip = (int) (relativePos % 16);
            long alignedRelativeStart = relativePos - skip;

            // Прыгаем в файле: 40 (заголовок) + выровненный старт контента
            raf.seek(HEADER_SIZE + alignedRelativeStart);

            // Инициализируем шифр с ПРАВИЛЬНОЙ позиции (0, 16, 32...)
            // Теперь для 40-го байта файла позиция в шифре будет 0.
            Cipher cipher = VD.createCipher(Cipher.DECRYPT_MODE, alignedRelativeStart);

            byte[] buffer = new byte[8192];
            long totalToRead = length + skip;
            long processed = 0;
            boolean firstChunk = true;

            while (processed < totalToRead) {
                int toRead = (int) Math.min(buffer.length, totalToRead - processed);
                int read = raf.read(buffer, 0, toRead);
                if (read == -1) break;

                byte[] out;
                if (processed + read == totalToRead) {
                    out = cipher.doFinal(buffer, 0, read);
                } else {
                    out = cipher.update(buffer, 0, read);
                }
                if (out != null && out.length > 0) {
                    int offset = 0;
                    int len = out.length;

                    if (firstChunk) {
                        offset = skip;
                        len -= skip;
                        firstChunk = false;
                    }

                    if (len > 0) {
                        os.write(out, offset, len);
                    }
                }
                processed += read;
            }
            os.flush();
        }
    }


    public static File checkFile(HttpExchange exchange, Map<String, String> params) throws Exception {
        String filePath = params.get("file");
        if (filePath == null) {
            exchange.sendResponseHeaders(400, -1);
            log.info("File parameter is null");
            throw new NullPointerException("File parameter is missing");
        }

        String decodedPath = URLDecoder.decode(filePath, StandardCharsets.UTF_8);
        File file = new File(buildFilePath(decodedPath));

        if (!file.exists()) {
            exchange.sendResponseHeaders(404, -1);
            log.info("File {} not found", file.getAbsolutePath());
            throw new FileNotFoundException("File not found: " + decodedPath);
        }

        String canonicalPath = file.getCanonicalPath();
        String basePath = new File(FileUtils.getPvDownloadsPath().toString()).getCanonicalPath();

        if (!canonicalPath.startsWith(basePath)) {
            exchange.sendResponseHeaders(403, -1);
            log.warn("Directory traversal attempt: {}", decodedPath);
            throw new SecurityException("Access denied");
        }

        return file;
    }

    public static String buildFilePath(String name) {
        return FileUtils.getPvDownloadsPath() + name;
    }
}