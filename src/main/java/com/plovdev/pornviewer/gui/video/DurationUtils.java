package com.plovdev.pornviewer.gui.video;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        BigDecimal mills = new BigDecimal(String.valueOf(total.toMillis()));

        BigDecimal totalSeconds = mills.divide(new BigDecimal("1000.0"), 10, RoundingMode.HALF_UP);

        int hours = totalSeconds.intValue() / (60 * 60);
        String h = "";
        if (hours != 0) h = hours + ":";

        BigDecimal minutes = totalSeconds.divide(new BigDecimal("60.0"), 10, RoundingMode.HALF_UP);
        BigDecimal seconds = totalSeconds.remainder(new BigDecimal("60.0"));

        long sec = Math.round(seconds.doubleValue());
        long min = Math.round(minutes.doubleValue());

        return h + String.format("%2s:%2s", min, sec);
    }
}