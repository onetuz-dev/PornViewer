package com.plovdev.pronviewer.gui.menu;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

public class MainMenuBar extends MenuBar {
    public MainMenuBar() {
        getStyleClass().add("porn-bar");
        Menu app = new Menu("App");

        Menu settings = new Menu("Settings");

        getMenus().addAll(app, settings);
    }
}