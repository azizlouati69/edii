package com.example.EdiASN.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetDefaultCardboardRequest {
    private Long articleId;
    private Long cardboardId;
}