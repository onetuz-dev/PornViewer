package com.plovdev.pornviewer;

import com.plovdev.pornviewer.events.listeners.FileDownloadingListener;
import com.plovdev.pornviewer.utility.files.EnvReader;
import com.plovdev.pornviewer.utility.files.FileUtils;
import com.plovdev.pornviewer.utility.security.VideoCipherrer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Test {
    private static final Logger log = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) throws Exception {
        VideoCipherrer cipher = new VideoCipherrer(EnvReader.getEnv("VIDEO_PASSWORD"));
        try (FileOutputStream file = new FileOutputStream(FileUtils.getPvDownloadsPath() + "/encvideo")) {
            try (InputStream in = new FileInputStream(FileUtils.getPvDownloadsPath() + "/28264HD.mp4")) {
                long totalRead = 0;
                int read;
                byte[] buffer = new byte[8192];

                while ((read = in.read(buffer)) != -1) {
                    byte[] originalChunk = new byte[read];
                    System.arraycopy(buffer, 0, originalChunk, 0, read);

                    byte[] encryptedChunk = cipher.encrypt(originalChunk, totalRead);

                    file.write(encryptedChunk);
                    totalRead += read;
                }

                log.info("Download completed. Total bytes: {}", totalRead);
            }
        } catch (Exception e) {
            log.error("Error loading file: ", e);
            FileDownloadingListener.notifyErrorListeners(e);
        }
    }
}