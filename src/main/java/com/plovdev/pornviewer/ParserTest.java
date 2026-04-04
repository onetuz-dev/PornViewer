package com.plovdev.pornviewer;

import com.google.gson.Gson;
import com.plovdev.pornviewer.encryptionsupport.videoparser.read.PVVFParser;
import com.plovdev.pornviewer.encryptionsupport.videoparser.videomodel.VideoChunk;
import com.plovdev.pornviewer.encryptionsupport.videoparser.videomodel.VideoHeader;
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
            long encSize = 2L * (128 * 1024 + 16);
            VideoHeader finalHeader = new VideoHeader((byte) 1, (byte) 0, "TXT ", 0, encSize, encSize, mockNonce, mockCrc);
            writer.writeVideoHeader(finalHeader);

            byte[] fakeData1 = new byte[128 * 1024];
            byte[] fakeData2 = new byte[128 * 1024];
            byte[] fakeTag = new byte[16];

            System.arraycopy("HELLO CHUNK 0".getBytes(), 0, fakeData1, 0, 13);
            System.arraycopy("HELLO CHUNK 1".getBytes(), 0, fakeData2, 0, 13);

            writer.appendVideoChunk(new VideoChunk(0, fakeData1, fakeTag));
            writer.appendVideoChunk(new VideoChunk(1, fakeData2, fakeTag));
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