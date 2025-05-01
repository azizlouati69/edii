package com.edi.ediconvertor.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.*;
import javax.xml.transform.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.annotations.LazyCollection;

import org.springframework.stereotype.Service;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
@Component
@Service
public class FileConversionService {



    @Value("${paths.output}")
    private String outputPath;
    private static String staticOutputPath;
    @PostConstruct
    public void init() {
        staticOutputPath = outputPath;

    }



    public static String getOutputPath() {
        return staticOutputPath;
    }


        public static ArrayList<Map<String, String>> remove_dup(ArrayList<Map<String, String>> l) {
            ArrayList<Map<String, String>> l1 = new ArrayList<>();

            for (Map<String, String> i : l) {

                if (l1.stream().map(String::valueOf).noneMatch(j -> String.valueOf(i).equals(String.valueOf(j)))) {
                    l1.add(i);
                }
            }

            return l1;
        }
        public static String toDate(String x) {
            if (x == null || x.trim().isEmpty()) {
                return null;
            }

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDate date = LocalDate.parse(x, inputFormatter);
            return date.format(outputFormatter);
        }

        public static  String  sanitizeFileName(String name) {

            return name.replaceAll("[\\\\/:*?\"<>|]", "_");
        }
    public static Map<String, Object> decryptFile(String di, String filename) {
        Map<String, Object> k = new LinkedHashMap<>();
        try {

            File file = new File(di + "/" + filename);
            BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            String txt = content.toString();
            if (txt.contains("UNOA:1"))
                if(txt.contains("UNA:+,?")){

                    String sender_id = txt.substring(txt.indexOf("UNOA:1+") + "UNOA:1+".length(),
                            txt.indexOf(":", txt.indexOf("UNOA:1+") + "UNOA:1+".length()));
                    String receiver_id = txt.substring(txt.indexOf(sender_id) + sender_id.length() + 4,
                            txt.indexOf("+", txt.indexOf(sender_id) + sender_id.length() + 4));
                    String ref_file = txt.substring(txt.indexOf("UNZ+") + "UNZ+".length(),
                            txt.indexOf("'", txt.indexOf("UNZ+") + "UNZ+".length()));
                    String id_file = null;
                    int startndex = txt.indexOf("UNOA:1+");

                    if (startndex != -1) {
                        startndex += "UNOA:1+".length();


                        int firstColon = txt.indexOf(":", startndex);

                        if (firstColon != -1) {

                            int secondColon = txt.indexOf(":", firstColon + 1);

                            if (secondColon != -1) {

                                int plusIndex = txt.indexOf("+", secondColon);

                                if (plusIndex != -1) {

                                    id_file = txt.substring(secondColon + 1, plusIndex);
                                } else {

                                    id_file = txt.substring(secondColon + 1);
                                }
                            }
                        }
                    }
                    String dateDocument = txt.substring(txt.indexOf(":", txt.indexOf("BGM")) + 1, txt.indexOf(":", txt.indexOf(":", txt.indexOf("BGM")) + 1));

                    dateDocument = dateDocument.substring(0, 4) + "-" + dateDocument.substring(4, 6) + "-" + dateDocument.substring(6);
                    int buyerStartIndex = txt.indexOf("NAD+BY+") + "NAD+BY+".length();
                    int buyerEndIndex = txt.indexOf(":", buyerStartIndex);
                    String buyer = txt.substring(buyerStartIndex, buyerEndIndex);
                    int shiptoStartIndex = txt.indexOf("NAD+CN+") + "NAD+CN+".length();
                    int shiptoEndIndex = txt.indexOf(":", shiptoStartIndex);
                    String shipto = txt.substring(shiptoStartIndex, shiptoEndIndex);
                    k.put("rec_id", receiver_id);
                    k.put("sen_id", sender_id);
                    k.put("ref_file", ref_file);
                    k.put("id_file", id_file);

                    txt = txt.substring(txt.indexOf("LIN+++"));
                    while (txt.contains("LIN+++")) {
                        txt = txt.substring(txt.indexOf("LIN+++"));
                        int nextndex = txt.indexOf("LIN+++", "LIN+++".length()); // Find the next "LIN+++"
                        String txt1;

                        if (nextndex != -1) {
                            txt1 = txt.substring(0, nextndex); // Extract from current "LIN+++" to next "LIN+++"
                            txt = txt.substring(nextndex); // Move to the next "LIN+++"
                        } else {
                            txt1 = txt; // Last section if no more "LIN+++"
                            txt = ""; // Exit loop
                        }
                        int documentIdStart = txt1.indexOf("LIN+++") + "LIN+++".length();
                        int documentIdEnd = txt1.indexOf(":", documentIdStart);
                        String document_id = txt1.substring(documentIdStart, documentIdEnd);
                        String article_description = txt1.substring(txt1.indexOf("IMD+++:::") + "IMD+++:::".length(), txt1.indexOf("'", txt1.indexOf("IMD+++:::")));
                        String loc_id = txt1.substring(txt1.indexOf("LOC+11+") + "LOC+11+".length(), txt1.indexOf("'", txt1.indexOf("LOC+11+")));
                        String document_reference_number_order = txt1.substring(txt1.indexOf("RFF+ON:") + "RFF+ON:".length(), txt1.indexOf("'", txt1.indexOf("RFF+ON:")));
                        String pce_doc_cumul = null;
                        int x = txt1.indexOf("QTY+70:");

                        if (x != -1) {
                            int start = x + "QTY+70:".length();
                            int end = txt1.indexOf(":", start);

                            if (end != -1) {
                                pce_doc_cumul = txt1.substring(start, end);
                            } else {
                                pce_doc_cumul = txt1.substring(start);
                            }
                        }
                        String int_destination = null;
                        int y = txt1.indexOf("LOC+159+");

                        if (y != -1) {
                            int start = y + "LOC+159+".length();
                            int end = txt1.indexOf("'", start);

                            if (end != -1) {
                                int_destination = txt1.substring(start, end);
                            } else {
                                int_destination = txt1.substring(start);
                            }
                        }

                        ArrayList<Map<String, String>> docNumbersList = new ArrayList<>();
                        int b = txt1.indexOf("RFF+AAK");
                        String doc_numbers = null;
                        if (b != -1 && b >= 5) {
                            doc_numbers = txt1.substring(b - 5);

                        } else {
                            System.out.println("'RFF+AAK' not found  ");
                        }
                        while ( doc_numbers!= null
                                && doc_numbers.contains("RFF+AAK")) {
                            int rffIndexx = doc_numbers.indexOf("RFF+AAK");
                            int colonIndexx = doc_numbers.lastIndexOf(":", rffIndexx);

                            String doc_number_pce = doc_numbers.substring(colonIndexx + 1, rffIndexx);
                            doc_number_pce = doc_number_pce.replaceAll("[^\\d]", "");
                            int colonIndex = doc_numbers.indexOf(':', 40);
                            String doc_number_1 = doc_numbers.substring(0, colonIndex);
                            int colonIndex1 = doc_number_1.indexOf(':', 2);
                            int quoteIndex = doc_number_1.indexOf('\'', 8);
                            String docNumber1Id = doc_number_1.substring(colonIndex1 + 1, quoteIndex);

                            int startDate = doc_number_1.indexOf(docNumber1Id) + docNumber1Id.length() + 8;
                            int endDate = startDate + 8;
                            String docNumber1Date = doc_number_1.substring(startDate, endDate);
                            doc_numbers = doc_numbers.substring(doc_number_1.length());

                            int index = doc_numbers.indexOf("RFF+AAK");

                            if (index >= 5) {
                                doc_numbers = doc_numbers.substring(index - 5);
                            }
                            boolean containsAlpha = false;
                            for (char c : docNumber1Id.toCharArray()) {
                                if (Character.isAlphabetic(c)) {

                                    containsAlpha = true;
                                    break;
                                }
                            }
                            if (containsAlpha) {

                                Map<String, String> docData = new HashMap<>();
                                docData.put("pce", doc_number_pce);
                                docData.put("doc_id", docNumber1Id);
                                docData.put("doc_date", docNumber1Date);
                                docNumbersList.add(docData);

                            }

                        }

                        ArrayList<Map<String, String>> firmList = new ArrayList<>();
                        if (txt1.contains("SCC+1++F")) {

                            if (txt1.contains("SCC+4++F")) {
                                String firm1 = txt1.substring(txt1.indexOf("SCC+1++F"));

                                while (firm1.contains("SCC+1++F")) {
                                    int startIndex = firm1.indexOf("SCC+1++F");
                                    int endIndex = firm1.indexOf("SCC+4++F") + 8;
                                    String firm = firm1.substring(startIndex, endIndex);

                                    firm1 = firm1.substring(firm.length());

                                    int nextIndex = firm1.indexOf("SCC+1++F");
                                    if (nextIndex != -1) {
                                        firm1 = firm1.substring(nextIndex);
                                    }

                                    while (firm.contains(":")) {
                                        int colonIndex = firm.indexOf(":");
                                        String x1 = firm.substring(0, colonIndex + 1);
                                        firm = firm.substring(x1.length());
                                        colonIndex = firm.indexOf(":");
                                        String pce = firm.substring(0, colonIndex + 1);
                                        firm = firm.substring(pce.length());
                                        pce = pce.replaceAll("[^0-9]", "");

                                        int colonIndex2 = firm.indexOf(":");
                                        String x3 = firm.substring(0, colonIndex2 + 1);
                                        firm = firm.substring(x3.length());

                                        colonIndex2 = firm.indexOf(":");
                                        String dateafter = firm.substring(0, colonIndex2 + 1);
                                        firm = firm.substring(dateafter.length());
                                        dateafter = dateafter.replaceAll("[^0-9]", "");
                                        int colonIndex3 = firm.indexOf(":");
                                        String x4 = firm.substring(0, colonIndex3 + 1);
                                        firm = firm.substring(x4.length());
                                        colonIndex3 = firm.indexOf(":");
                                        String datebefore = firm.substring(0, colonIndex3 + 1);
                                        firm = firm.substring(datebefore.length());
                                        datebefore = datebefore.replaceAll("[^0-9]", "");
                                        if (!pce.equals("0")) {
                                            Map<String, String> dataFirm = new HashMap<>();
                                            dataFirm.put("pce", pce);
                                            dataFirm.put("databefore", toDate(datebefore));
                                            dataFirm.put("dateafter", toDate(dateafter));

                                            firmList.add(dataFirm);
                                        }

                                    }
                                }

                            }

                        }


                        ArrayList<Map<String, String>> forecastList = new ArrayList<>();
                        if (txt1.contains("SCC+4++F")) {
                            String forecast = txt1.substring(txt1.indexOf("SCC+4++F") + 9);





                            forecast = forecast.trim();
                            while (forecast.contains(":")) {
                                String x1 = forecast.substring(0, forecast.indexOf(":") + 1);
                                forecast = forecast.substring(x1.length());

                                String pce = forecast.substring(0, forecast.indexOf(":") + 1);
                                forecast = forecast.substring(pce.length());

                                pce = pce.replaceAll("[^0-9]", "");

                                String x3 = forecast.substring(0, forecast.indexOf(":") + 1);
                                forecast = forecast.substring(x3.length());

                                String datebefore = forecast.substring(0, forecast.indexOf(":") + 1);
                                forecast = forecast.substring(datebefore.length());

                                datebefore = datebefore.replaceAll("[^0-9]", "");

                                String x4 = forecast.substring(0, forecast.indexOf(":") + 1);
                                forecast = forecast.substring(x4.length());

                                String dateafter = forecast.substring(0, forecast.indexOf(":") + 1);
                                forecast = forecast.substring(dateafter.length());

                                dateafter = dateafter.replaceAll("[^0-9]", "");

                                if (!pce.equals("0")) {
                                    Map<String, String> dataforecast = new HashMap<>();
                                    dataforecast.put("pce", pce);
                                    dataforecast.put("databefore", toDate(dateafter));
                                    dataforecast.put("dateafter", toDate(datebefore));

                                    forecastList.add(dataforecast);
                                }
                            }

                        }



                        String articleRef = txt1.substring(txt1.indexOf("RFF+AAN:") + 8, txt1.indexOf("'", txt1.indexOf("RFF+AAN:") + 8));



                        List<Map<String, String>> firmListDistinct = remove_dup(firmList);
                        List<Map<String, String>> forecastListDistinct = remove_dup(forecastList);

                        firmListDistinct.sort(Comparator.comparing(item -> item.get("dateafter")));
                        forecastListDistinct.sort(Comparator.comparing(item -> item.get("dateafter")));


                        Map<String, Object> l = new HashMap<>();
                        l.put("art_ref", articleRef);
                        l.put("date_doc", dateDocument);
                        l.put("doc_id", document_id);
                        l.put("article_description", article_description);
                        l.put("loc_id", loc_id);
                        l.put("doc_ref_num_order", document_reference_number_order);
                        l.put("pce_doc_cumul", pce_doc_cumul);
                        l.put("internal destination", int_destination);
                        l.put("doc_numbers_list", docNumbersList);
                        l.put("firmlist", firmListDistinct);
                        l.put("forecastlist", forecastListDistinct);
                        l.put("buyer", buyer);
                        l.put("shipto", shipto);

                        k.put((String) l.get("doc_id"), l);


                    }

                }






                else{

                    String sender_id = txt.substring(txt.indexOf("UNOA:1+") + "UNOA:1+".length(),
                            txt.indexOf("+", txt.indexOf("UNOA:1+") + "UNOA:1+".length()));
                    String receiver_id = txt.substring(txt.indexOf(sender_id) + sender_id.length() + 1,
                            txt.indexOf("+", txt.indexOf(sender_id) + sender_id.length() + 1));
                    String ref_file = txt.substring(txt.indexOf("UNZ+") + "UNZ+".length(),
                            txt.indexOf("'", txt.indexOf("UNZ+") + "UNZ+".length()));
                    String id_file = txt.substring(txt.indexOf(":", txt.indexOf("UNOA:1+") + "UNOA:1+".length()) + 1,
                            txt.indexOf("+", txt.indexOf(":", txt.indexOf("UNOA:1+") + "UNOA:1+".length())));

                    k.put("rec_id", receiver_id);
                    k.put("sen_id", sender_id);
                    k.put("ref_file", ref_file);
                    k.put("id_file", id_file);

                    txt = txt.substring(txt.indexOf("UNH+"));
                    while (txt.contains("UNH+")) {
                        txt = txt.substring(txt.indexOf("UNH+"));
                        String txt1 = txt.substring(txt.indexOf("UNH+"), txt.indexOf("UNT+") + "UNT+".length());
                        txt = txt.substring(txt.indexOf("UNT+") + "UNT+".length());
                        int documentIdStart = txt1.indexOf("LIN+++") + "LIN+++".length();
                        int documentIdEnd = txt1.indexOf(":", documentIdStart);
                        String document_id = txt1.substring(documentIdStart, documentIdEnd);
                        String article_description = txt1.substring(txt1.indexOf("IMD+++:::") + "IMD+++:::".length(), txt1.indexOf("'", txt1.indexOf("IMD+++:::")));
                        String loc_id = txt1.substring(txt1.indexOf("LOC+11+") + "LOC+11+".length(), txt1.indexOf("'", txt1.indexOf("LOC+11+")));
                        String document_reference_number_order = txt1.substring(txt1.indexOf("RFF+ON:") + "RFF+ON:".length(), txt1.indexOf("'", txt1.indexOf("RFF+ON:")));
                        String pce_doc_cumul = null;
                        int x = txt1.indexOf("QTY+70:");

                        if (x != -1) {
                            int start = x + "QTY+70:".length();
                            int end = txt1.indexOf(":", start);

                            if (end != -1) {
                                pce_doc_cumul = txt1.substring(start, end);
                            } else {
                                pce_doc_cumul = txt1.substring(start);
                            }
                        }
                        String int_destination = null;
                        int y = txt1.indexOf("LOC+159+");

                        if (y != -1) {
                            int start = y + "LOC+159+".length();
                            int end = txt1.indexOf("'", start);

                            if (end != -1) {
                                int_destination = txt1.substring(start, end);
                            } else {
                                int_destination = txt1.substring(start);
                            }
                        }

                        ArrayList<Map<String, String>> docNumbersList = new ArrayList<>();
                        int b = txt1.indexOf("RFF+AAK");
                        String doc_numbers = null;
                        if (b != -1 && b >= 5) {
                            doc_numbers = txt1.substring(b - 5);

                        } else {
                            System.out.println("'RFF+AAK' not found  ");
                        }
                        while ( doc_numbers!= null
                                && doc_numbers.contains("RFF+AAK")) {
                            int rffIndexx = doc_numbers.indexOf("RFF+AAK");
                            int colonIndexx = doc_numbers.lastIndexOf(":", rffIndexx);

                            String doc_number_pce = doc_numbers.substring(colonIndexx + 1, rffIndexx);
                            doc_number_pce = doc_number_pce.replaceAll("[^\\d]", "");
                            int colonIndex = doc_numbers.indexOf(':', 40);
                            String doc_number_1 = doc_numbers.substring(0, colonIndex);
                            int colonIndex1 = doc_number_1.indexOf(':', 2);
                            int quoteIndex = doc_number_1.indexOf('\'', 8);
                            String docNumber1Id = doc_number_1.substring(colonIndex1 + 1, quoteIndex);

                            int startDate = doc_number_1.indexOf(docNumber1Id) + docNumber1Id.length() + 8;
                            int endDate = startDate + 8;
                            String docNumber1Date = doc_number_1.substring(startDate, endDate);
                            doc_numbers = doc_numbers.substring(doc_number_1.length());

                            int index = doc_numbers.indexOf("RFF+AAK");

                            if (index >= 5) {
                                doc_numbers = doc_numbers.substring(index - 5);
                            }
                            boolean containsAlpha = false;
                            for (char c : docNumber1Id.toCharArray()) {
                                if (Character.isAlphabetic(c)) {

                                    containsAlpha = true;
                                    break;
                                }
                            }
                            if (containsAlpha) {

                                Map<String, String> docData = new HashMap<>();
                                docData.put("pce", doc_number_pce);
                                docData.put("doc_id", docNumber1Id);
                                docData.put("doc_date", docNumber1Date);
                                docNumbersList.add(docData);
                            }

                        }

                        ArrayList<Map<String, String>> firmList = new ArrayList<>();
                        if (txt1.contains("SCC+1++F")) {

                            if (txt1.contains("SCC+4++F")) {
                                String firm1 = txt1.substring(txt1.indexOf("SCC+1++F"));

                                while (firm1.contains("SCC+1++F")) {
                                    int startIndex = firm1.indexOf("SCC+1++F");
                                    int endIndex = firm1.indexOf("SCC+4++F") + 8;
                                    String firm = firm1.substring(startIndex, endIndex);

                                    firm1 = firm1.substring(firm.length());

                                    int nextIndex = firm1.indexOf("SCC+1++F");
                                    if (nextIndex != -1) {
                                        firm1 = firm1.substring(nextIndex);
                                    }

                                    while (firm.contains(":")) {
                                        int colonIndex = firm.indexOf(":");
                                        String x1 = firm.substring(0, colonIndex + 1);
                                        firm = firm.substring(x1.length());
                                        colonIndex = firm.indexOf(":");
                                        String pce = firm.substring(0, colonIndex + 1);
                                        firm = firm.substring(pce.length());
                                        pce = pce.replaceAll("[^0-9]", "");

                                        int colonIndex2 = firm.indexOf(":");
                                        String x3 = firm.substring(0, colonIndex2 + 1);
                                        firm = firm.substring(x3.length());

                                        colonIndex2 = firm.indexOf(":");
                                        String datebefore = firm.substring(0, colonIndex2 + 1);
                                        firm = firm.substring(datebefore.length());
                                        datebefore = datebefore.replaceAll("[^0-9]", "");
                                        int colonIndex3 = firm.indexOf(":");
                                        String x4 = firm.substring(0, colonIndex3 + 1);
                                        firm = firm.substring(x4.length());
                                        colonIndex3 = firm.indexOf(":");
                                        String dateafter = firm.substring(0, colonIndex3 + 1);
                                        firm = firm.substring(dateafter.length());
                                        dateafter = dateafter.replaceAll("[^0-9]", "");
                                        if (!pce.equals("0")) {
                                            Map<String, String> dataFirm = new HashMap<>();
                                            dataFirm.put("pce", pce);
                                            dataFirm.put("databefore", toDate(datebefore));
                                            dataFirm.put("dateafter", toDate(dateafter));

                                            firmList.add(dataFirm);
                                        }

                                    }
                                }

                            }

                        }


                        ArrayList<Map<String, String>> forecastList = new ArrayList<>();
                        if (txt1.contains("SCC+4++F")) {
                            String forecast1 = txt1.substring(txt1.indexOf("SCC+4++F") + 9);

                            while (forecast1.contains("PAC++1")) {
                                String forecast = forecast1.substring(0, forecast1.indexOf("PAC++1"));

                                forecast1 = forecast1.substring(forecast.length());

                                forecast1 = forecast1.substring(forecast1.indexOf("SCC+4++F") + 9);
                                while (forecast.contains(":")) {
                                    String x1 = forecast.substring(0, forecast.indexOf(":") + 1);
                                    forecast = forecast.substring(x1.length());

                                    String pce = forecast.substring(0, forecast.indexOf(":") + 1);
                                    forecast = forecast.substring(pce.length());

                                    pce = pce.replaceAll("[^0-9]", "");
                                    String x3 = forecast.substring(0, forecast.indexOf(":") + 1);
                                    forecast = forecast.substring(x3.length());

                                    String datebefore = forecast.substring(0, forecast.indexOf(":") + 1);
                                    forecast = forecast.substring(datebefore.length());

                                    datebefore = datebefore.replaceAll("[^0-9]", "");

                                    String x4 = forecast.substring(0, forecast.indexOf(":") + 1);
                                    forecast = forecast.substring(x4.length());

                                    String dateafter = forecast.substring(0, forecast.indexOf(":") + 1);
                                    forecast = forecast.substring(dateafter.length());

                                    dateafter = dateafter.replaceAll("[^0-9]", "");

                                    if (!pce.equals("0")) {
                                        Map<String, String> dataforecast = new HashMap<>();
                                        dataforecast.put("pce", pce);
                                        dataforecast.put("databefore", toDate(dateafter));
                                        dataforecast.put("dateafter", toDate(datebefore));

                                        forecastList.add(dataforecast);
                                    }
                                }

                            }

                        }

                        String articleRef = txt1.substring(txt1.indexOf("RFF+AAN:") + 8, txt1.indexOf("'", txt1.indexOf("RFF+AAN:") + 8));

                        String dateDocument = txt1.substring(txt1.indexOf(":", txt1.indexOf("BGM")) + 1, txt1.indexOf(":", txt1.indexOf(":", txt1.indexOf("BGM")) + 1));

                        dateDocument = dateDocument.substring(0, 4) + "-" + dateDocument.substring(4, 6) + "-" + dateDocument.substring(6);

                        List<Map<String, String>> firmListDistinct = remove_dup(firmList);
                        List<Map<String, String>> forecastListDistinct = remove_dup(forecastList);

                        firmListDistinct.sort(Comparator.comparing(item -> item.get("dateafter")));
                        forecastListDistinct.sort(Comparator.comparing(item -> item.get("dateafter")));

                        int buyerStartIndex = txt1.indexOf("NAD+BY+") + "NAD+BY+".length();
                        int buyerEndIndex = txt1.indexOf(":", buyerStartIndex);
                        String buyer = txt1.substring(buyerStartIndex, buyerEndIndex);
                        int shiptoStartIndex = txt1.indexOf("NAD+CN+") + "NAD+CN+".length();

                        int shiptoEndIndex = txt1.indexOf(":", shiptoStartIndex);

                        String shipto = txt1.substring(shiptoStartIndex, shiptoEndIndex);
                        Map<String, Object> l = new HashMap<>();
                        l.put("art_ref", articleRef);
                        l.put("date_doc", dateDocument);
                        l.put("doc_id", document_id);
                        l.put("article_description", article_description);
                        l.put("loc_id", loc_id);
                        l.put("doc_ref_num_order", document_reference_number_order);
                        l.put("pce_doc_cumul", pce_doc_cumul);
                        l.put("internal destination", int_destination);
                        l.put("doc_numbers_list", docNumbersList);
                        l.put("firmlist", firmListDistinct);
                        l.put("forecastlist", forecastListDistinct);
                        l.put("buyer", buyer);
                        l.put("shipto", shipto);

                        k.put((String) l.get("doc_id"), l);


                    }

                } else if ((txt.contains("UNOA:3+") ) ){
                try {
                    File filee = new File(di, filename);

                    try (BufferedReader readerr = new BufferedReader(new InputStreamReader(new FileInputStream(filee), StandardCharsets.ISO_8859_1))) {
                        StringBuilder contentt = new StringBuilder();
                        String linee;

                        while ((linee = readerr.readLine()) != null) {
                            contentt.append(linee).append("\n");
                        }

                        String txtt = contentt.toString().trim();

                        int startIndex = txtt.indexOf("UNOA:");

                        startIndex = txtt.indexOf('+', startIndex + "UNOA:".length()) + 1;
                        int endIndex = txtt.indexOf('+', startIndex);
                        String sender_id = txtt.substring(startIndex, endIndex);
                        int senderIndex = txtt.indexOf(sender_id);
                        String receive_id = txtt.substring(senderIndex + sender_id.length() + 1,
                                txtt.indexOf('+', senderIndex + sender_id.length() + 2));
                        String ref_file = txtt.substring(txtt.indexOf('+', txtt.indexOf("UNZ") + "UNZ".length()) + 1,
                                txtt.indexOf('\'', txtt.indexOf("UNZ") + 1));
                        String id_file = txtt.substring(txtt.indexOf(':', txtt.indexOf("UNOA:") + "UNOA:".length()) + 1,
                                txtt.indexOf('+', txtt.indexOf(':', txtt.indexOf("UNOA:") + "UNOA:".length()) + 1));

                        k.put("rec_id", receive_id);
                        k.put("sen_id", sender_id);
                        k.put("ref_file", ref_file);
                        k.put("id_file", id_file);

                        txtt = txtt.substring(txtt.indexOf("UNH+"));

                        while (txtt.contains("UNH+")) {
                            int startIdx = txtt.indexOf("UNH+");
                            int endIdx = txtt.indexOf("UNT+") +4;
                            String txt1 = txtt.substring(startIdx, endIdx);
                            txtt = txtt.substring(txtt.indexOf("UNT+") + "UNT+".length());


                            String document_id = txt1.substring(txt1.indexOf("LIN+1++") + 7, txt1.indexOf(":", txt1.indexOf("LIN+1++") + 7));
                            String article_description = null;
                            if (txt1.contains("IMD")) {
                                article_description = txt1.substring(txt1.indexOf("IMD+++:::") + "IMD+++:::".length(), txt1.indexOf("'", txt1.indexOf("IMD+++:::")));
                            }
                            String loc_id = txt1.substring(txt1.indexOf("LOC+11+") + "LOC+11+".length(), txt1.indexOf("'", txt1.indexOf("LOC+11+")));
                            String document_reference_number_order = txt1.substring(txt1.indexOf("RFF+ON:") + "RFF+ON:".length(), txt1.indexOf("'", txt1.indexOf("RFF+ON:")));
                            String pce_doc_cumul = txt1.substring(txt1.indexOf("QTY+70:") + "QTY+70:".length(), txt1.indexOf(":", txt1.indexOf("QTY+70:") + "QTY+70:".length()));
                            String int_destination = null;
                            int y = txt1.indexOf("LOC+159+");

                            if (y != -1) {
                                int start = y + "LOC+159+".length();
                                int end = txt1.indexOf("'", start);

                                if (end != -1) {
                                    int_destination = txt1.substring(start, end);
                                } else {
                                    int_destination = txt1.substring(start);
                                }
                            }
                            ArrayList<Map<String, String>> docNumbersList = new ArrayList<>();
                            String docNumbers = txt1.substring(txt1.indexOf("RFF+AAU") - 13);
                            String docNumberPce = docNumbers.substring(docNumbers.indexOf(":") + 1, docNumbers.indexOf(":PCE"));
                            docNumberPce = docNumberPce.replaceAll("[^0-9]", "");
                            String docNumber1 = docNumbers.substring(0, docNumbers.indexOf(":", 40));
                            String docNumber1Id = docNumber1.substring(docNumber1.indexOf("RFF+AAU:") + "RFF+AAU".length() + 1,
                                    docNumber1.indexOf("'", docNumber1.indexOf("RFF+AAU:")));
                            String docNumber1Date = docNumber1.substring(docNumber1.indexOf(docNumber1Id) + docNumber1Id.length() + 9,
                                    docNumber1.indexOf(docNumber1Id) + docNumber1Id.length() + 17);
                            Map<String, String> docData = new HashMap<>();
                            docData.put("pce", docNumberPce);
                            docData.put("doc_id", docNumber1Id);
                            docData.put("doc_date", docNumber1Date);
                            docNumbersList.add(docData);


                            String txt2 = txt1;
                            ArrayList<Map<String, String>> firmList = new ArrayList<>();
                            if (txt1.contains("SCC+1++F")) {

                                if (txt1.contains("SCC+4++F")) {
                                    String firm1 = txt1.substring(txt1.indexOf("SCC+1++F"));

                                    while (firm1.contains("SCC+1++F")) {
                                        int starttIndex = firm1.indexOf("SCC+1++F");
                                        int enddIndex = firm1.indexOf("SCC+4++F") + 8;
                                        String firm = firm1.substring(starttIndex, enddIndex);

                                        firm1 = firm1.substring(firm.length());

                                        int nextIndex = firm1.indexOf("SCC+1++F");
                                        if (nextIndex != -1) {
                                            firm1 = firm1.substring(nextIndex);
                                        }

                                        while (firm.contains(":")) {
                                            int colonIndex = firm.indexOf(":");
                                            String x1 = firm.substring(0, colonIndex + 1);

                                            firm = firm.substring(x1.length());


                                            colonIndex = firm.indexOf(":");
                                            String pce = firm.substring(0, colonIndex + 1);

                                            firm = firm.substring(pce.length());

                                            pce = pce.replaceAll("[^0-9]", "");

                                            int colonIndex2 = firm.indexOf(":");
                                            String x3 = firm.substring(0, colonIndex2 + 1);

                                            firm = firm.substring(x3.length());

                                            colonIndex2 = firm.indexOf(":");
                                            String datebefore = firm.substring(0, colonIndex2 + 1);

                                            firm = firm.substring(datebefore.length());

                                            datebefore = datebefore.replaceAll("[^0-9]", "");
                                            int colonIndex3 = firm.indexOf(":");
                                            String x4 = firm.substring(0, colonIndex3 + 1);
                                            firm = firm.substring(x4.length());
                                            colonIndex3 = firm.indexOf(":");
                                            String dateafter = firm.substring(0, colonIndex3 + 1);
                                            firm = firm.substring(dateafter.length());
                                            dateafter = dateafter.replaceAll("[^0-9]", "");
                                            if (!pce.equals("0")) {
                                                Map<String, String> dataFirm = new HashMap<>();
                                                dataFirm.put("pce", pce);
                                                dataFirm.put("databefore", toDate(datebefore));
                                                dataFirm.put("dateafter", toDate(dateafter));

                                                firmList.add(dataFirm);
                                            }

                                        }
                                    }

                                }

                            }
                            else {
                                System.out.println("there is no firm list.");
                            }

                            txt2 =txt1;
                            ArrayList<Map<String, String>> forecastList = new ArrayList<>();
                            String forecast1 = txt1.substring(txt1.indexOf("SCC+9")+9);
                            while (forecast1.contains("SCC+2")) {
                                String forecast = forecast1.substring(0, forecast1.indexOf("SCC+2"));

                                forecast1 = forecast1.substring(forecast.length());

                                forecast1 = forecast1.substring(forecast1.indexOf("SCC+9") + 9);
                                while (forecast.contains(":")) {
                                    String x1 = forecast.substring(0, forecast.indexOf(":") + 1);
                                    forecast = forecast.substring(x1.length());

                                    String pce = forecast.substring(0, forecast.indexOf(":") + 1);
                                    forecast = forecast.substring(pce.length());

                                    pce = pce.replaceAll("[^0-9]", "");

                                    String x3 = forecast.substring(0, forecast.indexOf(":") + 1);
                                    forecast = forecast.substring(x3.length());

                                    String datebefore = forecast.substring(0, forecast.indexOf(":") + 1);
                                    forecast = forecast.substring(datebefore.length());

                                    datebefore = datebefore.replaceAll("[^0-9]", "");

                                    String dateafter = datebefore;

                                    if (!pce.equals("0")) {
                                        Map<String, String> dataforecast = new HashMap<>();
                                        dataforecast.put("pce", pce);
                                        dataforecast.put("databefore", toDate(dateafter));
                                        dataforecast.put("dateafter", toDate(datebefore));

                                        forecastList.add(dataforecast);
                                    }
                                }

                            }

                            String articleRef = txt1.substring(txt1.indexOf("RFF+AAN:") + 8, txt1.indexOf("'", txt1.indexOf("RFF+AAN:") + 8));

                            String dateDocument = txt2.substring(txt1.indexOf(":", txt1.indexOf("BGM")) + 1, txt1.indexOf(":", txt1.indexOf(":", txt1.indexOf("BGM")) + 1));

                            dateDocument = dateDocument.substring(0, 4) + "-" + dateDocument.substring(4, 6) + "-" + dateDocument.substring(6);


                            List<Map<String, String>> firmListDistinct = remove_dup(firmList);
                            List<Map<String, String>> forecastListDistinct = remove_dup(forecastList);


                            firmListDistinct.sort(Comparator.comparing(item -> item.get("dateafter")));
                            forecastListDistinct.sort(Comparator.comparing(item -> item.get("dateafter")));


                            int buyerStartIndex = txt1.indexOf("NAD+BY+") + "NAD+BY+".length();
                            int buyerEndIndex = txt1.indexOf(":", buyerStartIndex);
                            String buyer = txt1.substring(buyerStartIndex, buyerEndIndex);
                            int shiptoStartIndex = txt1.indexOf("NAD+CN+") + "NAD+CN+".length();
                            int shiptoEndIndex = txt1.indexOf(":", shiptoStartIndex);
                            String shipto = txt1.substring(shiptoStartIndex, shiptoEndIndex);
                            Map<String, Object> l = new HashMap<>();
                            l.put("art_ref", articleRef);
                            l.put("date_doc", dateDocument);
                            l.put("doc_id", document_id);
                            l.put("article_description", article_description);
                            l.put("loc_id", loc_id);
                            l.put("doc_ref_num_order", document_reference_number_order);
                            l.put("pce_doc_cumul", pce_doc_cumul);
                            l.put("internal destination", int_destination);
                            l.put("doc_numbers_list", docNumbersList);
                            l.put("firmlist", firmListDistinct);
                            l.put("forecastlist", forecastListDistinct);
                            l.put("buyer", buyer);
                            l.put("shipto", shipto);
                            k.put((String) l.get("doc_id"), l);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(k);

        return (k);
    }
        public static List<File> convertFileToXML(Map<String, Object> data) {
            List<String> keys = new ArrayList<>(data.keySet());
            List<File> generatedXmlFiles = new ArrayList<>();


            try {
                for (int i = 0; i < keys.size() - 4; i++) {

                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.newDocument();

                    Element root = doc.createElement("Envelope");
                    root.setAttribute("Version", "2011B");
                    root.setAttribute("ControllingInstance", "auto-gration");
                    doc.appendChild(root);

                    Element header = doc.createElement("Header");
                    root.appendChild(header);

                    Element receiver = doc.createElement("ReceiverID");
                    receiver.appendChild(doc.createTextNode((String) data.get("rec_id")));
                    header.appendChild(receiver);
                    Element sender = doc.createElement("SenderID");
                    sender.appendChild(doc.createTextNode((String) data.get("sen_id")));
                    header.appendChild(sender);
                    Element body = doc.createElement("Body");
                    root.appendChild(body);


                    Element deliveryInstruction = doc.createElement("DeliveryInstruction");
                    deliveryInstruction.setAttribute("Version", "2012A");
                    deliveryInstruction.setAttribute("ControllingInstance", "auto-gration");
                    body.appendChild(deliveryInstruction);


                    Element subtype = doc.createElement("SubType");
                    subtype.appendChild(doc.createTextNode("DeliveryInstruction"));
                    deliveryInstruction.appendChild(subtype);

                    Element doc_id_1 = doc.createElement("DocumentID");
                    doc_id_1.appendChild(doc.createTextNode(
                            (String) ((Map<String, Object>) data.get(keys.get(i + 4))).get("art_ref") +
                                    (String) ((Map<String, Object>) data.get(keys.get(i + 4))).get("doc_id")
                    ));
                    deliveryInstruction.appendChild(doc_id_1);
                    Element issueDate = doc.createElement("IssueDate");
                    issueDate.setAttribute("Format", "CCYYMMDDHHMM");
                    issueDate.setAttribute("Qualifier", "AT");

                    issueDate.appendChild(doc.createTextNode((String) ((Map<String, Object>) data.get(keys.get(i + 4))).get("date_doc")));
                    deliveryInstruction.appendChild(issueDate);
                    Element header2 = doc.createElement("Header");
                    deliveryInstruction.appendChild(header2);
                    Element buyer = doc.createElement("Buyer");
                    header2.appendChild(buyer);
                    Element identifier1 = doc.createElement("Identifier");
                    Attr agencyAttr1 = doc.createAttribute("Agency");
                    agencyAttr1.setValue("Odette");
                    identifier1.setAttributeNode(agencyAttr1);
                    identifier1.appendChild(doc.createTextNode((String) ((Map<String, Object>) data.get(keys.get(i + 4))).get("buyer")));
                    buyer.appendChild(identifier1);

                    Element seller = doc.createElement("Seller");
                    header2.appendChild(seller);

                    Element identifier2 = doc.createElement("Identifier");
                    Attr agencyAttr2 = doc.createAttribute("Agency");
                    agencyAttr2.setValue("Buyer");
                    identifier2.setAttributeNode(agencyAttr2);
                    identifier2.appendChild(doc.createTextNode((String) data.get("rec_id")));
                    seller.appendChild(identifier2);

                    Element shiptoline = doc.createElement("ShipToLine");
                    deliveryInstruction.appendChild(shiptoline);

                    Element shipto = doc.createElement("ShipTo");
                    shiptoline.appendChild(shipto);
                    Element identifier3 = doc.createElement("Identifier");
                    identifier3.setAttribute("Agency", "Odette");
                    identifier3.appendChild(doc.createTextNode((String) ((Map<String, Object>) data.get(keys.get(i + 4))).get("shipto")));
                    shipto.appendChild(identifier3);

                    Element itemline = doc.createElement("ItemLine");
                    shiptoline.appendChild(itemline);

                    Element linenumber = doc.createElement("LineNumber");
                    linenumber.appendChild(doc.createTextNode("1"));
                    itemline.appendChild(linenumber);
                    Element buyerarticlenum = doc.createElement("BuyerArticleNumber");
                    buyerarticlenum.appendChild(doc.createTextNode((String) ((Map<String, Object>) data.get(keys.get(i + 4))).get("doc_id")));
                    itemline.appendChild(buyerarticlenum);

                    Element engineering = doc.createElement("EngineeringChangeID");
                    engineering.appendChild(doc.createTextNode("-"));
                    itemline.appendChild(engineering);

                    Element article_des = doc.createElement("ArticleDescription");
                    article_des.appendChild(doc.createTextNode((String) ((Map<String, Object>) data.get(keys.get(i + 4))).get("article_description")));
                    itemline.appendChild(article_des);

                    Element docum_ref = doc.createElement("DocumentReference");
                    docum_ref.setAttribute("Qualifier", "Order");
                    itemline.appendChild(docum_ref);

                    Element docum_num = doc.createElement("DocumentNumber");
                    docum_num.appendChild(doc.createTextNode((String) ((Map<String, Object>) data.get(keys.get(i + 4))).get("doc_ref_num_order")));
                    docum_ref.appendChild(docum_num);

                    Element placeofdis = doc.createElement("PlaceOfDischarge");
                    itemline.appendChild(placeofdis);


                    Element location_id = doc.createElement("LocationID");
                    location_id.setAttribute("Agency", "Buyer");
                    location_id.appendChild(doc.createTextNode((String) ((Map<String, Object>) data.get(keys.get(i + 4))).get("loc_id")));
                    placeofdis.appendChild(location_id);

                    Element internal = doc.createElement("InternalDestination");
                    itemline.appendChild(internal);

                    Element location_id_2 = doc.createElement("LocationID");
                    String intDestination = (String) ((Map<String, Object>) data.get(keys.get(i+4))).get("internal destination");
                    String locId = (String) ((Map<String, Object>) data.get(keys.get(i+4))).get("loc_id");


                    String value = (intDestination != null && !intDestination.isEmpty()) ? intDestination : locId;

                    location_id_2.appendChild(doc.createTextNode(value));

                    internal.appendChild(location_id_2);
                    List<Map<String, String>> forecastList = (List<Map<String, String>>) ((Map<String, Object>) data.get(keys.get(i+4))).get("forecastlist");

                    List<Map<String, String>> docNumbersList = (List<Map<String, String>>) ((Map<String, Object>) data.get(keys.get(i+4))).get("doc_numbers_list");
                    List<Map<String, String>> firmList = (List<Map<String, String>>) ((Map<String, Object>) data.get(keys.get(i+4))).get("firmlist");

                    if (docNumbersList != null && !docNumbersList.isEmpty()) {

                        Map<String, String> docData = docNumbersList.get(0);

                        Element recQuant = doc.createElement("ReceivedQuantity");
                        itemline.appendChild(recQuant);

                        Element lastRecQuant = doc.createElement("LatestReceivedQuantity");
                        lastRecQuant.setAttribute("UoM", "PCE");
                        lastRecQuant.appendChild(doc.createTextNode(docData.get("pce")));
                        recQuant.appendChild(lastRecQuant);

                        Element receivingDate = doc.createElement("ReceivingDate");
                        receivingDate.setAttribute("Qualifier", "At");
                        receivingDate.setAttribute("Format", "CCYYMMDD");
                        receivingDate.appendChild(doc.createTextNode(toDate(docData.get("doc_date"))));
                        recQuant.appendChild(receivingDate);

                        Element documRefQual = doc.createElement("DocumentReference");
                        documRefQual.setAttribute("Qualifier", "DeliveryNote");
                        recQuant.appendChild(documRefQual);

                        Element documNum2 = doc.createElement("DocumentNumber");
                        documNum2.appendChild(doc.createTextNode(docData.get("doc_id")));
                        documRefQual.appendChild(documNum2);

                        Element cumulRecQuant = doc.createElement("CumulativeReceivedQuantity");
                        cumulRecQuant.setAttribute("UoM", "PCE");
                        cumulRecQuant.appendChild(doc.createTextNode((String) ((Map<String, String>) data.get(keys.get(i+4))).get("pce_doc_cumul")));
                        recQuant.appendChild(cumulRecQuant);
                        if (((firmList != null) && !firmList.isEmpty()) && (( forecastList.isEmpty()))){
                        Element recQuant1 = doc.createElement("ReceivedQuantity");
                        itemline.appendChild(recQuant1);

                        Element lastRecQuant1 = doc.createElement("LatestReceivedQuantity");
                        lastRecQuant1.setAttribute("UoM", "PCE");
                        lastRecQuant1.appendChild(doc.createTextNode("0"));
                        recQuant1.appendChild(lastRecQuant1);

                        Element receivingDate1 = doc.createElement("ReceivingDate");
                        receivingDate1.setAttribute("Qualifier", "At");
                        receivingDate1.setAttribute("Format", "CCYYMMDD");
                        receivingDate1.appendChild(doc.createTextNode(toDate(docData.get("doc_date"))));
                        recQuant1.appendChild(receivingDate1);

                        Element documRefQual1 = doc.createElement("DocumentReference");
                        documRefQual1.setAttribute("Qualifier", "DeliveryNote");
                        recQuant1.appendChild(documRefQual1);

                        Element documNum21 = doc.createElement("DocumentNumber");
                        documNum21.appendChild(doc.createTextNode(docData.get("doc_id")));
                        documRefQual.appendChild(documNum21);

                        Element cumulRecQuant1 = doc.createElement("CumulativeReceivedQuantity");
                        cumulRecQuant1.setAttribute("UoM", "PCE");
                        cumulRecQuant1.appendChild(doc.createTextNode((String) ((Map<String, String>) data.get(keys.get(i+4))).get("pce_doc_cumul")));
                        recQuant1.appendChild(cumulRecQuant1);
                        }



                        Element calcDate = doc.createElement("CalculationDate");
                        calcDate.setAttribute("Qualifier", "At");
                        calcDate.setAttribute("Format", "CCYYMMDD");
                        calcDate.appendChild(doc.createTextNode(toDate(docNumbersList.get(0).get("doc_date"))));
                        itemline.appendChild(calcDate);
                    }

                    if (firmList != null && !firmList.isEmpty()) {
                        Element deliveryFirm = doc.createElement("DeliveryScheduleFirm");
                        deliveryFirm.setAttribute("CommittmentLevel", "Firm");
                        itemline.appendChild(deliveryFirm);

                        for (Map<String, String> firmData : firmList) {
                            Element scheduleLine = doc.createElement("ScheduleLineFirm");
                            deliveryFirm.appendChild(scheduleLine);

                            Element deliveryQuantity = doc.createElement("DeliveryQuantityFirm");
                            deliveryQuantity.setAttribute("UoM", "PCE");
                            deliveryQuantity.appendChild(doc.createTextNode(firmData.get("pce")));
                            scheduleLine.appendChild(deliveryQuantity);

                            Element deliveryDateAfter = doc.createElement("DeliveryDateFirm");
                            deliveryDateAfter.setAttribute("Qualifier", "After");
                            deliveryDateAfter.setAttribute("Format", "CCYYMMDD");
                            deliveryDateAfter.appendChild(doc.createTextNode(firmData.get("dateafter")));
                            scheduleLine.appendChild(deliveryDateAfter);

                            Element deliveryDateBefore = doc.createElement("DeliveryDateFirm");
                            deliveryDateBefore.setAttribute("Qualifier", "Before");
                            deliveryDateBefore.setAttribute("Format", "CCYYMMDD");
                            deliveryDateBefore.appendChild(doc.createTextNode(firmData.get("databefore")));
                            scheduleLine.appendChild(deliveryDateBefore);
                        }
                    }

                    if (forecastList != null && !forecastList.isEmpty()) {
                        Element deliveryForecast = doc.createElement("DeliveryScheduleForecast");
                        deliveryForecast.setAttribute("CommittmentLevel", "Forecast");
                        itemline.appendChild(deliveryForecast);

                        for (Map<String, String> forecastData : forecastList) {
                            Element scheduleLine = doc.createElement("ScheduleLine");
                            deliveryForecast.appendChild(scheduleLine);

                            Element deliveryQuantity = doc.createElement("DeliveryQuantity");
                            deliveryQuantity.setAttribute("UoM", "PCE");
                            deliveryQuantity.appendChild(doc.createTextNode(forecastData.get("pce")));
                            scheduleLine.appendChild(deliveryQuantity);

                            Element deliveryDateAfter = doc.createElement("DeliveryDate");
                            deliveryDateAfter.setAttribute("Qualifier", "After");
                            deliveryDateAfter.setAttribute("Format", "CCYYMMDD");
                            deliveryDateAfter.appendChild(doc.createTextNode(forecastData.get("dateafter")));
                            scheduleLine.appendChild(deliveryDateAfter);

                            Element deliveryDateBefore = doc.createElement("DeliveryDate");
                            deliveryDateBefore.setAttribute("Qualifier", "Before");
                            deliveryDateBefore.setAttribute("Format", "CCYYMMDD");
                            deliveryDateBefore.appendChild(doc.createTextNode(forecastData.get("databefore")));
                            scheduleLine.appendChild(deliveryDateBefore);
                        }
                    }



                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    DOMSource source = new DOMSource(doc);

// Sanitize the sender ID
                    String sanitizedSenId = sanitizeFileName(data.get("sen_id").toString());
                    String dirName = FileConversionService.staticOutputPath+ "/" + sanitizedSenId + "/";
                    new File(dirName).mkdirs();

// Build the file name safely
                    String fileName = "VTU_" + sanitizedSenId + "_" +
                            sanitizeFileName(data.get("ref_file").toString()) + "_" +
                            sanitizeFileName(((Map<String, Object>) data.get(keys.get(i + 4))).get("date_doc").toString()) + "_" +
                            sanitizeFileName(data.get("id_file").toString()) + "_" + (i + 1) + ".xml";

// Full file path
                    String filePath = dirName + fileName;

// Create result and transform
                    StreamResult result = new StreamResult(new File(filePath));
                    transformer.transform(source, result);
                    File xmlFile = new File(filePath);
                    transformer.transform(source, new StreamResult(xmlFile));

                    generatedXmlFiles.add(xmlFile);
                    System.out.println("File converted to XML successfully: " + filePath);}
            } catch (ParserConfigurationException | TransformerException e) {
                e.printStackTrace();
            }

            return generatedXmlFiles;
        }
    public static void conversion2(String directoryPath) throws IOException {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files == null) {
            System.out.println("Directory does not exist or is empty.");
            return;
        }

        for (File file : files) {
            if (file.isFile() && (file.getName().endsWith(".txt") || !file.getName().contains("."))) {
                System.out.println("Processing file: " + file.getName());

                Map<String, Object> data = decryptFile(directoryPath, file.getName());
                converttoxlsx(data);
            }
        }
    }
    public static File converttoxlsx(Map<String, Object> l) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Sheet 1");

        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 5);
        font.setFontName("Arial");

        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setWrapText(true);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        Font dateFont = workbook.createFont();
        dateFont.setFontHeightInPoints((short) 9);
        titleStyle.setFont(dateFont);

        CellStyle textStyle = workbook.createCellStyle();
        textStyle.setAlignment(HorizontalAlignment.CENTER);
        textStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        textStyle.setFont(font);

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        sheet.setColumnWidth(0, 15 * 256);
        sheet.setColumnWidth(1, 60 * 256);
        sheet.setColumnWidth(2, 4 * 256);
        sheet.setColumnWidth(3, 4 * 256);
        sheet.setColumnWidth(4, 4 * 256);

        Row headerRow = sheet.createRow(0);

        Cell cell1 = headerRow.createCell(0);
        cell1.setCellValue("Reception EDI");
        cell1.setCellStyle(titleStyle);

        Cell cell2 = headerRow.createCell(1);
        cell2.setCellValue("Date Reception");
        cell2.setCellStyle(titleStyle);

        Cell cell3 = headerRow.createCell(2);
        cell3.setCellStyle(cellStyle);

        Cell cell4 = headerRow.createCell(3);
        cell4.setCellStyle(cellStyle);

        Cell cell5 = headerRow.createCell(4);
        cell5.setCellStyle(cellStyle);

        List<String> keys = List.copyOf(l.keySet());

        int globalRowIndex = 1;
        String idFile = null;
        String refFile = null;
        String senId = null;

        for (int i = 0; i < keys.size() - 4; i++) {
            Row tr = sheet.createRow(globalRowIndex++);

            Cell celll1 = tr.createCell(0);
            String buyer = (String) ((Map<String, Object>) l.get(keys.get(i + 4))).get("buyer");
            String txt = "Autoliv " + buyer;
            celll1.setCellValue(txt);
            celll1.setCellStyle(textStyle);

            Cell celll2 = tr.createCell(1);
            senId = (String) l.get("sen_id");
            refFile = (String) l.get("ref_file");
            String dateDoc = (String) ((Map<String, Object>) l.get(keys.get(i + 4))).get("date_doc");
            idFile = (String) l.get("id_file");
            String vtuTxt = "VTU_" + senId + "_" + refFile + "_" + keys.get(i + 4) + "_" + dateDoc.substring(0, 11) + idFile;
            celll2.setCellValue(vtuTxt);
            celll2.setCellStyle(textStyle);
            celll2.setCellStyle(cellStyle);
            int cell2Length = vtuTxt.length();
            sheet.setColumnWidth(1, Math.max(sheet.getColumnWidth(1), (cell2Length + 2) * 256));

            List<Map<String, String>> firmList = (List<Map<String, String>>) ((Map<String, Object>) l.get(keys.get(i + 4))).get("firmlist");
            if (firmList != null && !firmList.isEmpty()) {
                tr = sheet.createRow(globalRowIndex++);
                // Add headers
                String[] firmHeaders = {"Firm", "Reference Article", "Qte", "Date 1", "Date 2"};
                for (int h = 0; h < firmHeaders.length; h++) {
                    Cell header = tr.createCell(h);
                    header.setCellValue(firmHeaders[h]);
                    header.setCellStyle(titleStyle);
                }

                for (Map<String, String> firm : firmList) {
                    tr = sheet.createRow(globalRowIndex++);
                    tr.createCell(0).setCellValue("firm");
                    tr.getCell(0).setCellStyle(textStyle);

                    String artRef = (String) ((Map<String, Object>) l.get(keys.get(i + 4))).get("art_ref");
                    String docId = (String) ((Map<String, Object>) l.get(keys.get(i + 4))).get("doc_id");
                    Cell cellArtRef = tr.createCell(1);
                    cellArtRef.setCellValue(artRef + docId);
                    cellArtRef.setCellStyle(textStyle);

                    Cell cellQteValue = tr.createCell(2);
                    String pceValue = firm.get("pce");
                    if (pceValue != null) {
                        cellQteValue.setCellValue(Double.parseDouble(pceValue));
                    }
                    cellQteValue.setCellStyle(textStyle);

                    Cell cellDate1Value = tr.createCell(3);
                    String dateBefore = firm.get("databefore");
                    if (dateBefore != null) {
                        cellDate1Value.setCellValue(dateBefore);
                    }
                    cellDate1Value.setCellStyle(textStyle);

                    Cell cellDate2Value = tr.createCell(4);
                    String dateAfter = firm.get("dateafter");
                    if (dateAfter != null) {
                        cellDate2Value.setCellValue(dateAfter);
                    }
                    cellDate2Value.setCellStyle(textStyle);
                }
            }

            List<Map<String, String>> forecastList = (List<Map<String, String>>) ((Map<String, Object>) l.get(keys.get(i + 4))).get("forecastlist");
            if (forecastList != null && !forecastList.isEmpty()) {
                tr = sheet.createRow(globalRowIndex++);
                String[] forecastHeaders = {"Forecast", "Reference Article", "Qte", "Date 1", "Date 2"};
                for (int h = 0; h < forecastHeaders.length; h++) {
                    Cell header = tr.createCell(h);
                    header.setCellValue(forecastHeaders[h]);
                    header.setCellStyle(titleStyle);
                }

                for (Map<String, String> forecast : forecastList) {
                    tr = sheet.createRow(globalRowIndex++);
                    tr.createCell(0).setCellValue("forecast");
                    tr.getCell(0).setCellStyle(textStyle);

                    String artRef = (String) ((Map<String, Object>) l.get(keys.get(i + 4))).get("art_ref");
                    String docId = (String) ((Map<String, Object>) l.get(keys.get(i + 4))).get("doc_id");
                    Cell cellArtRef = tr.createCell(1);
                    cellArtRef.setCellValue(artRef + docId);
                    cellArtRef.setCellStyle(textStyle);

                    Cell cellQteValue = tr.createCell(2);
                    String pceValue = forecast.get("pce");
                    if (pceValue != null) {
                        cellQteValue.setCellValue(Double.parseDouble(pceValue));
                    }
                    cellQteValue.setCellStyle(textStyle);

                    Cell cellDate1Value = tr.createCell(3);
                    String dateBefore = forecast.get("databefore");
                    if (dateBefore != null) {
                        cellDate1Value.setCellValue(dateBefore);
                    }
                    cellDate1Value.setCellStyle(textStyle);

                    Cell cellDate2Value = tr.createCell(4);
                    String dateAfter = forecast.get("dateafter");
                    if (dateAfter != null) {
                        cellDate2Value.setCellValue(dateAfter);
                    }
                    cellDate2Value.setCellStyle(textStyle);
                }
            }
        }

        String sanitizedSenId = sanitizeFileName(senId.toString());
        String dirName = FileConversionService.staticOutputPath+ "/" + sanitizedSenId + "/";
        new File(dirName).mkdirs();

        File xlsxFile = new File(dirName + idFile + "_" + refFile + ".xlsx");
        try (FileOutputStream fileOut = new FileOutputStream(xlsxFile)) {
            workbook.write(fileOut);
        }

        workbook.close();
        return xlsxFile;
    }

    public static void conversion(String directoryPath) {
            File directory = new File(directoryPath);
            File[] files = directory.listFiles();

            if (files == null) {
                System.out.println("Directory does not exist or is empty.");
                return;
            }

            for (File file : files) {
                if (file.isFile() && (file.getName().endsWith(".txt") || !file.getName().contains("."))) {
                    System.out.println("Processing file: " + file.getName());

                    Map<String, Object> data = decryptFile(directoryPath, file.getName());
                    convertFileToXML(data);
                }
            }
        }









}
