package com.plovdev.pornviewer.encryptsupport.videoparser.write;

import com.plovdev.pornviewer.encryptsupport.videoparser.videomodel.VideoChunk;
import com.plovdev.pornviewer.encryptsupport.videoparser.videomodel.VideoHeader;
import com.plovdev.pornviewer.encryptsupport.videoparser.videomodel.VideoMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.plovdev.pornviewer.encryptsupport.videoparser.videomodel.VideoChunk.TOTAL_CHUNK_SIZE;
import static com.plovdev.pornviewer.encryptsupport.videoparser.videomodel.VideoHeader.HEADER_SIZE;
import static com.plovdev.pornviewer.encryptsupport.videoparser.videomodel.VideoHeader.MAGIC_NUMBER;

public class PVVFWriter implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(PVVFWriter.class);

    /**
     * Charset to string decoding
     */
    private static final Charset IO_CHARSET = StandardCharsets.US_ASCII;

    /**
     * Sources to write data.
     */
    private File file;
    private RandomAccessFile RAF;

    /**
     * Создает экземпляр писателя для указанного файла.
     *
     * @param file Файл для записи.
     */
    public PVVFWriter(File file) {
        this.file = file;
        updateRaf(file);
    }

    public File getFile() {
        return file;
    }

    /**
     * Обновляет целевой файл и переоткрывает поток записи.
     *
     * @param file Новый файл для записи.
     */
    public synchronized void setFile(File file) {
        this.file = file;
        updateRaf(file);
    }


    public synchronized void writeVideoHeader(VideoHeader videoHeader) {
        Objects.requireNonNull(videoHeader);

        try {
            // always setup RAF to file start
            RAF.seek(0);

            // step 1 - wrie magic number:
            writeString(MAGIC_NUMBER);

            // step 2 - write version:
            RAF.writeByte(videoHeader.version());
            // step 3 - write flag:
            RAF.writeByte(videoHeader.flag());

            // step 4 - write video mime type:
            writeString(videoHeader.mime());

            // step 5 - write sizes:
            RAF.writeInt(videoHeader.lastChunkPaddingSize());
            RAF.writeLong(videoHeader.plainVideoSize());
            RAF.writeLong(videoHeader.encVideoSize());

            // step 6 - write nonce and crc:
            RAF.write(videoHeader.baseNonce());
            RAF.writeInt((int) videoHeader.calculateCRC32());

            // step 7 - check if file pointer is equal header size:
            if (RAF.getFilePointer() != HEADER_SIZE) {
                throw new IOException("Error write file header: invalid pointer: " + RAF.getFilePointer());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void writeVideoMetadata(long encVideoSize, VideoMetadata toWrite) {
        Objects.requireNonNull(toWrite);
        if (encVideoSize < 0) {
            throw new IllegalArgumentException("Enc video size must be a greather then 0");
        }

        try {
            // calculate real metadata position(42 + enc video size):
            long metadataOffset = HEADER_SIZE + encVideoSize;
            RAF.seek(metadataOffset); // seek to metadata block

            /*
            Задача записать блок с метаданными:
            1 - размеры данных в метадате
            2 - metadata nonce
            3 - данные с их ChaCha20-tag'ами
            4 - crc32
             */

            // sizes block
            RAF.writeInt(toWrite.metadataSize());
            RAF.writeInt(toWrite.encryptedJsonSize());
            RAF.writeInt(toWrite.encryptedPreviewSize());

            // base nonce
            RAF.write(toWrite.metadataNonce());

            // write JSON and tag:
            RAF.write(toWrite.encryptedJson());
            RAF.write(toWrite.jsonTag());

            // read preview and tag:
            RAF.write(toWrite.encryptedPreview());
            RAF.write(toWrite.previewTag());

            RAF.writeInt((int) toWrite.calculateCRC32());

            try (FileChannel channel = RAF.getChannel()) {
                channel.truncate(RAF.getFilePointer());
                channel.force(true);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void writeVideoChunk(VideoChunk videoChunk) {
        Objects.requireNonNull(videoChunk);

        try {
            // calculating chunk start position
            long chunkStart = HEADER_SIZE + (TOTAL_CHUNK_SIZE * videoChunk.chunkNumber());
            RAF.seek(chunkStart); // seek to chunk

            RAF.write(videoChunk.prepareChunk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Вспомогательный метод для записи строки.
     */
    private void writeString(String str) throws IOException {
        byte[] bytes = str.getBytes(IO_CHARSET);
        if (bytes.length != 4) {
            byte[] fixed = new byte[4];
            System.arraycopy(bytes, 0, fixed, 0, Math.min(bytes.length, 4));
            RAF.write(fixed);
        } else {
            RAF.write(bytes);
        }
    }


    /**
     * Инициализирует или переоткрывает {@link RandomAccessFile} в режиме записи.
     *
     * @param file Файл для открытия.
     * @throws RuntimeException если файл не найден или доступ запрещен.
     */
    private void updateRaf(File file) {
        try {
            if (RAF != null) {
                RAF.close();
            }
            RAF = new RandomAccessFile(file, "rw");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Закрывает {@link RandomAccessFile}.
     * Вызывается автоматически при использовании в try-with-resources.
     */
    @Override
    public void close() throws Exception {
        RAF.close();
    }
}