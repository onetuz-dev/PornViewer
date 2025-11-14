package com.plovdev.pronviewer.events.listeners;

import com.plovdev.pronviewer.events.FileDownloadingEvent;

import java.util.ArrayList;

public class FileDownloadingListener {
    private static final ArrayList<FileDownloadingEvent> starts = new ArrayList<>();

    private FileDownloadingListener() {

    }

    public static void addListener(FileDownloadingEvent event) {
        starts.add(event);
    }
    public static void removeListener(FileDownloadingEvent event) {
        starts.remove(event);
    }

    public static void notifyErrorListeners(Exception exception) {
        for (FileDownloadingEvent e : starts) e.onError(exception);
    }
    public static void notifyStartsListeners(long total) {
        for (FileDownloadingEvent e : starts) {
            e.onDownloadStrarting(total);
        }
    }

    public static void notifyEndListeners(String file) {
        for (FileDownloadingEvent e : starts) e.onDownloadFinishing(file);
    }

    public static void notifyProcessListeners(long total) {
        for (FileDownloadingEvent e : starts) e.fileDownloading(total);
    }
}