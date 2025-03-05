package com.example.backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FirebaseTokenResponse {
    private String firebaseToken;
}
