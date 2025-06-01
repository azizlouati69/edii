package com.example.EdiASN.controller;

import com.example.EdiASN.dto.ArticleCardboardRequest;
import com.example.EdiASN.entity.ArticleCardboard;
import com.example.EdiASN.security.JwtService;
import com.example.EdiASN.service.ArticleCardboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/art-cards")
public class ArticleCardboardController {

    private final ArticleCardboardService articleCardboardService;
    @Autowired
    private JwtService jwtService;
    public ArticleCardboardController(ArticleCardboardService articleCardboardService) {
        this.articleCardboardService = articleCardboardService;
    }
    private Long extractUserIdFromHeader(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtService.extractUserId(token);
    }
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(articleCardboardService.getAll());
    }

    // ✅ User GET all
    @GetMapping("/user")
    public ResponseEntity<?> getAllByUser(@RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(articleCardboardService.getAllByUserId(userId));
    }

    // ✅ Global GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(articleCardboardService.getById(id));
    }

    // ✅ User GET by ID
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getByIdForUser(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(articleCardboardService.getByIdForUser(id, userId));
    }

    // ✅ PUT (partial updates allowed using DTO)
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ArticleCardboardRequest request) {
        return ResponseEntity.ok(articleCardboardService.update(id, request));
    }

    // ✅ PUT (user-secured)
    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateForUser(@PathVariable Long id, @RequestBody ArticleCardboardRequest request,
                                           @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(articleCardboardService.updateForUser(id, request, userId));
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        articleCardboardService.delete(id);
        return ResponseEntity.ok().build();
    }

    // ✅ DELETE (user-secured)
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteForUser(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromHeader(authHeader);
        articleCardboardService.deleteForUser(id, userId);
        return ResponseEntity.ok().build();
    }
    @PostMapping
    public ResponseEntity<?> createArticleCardboard(@RequestBody ArticleCardboardRequest request ,@RequestHeader("Authorization") String authHeader) {
        try {         Long userId = extractUserIdFromHeader(authHeader);

            ArticleCardboard saved = articleCardboardService.createArticleCardboardg(
                    request.getArticleId(),
                    request.getCardboardId(),
                    request.getQuantityPerCardboard(),userId
            );
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }

    }
    @PostMapping("/user")
    public ResponseEntity<?> createForCurrentUser(@RequestBody ArticleCardboardRequest request, @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = extractUserIdFromHeader(authHeader);
            ArticleCardboard saved = articleCardboardService.createArticleCardboard(
                    request.getArticleId(),
                    request.getCardboardId(),
                    request.getQuantityPerCardboard(),
                    userId
            );
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }


}
