package com.example.backend.dto.auth;

import lombok.Getter;

@Getter
public class RefreshTokenRequest {
    private String refreshToken;
}
