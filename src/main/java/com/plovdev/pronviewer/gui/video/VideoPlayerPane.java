package com.plovdev.pronviewer.gui.video;

import com.plovdev.pronviewer.models.VideoCard;
import com.plovdev.pronviewer.models.VideoInfo;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.stage.Stage;

import java.util.Objects;

public class VideoPlayerPane extends Stage {
    public VideoPlayerPane(VideoCard card) {
        super();
        VideoInfo info = card.getInfo();
        System.out.println(info.getUrls());
        Media media = new Media(info.getUrls().get("HQ"));

        VideoPlyer plyer = new VideoPlyer(media, card.getUrl(), card.getTitle());

        Scene scene = new Scene(plyer, 1000, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/plovdev/pronviewer/white.css")).toExternalForm());

        setScene(scene);
        setTitle(card.getTitle());

        setOnCloseRequest(e -> {
            plyer.stop();
            close();
        });
    }
}