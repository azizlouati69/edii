package com.example.AuthService.DTO;


import lombok.*;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String firstname;
    private String lastname;
    private String phone;


}