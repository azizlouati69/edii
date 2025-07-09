package com.example.EdiASN.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String adress;
    private String edi_adress;
    private String ref_client;
    private String siret;
    private String pia;
    private String nad;
    private String loc159;
    private String loc11;
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Invoice> invoices = new ArrayList<>();
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<ArticleClient> articleClients = new ArrayList<>();
    private Long userId;
    @ManyToOne
    @JsonManagedReference

    @JoinColumn(name = "invoicearticleclientcardboard_id")
    private InvoiceArticleClientCardboard invoicearticleclientcardboard;

}
