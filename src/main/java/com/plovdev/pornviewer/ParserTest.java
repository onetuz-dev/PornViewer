package com.plovdev.pornviewer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.plovdev.pornviewer.encryptsupport.videoparser.VideoMetadata;
import com.plovdev.pornviewer.encryptsupport.videoparser.read.VideoReader;
import com.plovdev.pornviewer.encryptsupport.videoparser.write.VideoWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.Duration;

public class ParserTest {
    private static final Logger log = LoggerFactory.getLogger(ParserTest.class);
    private static final Gson gson = new Gson();

    public static void main(String[] args) throws Exception {
        JsonObject object = new JsonObject();
        object.addProperty("name", "Сделал массаж мачехе и поимел ее заодно!");
        object.addProperty("duration", String.valueOf(Duration.ofSeconds(17 * 60 + 50).toMillis()));
        object.addProperty("mime", "video/mp4");
        String json = gson.toJson(object);
        System.out.println(json);
        System.out.println(json.length());

        VideoMetadata metadata = new VideoMetadata(json, new byte[0]);
        System.out.println(metadata);
        VideoWriter.writeMetadataToStream(new FileOutputStream("fileout.mp4"), metadata);

        VideoMetadata readed = VideoReader.readMetadata(new FileInputStream("fileout.mp4"));
        System.out.println(readed);
    }
}