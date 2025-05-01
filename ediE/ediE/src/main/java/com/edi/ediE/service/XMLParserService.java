package com.edi.ediE.service;

import entities.model.*;
import repository.clientrepository;
import repository.sellerrepository;
import repository.orderrepository;
import repository.quantityrepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
public class XMLParserService {

    private final clientrepository clientrepository;
    private final sellerrepository sellerrepository;
    private final orderrepository orderrepository;
    private final quantityrepository quantityrepository;

    public XMLParserService(clientrepository clientrepository, sellerrepository sellerrepository, orderrepository orderrepository, quantityrepository quantityrepository) {
        this.clientrepository = clientrepository;
        this.sellerrepository = sellerrepository;
        this.orderrepository = orderrepository;
        this.quantityrepository = quantityrepository;
    }
    @Transactional
    public void parseAndSaveXML(String folderPath) {
        try {
            Files.walk(Paths.get(folderPath))
                    .filter(path -> path.toString().endsWith(".xml"))
                    .forEach(this::processXMLFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Transactional(propagation = Propagation.REQUIRED)
    private void processXMLFile(Path filePath) {
        try {
            File xmlFile = filePath.toFile();

            // 1️⃣ Wait for the file to be fully written
            if (!isFileFullyWritten(xmlFile)) {
                System.err.println("Skipping file (still being written): " + filePath);
                return;
            }

            // 2️⃣ Check if file exists and is not empty
            if (!xmlFile.exists() || xmlFile.length() == 0) {
                System.err.println("Error: XML file is empty or missing - " + filePath);
                return;
            }

            // 3️⃣ Parse XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // Extract SenderID
            Node senderNode = doc.getElementsByTagName("SenderID").item(0);
            String senderID = senderNode != null ? senderNode.getTextContent() : "Not Found";

            // Extract ReceiverID
            Node receiverNode = doc.getElementsByTagName("ReceiverID").item(0);
            String receiverID = receiverNode != null ? receiverNode.getTextContent() : "Not Found";

            // Extract Buyer Identifier
            NodeList buyerNodes = doc.getElementsByTagName("Buyer");
            String buyerIdentifier = "Not Found";
            if (buyerNodes.getLength() > 0) {
                Node buyerNode = buyerNodes.item(0);
                if (buyerNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element buyerElement = (Element) buyerNode;
                    buyerIdentifier = buyerElement.getElementsByTagName("Identifier").item(0).getTextContent();
                }
            }

            // Extract ShipTo Identifier
            NodeList shipToNodes = doc.getElementsByTagName("ShipTo");
            String shipToIdentifier = "Not Found";
            if (shipToNodes.getLength() > 0) {
                Node shipToNode = shipToNodes.item(0);
                if (shipToNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element shipToElement = (Element) shipToNode;
                    shipToIdentifier = shipToElement.getElementsByTagName("Identifier").item(0).getTextContent();
                }
            }

            // Extract PlaceOfDischarge LocationID
            NodeList placeOfDischargeNodes = doc.getElementsByTagName("PlaceOfDischarge");
            String placeOfDischarge = "Not Found";
            if (placeOfDischargeNodes.getLength() > 0) {
                Node placeOfDischargeNode = placeOfDischargeNodes.item(0);
                if (placeOfDischargeNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element placeOfDischargeElement = (Element) placeOfDischargeNode;
                    placeOfDischarge = placeOfDischargeElement.getElementsByTagName("LocationID").item(0).getTextContent();
                }
            }

            // Extract InternalDestination LocationID
            NodeList internalDestinationNodes = doc.getElementsByTagName("InternalDestination");
            String internalDestination = "Not Found";
            if (internalDestinationNodes.getLength() > 0) {
                Node internalDestinationNode = internalDestinationNodes.item(0);
                if (internalDestinationNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element internalDestinationElement = (Element) internalDestinationNode;
                    internalDestination = internalDestinationElement.getElementsByTagName("LocationID").item(0).getTextContent();
                }
            }

            // Log extracted values
            System.out.println("SenderID: " + senderID);
            System.out.println("ReceiverID: " + receiverID);
            System.out.println("BuyerIdentifier: " + buyerIdentifier);
            System.out.println("ShipToIdentifier: " + shipToIdentifier);
            System.out.println("PlaceOfDischarge: " + placeOfDischarge);
            System.out.println("InternalDestination: " + internalDestination);

            client client;
            if (isSenderIDUnique(senderID)) {
                client = new client();
                client.setSenderId(senderID);
                client.setBuyerIdentifier(buyerIdentifier);
                client = clientrepository.save(client); // Save new client
            } else {
                client = clientrepository.findBySenderId(senderID).get(0);
            }

            // Ensure Seller (Receiver) Exists
            seller seller;
            if (isReceiverIDUnique(receiverID)) {
                seller = new seller();
                seller.setReceiverId(receiverID);
                seller = sellerrepository.save(seller); // Save new seller
            } else {
                seller = sellerrepository.findByReceiverId(receiverID).get(0);
            }

            // Initialize Lazy-Loaded Collections
            seller.getClients().size();  // Ensures seller's clients are initialized within the transaction
            client.getSellers().size();  // Ensures client's sellers are initialized within the transaction

            // Ensure bidirectional association is correct
            if (!client.getSellers().contains(seller)) {
                client.getSellers().add(seller);
            }
            if (!seller.getClients().contains(client)) {
                seller.getClients().add(client);
            }

            // Save only the owning side (Seller)
            sellerrepository.save(seller); // The client will be saved as part of the seller's association


            // Extract Order Information from ItemLine
            NodeList itemLines = doc.getElementsByTagName("ItemLine");
            for (int i = 0; i < itemLines.getLength(); i++) {
                Node itemLineNode = itemLines.item(i);
                if (itemLineNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element itemElement = (Element) itemLineNode;

                    // Extract Buyer Article Number
                    String buyerArticleNumber = itemElement.getElementsByTagName("BuyerArticleNumber").item(0).getTextContent();

                    // Extract Description
                    String description = itemElement.getElementsByTagName("ArticleDescription").item(0).getTextContent();

                    // Extract Document ID
                    String documentID = doc.getElementsByTagName("DocumentID").item(0).getTextContent();

                    // Extract Document Number (Order Reference)
                    String documentNumber = "";
                    NodeList docRefs = itemElement.getElementsByTagName("DocumentReference");
                    for (int j = 0; j < docRefs.getLength(); j++) {
                        Element docRefElement = (Element) docRefs.item(j);
                        if (docRefElement.getAttribute("Qualifier").equals("Order")) {
                            documentNumber = docRefElement.getElementsByTagName("DocumentNumber").item(0).getTextContent();
                            break;
                        }
                    }

                    // Extract Issue Date
                    String issueDate = doc.getElementsByTagName("IssueDate").item(0).getTextContent();

                    // Extract Calculation Date
                    String calculationDate = null;  // Default to null if not found
                    NodeList calculationDateNodes = itemElement.getElementsByTagName("CalculationDate");
                    if (calculationDateNodes.getLength() > 0) {
                        calculationDate = calculationDateNodes.item(0).getTextContent();
                    }

// Extract Quantity Data
                    String latestReceivedQuantity = null;  // Default to null if not found
                    String receivingDate = null;  // Default to null if not found
                    String deliveryNoteDocNumber = null;  // Default to null if not found
                    String cumulativeReceivedQuantity = null;  // Default to null if not found

                    NodeList receivedQuantityNodes = itemElement.getElementsByTagName("ReceivedQuantity");
                    if (receivedQuantityNodes.getLength() > 0) {
                        Element receivedQuantityElement = (Element) receivedQuantityNodes.item(0);

                        NodeList latestReceivedQuantityNodes = receivedQuantityElement.getElementsByTagName("LatestReceivedQuantity");
                        if (latestReceivedQuantityNodes.getLength() > 0) {
                            latestReceivedQuantity = latestReceivedQuantityNodes.item(0).getTextContent();
                        }

                        NodeList receivingDateNodes = receivedQuantityElement.getElementsByTagName("ReceivingDate");
                        if (receivingDateNodes.getLength() > 0) {
                            receivingDate = receivingDateNodes.item(0).getTextContent();
                        }

                        NodeList docRefsQuantity = receivedQuantityElement.getElementsByTagName("DocumentReference");
                        for (int k = 0; k < docRefsQuantity.getLength(); k++) {
                            Element docRefElement = (Element) docRefsQuantity.item(k);
                            if (docRefElement.getAttribute("Qualifier").equals("DeliveryNote")) {
                                NodeList docNumberNodes = docRefElement.getElementsByTagName("DocumentNumber");
                                if (docNumberNodes.getLength() > 0) {
                                    deliveryNoteDocNumber = docNumberNodes.item(0).getTextContent();
                                }
                                break;
                            }
                        }

                        NodeList cumulativeReceivedQuantityNodes = receivedQuantityElement.getElementsByTagName("CumulativeReceivedQuantity");
                        if (cumulativeReceivedQuantityNodes.getLength() > 0) {
                            cumulativeReceivedQuantity = cumulativeReceivedQuantityNodes.item(0).getTextContent();
                        }
                    }

// Log Order details
                    System.out.println("BuyerArticleNumber: " + buyerArticleNumber);
                    System.out.println("Description: " + description);
                    System.out.println("DocumentID: " + documentID);
                    System.out.println("DocumentNumber: " + documentNumber);
                    System.out.println("IssueDate: " + issueDate);
                    System.out.println("CalculationDate: " + calculationDate);  // May be null
                    System.out.println("LatestReceivedQuantity: " + latestReceivedQuantity);  // May be null
                    System.out.println("ReceivingDate: " + receivingDate);  // May be null
                    System.out.println("DeliveryNoteDocNumber: " + deliveryNoteDocNumber);  // May be null
                    System.out.println("CumulativeReceivedQuantity: " + cumulativeReceivedQuantity);  // May be null

// Create and save the Order if DocumentID is unique
                    if (isDocumentIDUnique(documentID)) {
                        order ord = new order();
                        ord.setBuyerArticleNumber(buyerArticleNumber);
                        ord.setDescription(description);
                        ord.setDocumentId(documentID);
                        ord.setDocumentNumber(documentNumber);
                        ord.setIssueDate(issueDate);
                        ord.setCalculationDate(calculationDate);  // May be null
                        ord.setShipto(shipToIdentifier);
                        ord.setInternaldestination(internalDestination);
                        ord.setPlaceofdischarge(placeOfDischarge);
                        quantity q = new quantity();

                        boolean hasQuantityData = false;

                        if (cumulativeReceivedQuantity != null) {
                            q.setCumulativeReceivedQuantity(cumulativeReceivedQuantity);
                            hasQuantityData = true;
                        }
                        if (latestReceivedQuantity != null) {
                            q.setLatestReceivedQuantity(latestReceivedQuantity);
                            hasQuantityData = true;
                        }
                        if (receivingDate != null) {
                            q.setReceivingDate(receivingDate);
                            hasQuantityData = true;
                        }
                        if (deliveryNoteDocNumber != null) {
                            q.setDeliveryNoteDocNumber(deliveryNoteDocNumber);
                            hasQuantityData = true;
                        }

                        if (hasQuantityData) {
                            q.setOrder(ord);
                            ord.setQuantity(q);}
                        // Proceed with setting the Order only if qty exists
                        ord.setClient(client);
                        ord.setSeller(seller);
                        client.getOrders().add(ord);
                        seller.getOrders().add(ord);
                        NodeList firmNodes = doc.getElementsByTagName("DeliveryScheduleFirm");
                        for (int ii = 0; ii < firmNodes.getLength(); ii++) {
                            Node firmNode = firmNodes.item(ii);
                            if (firmNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element firmElement = (Element) firmNode;

                                // Assuming we need the firm information in the form of "ScheduleLineFirm"
                                NodeList scheduleLineFirms = firmElement.getElementsByTagName("ScheduleLineFirm");
                                List<firmitem> firmItems = new ArrayList<>();

                                // Loop through each ScheduleLineFirm and extract firm details
                                for (int j = 0; j < scheduleLineFirms.getLength(); j++) {
                                    Element scheduleLineFirmElement = (Element) scheduleLineFirms.item(j);

                                    firmitem firmItem = new firmitem();

                                    // Extract Delivery Quantity
                                    String deliveryQuantityFirm = scheduleLineFirmElement.getElementsByTagName("DeliveryQuantityFirm").item(0).getTextContent();
                                    firmItem.setDeliveryQuantity(deliveryQuantityFirm);

                                    // Extract Delivery Dates (After and Before)
                                    NodeList deliveryDates = scheduleLineFirmElement.getElementsByTagName("DeliveryDateFirm");
                                    String deliveryDateAfter = null;
                                    String deliveryDateBefore = null;

                                    for (int k = 0; k < deliveryDates.getLength(); k++) {
                                        Element deliveryDateElement = (Element) deliveryDates.item(k);
                                        String qualifier = deliveryDateElement.getAttribute("Qualifier");
                                        String deliveryDate = deliveryDateElement.getTextContent();

                                        if ("After".equals(qualifier)) {
                                            deliveryDateAfter = deliveryDate;
                                        } else if ("Before".equals(qualifier)) {
                                            deliveryDateBefore = deliveryDate;
                                        }
                                    }

                                    // Set Delivery Dates to the firmItem
                                    firmItem.setDeliveryDateAfter(deliveryDateAfter);
                                    firmItem.setDeliveryDateBefore(deliveryDateBefore);
                                    firmItem.setOrder(ord);
                                    // Add the firm item to the list
                                    ord.getFirmItems().add(firmItem);
                                }

                                // Assuming you are adding firmItems to your order

                            }
                        }
                        NodeList forecastNodes = doc.getElementsByTagName("DeliveryScheduleForecast");
                        for (int i1 = 0; i1 < forecastNodes.getLength(); i1++) {
                            Node forecastNode = forecastNodes.item(i1);
                            if (forecastNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element forecastElement = (Element) forecastNode;

                                // Assuming we need the forecast information in the form of "ScheduleLine"
                                NodeList scheduleLines = forecastElement.getElementsByTagName("ScheduleLine");
                                List<forecastitem> forecastItems = new ArrayList<>();

                                // Loop through each ScheduleLine and extract forecast details
                                for (int j = 0; j < scheduleLines.getLength(); j++) {
                                    Element scheduleLineElement = (Element) scheduleLines.item(j);

                                    forecastitem forecastItem = new forecastitem();

                                    // Extract Delivery Quantity
                                    String deliveryQuantity = scheduleLineElement.getElementsByTagName("DeliveryQuantity").item(0).getTextContent();
                                    forecastItem.setForecastQuantity(deliveryQuantity);

                                    // Extract Delivery Dates (After and Before)
                                    NodeList deliveryDates = scheduleLineElement.getElementsByTagName("DeliveryDate");
                                    String deliveryDateAfter = null;
                                    String deliveryDateBefore = null;

                                    for (int k = 0; k < deliveryDates.getLength(); k++) {
                                        Element deliveryDateElement = (Element) deliveryDates.item(k);
                                        String qualifier = deliveryDateElement.getAttribute("Qualifier");
                                        String deliveryDate = deliveryDateElement.getTextContent();

                                        if ("After".equals(qualifier)) {
                                            deliveryDateAfter = deliveryDate;
                                        } else if ("Before".equals(qualifier)) {
                                            deliveryDateBefore = deliveryDate;
                                        }
                                    }

                                    // Set Delivery Dates to the forecastItem
                                    forecastItem.setForecastDateAfter(deliveryDateAfter);
                                    forecastItem.setForecastDateBefore(deliveryDateBefore);
                                    forecastItem.setOrder(ord);
                                    // Add the forecast item to the list
                                    ord.getForecastItems().add(forecastItem);
                                }

                                // Assuming you are adding forecastItems to your ord
                            }
                        }


                        // Save the order which will also persist the quantity due to cascade
                        orderrepository.save(ord); // Save the order, which will also persist quantity if cascaded

                        System.out.println("Saved Order: " + ord);
                    } else {
                        System.out.println("Skipped Order (duplicate DocumentID in database): " + documentID);
                    }
                }}} catch (Exception e) {
            e.printStackTrace();
        }
    }















    // Check if DocumentID already exists in the database
    private boolean isDocumentIDUnique(String documentID) {
        return orderrepository.findByDocumentId(documentID).isEmpty();
    }

    // Check if SenderID is unique
    private boolean isSenderIDUnique(String senderID) {
        return clientrepository.findBySenderId(senderID).isEmpty();
    }

    // Check if ReceiverID is unique
    private boolean isReceiverIDUnique(String receiverID) {
        return sellerrepository.findByReceiverId(receiverID).isEmpty();
    }

    private boolean isFileFullyWritten(File file) {
        long previousSize = -1;
        long currentSize = file.length();

        try {
            while (previousSize != currentSize) {
                previousSize = currentSize;
                Thread.sleep(500);  // Wait 500ms before checking again
                currentSize = file.length();  // Check if size has changed
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return file.exists() && file.length() > 0;
    }
}
