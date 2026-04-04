package com.plovdev.pornviewer.events;

import java.io.File;

public abstract class FileDownloadingEventAdapter implements FileDownloadingEvent {
    @Override
    public void fileDownloading(long downloadedBytes) {

    }

    @Override
    public void onDownloadFinishing(File file) {

    }

    @Override
    public void onDownloadStrarting(long totalBytes) {

    }

    @Override
    public void onError(Exception e) {

    }
}
