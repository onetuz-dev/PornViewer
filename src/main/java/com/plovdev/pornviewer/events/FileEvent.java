package com.plovdev.pornviewer.events;

import com.plovdev.pornviewer.utility.constants.EntryEventTypes;

public interface FileEvent {
    void update(String path, EntryEventTypes type);
}