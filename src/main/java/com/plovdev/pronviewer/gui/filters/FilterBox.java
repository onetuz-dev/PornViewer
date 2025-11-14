package com.plovdev.pronviewer.gui.filters;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class FilterBox extends FlowPane {
    private List<Pane> content = new ArrayList<>();
    private List<ToggleButton> buttons = new ArrayList<>();

    public FilterBox(Pane pane) {
        super(30,30);
        makeGui(pane);
    }
    public FilterBox(List<Pane> c, Pane pane) {
        super(30,30);

        content = c;
        makeGui(pane);
    }

    public List<Pane> getContent() {
        return content;
    }

    public void setContent(List<Pane> content) {
        this.content = content;
    }

    private void makeGui(Pane pane) {
        ToggleButton name = new ToggleButton("Название");
        ToggleButton date = new ToggleButton("Дата");
        buttons.add(name);
        buttons.add(date);

        name.selectedProperty().addListener((b1, b2, b3) -> {
            if (b3) {

            }
        });
        date.setOnMousePressed(a -> {
            if (date.isSelected()) {
                ToggleGroup group = new ToggleGroup();
                RadioMenuItem item1 = new RadioMenuItem("Вверх");
                item1.setToggleGroup(group);

                RadioMenuItem item2 = new RadioMenuItem("Вниз");
                item2.setToggleGroup(group);

                ContextMenu menu = new ContextMenu(item1, item2);
                menu.show(date, a.getScreenX(),a.getScreenY());
            }
        });

        ToggleGroup group = new ToggleGroup();
        for (ToggleButton button : buttons) {
            button.setToggleGroup(group);
            button.prefWidthProperty().bind(widthProperty().divide(3));
            button.prefHeightProperty().bind(widthProperty().divide(2));
            getChildren().add(button);
        }
    }
}