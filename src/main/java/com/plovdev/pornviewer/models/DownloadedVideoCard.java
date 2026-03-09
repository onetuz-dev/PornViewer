package com.plovdev.pornviewer.models;

import com.plovdev.pornviewer.gui.video.DownloadedVideoPlayerPane;
import com.plovdev.pornviewer.utility.DialogShower;
import com.plovdev.pornviewer.utility.files.FileUtils;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class DownloadedVideoCard extends VideoCard {
    private static final Logger log = LoggerFactory.getLogger(DownloadedVideoCard.class);
    protected String path;
    protected String size;
    private String date;
    protected boolean isSelf = false;

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    protected final Pane pane;
    protected Runnable deleteRun;
    public DownloadedVideoCard(Pane component) {
        pane = component;
    }

    public Runnable getDeleteRun() {
        return deleteRun;
    }

    public void setDeleteRun(Runnable deleteRun) {
        this.deleteRun = deleteRun;
    }

    public Pane getPane() {
        return pane;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPath() {
        return FileUtils.replaceFileToHttpPath(path);
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return getPath();
    }

    @Override
    public Pane display() {
        AnchorPane anchorPane = new AnchorPane();

        minWidthProperty().bind(pane.widthProperty().divide(1.03));
        prefWidthProperty().bind(pane.widthProperty().divide(1.03));
        maxWidthProperty().bind(pane.widthProperty().divide(1.03));
        anchorPane.minWidthProperty().bind(pane.widthProperty().divide(1.03));
        anchorPane.prefWidthProperty().bind(pane.widthProperty().divide(1.03));
        anchorPane.maxWidthProperty().bind(pane.widthProperty().divide(1.03));

        ImageView view = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/plovdev/pornviewer/download.png"))));
        view.setFitHeight(70);
        view.setFitWidth(70);
        view.setPreserveRatio(true);
        AnchorPane.setLeftAnchor(view, 20.0);
        AnchorPane.setBottomAnchor(view, 5.0);
        AnchorPane.setTopAnchor(view, 25.0);
        anchorPane.getChildren().add(view);

        Hyperlink title = new Hyperlink(getTitle());
        title.setOnAction(e -> {
            DownloadedVideoPlayerPane playerPane = new DownloadedVideoPlayerPane(this);
            playerPane.show();
        });
        title.getStyleClass().add("video-title-download");
        AnchorPane.setTopAnchor(title,10.0);
        AnchorPane.setLeftAnchor(title, 120.0);
        anchorPane.getChildren().add(title);

        Label durLabel = new Label("0:00");
        durLabel.getStyleClass().add("marker-download");
        AnchorPane.setBottomAnchor(durLabel,10.0);
        AnchorPane.setLeftAnchor(durLabel, 120.0);
        anchorPane.getChildren().add(durLabel);


        Label dateLabel = new Label(getDate());
        dateLabel.getStyleClass().add("marker-download");
        AnchorPane.setTopAnchor(dateLabel,10.0);
        AnchorPane.setRightAnchor(dateLabel, 40.0);
        anchorPane.getChildren().add(dateLabel);

        Label sizeLabel = new Label(size + "MB");
        sizeLabel.getStyleClass().add("marker-download");
        AnchorPane.setBottomAnchor(sizeLabel,10.0);
        AnchorPane.setRightAnchor(sizeLabel, 40.0);
        anchorPane.getChildren().add(sizeLabel);

        Label delete = new Label("×");
        delete.getStyleClass().add("delete-label");
        delete.setOnMousePressed(e -> DialogShower.showConfirm("Удалить видео?", deleteRun));
        AnchorPane.setBottomAnchor(delete,45.0);
        AnchorPane.setTopAnchor(delete, 45.0);
        AnchorPane.setRightAnchor(delete, 10.0);
        anchorPane.getChildren().add(delete);

        getStyleClass().add("download-video-card");

        try {
            MediaPlayer player = new MediaPlayer(new Media(path));
            player.setOnReady(() -> durLabel.setText(getVideoDuration(player.getTotalDuration())));
            durLabel.setText(getVideoDuration(player.getTotalDuration()));
        } catch (Exception e) {
            log.error("Error to show file info: ", e);
        }

        getChildren().add(anchorPane);
        return this;
    }

    protected String getVideoDuration(Duration total) {
        if (total != Duration.UNKNOWN) {
            BigDecimal mills = new BigDecimal(String.valueOf(total.toMillis()));

            BigDecimal totalSeconds = mills.divide(new BigDecimal("1000.0"), 10, RoundingMode.HALF_UP);

            int hours = totalSeconds.intValue() / (60*60);
            String h = "";
            if (hours != 0) h = hours+":";

            BigDecimal minutes = totalSeconds.divide(new BigDecimal("60.0"), 10, RoundingMode.HALF_UP);
            BigDecimal seconds = totalSeconds.remainder(new BigDecimal("60.0"));

            long sec = Math.round(seconds.doubleValue());
            long min = Math.round(minutes.doubleValue());

            return h+String.format("%2s:%2s", min, sec);
        }
        return "00:00";
    }

    private Region getVRegion() {
        Region region = new Region();
        VBox.setVgrow(region, Priority.ALWAYS);
        return region;
    }
    private Region getHRegion() {
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        return region;
    }
}