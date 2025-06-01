package com.example.EdiASN.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ArticleCardboardRequest {
    private Long articleId;
    private Long cardboardId;
    private Integer quantityPerCardboard;

    // Getters and setters
}
