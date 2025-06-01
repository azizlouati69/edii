package com.example.EdiASN.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleDTO {
    private String nameint;
    private String nameext;
    private String designation;
    private Float gross_weight;
    private Float net_weight;
    private String lot_number;
    private String order_number;
    private Long clientId;
    private String clientName;

    // getters and setters
}
