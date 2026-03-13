package com.plovdev.pornviewer.gui.panes;

import com.plovdev.pornviewer.databases.UserPreferences;
import com.plovdev.pornviewer.httpquering.*;
import com.plovdev.pornviewer.httpquering.defimpl.PBPornHandler;
import com.plovdev.pornviewer.models.ModelCard;
import com.plovdev.pornviewer.models.ModelInfo;
import com.plovdev.pornviewer.models.PornCard;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import java.awt.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ModelsPane extends AnchorPane {
    private final ObservableList<Pane> originNots = FXCollections.observableArrayList();
    private final ObservableList<Pane> loadedModels = FXCollections.observableArrayList();
    private final Button back = new Button("<--");
    private final Resourcer resourcer;
    private final PornVideoAdapter adapter;
    private final PornChecker checker;
    private final PornHandler handler = new PBPornHandler();

    public ModelsPane() {
        adapter = UserPreferences.get("0000").getPornAdapter();
        resourcer = adapter.getResourcer();
        checker = adapter.getChecker();

        BorderPane root = new BorderPane();
        VBox vBox = new VBox(10);

        back.setVisible(false);
        back.getStyleClass().add("categories");

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

        Region r1 = new Region();
        HBox.setHgrow(r1, Priority.ALWAYS);

        Region r2 = new Region();
        HBox.setHgrow(r2, Priority.ALWAYS);

        Label clear = new Label("✕");
        clear.setVisible(false);
        clear.setOnMousePressed(e -> field.setText(""));
        clear.getStyleClass().add("clear-search");

        vBox.getChildren().addAll(new HBox(field,clear, r2, back), new HBox(30, new VBox(10, box1, box3), new VBox(10, box4, box6), r1));
        vBox.setPadding(new Insets(0, 0, 30, 0));
        root.setTop(new VBox(vBox));

        root.getStyleClass().add("main-pane");
        FlowPane pane = new FlowPane(50, 50);
        pane.setAlignment(Pos.TOP_LEFT);
        pane.getStyleClass().add("main-pane-content");
        pane.setPadding(new Insets(10, 10, 50, 10));
        runPornParsing(pane, resourcer.baseUrl() + resourcer.modelsUrl());

        back.setOnAction(e -> {
            pane.getChildren().clear();
            pane.getChildren().addAll(loadedModels);
            back.setVisible(false);
        });

        field.setOnAction(e -> {
            if (!checker.canSearch()) return;

            String txt = field.getText();
            txt = txt.replace("/","");
            if (!txt.isEmpty()) {
                pane.getChildren().clear();
                originNots.clear();
                runPornParsing(pane, resourcer.baseUrl() + resourcer.modelsUrl() + URLEncoder.encode(field.getText(), Charset.defaultCharset()));
            }
        });
        field.textProperty().addListener((e1,e2,e3) -> {
            clear.setVisible(!e3.isEmpty());

            List<Pane> panes = new ArrayList<>(originNots);
            panes = panes.stream().filter(e -> {
                if (e instanceof ModelCard card) {
                    return card.getModelInfo().getRusName().toLowerCase().contains(e3.trim().toLowerCase());
                }
                return true;
            }).toList();

            pane.getChildren().setAll(panes);
        });


        ScrollPane pornScroll = new ScrollPane(pane);
        pornScroll.getStyleClass().add("porn-scroll");
        pornScroll.setFitToHeight(true);
        pornScroll.setFitToWidth(true);

        root.setCenter(pornScroll);

        getChildren().add(root);

        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);
    }

    private void runPornParsing(FlowPane pane, String url) {
        Thread.startVirtualThread(getParseTask(pane, url));
    }

    private Runnable getParseTask(FlowPane pane, String url) {
        return () -> {
            try {
                if (!checker.hasModels()) return;

                PornHandler handler = new PBPornHandler();
                PornParser parser = adapter.getParser();
                List<ModelInfo> cards = parser.getModels(handler.requestPorn(url));
                cards.forEach(e -> {
                    ModelCard card = new ModelCard(e, pane);
                    card.addListener(info -> {
                        back.setVisible(true);
                        runModelParsing(pane, info.getUrl());
                    });
                    Pane p = card.display();
                    originNots.add(p);
                    loadedModels.add(p);
                    Platform.runLater(() -> pane.getChildren().add(p));
                });
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        };
    }


    private void runModelParsing(FlowPane pane, String url) {
        pane.getChildren().clear();
        originNots.clear();
        Thread.startVirtualThread(getParseModelTask(pane, url));
    }

    private Runnable getParseModelTask(FlowPane pane, String url) {
        return () -> {
            try {
                if (!checker.hasModels()) return;

                System.out.println("start");
                PornParser pornParser = adapter.getParser();
                System.out.println("handled");
                List<PornCard> cards = pornParser.getAll(handler.requestPorn(url));
                System.out.println("parsed");
                cards.forEach(e -> {
                    Pane card = e.display();
                    originNots.add(card);
                    Platform.runLater(() -> pane.getChildren().add(card));
                });
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        };
    }
}