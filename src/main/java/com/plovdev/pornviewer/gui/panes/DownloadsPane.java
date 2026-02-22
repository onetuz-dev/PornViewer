package com.plovdev.pornviewer.gui.panes;

import com.plovdev.pornviewer.events.listeners.EventListener;
import com.plovdev.pornviewer.events.listeners.FileListener;
import com.plovdev.pornviewer.gui.filters.FilterBox;
import com.plovdev.pornviewer.models.DownloadedVideoCard;
import com.plovdev.pornviewer.models.DownloadingVideoCard;
import com.plovdev.pornviewer.models.VideoCard;
import com.plovdev.pornviewer.utility.constants.EntryEventTypes;
import com.plovdev.pornviewer.utility.files.FileUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DownloadsPane extends AnchorPane {
    private final ObservableList<Pane> originNots = FXCollections.observableArrayList();
    private boolean isSelf = false;
    private final DateTimeFormatter createFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public DownloadsPane() {
        BorderPane root = new BorderPane();
        FlowPane pane = new FlowPane(50, 50);
        VBox vBox = new VBox(10);

        vBox.getStyleClass().add("vbox");
        TextField field = new TextField();
        field.getStyleClass().add("porn-search");
        field.prefWidthProperty().bind(widthProperty().divide(1.2));
        field.setPrefHeight(35);

        field.setPromptText("Поиск...");

        CheckBox box1 = new CheckBox("Названия");
        box1.getStyleClass().add("porn-check-box");
        CheckBox box3 = new CheckBox("Длительность");
        box3.getStyleClass().add("porn-check-box");
        CheckBox box4 = new CheckBox("Дата");
        box4.getStyleClass().add("porn-check-box");
        CheckBox box6 = new CheckBox("Размер");
        box6.getStyleClass().add("porn-check-box");

        Region r1 = new Region();
        HBox.setHgrow(r1, Priority.ALWAYS);

        FilterBox filterBox = new FilterBox(pane);
        filterBox.setPrefSize(300, 100);

        vBox.getChildren().addAll(new HBox(field), new HBox(30, new VBox(10, box1, box3), new VBox(10, box4, box6), r1));
        vBox.setPadding(new Insets(0, 0, 30, 0));
        root.setTop(new VBox(vBox));

        root.getStyleClass().add("main-pane");
        pane.setAlignment(Pos.TOP_LEFT);
        pane.getStyleClass().add("main-pane-content");
        pane.setPadding(new Insets(10, 10, 50, 10));
        runPornParsing(pane);

        field.textProperty().addListener((e1, e2, e3) -> {
            List<Pane> panes = new ArrayList<>(originNots);
            panes = panes.stream().filter(e -> {
                VideoCard card = (VideoCard) e;
                return card.getTitle().toLowerCase().contains(e3.trim().toLowerCase());
            }).toList();
            pane.getChildren().setAll(panes);
        });


        ScrollPane pornScroll = new ScrollPane(pane);
        pornScroll.getStyleClass().add("porn-scroll");
        pornScroll.setFitToHeight(true);
        pornScroll.setFitToWidth(true);

        root.setCenter(pornScroll);

        getChildren().addAll(root);
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 0.0);

        FileListener fileListener = new FileListener(FileUtils.getPvDownloadsPath().toString());
        fileListener.addListener((a, b) -> {
            if (isSelf) {
                if (b == EntryEventTypes.ENTRY_CREATE || b == EntryEventTypes.ENTRY_DELETE) {
                    runPornParsing(pane);
                }
            }
        });

        EventListener.addListener(e -> {
            if (e.startsWith("START_DWONLOAD:")) {
                String name = e.substring(e.indexOf(':')+1);
                DownloadingVideoCard card = new DownloadingVideoCard(pane);
                card.setTitle(name);
            }
        });
    }

    private void runPornParsing(FlowPane pane) {
        Thread thread = new Thread(getParseTask(pane), "Parser");
        thread.start();
    }

    private Runnable getParseTask(FlowPane pane) {
        return () -> {
            try (Stream<Path> stream = Files.walk(FileUtils.getPvDownloadsPath())) {
                List<Path> paths = stream.toList();
                List<DownloadedVideoCard> cards = paths.stream().filter(e -> e.toString().endsWith(".mp4")).map(p -> {
                    DownloadedVideoCard card = new DownloadedVideoCard(pane);
                    try {
                        File file = p.toFile();
                        BasicFileAttributes attributes = Files.readAttributes(p, BasicFileAttributes.class);
                        FileTime time = attributes.creationTime();
                        LocalDateTime dateTime = LocalDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault());
                        card.setDate(dateTime.format(createFormatter));
                        card.setTitle(p.getFileName().toString());
                        card.setPath(file.toURI().toString());

                        card.setDeleteRun(() -> {
                            isSelf = card.isSelf();
                            try {
                                File toDelete = new File(card.getPath().substring(card.getPath().indexOf(':') + 1));
                                System.out.println("File deleted: " + toDelete.delete());

                                pane.getChildren().forEach(fp -> {
                                    if (fp instanceof VideoCard vc) {
                                        if (vc.getTitle().equals(card.getTitle())) {
                                            Platform.runLater(() -> pane.getChildren().remove(fp));
                                            originNots.remove(fp);
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                System.err.println(e.getMessage());
                            }
                        });

                        BigDecimal size = new BigDecimal(String.valueOf(file.length())).divide(new BigDecimal("1000000.0"), 10, RoundingMode.HALF_UP);
                        DecimalFormat format = new DecimalFormat("#0.00");
                        card.setSize(format.format(size));
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                    return card;
                }).toList();
                originNots.clear();
                Platform.runLater(() -> pane.getChildren().clear());

                cards.forEach(e -> {
                    DownloadedVideoCard card = (DownloadedVideoCard) e.display();
                    originNots.add(card);
                });
                Platform.runLater(() -> pane.getChildren().addAll(originNots));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        };
    }
}