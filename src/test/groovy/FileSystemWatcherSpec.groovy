import com.github.disc99.filesystem.watcher.FileSystemWatcher
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers
import spock.lang.Specification

import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent

class FileSystemWatcherSpec extends Specification {

    String testDirPath = "/tmp/testDirPath-" + UUID.randomUUID().toString()
    File dir

    def setup() {
        dir = new File(testDirPath);
        dir.mkdirs();
    }

    def cleanup() {
        File[] c = dir.listFiles();
        System.out.println("Cleaning out folder:" + dir.toString());
        for (File file : c){
            if (file.isDirectory()){
                System.out.println("Deleting file:" + file.toString());
                file.delete();
            } else {
                file.delete();
            }
        }

        dir.delete();
    }

    def "ファイルパスと対象のイベントを渡すと、イベント発生時にイベント内容が返る"() {
//        Map<Path, FileSystemEventKind[]> paths = new HashMap<>();
//
//        paths.put(dir.toPath(), new FileSystemEventKind[] {
//            FileSystemEventKind.ENTRY_CREATE,
//            FileSystemEventKind.ENTRY_MODIFY } );

        setup:
        TestSubscriber subscriber = new TestSubscriber();

//        Observable<FileSystemEvent> fileSystemWatcher =
//                FileSystemWatcher
//                        .newBuilder()
//                        .addPaths(paths)
//                        .withScheduler(Schedulers.io())
//                        .build();
        WatchEvent.Kind<?>[] events = [StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY]


        when:
        FileSystemWatcher.watch(dir.path.toString(), events)
                .subscribeOn(Schedulers.io())
                .subscribe(subscriber)

        def now = System.currentTimeMillis()
        new File("$dir/testFile$now").text = "test"
        Thread.sleep(10_000)

        then:
        subscriber.assertNoErrors()
        subscriber.assertValueCount(1)

        when:
        new File("$dir/testFile$now").text = "test"
        Thread.sleep(10_000)

        then:
        subscriber.assertNoErrors()
        subscriber.assertValueCount(2)


//        CountDownLatch latch = new CountDownLatch(2);
//        Subscription subscribe = fileSystemWatcher
//                .doOnNext(a -> {
//            latch.countDown();
//            FileSystemEventKind kind = a.getFileSystemEventKind();
//
//            System.out.println("Got an event for " + kind.name());
//
//        })
//                .subscribe(subscriber);
//
//        boolean closed = subscribe.isUnsubscribed();
//
//        Assert.assertFalse(closed);
//
//        File file = new File(dir, "testFile" + System.currentTimeMillis());
//        file.createNewFile();
//
//        Thread.sleep(3000);
//
//        FileWriter writer = new FileWriter(file, true);
//        writer.write(1);
//        writer.flush();
//        writer.close();
//
//        latch.await();
//
//        subscribe.unsubscribe();
//
//        closed = subscribe.isUnsubscribed();
//
//        Assert.assertTrue(closed);
//
//        if (FileSystemWatcher.IS_MAC) {
//            subscriber.assertValueCount(3);
//        } else {
//            subscriber.assertValueCount(2);
//        }

//        setup:
//        String dir = "/Users/daisuke/work/filewatch"
//        WatchEvent.Kind<?>[] events = [
//                StandardWatchEventKinds.ENTRY_CREATE,
//                StandardWatchEventKinds.ENTRY_MODIFY,
//                StandardWatchEventKinds.ENTRY_DELETE
//        ]
//
//
//        expect:
//        FileSystemWatcher.watch(dir, events)
//                .subscribeOn(Schedulers.io())
//                .subscribe(new Subscriber() {
//            @Override
//            void onCompleted() {
//                println "onCompleted"
//            }
//
//            @Override
//            void onError(Throwable e) {
//                println "send mail"
//                println "onError: $e"
//
//            }
//
//            @Override
//            void onNext(Object o) {
//                println "call sync logic"
//                println "onNext: $o"
//            }
//        });
//
//        Thread.sleep(100_000);


    }
}
