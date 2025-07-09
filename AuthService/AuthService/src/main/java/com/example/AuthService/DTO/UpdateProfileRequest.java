package com.example.AuthService.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateProfileRequest {
    private String phone;
    private String firstname;
    private String lastname;
    private String bio;
    private byte[] picture;
    private String sex;
    private LocalDate birthday;// optional
}