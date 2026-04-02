package com.plovdev.pornviewer.encryptsupport.videoparser;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.plovdev.pornviewer.utility.files.FileUtils;
import com.plovdev.pornviewer.utility.security.CipherManager;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

public class VideoMetadata {
    private static final Gson gson = new Gson();
    private static final CipherManager cm = new CipherManager(CipherManager.getPassword());

    private String signature;
    private long videoSize;
    private int metadataSize;

    private int jsonSize;
    private String originalJson;

    //NO-WRITES
    private String originalName;
    private String description;
    private String url;
    private List<Timecode> timecodes;
    private List<String> tags;
    private String mimeType;
    private Duration totalDuration;
    //NO-WRITES

    private int previewSize;
    private byte[] preview;

    public VideoMetadata(String signature, long videoSize, int metadataSize, int jsonSize, String originalJson, String originalName, String mimeType, Duration totalDuration, int previewSize, byte[] preview) {
        this.signature = signature;
        this.videoSize = videoSize;
        this.metadataSize = metadataSize;
        this.jsonSize = jsonSize;
        this.originalJson = originalJson;
        this.originalName = originalName;
        this.mimeType = mimeType;
        this.totalDuration = totalDuration;
        this.previewSize = previewSize;
        this.preview = preview;
    }

    public VideoMetadata(String json, byte[] preview) {
        byte[] bytes = cm.encrypt(json.getBytes(StandardCharsets.UTF_8));
        int metaSize = bytes.length + preview.length + 4; // +4 - [metadata size] block

        JsonObject jsonMetadata = gson.fromJson(json, JsonObject.class);
        String originalName = jsonMetadata.get("name").getAsString();
        String mimeType = jsonMetadata.get("mime").getAsString();
        Duration totalDuration = Duration.parse(jsonMetadata.get("duration").getAsString());

        this.signature = FileUtils.PORN_VIEWER_SIGN;
        this.metadataSize = metaSize;
        this.jsonSize = bytes.length;
        this.originalJson = json;
        this.originalName = originalName;
        this.mimeType = mimeType;
        this.totalDuration = totalDuration;
        this.previewSize = preview.length;
        this.preview = preview;
    }

    public VideoMetadata(String name, String mime, Duration duration, byte[] preview) {
        JsonObject object = new JsonObject();
        object.addProperty("name", name);
        object.addProperty("duration", duration.toString());
        object.addProperty("mime", mime);

        String json = gson.toJson(object);
        byte[] bytes = cm.encrypt(json.getBytes(StandardCharsets.UTF_8));

        int metaSize = bytes.length + preview.length + 4;

        this.signature = FileUtils.PORN_VIEWER_SIGN;
        this.metadataSize = metaSize;
        this.jsonSize = bytes.length;
        this.originalJson = json;
        this.originalName = name;
        this.mimeType = mime;
        this.totalDuration = duration;
        this.previewSize = preview.length;
        this.preview = preview;
    }

    public VideoMetadata() {
    }

    public long getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(long videoSize) {
        this.videoSize = videoSize;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public Duration getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(Duration totalDuration) {
        this.totalDuration = totalDuration;
    }

    public byte[] getPreview() {
        return preview;
    }

    public void setPreview(byte[] preview) {
        this.preview = preview;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getMetadataSize() {
        return metadataSize;
    }
    public int getTotalMetaSize() {
        return getMetadataSize() + 40;
    }

    public void setMetadataSize(int metadataSize) {
        this.metadataSize = metadataSize;
    }

    public int getJsonSize() {
        return jsonSize;
    }

    public void setJsonSize(int jsonSize) {
        this.jsonSize = jsonSize;
    }

    public String getOriginalJson() {
        return originalJson;
    }

    public void setOriginalJson(String originalJson) {
        this.originalJson = originalJson;
    }

    public int getPreviewSize() {
        return previewSize;
    }

    public void setPreviewSize(int previewSize) {
        this.previewSize = previewSize;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Image getJavaFxPreview() {
        return new Image(new ByteArrayInputStream(preview));
    }
    public BufferedImage getJavaPreview() {
        try {
            return ImageIO.read(new ByteArrayInputStream(preview));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Timecode {
        private Duration time;
        private String text;

        public Timecode(Duration time, String text) {
            this.time = time;
            this.text = text;
        }

        public Duration getTime() {
            return time;
        }

        public void setTime(Duration time) {
            this.time = time;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    @Override
    public String toString() {
        return "VideoMetadata {" +
                "\nsignature='" + signature + '\'' +
                ",\nvideoSize=" + videoSize +
                ",\nmetadataSize=" + metadataSize +
                ",\njsonSize=" + jsonSize +
                ",\noriginalJson='" + originalJson + '\'' +
                ",\noriginalName='" + originalName + '\'' +
                ",\nmimeType='" + mimeType + '\'' +
                ",\ntotalDuration=" + totalDuration +
                ",\npreviewSize=" + previewSize +
                "\n}";

    }
}