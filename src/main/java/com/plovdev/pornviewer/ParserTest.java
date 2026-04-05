package com.plovdev.pornviewer;

import com.google.gson.Gson;
import com.plovdev.pornviewer.encryptionsupport.CipherEngineUtils;
import com.plovdev.pornviewer.encryptionsupport.videoparser.read.PVVFParser;
import com.plovdev.pornviewer.encryptionsupport.videoparser.videomodel.VideoChunk;
import com.plovdev.pornviewer.encryptionsupport.videoparser.videomodel.VideoHeader;
import com.plovdev.pornviewer.encryptionsupport.videoparser.videomodel.VideoMetadata;
import com.plovdev.pornviewer.encryptionsupport.videoparser.write.PVVFWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ParserTest {
    private static final Logger log = LoggerFactory.getLogger(ParserTest.class);
    private static final Gson gson = new Gson();

    public static void main(String[] args) throws Exception {
        File testFile = new File("test.pvvf");
        byte[] mockNonce = new byte[8];
        long mockCrc = 0;

        try (PVVFWriter writer = new PVVFWriter(testFile)) {
            long plainSize = 2L * (128 * 1024);
            VideoHeader finalHeader = VideoHeader.ofOnlyRequired("TXT ", 0, plainSize);
            writer.writeVideoHeader(finalHeader);

            byte[] fakeData1 = new byte[128 * 1024];
            byte[] fakeData2 = new byte[128 * 1024];
            byte[] fakeTag = new byte[16];

            System.arraycopy("HELLO CHUNK 0".getBytes(), 0, fakeData1, 0, 13);
            System.arraycopy("HELLO CHUNK 1".getBytes(), 0, fakeData2, 0, 13);

            writer.appendVideoChunk(new VideoChunk(0, fakeData1, fakeTag));
            writer.appendVideoChunk(new VideoChunk(1, fakeData2, fakeTag));

            byte[] nonce = new byte[VideoMetadata.BASE_NONCE_LENGTH];
            CipherEngineUtils.createRandomPassword(nonce);

            byte[] fakeJson = new byte[1024];
            byte[] fakePreview = new byte[2048];

            System.arraycopy("{\"tag\": \"padded\"}".getBytes(), 0, fakeJson, 0, 17);
            System.arraycopy(fakeTag, 0, fakeJson, 1024 - 16, 16);

            System.arraycopy("fnirbwekjnuirfnwds".getBytes(), 0, fakePreview, 0, 18);
            System.arraycopy(fakeTag, 0, fakePreview, 2048 - 16, 16);

            VideoMetadata metadata = VideoMetadata.ofOnlyRequired(nonce, fakeJson, fakePreview);
            writer.writeVideoMetadata(metadata);
        }

        // Читаем обратно
        try (PVVFParser parser = new PVVFParser(testFile)) {
            VideoHeader readedHeader = parser.parseVideoHeader();
            System.out.println("MIME: " + readedHeader.mime());

            VideoChunk chunk0 = parser.parseVideoChunk(0);
            System.out.println("Data 0: " + new String(chunk0.encryptedData()).substring(0, 13));

            VideoChunk chunk1 = parser.parseVideoChunk(1);
            System.out.println("Data 1: " + new String(chunk1.encryptedData()).substring(0, 13));
        }
    }
}