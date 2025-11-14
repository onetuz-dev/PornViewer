package com.plovdev.pronviewer.utility;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Objects;

public class DialogShower {
    public static void showConfirm(String text, Runnable onOk, String ... styles) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Confirm");
        BorderPane content = new BorderPane();
        Scene scene = new Scene(content, 300, 250);
        scene.setFill(Color.valueOf("#FAFAFA"));
        scene.getStylesheets().add(Objects.requireNonNull(BorderPane.class.getResource("/com/plovdev/pronviewer/white.css")).toExternalForm());
        stage.setScene(scene);

        Label confirm = new Label(text);
        confirm.getStyleClass().add("marker-download");

        content.setCenter(confirm);

        Button ok = new Button("OK");
        ok.setOnAction(e -> {
            new Thread(onOk).start();
            stage.close();
        });

        Button cancel = new Button("Cancel");
        cancel.setCancelButton(true);
        cancel.setOnAction(e -> stage.close());

        content.setBottom(new HBox(30, ok, cancel));

        for (String style : styles) scene.getStylesheets().add(style);

        stage.show();
    }
}