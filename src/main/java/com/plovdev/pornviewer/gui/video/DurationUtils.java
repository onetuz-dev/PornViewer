package com.plovdev.pornviewer.gui.video;

import java.time.Duration;

public class DurationUtils {
    public static String formatDurationToString(Duration duration) {
        long h = duration.toHours();
        long m = duration.toMinutesPart();
        long s = duration.toSecondsPart();
        return (h > 0) ? String.format("%02d:%02d:%02d", h, m, s) : String.format("%02d:%02d", m, s);
    }

    public static Duration ofJavaFxDuraion(javafx.util.Duration duration) {
        return Duration.ofMillis((long) duration.toMillis());
    }
    public static javafx.util.Duration ofJavaDuration(Duration duration) {
        return javafx.util.Duration.millis(duration.toMillis());
    }

    public static String getVideoDuration(Duration total) {
        long totalSeconds = total.toSeconds();

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
}