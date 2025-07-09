package com.example.EdiASN.controller;

import com.example.EdiASN.dto.ArticleClientRequest;
import com.example.EdiASN.entity.ArticleClient;
import com.example.EdiASN.security.JwtService;
import com.example.EdiASN.service.ArticleClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/edi-asn/art-clients")
public class ArticleClientController {

    private final ArticleClientService articleClientService;
    @Autowired
    private JwtService jwtService;
    public ArticleClientController(ArticleClientService articleClientService) {
        this.articleClientService = articleClientService;
    }
    private Long extractUserIdFromHeader(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtService.extractUserId(token);
    }
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(articleClientService.getAll());
    }

    // ✅ User GET all
    @GetMapping("/user")
    public ResponseEntity<?> getAllByUser(@RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(articleClientService.getAllByUserId(userId));
    }

    // ✅ Global GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(articleClientService.getById(id));
    }

    // ✅ User GET by ID
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getByIdForUser(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(articleClientService.getByIdForUser(id, userId));
    }

    // ✅ PUT (partial updates allowed using DTO)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ArticleClientRequest request) {
        return ResponseEntity.ok(articleClientService.update(id, request));
    }

    // ✅ PUT (user-secured)
    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateForUser(@PathVariable Long id, @RequestBody ArticleClientRequest request,
                                           @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(articleClientService.updateForUser(id, request, userId));
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        articleClientService.delete(id);
        return ResponseEntity.ok().build();
    }

    // ✅ DELETE (user-secured)
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteForUser(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromHeader(authHeader);
        articleClientService.deleteForUser(id, userId);
        return ResponseEntity.ok().build();
    }
    @PostMapping
    public ResponseEntity<?> createArticleClient(@RequestBody ArticleClientRequest request ,@RequestHeader("Authorization") String authHeader) {
        try {         Long userId = extractUserIdFromHeader(authHeader);

            ArticleClient saved = articleClientService.createArticleClientg(
                    request.getArticleId(),
                    request.getClientId()
                    ,userId
            );
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }

    }
    @PostMapping("/user")
    public ResponseEntity<?> createForCurrentUser(@RequestBody ArticleClientRequest request, @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = extractUserIdFromHeader(authHeader);
            ArticleClient saved = articleClientService.createArticleClient(
                    request.getArticleId(),
                    request.getClientId(),

                    userId
            );
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }


}
