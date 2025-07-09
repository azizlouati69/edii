package com.example.EdiASN.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ArticleClientCardboardRequest {
    private Long articleClientId;
    private Long cardboardId;
    private Long default_cardboardId;
    private Integer quantityPerCardboard;

    // Getters and setters
}
