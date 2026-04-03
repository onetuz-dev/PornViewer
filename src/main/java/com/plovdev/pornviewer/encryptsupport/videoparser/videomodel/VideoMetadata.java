package com.plovdev.pornviewer.encryptsupport.videoparser.videomodel;

import com.plovdev.pornviewer.encryptsupport.LoadersUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.zip.CRC32;

/**
 * Метаданные видеофайла PVVF.
 * Содержит зашифрованный JSON и Preview с их индивидуальными тегами Poly1305.
 */
public record VideoMetadata(int metadataSize, int encryptedJsonSize, int encryptedPreviewSize, byte[] metadataNonce,
                            byte[] encryptedJson, byte[] jsonTag, byte[] encryptedPreview, byte[] previewTag,
                            long metadataCRC32) {

    public static final int BASE_NONCE_LENGTH = 8;
    public static final int TAG_SIZE = 16;
    public static final int CRC_SIZE = 4;
    public static final String JSON_INDIFICATOR = "JSON";
    public static final String PREVIEW_INDIFICATOR = "PRVW";

    public VideoMetadata {
        Objects.requireNonNull(metadataNonce);
        Objects.requireNonNull(encryptedJson);
        Objects.requireNonNull(jsonTag);
        Objects.requireNonNull(encryptedPreview);
        Objects.requireNonNull(previewTag);

        if (metadataNonce.length != BASE_NONCE_LENGTH) {
            throw new IllegalArgumentException("Metadata nonce must be 8 bytes");
        }
        if (jsonTag.length != TAG_SIZE || previewTag.length != TAG_SIZE) {
            throw new IllegalArgumentException("ChaCha20 tags must be 16 bytes");
        }
    }

    /**
     * Формирует полный 12-байтовый Nonce для JSON блока.
     */
    public byte[] getJsonFullNonce() {
        return ByteBuffer.allocate(12).put(metadataNonce).put(JSON_INDIFICATOR.getBytes(StandardCharsets.US_ASCII)).array();
    }

    /**
     * Формирует полный 12-байтовый Nonce для Preview блока.
     */
    public byte[] getPreviewFullNonce() {
        return ByteBuffer.allocate(12).put(metadataNonce).put(PREVIEW_INDIFICATOR.getBytes(StandardCharsets.US_ASCII)).array();
    }

    public long calculateCRC32() {
        CRC32 crc32 = new CRC32();
        crc32.update(LoadersUtils.intToBytes(metadataSize));
        crc32.update(LoadersUtils.intToBytes(encryptedJsonSize));
        crc32.update(LoadersUtils.intToBytes(encryptedPreviewSize));
        crc32.update(metadataNonce);
        crc32.update(encryptedJson);
        crc32.update(jsonTag);
        crc32.update(encryptedPreview);
        crc32.update(previewTag);

        return crc32.getValue();
    }
}