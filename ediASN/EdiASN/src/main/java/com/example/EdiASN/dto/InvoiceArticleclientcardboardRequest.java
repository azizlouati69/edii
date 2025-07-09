package com.example.EdiASN.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InvoiceArticleclientcardboardRequest {
    private Long articleclientcardboardId;
    private Long invoiceId;
    private Integer totalQuantity;

    // Getters and setters
}