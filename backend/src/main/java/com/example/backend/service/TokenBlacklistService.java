package com.example.backend.service;

import com.example.backend.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class TokenBlacklistService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;

    public void blacklistToken(String token) {
        Claims claims = jwtUtil.getAllClaims(token);
        Date expiration = claims.getExpiration();

        // Calculate time to live
        long ttl = expiration.getTime() - System.currentTimeMillis();
        if (ttl > 0) {
            // Set token to blacklist with time to live
            redisTemplate.opsForValue().set("blacklist:" + token, "true", ttl, TimeUnit.MILLISECONDS);
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.toString().equals(redisTemplate.opsForValue().get("blacklist:" + token));
    }
}
