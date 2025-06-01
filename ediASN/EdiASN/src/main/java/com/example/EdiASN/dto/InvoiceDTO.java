package com.example.EdiASN.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Setter
@Getter
public class InvoiceDTO {
    private Long clientId;
    private String invoice_number;
    private LocalDate deliveryDate;
}
