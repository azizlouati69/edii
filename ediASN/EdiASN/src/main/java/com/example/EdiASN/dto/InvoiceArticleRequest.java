package com.example.EdiASN.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InvoiceArticleRequest {
    private Long articleId;
    private Long invoiceId;
    private Integer totalQuantity;

    // Getters and setters
}