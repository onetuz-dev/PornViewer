package com.plovdev.pronviewer.gui.panes;

import com.plovdev.pronviewer.databases.UserPreferences;
import com.plovdev.pronviewer.events.listeners.FavoriteListener;
import com.plovdev.pronviewer.events.listeners.PornUpdateListener;
import com.plovdev.pronviewer.gui.filters.CategoryManager;
import com.plovdev.pronviewer.gui.filters.TrinaglePaginationBlock;
import com.plovdev.pronviewer.gui.panes.pagination.MainPagination;
import com.plovdev.pronviewer.httpquering.*;
import com.plovdev.pronviewer.httpquering.defimpl.PBPornHandler;
import com.plovdev.pronviewer.models.PornCard;
import com.plovdev.pronviewer.models.VideoCard;
import com.plovdev.pronviewer.pornimpl.sexstuds.SexStudsVideoAdapter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainMenuPane extends AnchorPane {
    private final ObservableList<Pane> originNots = FXCollections.observableArrayList();
    private PornVideoAdapter adapter = UserPreferences.get("0000").getPornAdapter();
    private Resourcer resourcer = adapter.getResourcer();
    private PornChecker checker = adapter.getChecker();
    private final PornHandler handler = new PBPornHandler();

    public MainMenuPane() {
        FlowPane pane = new FlowPane(50, 50);

        BorderPane root = new BorderPane();
        VBox vBox = new VBox(10);

        Button categ = new Button("K");
        categ.getStyleClass().add("categories");

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

        ComboBox<String> urlBox = new ComboBox<>(FXCollections.observableArrayList(resourcer.getUrls()));
        ComboBox<String> adaptersBox = new ComboBox<>(FXCollections.observableArrayList(resourcer.baseUrl(), "https://sex-studentki.live"));

        Platform.runLater(() -> {
            urlBox.setValue(urlBox.getItems().getFirst());
            urlBox.getStyleClass().add("porn-select-box");
            adaptersBox.setValue(adaptersBox.getItems().getFirst());
            adaptersBox.getStyleClass().add("porn-select-box");

            urlBox.valueProperty().addListener((p1, p2, p3) -> runPornParsing(pane, p3));
            adaptersBox.valueProperty().addListener((p1, p2, p3) -> {
                adapter = new SexStudsVideoAdapter();
                resourcer = adapter.getResourcer();
                checker = adapter.getChecker();
                runPornParsing(pane, resourcer.baseUrl());
            });
        });

        Region r1 = new Region();
        HBox.setHgrow(r1, Priority.ALWAYS);

        Label clear = new Label("✕");
        clear.setVisible(false);
        clear.setOnMousePressed(e -> field.setText(""));
        clear.getStyleClass().add("clear-search");


        TrinaglePaginationBlock block = new TrinaglePaginationBlock(null, null, null);
        block.setTranslateX(38);
        block.setTranslateY(35);
        block.prefWidthProperty().bind(widthProperty().divide(5));

        vBox.getChildren().addAll(new HBox(field, clear), new HBox(30, new VBox(10, box1, box3), new VBox(10, box4, box6), urlBox, adaptersBox, r1, block, categ));
        vBox.setPadding(new Insets(0, 0, 30, 0));
        root.setTop(new VBox(vBox));

        CategoryManager manager = new CategoryManager(resourcer);
        AnchorPane.setBottomAnchor(manager, 0.0);
        AnchorPane.setTopAnchor(manager, 0.0);
        AnchorPane.setRightAnchor(manager, 0.0);

        manager.resize(this);
        categ.setOnAction(e -> manager.toggle());

        root.getStyleClass().add("main-pane");
        pane.setAlignment(Pos.TOP_LEFT);
        pane.getStyleClass().add("main-pane-content");
        pane.setPadding(new Insets(10, 10, 50, 10));
        runPornParsing(pane, resourcer.baseUrl());

        MainPagination pagination = new MainPagination(pane, block);

        field.setOnAction(e -> {
            if (!checker.canSearch()) return;

            String txt = field.getText();
            txt = txt.replace("/", "");
            if (!txt.isEmpty()) {
                runPornParsing(pane, resourcer.baseUrl() + resourcer.searchUrl() + URLEncoder.encode(field.getText(), Charset.defaultCharset()) + "/popular");
            }
        });
        field.textProperty().addListener((e1, e2, e3) -> {
            clear.setVisible(!e3.isEmpty());

            List<Pane> panes = new ArrayList<>(originNots);
            panes = panes.stream().filter(e -> {
                if (e instanceof VideoCard card) {
                    return card.getTitle().toLowerCase().contains(e3.trim().toLowerCase());
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

        getChildren().addAll(root, manager);
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);

        PornUpdateListener.addListener((e, t) -> {
            if (t == 0) {
                runPornParsing(pane, e);
                pagination.setBaseUrl(e);
            }
        });

        FavoriteListener.addListener(c -> {
            if (!c.isFavorite()) {
                int id = c.getCardId();
                pane.getChildren().forEach(crd -> {
                    if (crd instanceof VideoCard videoCard && videoCard.getCardId() == id) {
                        videoCard.setFavorite(false);
                    }
                });
            }
        });
    }

    private void runPornParsing(FlowPane pane, String url) {
        pane.getChildren().clear();
        originNots.clear();
        Thread.startVirtualThread(getParseTask(pane, url));
    }

    private Runnable getParseTask(FlowPane pane, String url) {
        return () -> {
            try {
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
                System.out.println("Added");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        };
    }
}