package com.plovdev.pornviewer.events.listeners;

import com.plovdev.pornviewer.events.FileEvent;
import com.plovdev.pornviewer.utility.constants.EntryEventTypes;

import java.nio.file.*;
import java.util.ArrayList;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileListener {
    private final String rootPath;
    private final ArrayList<FileEvent> events = new ArrayList<>();

    public FileListener(String root) {
        rootPath = root;
        notifyListeners();
    }

    public void addListener(FileEvent e) {
        events.add(e);
    }

    public void removeListener(FileEvent e) {
        events.remove(e);
    }

    private void notifyListeners() {
        new Thread(() -> {
            while (true) {
                try (WatchService service = FileSystems.getDefault().newWatchService()) {
                    Paths.get(rootPath).register(service, ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE);
                    WatchKey key = service.take();
                    for (WatchEvent<?> e : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = e.kind();
                        Path changed = (Path) e.context();
                        for (FileEvent fileEvent : events) {
                            System.out.println(kind.name());
                            fileEvent.update(changed.toString(), EntryEventTypes.valueOf(kind.name()));
                        }
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }
}