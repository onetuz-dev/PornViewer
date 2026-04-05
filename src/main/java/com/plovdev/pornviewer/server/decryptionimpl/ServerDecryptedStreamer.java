package com.plovdev.pornviewer.server.decryptionimpl;

import com.plovdev.pornviewer.encryptionsupport.videoparser.read.VideoChunkReader;
import com.plovdev.pornviewer.encryptionsupport.videoparser.videomodel.EncryptedVideo;
import com.plovdev.pornviewer.encryptionsupport.videoparser.videomodel.VideoChunk;

import java.io.File;
import java.io.OutputStream;

public class ServerDecryptedStreamer {
    private final File file;
    private final long startPosition;
    private final long length;
    private final EncryptedVideo video;
    private final VideoChunkReader chunkReader;

    public ServerDecryptedStreamer(File file, EncryptedVideo video, long startInFile, long length) {
        this.file = file;
        this.startPosition = startInFile;
        this.length = length;
        this.video = video;
        chunkReader = new VideoChunkReader(file, video.getVideoHeader().baseNonce());
    }

    public File getFile() {
        return file;
    }

    public long getStartPosition() {
        return startPosition;
    }

    public long getLength() {
        return length;
    }

    public EncryptedVideo getVideo() {
        return video;
    }

    public VideoChunkReader getChunkReader() {
        return chunkReader;
    }

    public void transferToOutput(OutputStream stream) {
        try {
            // Определяем нужные чанки
            int startChunk = (int) (startPosition / VideoChunk.PLAIN_CHUNK_SIZE);
            int endChunk = (int) ((startPosition + length - 1) / VideoChunk.PLAIN_CHUNK_SIZE);
            int offsetInStartChunk = (int) (startPosition % VideoChunk.PLAIN_CHUNK_SIZE);
            long remaining = length;

            for (int chunkIdx = startChunk; chunkIdx <= endChunk && remaining > 0; chunkIdx++) {
                // Читаем и расшифровываем чанк
                byte[] plainChunk = chunkReader.readEncryptedChunk(chunkIdx);
                // Вычисляем границы в этом чанке
                int start = (chunkIdx == startChunk) ? offsetInStartChunk : 0;
                int toWrite = (int) Math.min(VideoChunk.PLAIN_CHUNK_SIZE - start, remaining);
                // Отправляем клиенту
                stream.write(plainChunk, start, toWrite);
                remaining -= toWrite;
            }

            stream.flush();
        } catch (Exception e) {
            throw new RuntimeException("Failed to stream decrypted video", e);
        }
    }
}