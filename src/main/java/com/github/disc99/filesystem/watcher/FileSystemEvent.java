package com.github.disc99.filesystem.watcher;


import lombok.NonNull;
import lombok.Value;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

@Value
class FileSystemEvent {
    @NonNull
    WatchEvent<Path> watchEvent;
    @NonNull
    Path dir;

    public File file() {
        return dir.resolve(watchEvent.context()).toFile();
    }

    public long time() {
        return file().lastModified();
    }

    public WatchEvent.Kind<?> kind() {
        return watchEvent.kind();
    }

    boolean sameTimeEvent(FileSystemEvent event) {
        boolean sameTime = time() == event.time();
        boolean sameFile = file().equals(event.file());
        return sameTime && sameFile;
    }
}
