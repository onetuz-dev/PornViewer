package com.plovdev.pornviewer.server;

public class Chunk {
    private long start;
    private long end;

    public Chunk(long start, long end) {
        this.start = start;
        this.end = end;
    }

    public Chunk() {
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long length() {
        return end - start + 1;
    }

    @Override
    public String toString() {
        return String.format("Bytes: [%s-%s](%sb)", start, end, length());
    }
}