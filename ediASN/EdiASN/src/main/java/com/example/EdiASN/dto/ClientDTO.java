package com.example.EdiASN.dto;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class ClientDTO {
    private String name;
    private String adress;
    private String edi_adress;
    private String ref_client;
    private String siret;
    private String pia;
    private String nad;
    private String loc159;
    private String loc11;
}
