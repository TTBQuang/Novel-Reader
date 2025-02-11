package com.example.backend.dto.auth;

import lombok.Getter;

@Getter
public class RegistrationRequest {
    private String username;
    private String password;
    private String email;
}

