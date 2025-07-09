package com.example.EdiASN.controller;

import com.example.EdiASN.dto.ArticleClientCardboardRequest;
import com.example.EdiASN.entity.ArticleClientCardboard;
import com.example.EdiASN.security.JwtService;
import com.example.EdiASN.service.ArticleClientCardboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/edi-asn/artclient-cards")
public class ArticleClientCardboardController {

    private final ArticleClientCardboardService articleclientCardboardService;
    @Autowired
    private JwtService jwtService;
    public ArticleClientCardboardController(ArticleClientCardboardService articleclientCardboardService) {
        this.articleclientCardboardService = articleclientCardboardService;
    }
    private Long extractUserIdFromHeader(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtService.extractUserId(token);
    }
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(articleclientCardboardService.getAll());
    }

    // ✅ User GET all
    @GetMapping("/user")
    public ResponseEntity<?> getAllByUser(@RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(articleclientCardboardService.getAllByUserId(userId));
    }

    // ✅ Global GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(articleclientCardboardService.getById(id));
    }

    // ✅ User GET by ID
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getByIdForUser(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(articleclientCardboardService.getByIdForUser(id, userId));
    }

    // ✅ PUT (partial updates allowed using DTO)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ArticleClientCardboardRequest request) {
        return ResponseEntity.ok(articleclientCardboardService.update(id, request));
    }

    // ✅ PUT (user-secured)
    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateForUser(@PathVariable Long id, @RequestBody ArticleClientCardboardRequest request,
                                           @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(articleclientCardboardService.updateForUser(id, request, userId));
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        articleclientCardboardService.delete(id);
        return ResponseEntity.ok().build();
    }

    // ✅ DELETE (user-secured)
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteForUser(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromHeader(authHeader);
        articleclientCardboardService.deleteForUser(id, userId);
        return ResponseEntity.ok().build();
    }
    @PostMapping
    public ResponseEntity<?> createArticleClientCardboard(@RequestBody ArticleClientCardboardRequest request ,@RequestHeader("Authorization") String authHeader) {
        try {         Long userId = extractUserIdFromHeader(authHeader);

            ArticleClientCardboard saved = articleclientCardboardService.createArticleClientCardboardg(
                    request.getArticleClientId(),
                    request.getCardboardId(),
                    request.getDefault_cardboardId(),
                    request.getQuantityPerCardboard(),userId
            );
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }

    }
    @PostMapping("/user")
    public ResponseEntity<?> createForCurrentUser(@RequestBody ArticleClientCardboardRequest request, @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = extractUserIdFromHeader(authHeader);
            ArticleClientCardboard saved = articleclientCardboardService.createArticleClientCardboard(
                    request.getArticleClientId(),
                    request.getCardboardId(),
                    request.getDefault_cardboardId(),
                    request.getQuantityPerCardboard(),
                    userId
            );
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }


}
