package com.plovdev.pornviewer.encryptionsupport.videoparser.videomodel;

public class EncryptedVideo {
    private VideoHeader videoHeader;
    private VideoMetadata videoMetadata;

    public EncryptedVideo(VideoHeader videoHeader, VideoMetadata videoMetadata) {
        this.videoHeader = videoHeader;
        this.videoMetadata = videoMetadata;
    }

    public EncryptedVideo() {
    }

    public VideoHeader getVideoHeader() {
        return videoHeader;
    }

    public void setVideoHeader(VideoHeader videoHeader) {
        this.videoHeader = videoHeader;
    }

    public VideoMetadata getVideoMetadata() {
        return videoMetadata;
    }

    public void setVideoMetadata(VideoMetadata videoMetadata) {
        this.videoMetadata = videoMetadata;
    }
}