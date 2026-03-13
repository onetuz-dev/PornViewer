package com.plovdev.pornviewer.gui.panes.pagination;

import com.plovdev.pornviewer.gui.filters.TrinaglePaginationBlock;
import com.plovdev.pornviewer.httpquering.PornHandler;
import com.plovdev.pornviewer.httpquering.defimpl.PBPornHandler;
import com.plovdev.pornviewer.models.PornCard;
import com.plovdev.pornviewer.pornimpl.porn365.DefPornParser;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainPagination {
    private final Set<ObservableList<Pane>> cahe = new HashSet<>();
    private final IntegerProperty page = new SimpleIntegerProperty(0);
    private String baseUrl = "http://5porno365.net/russkoe";
    private String nextUrl = "http://5porno365.net/russkoe/1";
    private final TrinaglePaginationBlock block;

    public MainPagination(FlowPane content, TrinaglePaginationBlock block) {
        block.setOnBack(() -> {
            int next = Math.max(0, page.get()-1);
            content.getChildren().clear();
            runPornParsing(content, baseUrl + "/" + next);
            block.getBack().setDisable(next == 0);
            page.set(next);

            block.getBack().setText("Назад " + Math.max(0, next-1));
            block.getNext().setText((page.get()) + " Вперед");
        });
        block.setOnToStart(() -> {
            content.getChildren().clear();
            runPornParsing(content, baseUrl);
            page.set(0);
        });
        block.setOnNext(() -> {
            content.getChildren().clear();
            runPornParsing(content, nextUrl);
            page.set(page.get()+1);

            block.getBack().setText("Назад " + page.get());
            block.getNext().setText((page.get()+1) + " Вперед");
        });
        this.block = block;
    }

    private void runPornParsing(FlowPane pane, String url) {
        Thread thread = new Thread(getParseTask(pane, url), "Parser");
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    public Set<ObservableList<Pane>> getCahe() {
        return cahe;
    }

    public int getPage() {
        return page.get();
    }

    public IntegerProperty pageProperty() {
        return page;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    private Runnable getParseTask(FlowPane pane, String url) {
        return () -> {
            try {
                PornHandler handler = new PBPornHandler();
                System.out.println("start");
                String htmlPage = handler.requestPorn(url);
                nextUrl = handler.getNextLink(htmlPage);
                block.getNext().setDisable(nextUrl == null);
                DefPornParser pornParser = new DefPornParser();//new String(Files.readAllBytes(Path.of("src/html.html"))));
                System.out.println("handled");
                List<PornCard> cards = pornParser.getAll(htmlPage);
                System.out.println("parsed");
                cards.forEach(e -> {
                    Pane card = e.display();
                    Platform.runLater(() -> pane.getChildren().add(card));
                });
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        };
    }
}