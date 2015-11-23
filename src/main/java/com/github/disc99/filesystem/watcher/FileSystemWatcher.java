package com.github.disc99.filesystem.watcher;

import lombok.Value;
import rx.Observable;
import rx.Subscriber;

import java.nio.file.*;


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

                while (true) {
                    WatchKey key = watcher.take();
                    Observable.from(key.pollEvents()).forEach(event -> subscriber.onNext(new FileSystemEvent(event)));
                    key.reset();
                }

            } catch (Throwable e) {
                subscriber.onError(e);
            } finally {
                subscriber.onCompleted();
            }

        }

    }
}
