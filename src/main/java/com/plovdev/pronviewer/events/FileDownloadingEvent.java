package com.plovdev.pronviewer.events;

public interface FileDownloadingEvent {
    void fileDownloading(long downloadedBytes);
    void onDownloadFinishing(String file);
    void onDownloadStrarting(long totalBytes);
    void onError(Exception e);
}