package com.example.backend.dto.auth;

import com.example.backend.dto.user.UserBasicInfoDto;

public record LoginResponse(TokenResponse token, UserBasicInfoDto user) { }
