package com.plovdev.pronviewer.models;

import com.plovdev.pronviewer.databases.FavoriteVideos;
import com.plovdev.pronviewer.events.listeners.FavoriteListener;
import com.plovdev.pronviewer.gui.video.VideoPlayerPane;
import com.plovdev.pronviewer.httpquering.defimpl.PBPornHandler;
import com.plovdev.pronviewer.pornimpl.porn365.DefPornParser;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.util.Objects;

public class VideoCard extends PornCard {
    protected String duration;
    protected int views;
    protected String rating;
    protected VideoInfo info = null;
    protected boolean isFavorite = false;
    private final SVGPath favPath = new SVGPath();

    protected final PBPornHandler handler = new PBPornHandler();

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
        favPath.setFill(isFavorite() ? Color.rgb(255,15,45): Color.TRANSPARENT);
    }
    public void makeFavorite() {
        if (isFavorite) {
            System.out.println("Adding to favorite");
            FavoriteVideos.add(this);
        } else {
            System.out.println("Removing from favorite");
            FavoriteVideos.remove(String.valueOf(getCardId()));
        }
        FavoriteListener.notifyListeners(this);
    }

    public VideoInfo getInfo() {
        return info;
    }

    public void setInfo(VideoInfo info) {
        if (this.info == null) this.info = info;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    @Override
    public Pane display() {
        System.out.println("Displaying");
        DefPornParser parser = new DefPornParser();
        // Главный контейнер - StackPane для наложения элементов
        StackPane mainContainer = new StackPane();
        mainContainer.setMaxWidth(400);

        // Основной контент (видео + информация)
        ImageView view = new ImageView(new Image(pic));
        view.setSmooth(true);
        view.getStyleClass().add("video-preview");
        view.setFitWidth(420);
        view.setFitHeight(250);
        view.setPreserveRatio(true);

        Label label = new Label(String.valueOf(views));
        label.getStyleClass().add("marker");

        Hyperlink titleLabel = new Hyperlink(title);
        titleLabel.setOnAction(e -> {
            System.out.println(getUrl());
            setInfo(parser.parseVideo(getUrl()));
            VideoPlayerPane player = new VideoPlayerPane(this);
            player.show();
        });
        titleLabel.getStyleClass().add("video-title");
        titleLabel.setMaxWidth(350);
        Label dur = new Label(duration);
        dur.getStyleClass().add("marker");

        Label rating = new Label(this.rating);

        HBox hBox = new HBox(10, titleLabel, rating);
        hBox.getStyleClass().add("title-box");
        VBox box = new VBox(10, view, hBox);
        box.getStyleClass().add("trailer");

        // Панель с дополнительной информацией (показывается при наведении)
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        Region r = new Region();
        VBox.setVgrow(r, Priority.ALWAYS);

        Region r2 = new Region();
        VBox.setVgrow(r2, Priority.ALWAYS);

        Region r1 = new Region();
        HBox.setHgrow(r1, Priority.ALWAYS);

        Label fav = new Label();
        favPath.setContent("M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z");

        favPath.setStrokeWidth(2);
        favPath.setStroke(Color.WHITE);
        favPath.setFill(isFavorite() ? Color.rgb(255,15,45): Color.TRANSPARENT);
        fav.setGraphic(favPath);
        fav.setTranslateX(3.5);


        ImageView download = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/plovdev/pronviewer/download.png"))));
        download.setFitWidth(30);
        download.setFitHeight(30);
        download.setPreserveRatio(true);

        download.setOnMousePressed(e -> {
            setInfo(parser.parseVideo(getUrl()));
            showContextMenu(download, e.getScreenX(), e.getScreenY());
        });


        VBox vBox = new VBox(new HBox(r1, new VBox(10, fav, download)), r, new HBox(label, region, dur));
        vBox.setPadding(new Insets(10));
        HBox.setHgrow(vBox, Priority.ALWAYS);



        HBox infoOverlay = new HBox(vBox);
        infoOverlay.setVisible(false);
        infoOverlay.getStyleClass().add("video-hover");

        // Позиционируем overlay поверх изображения
        StackPane.setAlignment(infoOverlay, Pos.BOTTOM_CENTER);
        StackPane.setMargin(infoOverlay, new Insets(0, 0, 50, 0));

        // Собираем всё вместе
        mainContainer.getChildren().addAll(box, infoOverlay);

        // Обработчики наведения
        mainContainer.setOnMouseEntered(e -> infoOverlay.setVisible(true));
        mainContainer.setOnMouseExited(e -> infoOverlay.setVisible(false));
        fav.setOnMousePressed(e -> {
            setFavorite(!isFavorite());
            makeFavorite();
        });

        getChildren().add(mainContainer);
        return this;
    }

    private void showContextMenu(ImageView node, double x, double y) {
        ContextMenu menu = new ContextMenu();
        for (String str : info.getUrls().keySet()) {
            menu.getItems().add(getLoader(str));
        }
        menu.show(node, x, y);
    }
    protected MenuItem getLoader(String qual) {
        MenuItem item = new MenuItem(qual);
        item.setOnAction(e -> {
            Thread thread = new Thread(() -> handler.downloadPorn(info.getUrls().get(qual), "downloads/" + getTitle() + " - " + qual + ".mp4"));
            thread.start();
        });
        return item;
    }

    @Override
    public String toString() {
        return "VideoCard{" +
                "duration='" + duration + '\'' +
                ", views=" + views +
                ", rating='" + rating + '\'' +
                ", info=" + info +
                ", isFavorite=" + isFavorite +
                ", favPath=" + favPath +
                ", handler=" + handler +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", pic='" + pic + '\'' +
                '}';
    }
}