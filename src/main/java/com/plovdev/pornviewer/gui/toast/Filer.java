package com.plovdev.pornviewer.gui.toast;

import javafx.scene.control.Dialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Filer {
    private static final Logger log = LoggerFactory.getLogger(Filer.class);
    private String path = null;

    public Filer() {
        try {
            Dialog<String> fileDialog = new Dialog<>();
            fileDialog.setTitle("File chooser");
            fileDialog.setHeaderText("Выбирете файл:");
            FileChooser chooser = new FileChooser();

            Stage stage = new Stage();
            stage.setTitle("File selecting");

            File file = chooser.showSaveDialog(stage);

            if (file != null) path = file.getAbsolutePath();
        } catch (Exception e) {
            log.error("File selecting error: ", e);
        }
    }

    public String getPath() {
        return path;
    }
}