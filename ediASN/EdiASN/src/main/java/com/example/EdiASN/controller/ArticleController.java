package com.example.EdiASN.controller;

import com.example.EdiASN.dto.ArticleDTO;
import com.example.EdiASN.dto.SetDefaultCardboardRequest;
import com.example.EdiASN.entity.Article;
import com.example.EdiASN.security.JwtService;
import com.example.EdiASN.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private JwtService jwtService; // To extract userId from token
    private Long extractUserIdFromHeader(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtService.extractUserId(token);
    }



    @GetMapping("/user")
    public ResponseEntity<List<Article>> getAllArticlesByUser(@RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(articleService.getAllArticlesByUser(userId));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Article> getArticleById(@RequestHeader("Authorization") String authHeader,
                                                  @PathVariable Long id) {
        Long userId = extractUserIdFromHeader(authHeader);
        Article article = articleService.getArticleById(id, userId);
        return ResponseEntity.ok(article);
    }



    @PutMapping("/user/{id}")
    public ResponseEntity<Article> updateArticle(@RequestHeader("Authorization") String authHeader,
                                                 @PathVariable Long id,
                                                 @RequestBody ArticleDTO dto) {
        Long userId = extractUserIdFromHeader(authHeader);
        Article updated = articleService.updateArticle(id, dto, userId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteArticle(@RequestHeader("Authorization") String authHeader,
                                              @PathVariable Long id) {
        Long userId = extractUserIdFromHeader(authHeader);
        articleService.deleteArticle(id, userId);
        return ResponseEntity.noContent().build();
    }

    // === ADMIN/GLOBAL ENDPOINTS ===

    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles() {
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleByIdGlobal(@PathVariable Long id) {
        Article article = articleService.getArticleByIdGlobal(id);
        return ResponseEntity.ok(article);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticleGlobal(@PathVariable Long id,
                                                       @RequestBody ArticleDTO dto) {
        Article updated = articleService.updateArticleGlobal(id, dto);
        return ResponseEntity.ok(updated);
    }
    @PostMapping("/user/search")
    public ResponseEntity<List<Article>> searchArticlesByUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ArticleDTO searchDTO) {

        Long userId = extractUserIdFromHeader(authHeader);
        List<Article> results = articleService.searchArticlesByUser(searchDTO, userId);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/search")
    public ResponseEntity<List<Article>> searchArticlesGlobal(@RequestBody ArticleDTO searchDTO) {
        List<Article> results = articleService.searchArticlesGlobal(searchDTO);
        return ResponseEntity.ok(results);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticleGlobal(@PathVariable Long id) {
        articleService.deleteArticleGlobal(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/user/default-cardboard")
    public ResponseEntity<Article> setDefaultCardboardUser(@RequestHeader("Authorization") String authHeader ,@RequestBody SetDefaultCardboardRequest request) {
        Long userId = extractUserIdFromHeader(authHeader);
        Article updated = articleService.setDefaultCardboardByUser(request.getArticleId(), request.getCardboardId(), userId);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/default-cardboard")
    public ResponseEntity<Article> setDefaultCardboardAdmin(@RequestBody SetDefaultCardboardRequest request) {
        Article updated = articleService.setDefaultCardboardGlobal(request.getArticleId(), request.getCardboardId());
        return ResponseEntity.ok(updated);
    }

    @PostMapping
    public ResponseEntity<Article> createArticle(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ArticleDTO dto) {

        Long userId = extractUserIdFromHeader(authHeader); // Same way you did in Cardboard
        return ResponseEntity.ok(articleService.createArticle(dto, userId));
    }
}
