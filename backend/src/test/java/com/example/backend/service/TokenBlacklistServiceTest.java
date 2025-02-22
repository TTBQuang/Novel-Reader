package com.example.backend.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.backend.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void testBlacklistToken_validToken() {
        String token = "token";
        Claims claims = mock(Claims.class);
        when(jwtUtil.getAllClaims(token)).thenReturn(claims);
        long futureTime = System.currentTimeMillis() + 60000;
        when(claims.getExpiration()).thenReturn(new Date(futureTime));

        tokenBlacklistService.blacklistToken(token);

        verify(valueOperations).set(eq("blacklist:" + token), eq("true"), anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    void testBlacklistToken_expiredToken() {
        String token = "expired-token";
        Claims claims = mock(Claims.class);
        when(jwtUtil.getAllClaims(token)).thenReturn(claims);
        long pastTime = System.currentTimeMillis() - 60000;
        when(claims.getExpiration()).thenReturn(new Date(pastTime));

        tokenBlacklistService.blacklistToken(token);

        verify(valueOperations, never()).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
    }

    @Test
    void testIsTokenBlacklisted_true() {
        String token = "test-token";
        String key = "blacklist:" + token;
        when(valueOperations.get(key)).thenReturn("true");

        boolean result = tokenBlacklistService.isTokenBlacklisted(token);

        assertTrue(result);
    }

    @Test
    void testIsTokenBlacklisted_false() {
        String token = "test-token";
        String key = "blacklist:" + token;
        when(valueOperations.get(key)).thenReturn(null);

        boolean result = tokenBlacklistService.isTokenBlacklisted(token);

        assertFalse(result);
    }
}

