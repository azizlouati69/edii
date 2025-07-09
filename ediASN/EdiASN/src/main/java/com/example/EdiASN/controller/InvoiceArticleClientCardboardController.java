package com.example.EdiASN.controller;


import com.example.EdiASN.dto.InvoiceArticleclientcardboardRequest;
import com.example.EdiASN.entity.InvoiceArticleClientCardboard;
import com.example.EdiASN.security.JwtService;
import com.example.EdiASN.service.InvoiceArticleClientCardboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/edi-asn/inv-art-client-cards")
public class InvoiceArticleClientCardboardController {

    private final InvoiceArticleClientCardboardService InvoiceArticleClientCardboardService;
    @Autowired
    private JwtService jwtService;
    public InvoiceArticleClientCardboardController(InvoiceArticleClientCardboardService InvoiceArticleClientCardboardService) {
        this.InvoiceArticleClientCardboardService = InvoiceArticleClientCardboardService;
    }
    private Long extractUserIdFromHeader(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtService.extractUserId(token);
    }
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(InvoiceArticleClientCardboardService.getAll());
    }

    // ✅ User GET all
    @GetMapping("/user")
    public ResponseEntity<?> getAllByUser(@RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(InvoiceArticleClientCardboardService.getAllByUserId(userId));
    }

    // ✅ Global GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(InvoiceArticleClientCardboardService.getById(id));
    }

    // ✅ User GET by ID
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getByIdForUser(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(InvoiceArticleClientCardboardService.getByIdForUser(id, userId));
    }

    // ✅ PUT (partial updates allowed using DTO)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody InvoiceArticleclientcardboardRequest request) {
        return ResponseEntity.ok(InvoiceArticleClientCardboardService.update(id, request));
    }

    // ✅ PUT (user-secured)
    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateForUser(@PathVariable Long id, @RequestBody InvoiceArticleclientcardboardRequest request,
                                           @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(InvoiceArticleClientCardboardService.updateForUser(id, request, userId));
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        InvoiceArticleClientCardboardService.delete(id);
        return ResponseEntity.ok().build();
    }

    // ✅ DELETE (user-secured)
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteForUser(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromHeader(authHeader);
        InvoiceArticleClientCardboardService.deleteForUser(id, userId);
        return ResponseEntity.ok().build();
    }
    @PostMapping
    public ResponseEntity<?> createInvoiceArticleClientCardboard(@RequestBody InvoiceArticleclientcardboardRequest request ,@RequestHeader("Authorization") String authHeader) {
        try {         Long userId = extractUserIdFromHeader(authHeader);

            InvoiceArticleClientCardboard saved = InvoiceArticleClientCardboardService.createInvoiceArticleClientCardboardg(
                    request.getArticleclientcardboardId(),
                    request.getInvoiceId(),
                    request.getTotalQuantity(),userId
            );
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }

    }
    @PostMapping("/user")
    public ResponseEntity<?> createForCurrentUser(@RequestBody InvoiceArticleclientcardboardRequest request, @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = extractUserIdFromHeader(authHeader);
            InvoiceArticleClientCardboard saved = InvoiceArticleClientCardboardService.createInvoiceArticleClientCardboard(
                    request.getArticleclientcardboardId(),
                    request.getInvoiceId(),
                    request.getTotalQuantity(),userId
            );
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }


}
