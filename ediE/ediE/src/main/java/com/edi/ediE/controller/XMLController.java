package com.edi.ediE.controller;

import com.edi.ediE.service.XMLParserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/xml")
public class XMLController {

    private final XMLParserService xmlParserService;

    public XMLController(XMLParserService xmlParserService) {
        this.xmlParserService = xmlParserService;
    }

    @PostMapping("/parse")
    public String parseXML() {
        String filePath = "C:\\test";
        xmlParserService.parseAndSaveXML(filePath);
        return "XML file processed successfully!";
    }
}
