package com.plovdev.pornviewer.server.utils;

import com.plovdev.pornviewer.encryptionsupport.CryptoEngine;
import com.plovdev.pornviewer.encryptionsupport.videoparser.videomodel.EncryptedVideo;

public class VideoRequestSet {
    private EncryptedVideo encryptedVideo;
    private CryptoEngine cryptoEngine;

    public VideoRequestSet(EncryptedVideo encryptedVideo, CryptoEngine cryptoEngine) {
        this.encryptedVideo = encryptedVideo;
        this.cryptoEngine = cryptoEngine;
    }

    public EncryptedVideo getEncryptedVideo() {
        return encryptedVideo;
    }

    public void setEncryptedVideo(EncryptedVideo encryptedVideo) {
        this.encryptedVideo = encryptedVideo;
    }

    public CryptoEngine getCryptoEngine() {
        return cryptoEngine;
    }

    public void setCryptoEngine(CryptoEngine cryptoEngine) {
        this.cryptoEngine = cryptoEngine;
    }
}