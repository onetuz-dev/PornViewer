package com.plovdev.pornviewer.gui.video;

import com.plovdev.pornviewer.models.DownloadedVideoCard;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.stage.Stage;

import java.util.Objects;

public class DownloadedVideoPlayerPane extends Stage {
    public DownloadedVideoPlayerPane(DownloadedVideoCard card) {
        super();
        Media media = new Media(card.getPath());
        VideoPlyer plyer = new VideoPlyer(media, card, this);

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