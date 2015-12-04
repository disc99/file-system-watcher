package com.github.disc99.filesystem.watcher

import rx.observers.TestSubscriber
import rx.schedulers.Schedulers
import spock.lang.Specification

import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent
import java.util.concurrent.CountDownLatch

import static java.util.concurrent.TimeUnit.SECONDS

class FileSystemWatcherSpec extends Specification {

    String testDirPath = "/tmp/testDirPath-" + UUID.randomUUID().toString()
    File dir

    def setup() {
        dir = new File(testDirPath);
        dir.mkdirs();
    }

    def cleanup() {
        dir.delete();
    }

    def "ファイルパスと対象のイベントを渡すと、イベント発生時にイベント内容が返る"() {

        setup: "パスとイベント渡し、監視を開始していること"
        TestSubscriber subscriber = new TestSubscriber();
        File f1 = new File("$dir/testFile1")
        File f2 = new File("$dir/testFile2")
        WatchEvent.Kind<?>[] events = [
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE
        ]
        CountDownLatch c1 = new CountDownLatch(1)
        CountDownLatch m1 = new CountDownLatch(2)
        CountDownLatch c2 = new CountDownLatch(3)
        CountDownLatch m2 = new CountDownLatch(4)
        CountDownLatch m3 = new CountDownLatch(5)
        CountDownLatch m4 = new CountDownLatch(6)
        CountDownLatch d1 = new CountDownLatch(7)
        CountDownLatch m5 = new CountDownLatch(8)

        FileSystemWatcher.watch(Paths.get(testDirPath), events, Schedulers.io())
                .doOnNext({c1.countDown()})
                .doOnNext({c2.countDown()})
                .doOnNext({m1.countDown()})
                .doOnNext({m2.countDown()})
                .doOnNext({m3.countDown()})
                .doOnNext({m4.countDown()})
                .doOnNext({m5.countDown()})
                .doOnNext({d1.countDown()})
                .subscribe(subscriber)
        sleep(1000)
        List<FileSystemEvent> newEvents = []
        List<FileSystemEvent> lastEvents = []

        when: "新規にファイル1を作成したとき"
        f1.text = "c1"
        waitSubscriber(c1)
        newEvents = subscriber.getOnNextEvents().collect() - lastEvents
        lastEvents = subscriber.getOnNextEvents().collect()

        then: "ファイル1の新規作成イベントが1件発生する"
        newEvents.collect({it.kind().toString()}).contains("ENTRY_CREATE")


        when: "ファイル1を更新したとき"
        f1.text = "m1"
        waitSubscriber(m1)
        newEvents = subscriber.getOnNextEvents().collect() - lastEvents
        lastEvents = subscriber.getOnNextEvents().collect()

        then: "ファイル1の更新イベントが1件発生する"
        newEvents.collect({it.kind().toString()}).contains("ENTRY_MODIFY")


        when: "新規にファイル2を作成したとき"
        f2.text = "c2"
        waitSubscriber(c2)
        newEvents = subscriber.getOnNextEvents().collect() - lastEvents
        lastEvents = subscriber.getOnNextEvents().collect()

        then: "ファイル2の新規作成イベントが1件発生する"
        newEvents.collect({it.kind().toString()}).contains("ENTRY_CREATE")


        when: "ファイル1を更新したとき"
        f1.text = "m2"
        waitSubscriber(m2)
        newEvents = subscriber.getOnNextEvents().collect() - lastEvents
        lastEvents = subscriber.getOnNextEvents().collect()

        then: "ファイル1の更新イベントが1件発生する"
        newEvents.collect({it.kind().toString()}).contains("ENTRY_MODIFY")


        when: "ファイル1を連続で更新したとき"
        f1.text = "m3"
        waitSubscriber(m3)
        newEvents = subscriber.getOnNextEvents().collect() - lastEvents
        lastEvents = subscriber.getOnNextEvents().collect()

        then: "ファイル1の更新イベントが1件発生する"
        newEvents.collect({it.kind().toString()}).contains("ENTRY_MODIFY")


        when: "ファイル2を更新したとき"
        f2.text = "m4"
        waitSubscriber(m4)
        newEvents = subscriber.getOnNextEvents().collect() - lastEvents
        lastEvents = subscriber.getOnNextEvents().collect()

        then: "ファイル2の更新イベントが1件発生する"
        newEvents.collect({it.kind().toString()}).contains("ENTRY_MODIFY")


        when: "ファイル1を削除したとき"
        f1.delete()
        waitSubscriber(d1)
        newEvents = subscriber.getOnNextEvents().collect() - lastEvents
        lastEvents = subscriber.getOnNextEvents().collect()

        then: "ファイル1の削除イベントが1件発生する"
        newEvents.collect({it.kind().toString()}).contains("ENTRY_DELETE")

        
        when: "ファイル2を更新したとき"
        f2.text = "m5"
        waitSubscriber(m5)
        newEvents = subscriber.getOnNextEvents().collect() - lastEvents

        then: "ファイル2の更新イベントが1件発生する"
        newEvents.collect({it.kind().toString()}).contains("ENTRY_MODIFY")
    }

    def waitSubscriber(CountDownLatch latch) {
        latch.await(10, SECONDS)
        sleep(1000)
    }
}
