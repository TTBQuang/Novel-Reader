package com.example.backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OtpVerificationRequest {
    private String email;
    private String otp;
}
