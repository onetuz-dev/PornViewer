package com.plovdev.pornviewer.gui.video;

import com.plovdev.pornviewer.databases.UserPreferences;
import com.plovdev.pornviewer.httpquering.PornParser;
import com.plovdev.pornviewer.httpquering.PornVideoAdapter;
import com.plovdev.pornviewer.models.VideoCard;
import com.plovdev.pornviewer.models.VideoInfo;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.stage.Stage;

import java.util.Map;
import java.util.Objects;

public class VideoPlayerPane extends Stage {
    public VideoPlayerPane(VideoCard card) {
        super();
        PornVideoAdapter adapter = UserPreferences.get("0000").getPornAdapter();
        PornParser parser = adapter.getParser();

        VideoInfo info = card.getInfo();
        if (info == null) {
            info = parser.parseVideo(card.getUrl());
            card.setInfo(info);
        }
        Map<String, String> urls = info.getUrls();
        Media media = new Media(urls.containsKey("1080p")? urls.get("1080p") : urls.get("HQ"));

        VideoPlyer plyer = new VideoPlyer(media, card.getUrl(), card.getTitle(), this);

        Scene scene = new Scene(plyer, 1000, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/plovdev/pornviewer/white.css")).toExternalForm());

        setScene(scene);
        setTitle(card.getTitle());

        setOnCloseRequest(e -> {
            plyer.stop();
            close();
        });
    }
}