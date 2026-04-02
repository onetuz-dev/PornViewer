package com.plovdev.pornviewer;

import com.google.gson.Gson;
import com.plovdev.pornviewer.models.VideoInfo;
import com.plovdev.pornviewer.pornimpl.porn365.VideoInfoParser;
import com.plovdev.pornviewer.utility.json.JSONSerializer;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class ParserTest {
    private static final Logger log = LoggerFactory.getLogger(ParserTest.class);
    private static final Gson gson = new Gson();

    public static void main(String[] args) throws Exception {
        VideoInfo info = VideoInfoParser.parseInfo(Jsoup.parse(new File("test.html"), "UTF-8"));
        Files.writeString(Path.of("video.json"), JSONSerializer.serialize(info));
    }
}