package com.example.EdiASN.controller;

import com.example.EdiASN.dto.ClientDTO;
import com.example.EdiASN.entity.Client;
import com.example.EdiASN.security.JwtService;
import com.example.EdiASN.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientService ClientService;

    @Autowired
    private JwtService jwtService;

    // ==== USER AUTHENTICATED ENDPOINTS ====

    private Long extractUserIdFromHeader(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtService.extractUserId(token);
    }

    @PostMapping
    public ResponseEntity<Client> createClient(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ClientDTO dto) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(ClientService.createClient(dto, userId));
    }

    @GetMapping("/user")
    public ResponseEntity<List<Client>> getAllClientsByUser(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(ClientService.getAllClientsByUser(userId));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Client> getClientById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(ClientService.getClientById(id, userId));
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<Client> updateClient(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody ClientDTO dto) {
        Long userId = extractUserIdFromHeader(authHeader);
        return ResponseEntity.ok(ClientService.updateClient(id, dto, userId));
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteClient(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        Long userId = extractUserIdFromHeader(authHeader);
        ClientService.deleteClient(id, userId);
        return ResponseEntity.noContent().build();
    }

    // ==== ADMIN / GLOBAL ENDPOINTS ====

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        return ResponseEntity.ok(ClientService.getAllClients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientByIdGlobal(@PathVariable Long id) {
        return ResponseEntity.ok(ClientService.getClientByIdGlobal(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClientGlobal(
            @PathVariable Long id,
            @RequestBody ClientDTO dto) {
        return ResponseEntity.ok(ClientService.updateClientGlobal(id, dto));
    }
    @PostMapping("/search")
    public ResponseEntity<List<Client>> searchClients(@RequestBody ClientDTO searchDTO) {
        List<Client> results = ClientService.searchClients(searchDTO);
        return ResponseEntity.ok(results);
    }
    @PostMapping("/search/user")
    public ResponseEntity<List<Client>> searchClientsByUser(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ClientDTO searchDTO) {

        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtService.extractUserId(token);
        List<Client> results = ClientService.searchClients(searchDTO, userId);
        return ResponseEntity.ok(results);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClientGlobal(@PathVariable Long id) {
        ClientService.deleteClientGlobal(id);
        return ResponseEntity.noContent().build();
    }
}
