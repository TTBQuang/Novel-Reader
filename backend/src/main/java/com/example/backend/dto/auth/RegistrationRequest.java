package com.example.backend.dto.auth;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegistrationRequest {
    private String username;
    private String password;

    @Email(message = "Địa chỉ email không hợp lệ")
    private String email;
}

