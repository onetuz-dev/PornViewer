package com.plovdev.pronviewer.gui.video;

import com.plovdev.pronviewer.models.DownloadedVideoCard;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.stage.Stage;

import java.util.Objects;

public class DownloadedVideoPlayerPane extends Stage {
    public DownloadedVideoPlayerPane(DownloadedVideoCard card) {
        super();
        Media media = new Media(card.getPath());
        VideoPlyer plyer = new VideoPlyer(media, card.getPath(), card.getTitle());

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