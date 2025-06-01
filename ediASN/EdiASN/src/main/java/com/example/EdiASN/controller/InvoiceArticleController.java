package com.example.EdiASN.controller;


import com.example.EdiASN.dto.InvoiceArticleRequest;
import com.example.EdiASN.entity.InvoiceArticle;
import com.example.EdiASN.security.JwtService;
import com.example.EdiASN.service.InvoiceArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inv-arts")
public class InvoiceArticleController {

    private final InvoiceArticleService invoiceArticleService;
    @Autowired
    private JwtService jwtService;
    public InvoiceArticleController(InvoiceArticleService invoiceArticleService) {
        this.invoiceArticleService = invoiceArticleService;
    }
    private Long extractUserIdFromHeader(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtService.extractUserId(token);
    }
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(invoiceArticleService.getAll());
    }

    // ✅ User GET all
    @GetMapping("/user")
    public ResponseEntity<?> getAllByUser(@RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(invoiceArticleService.getAllByUserId(userId));
    }

    // ✅ Global GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceArticleService.getById(id));
    }

    // ✅ User GET by ID
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getByIdForUser(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(invoiceArticleService.getByIdForUser(id, userId));
    }

    // ✅ PUT (partial updates allowed using DTO)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody InvoiceArticleRequest request) {
        return ResponseEntity.ok(invoiceArticleService.update(id, request));
    }

    // ✅ PUT (user-secured)
    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateForUser(@PathVariable Long id, @RequestBody InvoiceArticleRequest request,
                                           @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(invoiceArticleService.updateForUser(id, request, userId));
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        invoiceArticleService.delete(id);
        return ResponseEntity.ok().build();
    }

    // ✅ DELETE (user-secured)
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteForUser(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromHeader(authHeader);
        invoiceArticleService.deleteForUser(id, userId);
        return ResponseEntity.ok().build();
    }
    @PostMapping
    public ResponseEntity<?> createInvoiceArticle(@RequestBody InvoiceArticleRequest request ,@RequestHeader("Authorization") String authHeader) {
        try {         Long userId = extractUserIdFromHeader(authHeader);

            InvoiceArticle saved = invoiceArticleService.createInvoiceArticleg(
                    request.getArticleId(),
                    request.getInvoiceId(),
                    request.getTotalQuantity(),userId
            );
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }

    }
    @PostMapping("/user")
    public ResponseEntity<?> createForCurrentUser(@RequestBody InvoiceArticleRequest request, @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = extractUserIdFromHeader(authHeader);
            InvoiceArticle saved = invoiceArticleService.createInvoiceArticle(
                    request.getArticleId(),
                    request.getInvoiceId(),
                    request.getTotalQuantity(),userId
            );
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }


}
