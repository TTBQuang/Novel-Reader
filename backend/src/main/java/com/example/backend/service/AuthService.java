package com.example.backend.service;

import com.example.backend.dto.auth.TokenResponse;
import com.example.backend.dto.auth.UserGoogleProfile;
import com.example.backend.entity.User;
import com.example.backend.exception.UserRegistrationException;
import com.example.backend.repository.UserRepository;
import com.example.backend.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    public void registerUser(String username, String rawPassword, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new UserRegistrationException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(email)) {
            throw new UserRegistrationException("Email đã tồn tại");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setIsAdmin(false);
        user.setIsCommentBlocked(false);
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);

        userRepository.save(user);
    }

    public TokenResponse loginUser(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BadCredentialsException("Sai mật khẩu hoặc thông tin đăng nhập.");
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return new TokenResponse(accessToken, refreshToken);
    }

    public TokenResponse loginUserGoogle(UserGoogleProfile userGoogleProfile) {
        Optional<User> optionalUser = userRepository.findByEmail(userGoogleProfile.getEmail());
        User user;
        if (optionalUser.isEmpty()) {
            user = new User();
            user.setEmail(userGoogleProfile.getEmail());
            user.setUsername(userGoogleProfile.getUsername());
            user.setIsAdmin(false);
            user.setIsCommentBlocked(false);
            user.setPassword("");
            userRepository.save(user);
        } else {
            user = optionalUser.get();
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        return new TokenResponse(accessToken, refreshToken);
    }

    public TokenResponse refreshAccessToken(String refreshToken) {
        if (!jwtUtil.isRefreshToken(refreshToken) || tokenBlacklistService.isTokenBlacklisted(refreshToken)) {
            throw new JwtException("Refresh token không hợp lệ hoặc đã hết hạn");
        }

        String userId = jwtUtil.getSubject(refreshToken);

        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user"));

        String newAccessToken = jwtUtil.generateAccessToken(user);

        return new TokenResponse(newAccessToken, refreshToken);
    }
}
