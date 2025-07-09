package com.example.EdiASN.service;

import com.example.EdiASN.dto.InvoiceDTO;
import com.example.EdiASN.entity.Invoice;
import com.example.EdiASN.entity.Client;
import com.example.EdiASN.repository.InvoiceRepository;
import com.example.EdiASN.repository.ClientRepository;
import com.example.EdiASN.security.JwtService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private JwtService jwtService;


    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private ClientRepository clientRepository;
    public Invoice createInvoice(InvoiceDTO dto, Long userId) {
        Invoice invoice = new Invoice();
        invoice.setUserId(userId);
        invoice.setInvoice_number(dto.getInvoice_number());
        invoice.setDeliveryDate(dto.getDeliveryDate());

        if (dto.getClientId() != null) {
            Client client = clientRepository.findById(dto.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            invoice.setClient(client);
        }
        return invoiceRepository.save(invoice);
    }
    public List<Invoice> getAllInvoicesByUser(Long userId) {
        return invoiceRepository.findByUserId(userId);
    }

    public Invoice getInvoiceById(Long id, Long userId) {
        return invoiceRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Article not found or not authorized"));
    }
    public Invoice updateInvoice(Long id, InvoiceDTO dto, Long userId) {
        Invoice invoice = getInvoiceById(id, userId);

        if (dto.getInvoice_number() != null) invoice.setInvoice_number(dto.getInvoice_number());
        if (dto.getDeliveryDate() != null) invoice.setDeliveryDate(dto.getDeliveryDate());

        if (dto.getClientId() != null) {
            Client client = clientRepository.findById(dto.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            invoice.setClient(client);
        }
        return invoiceRepository.save(invoice);
    }

    public void deleteInvoice(Long id, Long userId) {
        Invoice inv = getInvoiceById(id, userId);
        invoiceRepository.delete(inv);
    }
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice getInvoiceByIdGlobal(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    public Invoice updateInvoiceGlobal(Long id, InvoiceDTO dto) {
        Invoice invoice = getInvoiceByIdGlobal(id);
        if (dto.getInvoice_number() != null) invoice.setInvoice_number(dto.getInvoice_number());
        if (dto.getDeliveryDate() != null) invoice.setDeliveryDate(dto.getDeliveryDate());
        if (dto.getClientId() != null) {
            Client client = clientRepository.findById(dto.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            invoice.setClient(client);
        }
        return invoiceRepository.save(invoice);
    }

    public void deleteInvoiceGlobal(Long id) {
        Invoice invoice = getInvoiceByIdGlobal(id);
        invoiceRepository.delete(invoice);
    }











}
