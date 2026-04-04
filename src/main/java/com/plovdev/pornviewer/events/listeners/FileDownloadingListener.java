package com.plovdev.pornviewer.events.listeners;

import com.plovdev.pornviewer.events.FileDownloadingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;

public class FileDownloadingListener {
    private static final ArrayList<FileDownloadingEvent> starts = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(FileDownloadingListener.class);

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

    public static void notifyEndListeners(File file) {
        for (FileDownloadingEvent e : starts) e.onDownloadFinishing(file);
    }

    public static void notifyProcessListeners(long total) {
        for (FileDownloadingEvent e : starts) e.fileDownloading(total);
    }
}