package com.plovdev.pornviewer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.plovdev.pornviewer.encryptsupport.videoparser.VideoMetadata;
import com.plovdev.pornviewer.httpquering.defimpl.PBPornHandler;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.Duration;

public class Test {
    private static final Logger log = LoggerFactory.getLogger(Test.class);
    private static final VideoCipherrer cipher = new VideoCipherrer(EnvReader.getEnv("VIDEO_PASSWORD"));
    private static final CipherManager cipherManager = new CipherManager(EnvReader.getEnv("VIDEO_PASSWORD"));

    public static void main(String[] args) throws Exception {
        PBPornHandler handler = new PBPornHandler();
        String body = handler.requestPorn("https://vps402.strip2.co/");
        Files.writeString(Path.of("stripe/main.html"), body);
    }

    public static void encryptDatabase(String dbPath, String newPassword) {
        try {
            Connection conn = DriverManager.getConnection(dbPath);
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA rekey = '" + escapeString(newPassword) + "'");
                System.out.println("✅ База успешно зашифрована");
            }
            conn.close();
        } catch (Exception e) {
            System.err.println("❌ Ошибка при шифровании: " + e.getMessage());
        }
    }

    private static String escapeString(String s) {
        return s.replace("'", "''");
    }

    private static void encrypt(Path path) {
        log.info("Encrypt file: {}", path);

        String filePath = path.toString();
        String filePathTmp = filePath + ".tmp";
        Path tempPath = Path.of(filePathTmp);
        JsonObject object = new JsonObject();
        object.addProperty("name", "Сделал массаж мачехе и поимел ее заодно!");
        object.addProperty("duration", String.valueOf(Duration.ofSeconds(17 * 60 + 50).toMillis()));
        object.addProperty("mime", "video/mp4");
        String json = new Gson().toJson(object);
        System.out.println(json);
        System.out.println(json.length());

        VideoMetadata metadata = new VideoMetadata(json, new byte[0]);
        System.out.println(metadata);

        try (InputStream in = new FileInputStream(filePath);
             FileOutputStream file = new FileOutputStream(filePathTmp)) {
            //VideoWriter.writeMetadataToStream(file, metadata);
            long totalRead = 0;
            int read;
            byte[] buffer = new byte[8192];
            while ((read = in.read(buffer)) != -1) {
                byte[] originalChunk = new byte[read];
                System.arraycopy(buffer, 0, originalChunk, 0, read);
                file.write(originalChunk);
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