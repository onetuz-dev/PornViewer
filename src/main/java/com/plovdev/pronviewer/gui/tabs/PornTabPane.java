package com.plovdev.pronviewer.gui.tabs;

import com.plovdev.pronviewer.gui.panes.DownloadsPane;
import com.plovdev.pronviewer.gui.panes.FavoritePane;
import com.plovdev.pronviewer.gui.panes.MainMenuPane;
import com.plovdev.pronviewer.gui.panes.ModelsPane;
import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class PornTabPane extends TabPane {
    public PornTabPane(Stage stage) {
        Thread.startVirtualThread(() -> {
            getStyleClass().add("porn-tab-pane");
            setSide(Side.BOTTOM);
            MainMenuPane mainMenuPane = new MainMenuPane();
            ModelsPane modelsPane = new ModelsPane();
            FavoritePane favoritePane = new FavoritePane();
            DownloadsPane downloadsPane = new DownloadsPane();

            tabMinWidthProperty().bind(widthProperty().divide(4.5));
            tabMaxWidthProperty().bind(widthProperty().divide(4.5));

            setTabMinHeight(45);
            setTabMaxHeight(45);

            PornTab main = new PornTab(mainMenuPane, "Главная");

            PornTab models = new PornTab(modelsPane, "Модели");
            PornTab favorites = new PornTab(favoritePane, "Избранное");
            PornTab downloads = new PornTab(downloadsPane, "Загрузки");

            Platform.runLater(() -> getTabs().addAll(main, models, favorites, downloads));
        });
    }
}