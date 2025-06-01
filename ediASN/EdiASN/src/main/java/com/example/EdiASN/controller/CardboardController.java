package com.example.EdiASN.controller;

import com.example.EdiASN.dto.CardboardDTO;
import com.example.EdiASN.entity.Cardboard;
import com.example.EdiASN.security.JwtService;
import com.example.EdiASN.service.CardboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cardboards")
public class CardboardController {

    @Autowired
    private CardboardService cardboardService;

    @Autowired
    private JwtService jwtService;

    // ==== USER AUTHENTICATED ENDPOINTS ====

    private Long extractUserIdFromHeader(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtService.extractUserId(token);
    }

    @PostMapping
    public ResponseEntity<Cardboard> createCardboard(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CardboardDTO dto) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(cardboardService.createCardboard(dto, userId));
    }

    @GetMapping("/user")
    public ResponseEntity<List<Cardboard>> getAllCardboardsByUser(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(cardboardService.getAllCardboardsByUser(userId));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Cardboard> getCardboardById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(cardboardService.getCardboardById(id, userId));
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<Cardboard> updateCardboard(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody CardboardDTO dto) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(cardboardService.updateCardboard(id, dto, userId));
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteCardboard(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        Long userId = extractUserIdFromHeader(authHeader);
        cardboardService.deleteCardboard(id, userId);
        return ResponseEntity.noContent().build();
    }

    // ==== ADMIN / GLOBAL ENDPOINTS ====

    @GetMapping
    public ResponseEntity<List<Cardboard>> getAllCardboards() {
        return ResponseEntity.ok(cardboardService.getAllCardboards());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cardboard> getCardboardByIdGlobal(@PathVariable Long id) {
        return ResponseEntity.ok(cardboardService.getCardboardByIdGlobal(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cardboard> updateCardboardGlobal(
            @PathVariable Long id,
            @RequestBody CardboardDTO dto) {
        return ResponseEntity.ok(cardboardService.updateCardboardGlobal(id, dto));
    }
    @PostMapping("/search")
    public ResponseEntity<List<Cardboard>> searchCardboards(@RequestBody CardboardDTO searchDTO) {
        List<Cardboard> results = cardboardService.searchCardboards(searchDTO);
        return ResponseEntity.ok(results);
    }
    @PostMapping("/search/user")
    public ResponseEntity<List<Cardboard>> searchCardboardsByUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CardboardDTO searchDTO) {

        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        List<Cardboard> results = cardboardService.searchCardboards(searchDTO, userId);
        return ResponseEntity.ok(results);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCardboardGlobal(@PathVariable Long id) {
        cardboardService.deleteCardboardGlobal(id);
        return ResponseEntity.noContent().build();
    }
}
