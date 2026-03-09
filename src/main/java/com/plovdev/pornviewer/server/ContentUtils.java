package com.plovdev.pornviewer.server;

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

    public static void sendFileRange(HttpExchange exchange, Chunk chunk, File file, boolean needDecrypt) throws Exception {
        long start = chunk.getStart();
        long end = chunk.getEnd();
        long fileSize = file.length();
        long contentLength = chunk.length();

        log.info("Sending range {}-{}, needDecrypt: {}", start, end, needDecrypt);

        exchange.getResponseHeaders().set("Content-Type", "video/mp4");
        exchange.getResponseHeaders().set("Accept-Ranges", "bytes");
        exchange.getResponseHeaders().set("Connection", "keep-alive");
        exchange.getResponseHeaders().set("Keep-Alive", "timeout=600");
        exchange.getResponseHeaders().set("Content-Range", "bytes " + start + "-" + end + "/" + fileSize);
        exchange.sendResponseHeaders(206, contentLength);

        try (OutputStream os = exchange.getResponseBody()) {
            if (needDecrypt) {
                sendDecryptedRange(file, start, contentLength, os);
            } else {
                sendPlainRange(file, start, contentLength, os);
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

    private static void sendDecryptedRange(File file, long start, long length, OutputStream os) throws Exception {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            int skip = (int) (start % 16);
            long alignedStart = start - skip;
            raf.seek(alignedStart);

            int totalToRead = (int) (length + skip);
            byte[] buffer = new byte[totalToRead];
            raf.readFully(buffer);
            Cipher cipher = VD.createCipher(Cipher.DECRYPT_MODE, alignedStart);
            byte[] decrypted = cipher.doFinal(buffer);
            os.write(decrypted, skip, (int) length);
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

        // Security check - prevent directory traversal
        String canonicalPath = file.getCanonicalPath();
        String basePath = new File(FileUtils.getPvDownloadsPath().toString()).getCanonicalPath();

        if (!canonicalPath.startsWith(basePath)) {
            exchange.sendResponseHeaders(403, -1);
            log.warn("Directory traversal attempt: {}", decodedPath);
            throw new SecurityException("Access denied");
        }

        return file;
    }

    private static String buildFilePath(String name) {
        return FileUtils.getPvDownloadsPath() + name;
    }
}