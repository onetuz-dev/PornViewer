package com.plovdev.pronviewer.events;

import com.plovdev.pronviewer.utility.constants.EntryEventTypes;

public interface FileEvent {
    void update(String path, EntryEventTypes type);
}