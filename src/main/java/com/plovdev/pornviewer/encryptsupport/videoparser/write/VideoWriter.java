package com.plovdev.pornviewer.encryptsupport.videoparser.write;

import com.google.gson.Gson;
import com.plovdev.pornviewer.encryptsupport.LoadersUtils;
import com.plovdev.pornviewer.encryptsupport.videoparser.VideoMetadata;
import com.plovdev.pornviewer.utility.files.EnvReader;
import com.plovdev.pornviewer.utility.files.FileUtils;
import com.plovdev.pornviewer.utility.security.CipherManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class VideoWriter {
    private static final Gson gson = new Gson();
    private static final CipherManager cipherManager = new CipherManager(EnvReader.getEnv("VIDEO_PASSWORD"));

    public static void writeMetadataToStream(OutputStream stream, VideoMetadata metadata) throws IOException {
        stream.write(FileUtils.PORN_VIEWER_SIGN.getBytes(StandardCharsets.UTF_8));

        byte[] metaSize = LoadersUtils.intToLittleEndian(metadata.getMetadataSize());
        stream.write(metaSize);

        byte[] jsonSize = LoadersUtils.intToLittleEndian(metadata.getJsonSize());
        stream.write(jsonSize);

        stream.write(cipherManager.encrypt(metadata.getOriginalJson().getBytes(StandardCharsets.UTF_8)));

        byte[] previewSize = LoadersUtils.intToLittleEndian(metadata.getPreviewSize());
        stream.write(previewSize);

        stream.write(cipherManager.encrypt(metadata.getPreview()));
    }
}