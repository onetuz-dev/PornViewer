package com.plovdev.pornviewer.gui;

import com.plovdev.pornviewer.gui.tabs.PornTabPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class MainMenu extends Application {
    private static final Logger log = LoggerFactory.getLogger(MainMenu.class);

    @Override
    public void start(Stage stage) {
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/plovdev/pornviewer/pv-logo.png")));
        stage.getIcons().add(icon);

        PornTabPane pane = new PornTabPane(stage);

        //VBox root = new VBox(new MenuBar(new Menu("menu1"), new Menu("menu2")), pane);
        Scene scene = new Scene(pane, 1000, 600);
        stage.setOnCloseRequest(e -> {
            try {
                stop();
            } catch (Exception ex) {
                log.error("Stop error: ", ex);
            }
            System.exit(0);
        });

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/plovdev/pornviewer/white.css")).toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setTitle("PornViewer");
        stage.show();
    }
}
