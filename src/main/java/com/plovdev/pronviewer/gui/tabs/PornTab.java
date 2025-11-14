package com.plovdev.pronviewer.gui.tabs;

import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;

public class PornTab extends Tab {
    public PornTab(Pane content, String name) {
        setClosable(false);
        getStyleClass().add("porn-tab");
        setContent(content);
        setText(name);
    }
}