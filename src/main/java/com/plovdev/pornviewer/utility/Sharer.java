package com.plovdev.pornviewer.utility;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public class Sharer {
    public static void share(Stage owner, String url, String title) {
        VBox shareBox = new VBox(100);
        shareBox.getStyleClass().add("sharer");

        Button cancel = new Button("Отменить");
        cancel.getStyleClass().add("cancelShare");
        shareBox.getChildren().add(cancel);

        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.setTitle("Share");
        Scene scene = new Scene(shareBox, 300,400);
        scene.getStylesheets().add(Objects.requireNonNull(Sharer.class.getResource("/com/plovdev/pornviewer/white.css")).toExternalForm());

        stage.setScene(scene);
        stage.showAndWait();
    }
}
