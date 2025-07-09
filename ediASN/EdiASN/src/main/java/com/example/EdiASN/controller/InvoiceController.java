package com.example.EdiASN.controller;


import com.example.EdiASN.dto.InvoiceDTO;
import com.example.EdiASN.entity.Invoice;
import com.example.EdiASN.security.JwtService;
import com.example.EdiASN.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/edi-asn/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private JwtService jwtService; // To extract userId from token
    private Long extractUserIdFromHeader(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtService.extractUserId(token);
    }


    @GetMapping("/user")
    public ResponseEntity<List<Invoice>> getAllInvoicesByUser(@RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(invoiceService.getAllInvoicesByUser(userId));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@RequestHeader("Authorization") String authHeader,
                                                  @PathVariable Long id) {
        Long userId = extractUserIdFromHeader(authHeader);
        Invoice invoice = invoiceService.getInvoiceById(id, userId);
        return ResponseEntity.ok(invoice);
    }



    @PutMapping("/user/{id}")
    public ResponseEntity<Invoice> updateInvoice(@RequestHeader("Authorization") String authHeader,
                                                 @PathVariable Long id,
                                                 @RequestBody InvoiceDTO dto) {
        Long userId = extractUserIdFromHeader(authHeader);
        Invoice updated = invoiceService.updateInvoice(id, dto, userId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteInvoice(@RequestHeader("Authorization") String authHeader,
                                              @PathVariable Long id) {
        Long userId = extractUserIdFromHeader(authHeader);
        invoiceService.deleteInvoice(id, userId);
        return ResponseEntity.noContent().build();
    }

    // === ADMIN/GLOBAL ENDPOINTS ===

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceByIdGlobal(@PathVariable Long id) {
        Invoice invoice = invoiceService.getInvoiceByIdGlobal(id);
        return ResponseEntity.ok(invoice);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Invoice> updateInvoiceGlobal(@PathVariable Long id,
                                                       @RequestBody InvoiceDTO dto) {
        Invoice updated = invoiceService.updateInvoiceGlobal(id, dto);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoiceGlobal(@PathVariable Long id) {
        invoiceService.deleteInvoiceGlobal(id);
        return ResponseEntity.noContent().build();
    }













    @PostMapping
    public ResponseEntity<Invoice> createInvoice(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody InvoiceDTO dto) {

        Long userId = extractUserIdFromHeader(authHeader); // Same way you did in Cardboard
        return ResponseEntity.ok(invoiceService.createInvoice(dto, userId));
    }
}
