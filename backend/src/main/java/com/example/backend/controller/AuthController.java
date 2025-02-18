package com.example.backend.controller;

import com.example.backend.dto.auth.*;
import com.example.backend.dto.user.UserDto;
import com.example.backend.service.TokenBlacklistService;
import com.example.backend.service.AuthService;
import com.example.backend.service.UserService;
import com.example.backend.util.GoogleTokenVerifierUtil;
import com.example.backend.util.JwtUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
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
    private final GoogleTokenVerifierUtil GoogleTokenVerifierUtil;

    @PostMapping("/register/initiate")
    public ResponseEntity<String> initiateRegistration(@RequestBody RegistrationRequest request) {
        authService.initiateRegistration(request.getUsername(), request.getPassword(), request.getEmail());
        return ResponseEntity.ok("Mã xác thực đã được gửi đến email của bạn");
    }

    @PostMapping("/register/verify")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerificationRequest request) {
        authService.verifyOtpAndRegisterUser(request.getEmail(), request.getOtp());
        return ResponseEntity.ok("Đăng ký thành công");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest request) {
        TokenResponse tokens = authService.loginUser(request.getUsername(), request.getPassword());

        long userId = Long.parseLong(jwtUtil.getSubject(tokens.getAccessToken()));
        UserDto userDto = userService.getUserById(userId);

        return ResponseEntity.ok(new LoginResponse(tokens, userDto));
    }

    @PostMapping("/login-google")
    public ResponseEntity<LoginResponse> loginUserGoogle(@RequestBody LoginGoogleRequest request) {
        UserGoogleProfile userGoogleProfile = GoogleTokenVerifierUtil.getUserInfoFromIdToken(request.getIdToken());
        TokenResponse tokens = authService.loginUserGoogle(userGoogleProfile);

        long userId = Long.parseLong(jwtUtil.getSubject(tokens.getAccessToken()));
        UserDto userDto = userService.getUserById(userId);

        return ResponseEntity.ok(new LoginResponse(tokens, userDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        TokenResponse tokens = authService.refreshAccessToken(refreshToken);

        long userId = Long.parseLong(jwtUtil.getSubject(tokens.getAccessToken()));
        UserDto userDto = userService.getUserById(userId);

        return ResponseEntity.ok(new LoginResponse(tokens, userDto));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, @RequestBody RefreshTokenRequest refreshTokenRequest) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header không hợp lệ");
        }

        String accessToken = authHeader.substring(7);
        tokenBlacklistService.blacklistToken(accessToken);
        tokenBlacklistService.blacklistToken(refreshTokenRequest.getRefreshToken());

        return ResponseEntity.ok().build();
    }
}
