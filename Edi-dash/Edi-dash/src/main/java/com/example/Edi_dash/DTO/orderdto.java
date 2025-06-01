package com.example.Edi_dash.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class orderdto{
    private Long id;  // Added id field
    private String description;
    private String buyerArticleNumber;
    private String documentId;
    private String documentNumber;
    private LocalDate issueDate;
    // Constructor
    public orderdto( Long id , String documentId,String description, String buyerArticleNumber, String documentNumber, LocalDate issueDate) {
        this.id = id;
        this.description = description;
        this.buyerArticleNumber = buyerArticleNumber;
        this.documentId = documentId;
        this.documentNumber = documentNumber;

        this.issueDate = issueDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Method to format issueDate to only show date part (yyyy-MM-dd)
    private String formatIssueDate(String issueDate) {
        if (issueDate != null && issueDate.length() >= 10) {
            try {
                // Assuming the format is yyyy-MM-ddHHmm, take only the date part
                String datePart = issueDate.substring(0, 10);
                // You can also convert to LocalDate and format further if needed
                LocalDateTime dateTime = LocalDateTime.parse(datePart + "T00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (Exception e) {
                // Handle any parsing errors
                return issueDate; // return the original if formatting fails
            }
        }
        return issueDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBuyerArticleNumber() {
        return buyerArticleNumber;
    }

    public void setBuyerArticleNumber(String buyerArticleNumber) {
        this.buyerArticleNumber = buyerArticleNumber;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }
// Getters and setters...
}

