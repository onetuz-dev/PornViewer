package com.plovdev.pornviewer.gui.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class ClipboardUtils {
    private static final Logger log = LoggerFactory.getLogger(ClipboardUtils.class);

    private ClipboardUtils() {}

    public static void copy(String text) {
        if (text == null || text.isEmpty()) return;
        try {
            StringSelection selection = new StringSelection(text);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
        } catch (Exception e) {
            log.error("Copy error: ", e);
        }
    }

    public static String paste() {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable contents = clipboard.getContents(null);
            if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) contents.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (Exception e) {
            log.error("Paste error: ", e);
        }
        return "";
    }
}
