package com.example.backend.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginGoogleRequest {
    private String idToken;
}
