package com.plovdev.pornviewer;

import com.plovdev.pornviewer.databases.SecureDB;
import com.plovdev.pornviewer.encryptionsupport.videoparser.read.PVVFParser;
import com.plovdev.pornviewer.encryptionsupport.videoparser.read.PVVFVideoReader;
import com.plovdev.pornviewer.encryptionsupport.videoparser.videomodel.EncryptedVideo;
import com.plovdev.pornviewer.models.DownloadedVideoInfo;
import com.plovdev.pornviewer.utility.security.CipherManager;
import com.plovdev.pornviewer.utility.security.VideoCipherrer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;

public class Test {
    private static final Logger log = LoggerFactory.getLogger(Test.class);
    private static final VideoCipherrer cipher = new VideoCipherrer(CipherManager.getPassword());

    public static void main(String[] args) throws Exception {
        processVideo("3ad1e75879ee6c908cb837d44f658ff4eeb1cc6289bf31377dc2862dc309412e");
    }

    private static void processVideo(String videoFile) {
        File file = new File("/Users/mac/.PornViewer/downloads/" + videoFile);
        try (PVVFParser parser = new PVVFParser(file)) {
            EncryptedVideo video = parser.collectEncryptedVideo();
            System.out.println(video);
            DownloadedVideoInfo info = PVVFVideoReader.readInfo(file);
            System.out.println(info);
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