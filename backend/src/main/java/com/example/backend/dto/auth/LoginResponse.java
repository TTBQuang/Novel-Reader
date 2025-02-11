package com.example.backend.dto.auth;

import com.example.backend.dto.user.UserDto;

public record LoginResponse(TokenResponse token, UserDto user) { }
