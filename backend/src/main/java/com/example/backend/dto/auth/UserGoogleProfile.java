package com.example.backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserGoogleProfile {
    private String email;
    private String username;
}
