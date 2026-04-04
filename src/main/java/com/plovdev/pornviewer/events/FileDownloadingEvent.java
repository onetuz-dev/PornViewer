package com.plovdev.pornviewer.events;

import java.io.File;

public interface FileDownloadingEvent {
    void fileDownloading(long downloadedBytes);
    void onDownloadFinishing(File file);
    void onDownloadStrarting(long totalBytes);
    void onError(Exception e);
}