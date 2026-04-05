package com.plovdev.pornviewer.encryptionsupport.videoparser.read;

import com.plovdev.pornviewer.encryptionsupport.CryptoEngine;
import com.plovdev.pornviewer.encryptionsupport.videoparser.videomodel.VideoChunk;
import com.plovdev.pornviewer.utility.security.CipherManager;

import javax.crypto.Cipher;
import java.io.File;

public class VideoChunkReader implements AutoCloseable {
    private final PVVFParser pvvfParser;
    private final CryptoEngine engine;

    public VideoChunkReader(File file, byte[] baseNonce) {
        pvvfParser = new PVVFParser(file);
        engine = new CryptoEngine(Cipher.DECRYPT_MODE, CipherManager.getPassword().toCharArray(), baseNonce);
    }


    public byte[] readEncryptedChunk(long chunkIndex) {
        VideoChunk chunk = pvvfParser.parseVideoChunk(chunkIndex);
        return engine.processChunk(chunkIndex, chunk.prepareChunk());
    }

    @Override
    public void close() throws Exception {
        pvvfParser.close();
    }
}