package com.plovdev.pornviewer.encryptsupport.videoparser.read;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.plovdev.pornviewer.encryptsupport.LoadersUtils;
import com.plovdev.pornviewer.encryptsupport.videoparser.VideoMetadata;
import com.plovdev.pornviewer.utility.files.EnvReader;
import com.plovdev.pornviewer.utility.files.FileUtils;
import com.plovdev.pornviewer.utility.security.CipherManager;

import java.io.IOException;
import java.io.InputStream;
import javafx.util.Duration;

public class VideoReader {
    private static final Gson gson = new Gson();
    private static final CipherManager cipherManager = new CipherManager(EnvReader.getEnv("VIDEO_PASSWORD"));

    public static VideoMetadata readMetadata(InputStream stream) throws IOException {
        byte[] sign = new byte[FileUtils.PORN_VIEWER_SIGN.length()];
        int readedSign = stream.read(sign);
        if (readedSign != sign.length) {
            throw new IOException("Invalid readed sign length: " + readedSign + "/" + sign.length);
        }

        String signature = new String(sign);
        if (!signature.equals(FileUtils.PORN_VIEWER_SIGN)) {
            throw new IOException("Not a pv-encrypted video file!");
        }

        byte[] totalMetadataSizeBytes = new byte[4];
        readStreamToBytes(totalMetadataSizeBytes, stream);
        int totalMetadataSize = LoadersUtils.bytesToInt(totalMetadataSizeBytes);

        byte[] jsonSizeBytes = new byte[4];
        readStreamToBytes(jsonSizeBytes, stream);
        int jsonSize = LoadersUtils.bytesToInt(jsonSizeBytes);

        byte[] originalJsonBytes = new byte[jsonSize];
        readStreamToBytes(originalJsonBytes, stream);
        originalJsonBytes = cipherManager.decrypt(originalJsonBytes);
        String originalJson = new String(originalJsonBytes);
        JsonObject jsonMetadata = gson.fromJson(originalJson, JsonObject.class);
        String originalName = jsonMetadata.get("name").getAsString();
        String mimeType = jsonMetadata.get("mime").getAsString();
        Duration totalDuration = Duration.millis(jsonMetadata.get("duration").getAsDouble());

        byte[] previewSizeBytes = new byte[4];
        readStreamToBytes(previewSizeBytes, stream);
        int previewSize = LoadersUtils.bytesToInt(previewSizeBytes);

        byte[] previewBytes = new byte[previewSize];
        readStreamToBytes(previewBytes, stream);
        previewBytes = cipherManager.decrypt(previewBytes);

        return new VideoMetadata(signature, totalMetadataSize, jsonSize, originalJson, originalName, mimeType, totalDuration, previewSize, previewBytes);
    }

    private static void readStreamToBytes(byte[] bytes, InputStream stream) throws IOException {
        int readed = stream.read(bytes);
        if (readed != bytes.length) {
            throw new IOException("Invalid readed length: " + readed + "/" + bytes.length);
        }
    }
}