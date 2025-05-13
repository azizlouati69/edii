package com.example.AuthService.DTO;


import lombok.*;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String firstname;
    private String lastname;



}