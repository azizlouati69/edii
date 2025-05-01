package com.edi.ediE.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import org.springframework.beans.factory.annotation.Value;

@Service
public class FolderWatcherService {

    private final XMLParserService xmlParserService;
    @Value("${folder.watched}")
    private  String WATCHED_FOLDER ;
    private final WatchService watchService;
    private final Map<WatchKey, Path> keyPathMap = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<Path, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public FolderWatcherService(XMLParserService xmlParserService) throws IOException {
        this.xmlParserService = xmlParserService;
        this.watchService = FileSystems.getDefault().newWatchService();
    }

    @PostConstruct
    public void startWatching() {
        try {
            Path startPath = Paths.get(WATCHED_FOLDER);

            if (!Files.exists(startPath)) {
                System.err.println("⚠️ Folder does not exist: " + WATCHED_FOLDER);
                return;
            }

            System.out.println("✅ Watching folder and subfolders: " + WATCHED_FOLDER);

            registerAllSubfolders(startPath);
            startWatchLoop();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerAllSubfolders(Path startPath) throws IOException {
        Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                registerFolder(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void registerFolder(Path folder) throws IOException {
        WatchKey key = folder.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY);
        keyPathMap.put(key, folder);
        System.out.println("📂 Watching: " + folder);
    }

    private void startWatchLoop() {
        new Thread(() -> {
            while (true) {
                try {
                    WatchKey key = watchService.take();
                    Path folder = keyPathMap.get(key);

                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path filePath = folder.resolve((Path) event.context());

                        if (Files.isDirectory(filePath)) {
                            registerAllSubfolders(filePath);
                        } else {
                            debounceProcessing(filePath);
                        }
                    }
                    key.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void debounceProcessing(Path filePath) {
        // Ignore temporary lock files or hidden files
        String fileName = filePath.getFileName().toString();
        if (fileName.startsWith("~$") || fileName.startsWith(".~lock")) {
            System.out.println("🚫 Ignoring temporary file: " + filePath);
            return;
        }

        scheduledTasks.compute(filePath, (path, existingTask) -> {
            if (existingTask != null) {
                existingTask.cancel(false);
            }
            return scheduler.schedule(() -> processFile(filePath), 500, TimeUnit.MILLISECONDS);
        });
    }


    private void processFile(Path filePath) {
        try {
            if (!Files.exists(filePath)) return;
            System.out.println("✅ Processing file: " + filePath);
            xmlParserService.parseAndSaveXML(filePath.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
