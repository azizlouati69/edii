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
import java.time.LocalDate;
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

            // Handle Client: Create or Update
            client client;
            List<client> existingClients = clientrepository.findBySenderId(senderID);
            if (existingClients.isEmpty()) {
                client = new client();
                client.setSenderId(senderID);
            } else {
                client = existingClients.get(0); // Get existing client
            }
            client.setBuyerIdentifier(buyerIdentifier); // Update or set buyerIdentifier
            client = clientrepository.save(client); // Save (create or update)

            // Handle Seller: Create or Update
            seller seller;
            List<seller> existingSellers = sellerrepository.findByReceiverId(receiverID);
            if (existingSellers.isEmpty()) {
                seller = new seller();
                seller.setReceiverId(receiverID);
            } else {
                seller = existingSellers.get(0); // Get existing seller
            }
            seller = sellerrepository.save(seller); // Save (create or update)

            // Initialize Lazy-Loaded Collections
            seller.getClients().size(); // Ensures seller's clients are initialized within the transaction
            client.getSellers().size(); // Ensures client's sellers are initialized within the transaction

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
                    LocalDate issueDate = null;
                    Node issueDateNode = doc.getElementsByTagName("IssueDate").item(0);
                    if (issueDateNode != null) {
                        issueDate = LocalDate.parse(issueDateNode.getTextContent().substring(0, 10));
                    }

                    // Extract Calculation Date
                    LocalDate calculationDate = null;
                    NodeList calculationDateNodes = itemElement.getElementsByTagName("CalculationDate");
                    if (calculationDateNodes.getLength() > 0) {
                        calculationDate = LocalDate.parse(calculationDateNodes.item(0).getTextContent());
                    }

                    // Extract Quantity Data
                    String latestReceivedQuantity = null;
                    LocalDate receivingDate = null;
                    String deliveryNoteDocNumber = null;
                    String cumulativeReceivedQuantity = null;

                    NodeList receivedQuantityNodes = itemElement.getElementsByTagName("ReceivedQuantity");
                    if (receivedQuantityNodes.getLength() > 0) {
                        Element receivedQuantityElement = (Element) receivedQuantityNodes.item(0);

                        NodeList latestReceivedQuantityNodes = receivedQuantityElement.getElementsByTagName("LatestReceivedQuantity");
                        if (latestReceivedQuantityNodes.getLength() > 0) {
                            latestReceivedQuantity = latestReceivedQuantityNodes.item(0).getTextContent();
                        }

                        NodeList receivingDateNodes = receivedQuantityElement.getElementsByTagName("ReceivingDate");
                        if (receivingDateNodes.getLength() > 0) {
                            receivingDate = LocalDate.parse(receivingDateNodes.item(0).getTextContent());
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
                    System.out.println("CalculationDate: " + calculationDate);
                    System.out.println("LatestReceivedQuantity: " + latestReceivedQuantity);
                    System.out.println("ReceivingDate: " + receivingDate);
                    System.out.println("DeliveryNoteDocNumber: " + deliveryNoteDocNumber);
                    System.out.println("CumulativeReceivedQuantity: " + cumulativeReceivedQuantity);

                    // Handle Order: Create or Update
                    order ord;
                    List<order> existingOrders = orderrepository.findByDocumentId(documentID);
                    if (existingOrders.isEmpty()) {
                        ord = new order();
                        ord.setDocumentId(documentID);
                    } else {
                        ord = existingOrders.get(0); // Get existing order
                    }

                    // Update Order fields
                    ord.setBuyerArticleNumber(buyerArticleNumber);
                    ord.setDescription(description);
                    ord.setDocumentNumber(documentNumber);
                    ord.setIssueDate(issueDate);
                    ord.setCalculationDate(calculationDate);
                    ord.setShipto(shipToIdentifier);
                    ord.setInternaldestination(internalDestination);
                    ord.setPlaceofdischarge(placeOfDischarge);

                    // Handle Quantity: Create or Update
                    quantity q = ord.getQuantity();
                    boolean hasQuantityData = false;

                    if (q == null) {
                        q = new quantity();
                        q.setOrder(ord);
                    }

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
                        ord.setQuantity(q);
                    } else {
                        ord.setQuantity(null); // Remove quantity if no data is present
                    }

                    // Set Client and Seller associations
                    ord.setClient(client);
                    ord.setSeller(seller);
                    client.getOrders().add(ord);
                    seller.getOrders().add(ord);

                    // Handle Firm Items: Clear and Recreate
                    ord.getFirmItems().clear(); // Clear existing firm items
                    NodeList firmNodes = doc.getElementsByTagName("DeliveryScheduleFirm");
                    for (int ii = 0; ii < firmNodes.getLength(); ii++) {
                        Node firmNode = firmNodes.item(ii);
                        if (firmNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element firmElement = (Element) firmNode;

                            NodeList scheduleLineFirms = firmElement.getElementsByTagName("ScheduleLineFirm");
                            for (int j = 0; j < scheduleLineFirms.getLength(); j++) {
                                Element scheduleLineFirmElement = (Element) scheduleLineFirms.item(j);

                                firmitem firmItem = new firmitem();
                                String deliveryQuantityFirm = scheduleLineFirmElement.getElementsByTagName("DeliveryQuantityFirm").item(0).getTextContent();
                                firmItem.setDeliveryQuantity(deliveryQuantityFirm);

                                NodeList deliveryDates = scheduleLineFirmElement.getElementsByTagName("DeliveryDateFirm");
                                LocalDate deliveryDateAfter = null;
                                LocalDate deliveryDateBefore = null;

                                for (int k = 0; k < deliveryDates.getLength(); k++) {
                                    Element deliveryDateElement = (Element) deliveryDates.item(k);
                                    String qualifier = deliveryDateElement.getAttribute("Qualifier");
                                    String deliveryDate = deliveryDateElement.getTextContent();

                                    if ("After".equals(qualifier)) {
                                        deliveryDateAfter = deliveryDate != null ? LocalDate.parse(deliveryDate) : null;
                                    } else if ("Before".equals(qualifier)) {
                                        deliveryDateBefore = deliveryDate != null ? LocalDate.parse(deliveryDate) : null;
                                    }
                                }

                                firmItem.setDeliveryDateAfter(deliveryDateAfter);
                                firmItem.setDeliveryDateBefore(deliveryDateBefore);
                                firmItem.setOrder(ord);
                                ord.getFirmItems().add(firmItem);
                            }
                        }
                    }

                    // Handle Forecast Items: Clear and Recreate
                    ord.getForecastItems().clear(); // Clear existing forecast items
                    NodeList forecastNodes = doc.getElementsByTagName("DeliveryScheduleForecast");
                    for (int i1 = 0; i1 < forecastNodes.getLength(); i1++) {
                        Node forecastNode = forecastNodes.item(i1);
                        if (forecastNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element forecastElement = (Element) forecastNode;

                            NodeList scheduleLines = forecastElement.getElementsByTagName("ScheduleLine");
                            for (int j = 0; j < scheduleLines.getLength(); j++) {
                                Element scheduleLineElement = (Element) scheduleLines.item(j);

                                forecastitem forecastItem = new forecastitem();
                                String deliveryQuantity = scheduleLineElement.getElementsByTagName("DeliveryQuantity").item(0).getTextContent();
                                forecastItem.setForecastQuantity(deliveryQuantity);

                                NodeList deliveryDates = scheduleLineElement.getElementsByTagName("DeliveryDate");
                                LocalDate deliveryDateAfter = null;
                                LocalDate deliveryDateBefore = null;

                                for (int k = 0; k < deliveryDates.getLength(); k++) {
                                    Element deliveryDateElement = (Element) deliveryDates.item(k);
                                    String qualifier = deliveryDateElement.getAttribute("Qualifier");
                                    String deliveryDate = deliveryDateElement.getTextContent();

                                    if ("After".equals(qualifier)) {
                                        deliveryDateAfter = deliveryDate != null ? LocalDate.parse(deliveryDate) : null;
                                    } else if ("Before".equals(qualifier)) {
                                        deliveryDateBefore = deliveryDate != null ? LocalDate.parse(deliveryDate) : null;
                                    }
                                }

                                forecastItem.setForecastDateAfter(deliveryDateAfter);
                                forecastItem.setForecastDateBefore(deliveryDateBefore);
                                forecastItem.setOrder(ord);
                                ord.getForecastItems().add(forecastItem);
                            }
                        }
                    }

                    // Save the order (creates or updates, cascades to quantity, firm items, and forecast items)
                    orderrepository.save(ord);
                    System.out.println("Saved/Updated Order: " + ord);
                }
            }
        } catch (Exception e) {
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