package com.plovdev.pornviewer.events;

public abstract class FileDownloadingEventAdapter implements FileDownloadingEvent {
    @Override
    public void fileDownloading(long downloadedBytes) {

    }

    @Override
    public void onDownloadFinishing(String file) {

    }

    @Override
    public void onDownloadStrarting(long totalBytes) {

    }

    @Override
    public void onError(Exception e) {

    }
}
