package com.plovdev.pornviewer.utility.constants;

public enum EntryEventTypes {
    ENTRY_CREATE("ENTRY_CREATE"),
    ENTRY_MODIFY("ENTRY_MODIFY"),
    ENTRY_DELETE("ENTRY_DELETE");

    private final String type;
    EntryEventTypes(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}