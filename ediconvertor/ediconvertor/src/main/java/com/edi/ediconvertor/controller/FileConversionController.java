package com.edi.ediconvertor.controller;

import com.edi.ediconvertor.service.FileConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/file-conversion")  // ✅ Ensure correct base path
public class FileConversionController {

    private final FileConversionService fileConversionService;

    @Autowired
    public FileConversionController(FileConversionService fileConversionService) {
        this.fileConversionService = fileConversionService;
    }

    @PostMapping("/convert")
    public ResponseEntity<?> convertFiles(@RequestParam String directoryPath) {
        try {
            System.out.println("Received request to process directory: " + directoryPath);
            fileConversionService.conversion(directoryPath);
            fileConversionService.conversion2(directoryPath);
            return ResponseEntity.ok("Files are being processed successfully.");
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error processing files: " + e.getMessage());
        }
    }

    // ✅ Add a test endpoint to check if the controller is accessible
    @GetMapping("/test")
    public ResponseEntity<String> testService() {
        return ResponseEntity.ok("Service is running!");
    }
}