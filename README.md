# file-system-watcher
[![Build Status](https://travis-ci.org/disc99/file-system-watcher.svg?branch=master)](https://travis-ci.org/disc99/file-system-watcher)

## Overview
This library is a minimal file system watcher which use WatchService and RxJava.

## Description
[ReactiveX/RxJava](https://github.com/ReactiveX/RxJava)

## Requirement
JDK 8 +

## Usage
```java

Path dir = "/path/to/dir";
WatchEvent.Kind<?>[] events = {StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY};
Scheduler scheduler = Schedulers.io();

FileSystemWatcher.watch(dir, events, scheduler)
        // .filter(...)
        // .map(...)
        // ...
        .subscribe(subscriber);
```

## Licence

(The MIT License)

Copyright (c) 2015 @disc99

## Author

[disc99](https://github.com/disc99)
