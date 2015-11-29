package com.github.disc99.filesystem.watcher;

import lombok.Value;
import rx.Observable;
import rx.Subscriber;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

public class FileSystemWatcher {

    public static Observable<FileSystemEvent> watch(String dir, WatchEvent.Kind<?>[] events) {
        return Observable.create(new FileSystemEventOnSubscribe(dir, events));
    }

    @Value
    static class FileSystemEventOnSubscribe implements Observable.OnSubscribe<FileSystemEvent> {
        String dir;
        WatchEvent.Kind<?>[] events;

        @Override
        public void call(Subscriber<? super FileSystemEvent> subscriber) {
            FileSystem fileSystem = FileSystems.getDefault();
            try (WatchService watcher = fileSystem.newWatchService()) {

                Path path = fileSystem.getPath(dir);
                path.register(watcher, events);

                System.out.printf("[REGISTER] Dir:%s Event:%s\n", dir, Arrays.toString(events));

                while (true) {
                    System.out.printf("[EVENT WAITING]\n");
                    WatchKey key = watcher.take();
                    List<WatchEvent<?>> es = key.pollEvents();
                    System.out.printf("[EVENT OCCURS] Event count:%s\n", es.size());
                    Observable.from(es).forEach(event -> {
                        File file = path.resolve(dir + "/" + event.context()).toFile();
                        System.out.printf("[EVENT DETAIL] Time:%d EventKind:%s File:%s\n", file.lastModified(), event.kind(), file.getName());
                        subscriber.onNext(new FileSystemEvent(event));
                    });
                    key.reset();
                }

            } catch (Throwable e) {
                subscriber.onError(e);
            }
            subscriber.onCompleted();

        }

    }

}
