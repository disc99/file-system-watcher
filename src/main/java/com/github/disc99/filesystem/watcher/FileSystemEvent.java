package com.github.disc99.filesystem.watcher;


import lombok.Value;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

@Value
public class FileSystemEvent {
    WatchEvent<?> watchEvent;

    public Path file() {
        return (Path) watchEvent.context();
    }
}
