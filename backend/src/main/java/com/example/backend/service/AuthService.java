package com.example.backend.service;

import com.example.backend.dto.auth.TokenResponse;
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

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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

    public TokenResponse refreshAccessToken(String refreshToken) {
        if (jwtUtil.isRefreshToken(refreshToken)) {
            throw new JwtException("Refresh token không hợp lệ hoặc đã hết hạn");
        }

        String userId = jwtUtil.getSubject(refreshToken);

        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user"));

        String newAccessToken = jwtUtil.generateAccessToken(user);

        return new TokenResponse(newAccessToken, refreshToken);
    }
}
