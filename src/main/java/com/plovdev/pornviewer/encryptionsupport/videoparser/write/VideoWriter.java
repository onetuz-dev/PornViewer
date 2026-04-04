package com.plovdev.pornviewer.encryptionsupport.videoparser.write;

import com.google.gson.Gson;
import com.plovdev.pornviewer.encryptionsupport.LoadersUtils;
import com.plovdev.pornviewer.encryptionsupport.videoparser.VideoMetadata;
import com.plovdev.pornviewer.encryptionsupport.videoparser.read.VideoReader;
import com.plovdev.pornviewer.utility.files.FileUtils;
import com.plovdev.pornviewer.utility.security.CipherManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public record VideoWriter(VideoMetadata metadata) {
    private static final Gson gson = new Gson();
    private static final CipherManager cipherManager = new CipherManager(CipherManager.getPassword());
    private static final int VIDEO_DATA_OFFSET = 40;
    private static final Logger log = LoggerFactory.getLogger(VideoWriter.class);

    public void writeMetadataHeaderToStream(OutputStream stream) throws IOException {
        stream.write(FileUtils.PORN_VIEWER_SIGN.getBytes(StandardCharsets.UTF_8));
        stream.write(LoadersUtils.longToBytes(metadata.getVideoSize()));
    }

    public void writeMetadataToStream(OutputStream stream) throws IOException {
        byte[] metaSize = LoadersUtils.intToLittleEndian(metadata.getMetadataSize());
        stream.write(metaSize);

        byte[] jsonSize = LoadersUtils.intToLittleEndian(metadata.getJsonSize());
        stream.write(jsonSize);

        stream.write(cipherManager.encrypt(metadata.getOriginalJson().getBytes(StandardCharsets.UTF_8)));

        byte[] previewSize = LoadersUtils.intToLittleEndian(metadata.getPreviewSize());
        stream.write(previewSize);

        stream.write(cipherManager.encrypt(metadata.getPreview()));
    }

    public void updateMetadata(VideoMetadata newMetadata, File file) {
        try {
            VideoMetadata oldMetadata = VideoReader.readMetadata(file);

            try (RandomAccessFile stream = new RandomAccessFile(file, "rw")) {
                stream.seek(newMetadata.getVideoSize() + VIDEO_DATA_OFFSET);

                byte[] metaSize = LoadersUtils.intToLittleEndian(newMetadata.getMetadataSize());
                stream.write(metaSize);

                byte[] jsonSize = LoadersUtils.intToLittleEndian(newMetadata.getJsonSize());
                stream.write(jsonSize);

                stream.write(cipherManager.encrypt(newMetadata.getOriginalJson().getBytes(StandardCharsets.UTF_8)));

                byte[] previewSize = LoadersUtils.intToLittleEndian(newMetadata.getPreviewSize());
                stream.write(previewSize);
                stream.write(cipherManager.encrypt(newMetadata.getPreview()));

                if (newMetadata.getMetadataSize() < oldMetadata.getMetadataSize()) {
                    try (FileChannel channel = stream.getChannel().truncate(newMetadata.getVideoSize() + newMetadata.getTotalMetaSize())) {
                        channel.force(false);
                    }
                }
            }
        } catch (Exception e) {
            log.error("File parsing error: ", e);
        }
    }
}