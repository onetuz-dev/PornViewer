package com.plovdev.pornviewer.encryptionsupport;

import com.plovdev.pornviewer.encryptionsupport.videoparser.videomodel.VideoChunk;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Утилитарный класс для преобразования примитивов в байты и обратно.
 * Использует порядок байтов Big-Endian.
 */
public class LoadersUtils {
    public static byte[] intToLittleEndian(int value) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
    }

    public static int bytesToInt(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < bytes.length; i++) {
            value |= (bytes[i] & 0xFF) << (8 * i); // little-endian
        }
        return value;
    }

    public static byte[] longToBytes(long value) {
        byte[] bytes = new byte[8];
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (value >> (8 * i));
        }
        return bytes;
    }

    /**
     * Преобразует int в массив из 4 байт (Big-Endian).
     */
    public static byte[] intToBytes(int value) {
        return ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(value).array();
    }

    /**
     * Преобразует short в массив из 2 байт (Big-Endian).
     */
    public static byte[] shortToBytes(short value) {
        return ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN).putShort(value).array();
    }

    /**
     * Преобразует float в массив из 4 байт (Big-Endian).
     */
    public static byte[] floatToBytes(float value) {
        return ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putFloat(value).array();
    }

    /**
     * Преобразует массив байт в int (Big-Endian).
     * Ожидает массив длиной до 4 байт.
     */
    public static int bigEndianBytesToInt(byte[] bytes) {
        int value = 0;
        for (byte b : bytes) {
            value = (value << 8) | (b & 0xFF);
        }
        return value;
    }

    /**
     * Преобразует массив байт в long (Big-Endian).
     * Ожидает массив длиной до 8 байт.
     */
    public static long bigEndianBytesToLong(byte[] bytes) {
        long value = 0;
        for (byte b : bytes) {
            value = (value << 8) | (b & 0xFF);
        }
        return value;
    }

    /**
     * Преобразует long в массив из 8 байт (Big-Endian).
     */
    public static byte[] longToBigEndianBytes(long value) {
        byte[] bytes = new byte[8];
        for (int i = 7; i >= 0; i--) {
            bytes[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return bytes;
    }

    public static long calculateTotalEncVideoSize(long plainSize) {
        long totalPlainChunks = Math.ceilDiv(plainSize, VideoChunk.PLAIN_CHUNK_SIZE);
        return VideoChunk.TOTAL_CHUNK_SIZE * totalPlainChunks;
    }

    public static long calculateTotalChunksInPlainVideo(long plainSize) {
        return Math.ceilDiv(plainSize, VideoChunk.PLAIN_CHUNK_SIZE);
    }

    public static long calculateTotalChunksInEncVideo(long encSize) {
        return Math.ceilDiv(encSize, VideoChunk.TOTAL_CHUNK_SIZE);
    }

    public static long calculateTotalPlainVideoSize(long encSize) {
        long totalEncChunks = Math.ceilDiv(encSize, VideoChunk.TOTAL_CHUNK_SIZE);
        long totalTagsSize = totalEncChunks * VideoChunk.TAG_SIZE;
        if (encSize < totalTagsSize) return 0;

        return encSize - totalTagsSize;
    }
}