package com.plovdev.pornviewer.gui.panes;

import com.plovdev.pornviewer.databases.FavoriteVideos;
import com.plovdev.pornviewer.events.listeners.EventListener;
import com.plovdev.pornviewer.events.listeners.FavoriteListener;
import com.plovdev.pornviewer.models.VideoCard;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class FavoritePane extends AnchorPane {
    private final ObservableList<Pane> originNots = FXCollections.observableArrayList();

    public FavoritePane() {
        BorderPane root = new BorderPane();
        VBox vBox = new VBox(10);

        System.out.println("Inited");
        vBox.getStyleClass().add("vbox");
        TextField field = new TextField();
        field.getStyleClass().add("porn-search");
        field.prefWidthProperty().bind(widthProperty().divide(1.2));
        field.setPrefHeight(35);

        field.setPromptText("Поиск...");

        CheckBox box1 = new CheckBox("Названия");
        box1.getStyleClass().add("porn-check-box");
        CheckBox box3 = new CheckBox("Описания");
        box3.getStyleClass().add("porn-check-box");
        CheckBox box4 = new CheckBox("Дата");
        box4.getStyleClass().add("porn-check-box");
        CheckBox box6 = new CheckBox("Теги");
        box6.getStyleClass().add("porn-check-box");
        System.out.println("Created filteres");

        Region r1 = new Region();
        HBox.setHgrow(r1, Priority.ALWAYS);

        Region r2 = new Region();
        HBox.setHgrow(r2, Priority.ALWAYS);

        Label clear = new Label("✕");
        clear.setVisible(false);
        clear.setOnMousePressed(e -> field.setText(""));
        clear.getStyleClass().add("clear-search");

        vBox.getChildren().addAll(new HBox(field,clear), new HBox(30, new VBox(10, box1, box3), new VBox(10, box4, box6), r1));
        vBox.setPadding(new Insets(0, 0, 30, 0));
        root.setTop(new VBox(vBox));

        root.getStyleClass().add("main-pane");
        FlowPane pane = new FlowPane(50, 50);
        pane.setAlignment(Pos.TOP_LEFT);
        pane.getStyleClass().add("main-pane-content");
        pane.setPadding(new Insets(10, 10, 50, 10));
        System.out.println("Create flow pane");

        recountChildren(pane);
        System.out.println("Recounted");

        field.textProperty().addListener((e1,e2,e3) -> {
            clear.setVisible(!e3.isEmpty());

            List<Pane> panes = new ArrayList<>(originNots);
            panes = panes.stream().filter(e -> {
                if (e instanceof VideoCard card) {
                    return card.getTitle().toLowerCase().contains(e3.trim().toLowerCase());
                }
                return true;
            }).toList();

            pane.getChildren().setAll(panes.reversed());
        });

        System.out.println("Create scroll");
        ScrollPane pornScroll = new ScrollPane(pane);
        pornScroll.setFitToHeight(true);
        pornScroll.setFitToWidth(true);

        root.setCenter(pornScroll);

        getChildren().addAll(root);
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);
        System.out.println("Created components");

        FavoriteListener.addListener((videoCard) -> {
            if (!videoCard.isFavorite()) {
                // Удаляем из избранного: ищем карточку не по объекту, а по ID
                pane.getChildren().removeIf(node -> node instanceof VideoCard card && card.getCardId() == videoCard.getCardId());
                EventListener.notifyListeners("favorieStateChanged");
            } else {
                pane.getChildren().addFirst(copyCard(videoCard).display());
            }
        });
        System.out.println("Finish init");
    }

    private VideoCard copyCard(VideoCard videoCard) {
        VideoCard cardCopy = new VideoCard();
        cardCopy.setCardId(videoCard.getCardId());
        cardCopy.setTitle(videoCard.getTitle());
        cardCopy.setUrl(videoCard.getUrl());
        cardCopy.setPic(videoCard.getPic());
        cardCopy.setDuration(videoCard.getDuration());
        cardCopy.setViews(videoCard.getViews());
        cardCopy.setRating(videoCard.getRating());
        cardCopy.setInfo(videoCard.getInfo());
        cardCopy.setFavorite(true);
        return cardCopy;
    }

    private void recountChildren(FlowPane pane) {
        FavoriteVideos.getAll().forEach(e -> {
            Pane p = e.display();
            originNots.add(p);
            Platform.runLater(() -> pane.getChildren().addFirst(p));
        });
    }
}