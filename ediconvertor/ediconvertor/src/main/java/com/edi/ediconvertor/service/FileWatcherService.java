package com.edi.ediconvertor.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class FileWatcherService {
    @Value("${paths.input}")
    private String inputPath;
    private static String staticInputPath;

    public static String getInputPath() {
        return staticInputPath;
    }
    @PostConstruct
    public void init() {
        staticInputPath = inputPath;

    }
    @Autowired
    private emailservice emailService;
    @Autowired
    private FileConversionService fService;

    @Autowired
    private JavaMailSender mailSender;
    ;

    // Map to store file creation timestamps and processed status for files
    private Map<String, Long> fileTimestamps = new HashMap<>();
    private Map<String, Boolean> processedFiles = new HashMap<>();

    public void watchDirectory() {

        Path rootPath = Paths.get(FileWatcherService.staticInputPath);

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            Map<WatchKey, Path> keyDirMap = new HashMap<>();

            // Register all existing directories recursively
            Files.walk(rootPath)
                    .filter(Files::isDirectory)
                    .forEach(dir -> {
                        try {
                            WatchKey key = dir.register(watchService,
                                    StandardWatchEventKinds.ENTRY_CREATE,
                                    StandardWatchEventKinds.ENTRY_MODIFY);
                            keyDirMap.put(key, dir);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            System.out.println("Watching directory (and subdirectories): " + FileWatcherService.staticInputPath);

            Map<String, Long> fileTimestamps = new HashMap<>();

            while (true) {
                WatchKey key = watchService.take();
                Path currentDir = keyDirMap.get(key);

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path relativePath = (Path) event.context();
                    Path fullPath = currentDir.resolve(relativePath);
                    String fileName = fullPath.getFileName().toString();

                    // Skip temp files
                    if (fileName.endsWith(".tmp") || fileName.startsWith("~")) continue;

                    try {
                        // If a new directory is created, register it
                        if (Files.isDirectory(fullPath) && kind == StandardWatchEventKinds.ENTRY_CREATE) {
                            WatchKey newKey = fullPath.register(watchService,
                                    StandardWatchEventKinds.ENTRY_CREATE,
                                    StandardWatchEventKinds.ENTRY_MODIFY);
                            keyDirMap.put(newKey, fullPath);
                            System.out.println("Now watching new directory: " + fullPath);
                            continue;
                        }

                        // ✅ Skip if it's not a regular file
                        if (!Files.isRegularFile(fullPath)) continue;

                        long lastModified = Files.getLastModifiedTime(fullPath).toMillis();
                        long lastProcessed = fileTimestamps.getOrDefault(fullPath.toString(), 0L);

                        if (lastModified > lastProcessed && isFileStable(fullPath)) {
                            System.out.println("Processing file: " + fullPath);
                            processFile(fullPath, fileName);
                            fileTimestamps.put(fullPath.toString(), lastModified);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    keyDirMap.remove(key);
                    if (keyDirMap.isEmpty()) break; // all directories are inaccessible
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void processFile(Path newFile, String fileName) {


        // Decrypt the file and retrieve data
        Map<String, Object> data = FileConversionService.decryptFile(newFile.getParent().toString(), fileName);

        // Generate the HTML table
        String htmlTable = generateHtmlTable(data, fileName);

        // Generate XML files from the decrypted data
        List<File> Files = FileConversionService.convertFileToXML(data);

        try {
            File f = FileConversionService.converttoxlsx(data);
            Files.add(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Send the email with the generated table and XML files as attachments
        emailService.sendEmailWithTableAndAttachments(mailSender,
                "azizlouati69@gmail.com",
                "New EDI file received ",
                htmlTable,
                Files);

        // Optionally, convert the decrypted data to XLSX

    }
    private boolean isFileStable(Path filePath) {
        try {
            long initialSize = Files.size(filePath);
            Thread.sleep(1000); // wait a bit before rechecking
            long newSize = Files.size(filePath);

            return initialSize == newSize;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Generate an HTML table from the decrypted data.
     *
     * @param data The decrypted data map.
     * @return The HTML table as a String.
     */



    private String generateHtmlTable(Map<String, Object> data, String filename) {
        StringBuilder tableBuilder = new StringBuilder();

        // Header section
        tableBuilder.append("<html><body>");
        tableBuilder.append("<div style='font-size: 20px; font-weight: bold; margin-bottom: 7px; color: grey; background-color: #f3effd;'>EDI File Name :</div>");
        tableBuilder.append("<div style='font-size: 19px; margin-bottom: 20px; font-weight: bold; color: black;'>")
                .append(filename)
                .append("</div>");

        // Overview section
        tableBuilder.append("<div style='font-size: 20px; font-weight: bold; margin-bottom: 5px; color: grey; background-color: #f3effd;'>Overview :</div>");

        // Table header
        tableBuilder.append("<table border='1' style='border-collapse: collapse; width: 100%; border: 1px solid black; color: black;'>");
        tableBuilder.append("<tr>")

                .append("<th style='padding: 10px; border: 1px solid black; color: black;'>Code</th>")
                .append("<th style='padding: 10px; border: 1px solid black; color: black;'>Sender</th>")
                .append("<th style='padding: 10px; border: 1px solid black; color: black;'>Article</th>")
                .append("<th style='padding: 10px; border: 1px solid black; color: black;'>Latest Received Quantity</th>")
                .append("<th style='padding: 10px; border: 1px solid black; color: black;'>Receiving Date</th>")
                .append("<th style='padding: 10px; border: 1px solid black; color: black;'>Cumulative Received Quantity</th>")
                .append("</tr>");

        // Rows for each item
        data.forEach((key, value) -> {
            if (value instanceof Map) {
                Map<String, Object> articleData = (Map<String, Object>) value;

                String buyerArticleNumber = (String) articleData.get("doc_id");
                String buyer = (String) articleData.get("buyer");
                String articleDescription = Objects.toString(articleData.get("article_description"), "-");


                String latestReceivedQuantity = "-";
                String receivingDate = "-";
                String cumulativeQuantity = "";

                // Extract doc_numbers_list info if exists
                List<Map<String, String>> docNumbersList = (List<Map<String, String>>) articleData.get("doc_numbers_list");
                if (docNumbersList != null && !docNumbersList.isEmpty()) {
                    Map<String, String> docData = docNumbersList.get(0);
                    latestReceivedQuantity = docData.getOrDefault("pce", "");
                    receivingDate =  FileConversionService.toDate(docData.getOrDefault("doc_date", "")).toString();
                }

                // Extract cumulative quantity
                cumulativeQuantity = (String) articleData.getOrDefault("pce_doc_cumul", "");

                tableBuilder.append("<tr>")
                        .append("<td style='padding: 8px; border: 1px solid black; color: black; background-color: white;'>").append(buyerArticleNumber).append("</td>")
                        .append("<td style='padding: 8px; border: 1px solid black; color: black; background-color: white;'>").append(buyer).append("</td>")
                        .append("<td style='padding: 8px; border: 1px solid black; color: black; background-color: white;'>").append(articleDescription).append("</td>")
                        .append("<td style='padding: 8px; border: 1px solid black; color: black; background-color: white;'>").append(latestReceivedQuantity).append("</td>")
                        .append("<td style='padding: 8px; border: 1px solid black; color: black; background-color: white;'>").append(receivingDate).append("</td>")
                        .append("<td style='padding: 8px; border: 1px solid black; color: black; background-color: white;'>").append(cumulativeQuantity).append("</td>")
                        .append("</tr>");
            }
        });

        tableBuilder.append("</table>");
        tableBuilder.append("</body></html>");

        return tableBuilder.toString();
    }
}
