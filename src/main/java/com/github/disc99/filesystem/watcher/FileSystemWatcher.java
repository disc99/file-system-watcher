package com.github.disc99.filesystem.watcher;

import lombok.Value;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;

import java.nio.file.*;
import java.util.List;

public class FileSystemWatcher {

    public static Observable<FileSystemEvent> watch(Path dir, WatchEvent.Kind<?>[] events, Scheduler scheduler) {
        return Observable.create(new FileSystemEventOnSubscribe(dir, events, scheduler));
    }

    @Value
    static class FileSystemEventOnSubscribe implements Observable.OnSubscribe<FileSystemEvent> {

        Path dir;
        WatchEvent.Kind<?>[] events;
        Scheduler scheduler;

        @SuppressWarnings("unchecked")
        @Override
        public void call(Subscriber<? super FileSystemEvent> s) {
            Scheduler.Worker worker = scheduler.createWorker();
            s.add(worker);
            worker.schedule(() -> {
                FileSystem fileSystem = FileSystems.getDefault();
                try (WatchService watcher = fileSystem.newWatchService()) {

                    dir.register(watcher, events);

                    while (true) {
                        WatchKey key = watcher.take();
                        List<WatchEvent<?>> es = key.pollEvents();
                        Observable.from(es).forEach(event -> s.onNext(new FileSystemEvent((WatchEvent<Path>) event, dir)));
                        key.reset();
                    }

                } catch (Throwable e) {
                    s.onError(e);
                }
                s.onCompleted();
            });

        }

    }

}
