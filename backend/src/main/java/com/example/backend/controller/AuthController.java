package com.example.backend.controller;

import com.example.backend.dto.auth.*;
import com.example.backend.dto.user.UserDto;
import com.example.backend.service.TokenBlacklistService;
import com.example.backend.service.AuthService;
import com.example.backend.service.UserService;
import com.example.backend.util.JwtUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegistrationRequest request) {
        authService.registerUser(request.getUsername(), request.getPassword(), request.getEmail());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest request) {
        TokenResponse tokens = authService.loginUser(request.getUsername(), request.getPassword());

        long userId = Long.parseLong(jwtUtil.getSubject(tokens.getAccessToken()));
        UserDto userDto = userService.getUserById(userId);

        return ResponseEntity.ok(new LoginResponse(tokens, userDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshAccessToken(@RequestHeader("Authorization") String authHeader) {
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            throw new IllegalArgumentException("Authorization header không hợp lệ");
//        }
//        String token = authHeader.substring(7);
        TokenResponse tokens = authService.refreshAccessToken(authHeader);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header không hợp lệ");
        }
        String token = authHeader.substring(7);
        tokenBlacklistService.blacklistToken(token);
        return ResponseEntity.ok().build();
    }
}
