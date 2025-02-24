package com.example.backend.security;

import com.example.backend.service.TokenBlacklistService;
import com.example.backend.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private MockHttpServletRequest request;

    @Mock
    private PrintWriter writer;

    @BeforeEach
    void setUp() throws IOException {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil, tokenBlacklistService);
        request = new MockHttpServletRequest();
        SecurityContextHolder.clearContext();
        lenient().when(response.getWriter()).thenReturn(writer);
    }

    @Test
    void whenNoAuthorizationHeader_shouldContinueChain() throws ServletException, IOException {
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void whenValidAccessToken_shouldSetAuthentication() throws ServletException, IOException {
        String token = "valid.access.token";
        request.addHeader("Authorization", "Bearer " + token);

        Claims claims = mock(Claims.class);
        when(jwtUtil.isAccessToken(token)).thenReturn(true);
        when(jwtUtil.getAllClaims(token)).thenReturn(claims);
        when(jwtUtil.getSubject(token)).thenReturn("user123");
        when(claims.get("isAdmin", Boolean.class)).thenReturn(true);
        when(claims.get("isCommentBlocked", Boolean.class)).thenReturn(false);
        when(tokenBlacklistService.isTokenBlacklisted(token)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("COMMENT")));
    }

    @Test
    void whenBlacklistedToken_shouldReturnUnauthorized() throws ServletException, IOException {
        String token = "blacklisted.token";
        request.addHeader("Authorization", "Bearer " + token);
        when(tokenBlacklistService.isTokenBlacklisted(token)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void whenNonAccessToken_shouldReturnUnauthorized() throws ServletException, IOException {
        String token = "refresh.token";
        request.addHeader("Authorization", "Bearer " + token);
        when(tokenBlacklistService.isTokenBlacklisted(token)).thenReturn(false);
        when(jwtUtil.isAccessToken(token)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(writer).write("Unauthorized");
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void whenTokenWithNoAdminRole_shouldNotHaveAdminAuthority() throws ServletException, IOException {
        String token = "valid.access.token";
        request.addHeader("Authorization", "Bearer " + token);

        Claims claims = mock(Claims.class);
        when(jwtUtil.isAccessToken(token)).thenReturn(true);
        when(jwtUtil.getAllClaims(token)).thenReturn(claims);
        when(jwtUtil.getSubject(token)).thenReturn("user123");
        when(claims.get("isAdmin", Boolean.class)).thenReturn(false);
        when(claims.get("isCommentBlocked", Boolean.class)).thenReturn(false);
        when(tokenBlacklistService.isTokenBlacklisted(token)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertAll(
                () -> assertNotNull(authentication),
                () -> assertFalse(authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))),
                () -> assertTrue(authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("COMMENT")))
        );
    }

    @Test
    void whenCommentBlocked_shouldNotHaveCommentAuthority() throws ServletException, IOException {
        String token = "valid.access.token";
        request.addHeader("Authorization", "Bearer " + token);

        Claims claims = mock(Claims.class);
        when(jwtUtil.isAccessToken(token)).thenReturn(true);
        when(jwtUtil.getAllClaims(token)).thenReturn(claims);
        when(jwtUtil.getSubject(token)).thenReturn("user123");
        when(claims.get("isAdmin", Boolean.class)).thenReturn(false);
        when(claims.get("isCommentBlocked", Boolean.class)).thenReturn(true);
        when(tokenBlacklistService.isTokenBlacklisted(token)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertAll(
                () -> assertNotNull(authentication),
                () -> assertFalse(authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))),
                () -> assertFalse(authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("COMMENT")))
        );
    }
}
