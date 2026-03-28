package com.plovdev.pornviewer;

import com.plovdev.pornviewer.databases.FavoriteVideos;
import com.plovdev.pornviewer.utility.files.EnvReader;
import com.plovdev.pornviewer.utility.security.CipherManager;
import com.plovdev.pornviewer.utility.security.VideoCipherrer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Test {
    private static final Logger log = LoggerFactory.getLogger(Test.class);
    private static final VideoCipherrer cipher = new VideoCipherrer(EnvReader.getEnv("VIDEO_PASSWORD"));
    private static final CipherManager cipherManager = new CipherManager(EnvReader.getEnv("VIDEO_PASSWORD"));

    public static void main(String[] args) throws Exception {
        FavoriteVideos.updateUrls("http://5porno365.info");
    }

    private static void encrypt(Path path) {
        log.info("Encrypt file: {}", path);

        String filePath = path.toString();
        String filePathTmp = filePath + ".tmp";
        Path tempPath = Path.of(filePathTmp);

        try (InputStream in = new FileInputStream(filePath);
             FileOutputStream file = new FileOutputStream(filePathTmp)) {
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
            file.flush();
        } catch (Exception e) {
            log.error("Error decrypting file: ", e);
        }

        try {
            Files.deleteIfExists(path);
            rename(tempPath, path);
            Files.deleteIfExists(tempPath);
        } catch (IOException e) {
            log.error("Error to post process file: ", e);
        }

        log.info("File {} encrypted successful", path);
    }

    private static void rename(Path from, Path to) {
        try (InputStream in = new FileInputStream(from.toString())) {
            Files.copy(in, to, StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(from);
        } catch (Exception e) {
            log.error("Error decrypting file: ", e);
        }
    }
}