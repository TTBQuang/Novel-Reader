package com.example.backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordResetConfirmRequest {
    private String email;
    private String otp;
    private String newPassword;
}
