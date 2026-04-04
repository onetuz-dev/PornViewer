package com.plovdev.pornviewer;

import com.plovdev.pornviewer.databases.SecureDB;
import com.plovdev.pornviewer.encryptionsupport.videoparser.read.PVVFParser;
import com.plovdev.pornviewer.encryptionsupport.videoparser.videomodel.EncryptedVideo;
import com.plovdev.pornviewer.encryptionsupport.videoparser.write.PVVFWriter;
import com.plovdev.pornviewer.utility.security.CipherManager;
import com.plovdev.pornviewer.utility.security.VideoCipherrer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;

public class Test {
    private static final Logger log = LoggerFactory.getLogger(Test.class);
    private static final VideoCipherrer cipher = new VideoCipherrer(CipherManager.getPassword());

    public static void main(String[] args) throws Exception {
        processVideo();
    }

    private static void processVideo() {
        try (PVVFParser parser = new PVVFParser(new File("/Users/mac/.PornViewer/downloads/c3b3355fcb0ee92989f742b161497b2dd40df00448eb684e625011342711035a"))) {
            EncryptedVideo video = parser.collectEncryptedVideo();
            PVVFWriter writer = new PVVFWriter(new File("/Users/mac/.PornViewer/downloads/c3b3355fcb0ee92989f742b161497b2dd40df00448eb684e625011342711035a"));
            writer.updateVideoMetadata(video.getVideoHeader().encVideoSize(), video.getVideoMetadata());
        } catch (Exception e) {
            log.error("Reading error: ", e);
        }
    }

    public static void encryptDatabase(String newPassword) {
        try {
            Connection conn = SecureDB.initCipherer();
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA rekey = '" + newPassword + "'");
                System.out.println("✅ База успешно зашифрована");
            }
            conn.close();
        } catch (Exception e) {
            System.err.println("❌ Ошибка при шифровании: " + e.getMessage());
        }
    }
}