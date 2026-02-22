package com.plovdev.pornviewer.gui.tabs;

import com.plovdev.pornviewer.gui.panes.DownloadsPane;
import com.plovdev.pornviewer.gui.panes.FavoritePane;
import com.plovdev.pornviewer.gui.panes.MainMenuPane;
import com.plovdev.pornviewer.gui.panes.ModelsPane;
import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PornTabPane extends TabPane {

    private static final Logger log = LoggerFactory.getLogger(PornTabPane.class);

    public PornTabPane(Stage stage) {
        getStyleClass().add("porn-tab-pane");
        setSide(Side.BOTTOM);

        tabMinWidthProperty().bind(widthProperty().divide(4.5));
        tabMaxWidthProperty().bind(widthProperty().divide(4.5));

        setTabMinHeight(45);
        setTabMaxHeight(45);

        PornTab main = new PornTab(new Pane(), "Главная");
        PornTab models = new PornTab(new Pane(), "Модели");
        PornTab favorites = new PornTab(new Pane(), "Избранное");
        PornTab downloads = new PornTab(new Pane(), "Загрузки");

        getTabs().addAll(main, models, favorites, downloads);

        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(() -> {
            MainMenuPane mainMenuPane = new MainMenuPane();
            Platform.runLater(() -> main.setContent(mainMenuPane));
        });

        service.execute(() -> {
            ModelsPane modelsPane = new ModelsPane();
            Platform.runLater(() -> models.setContent(modelsPane));
        });

        service.execute(() -> {
            FavoritePane favoritePane = new FavoritePane();
            Platform.runLater(() -> favorites.setContent(favoritePane));
        });

        service.execute(() -> {
            DownloadsPane downloadsPane = new DownloadsPane();
            Platform.runLater(() -> downloads.setContent(downloadsPane));
        });
    }
}