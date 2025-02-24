package com.example.backend.controller;

import com.example.backend.dto.auth.*;
import com.example.backend.dto.user.UserDto;
import com.example.backend.exception.GlobalExceptionHandler;
import com.example.backend.service.AuthService;
import com.example.backend.service.TokenBlacklistService;
import com.example.backend.service.UserService;
import com.example.backend.util.GoogleTokenVerifierUtil;
import com.example.backend.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private GoogleTokenVerifierUtil googleTokenVerifierUtil;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("Registration Tests")
    class RegistrationTests {

        @Test
        void whenRegistrationInitiated_shouldReturn200() throws Exception {
            RegistrationRequest request = new RegistrationRequest("testuser", "password123", "test@example.com");
            doNothing().when(authService).initiateRegistration(request.getUsername(), request.getPassword(), request.getEmail());

            mockMvc.perform(post("/auth/register/initiate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(authService).initiateRegistration(request.getUsername(), request.getPassword(), request.getEmail());
        }

        @Test
        void whenInvalidEmail_shouldReturn400() throws Exception {
            RegistrationRequest request = new RegistrationRequest("testuser", "password123", "invalid-email");

            mockMvc.perform(post("/auth/register/initiate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(authService, never()).initiateRegistration(anyString(), anyString(), anyString());
        }

        @Test
        void whenOtpVerified_shouldReturn200() throws Exception {
            OtpVerificationRequest request = new OtpVerificationRequest("test@example.com", "123456");
            doNothing().when(authService).verifyOtpAndRegisterUser(request.getEmail(), request.getOtp());

            mockMvc.perform(post("/auth/register/verify")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(authService).verifyOtpAndRegisterUser(request.getEmail(), request.getOtp());
        }

        @Test
        void whenInvalidOtp_shouldReturn400() throws Exception {
            OtpVerificationRequest request = new OtpVerificationRequest("test@example.com", "invalid");
            doThrow(new IllegalArgumentException("Mã OTP không hợp lệ"))
                    .when(authService).verifyOtpAndRegisterUser(request.getEmail(), request.getOtp());

            mockMvc.perform(post("/auth/register/verify")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(authService).verifyOtpAndRegisterUser(request.getEmail(), request.getOtp());
        }
    }

    @Nested
    @DisplayName("Password Reset Tests")
    class PasswordResetTests {

        @Test
        void whenPasswordResetInitiated_shouldReturn200() throws Exception {
            PasswordResetRequest request = new PasswordResetRequest("test@example.com");
            doNothing().when(authService).initiatePasswordReset(request.getEmail());

            mockMvc.perform(post("/auth/password/reset/initiate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(authService).initiatePasswordReset(request.getEmail());
        }

        @Test
        void whenInvalidEmailForReset_shouldReturn400() throws Exception {
            PasswordResetRequest request = new PasswordResetRequest("invalid-email");

            mockMvc.perform(post("/auth/password/reset/initiate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(authService, never()).initiatePasswordReset(anyString());
        }

        @Test
        void whenPasswordResetConfirmed_shouldReturn200() throws Exception {
            PasswordResetConfirmRequest request = new PasswordResetConfirmRequest("test@example.com", "123456", "newPassword123");
            doNothing().when(authService).verifyOtpAndResetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());

            mockMvc.perform(post("/auth/password/reset/confirm")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(authService).verifyOtpAndResetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
        }

        @Test
        void whenInvalidOtpForReset_shouldReturn400() throws Exception {
            PasswordResetConfirmRequest request = new PasswordResetConfirmRequest("test@example.com", "invalid", "newPassword123");
            doThrow(new IllegalArgumentException("Mã OTP không hợp lệ"))
                    .when(authService).verifyOtpAndResetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());

            mockMvc.perform(post("/auth/password/reset/confirm")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(authService).verifyOtpAndResetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        void whenValidCredentials_shouldReturnTokensAndUserData() throws Exception {
            LoginRequest request = new LoginRequest("testuser", "password123");
            TokenResponse tokens = new TokenResponse("access-token", "refresh-token");
            UserDto userDto = createUserDto();

            when(authService.loginUser(request.getUsername(), request.getPassword())).thenReturn(tokens);
            when(jwtUtil.getSubject(tokens.getAccessToken())).thenReturn("1");
            when(userService.getUserById(1L)).thenReturn(userDto);

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token.accessToken").value("access-token"))
                    .andExpect(jsonPath("$.token.refreshToken").value("refresh-token"))
                    .andExpect(jsonPath("$.user.id").value(1))
                    .andExpect(jsonPath("$.user.email").value("test@example.com"))
                    .andExpect(jsonPath("$.user.username").value("testuser"));

            verify(authService).loginUser(request.getUsername(), request.getPassword());
            verify(jwtUtil).getSubject(tokens.getAccessToken());
            verify(userService).getUserById(1L);
        }

        @Test
        void whenInvalidCredentials_shouldReturn401() throws Exception {
            LoginRequest request = new LoginRequest("testuser", "wrongpassword");
            when(authService.loginUser(request.getUsername(), request.getPassword()))
                    .thenThrow(new BadCredentialsException("Tên đăng nhập hoặc mật khẩu không đúng"));

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());

            verify(authService).loginUser(request.getUsername(), request.getPassword());
        }
    }

    @Nested
    @DisplayName("Google Login Tests")
    class GoogleLoginTests {

        @Test
        void whenValidGoogleToken_shouldReturnTokensAndUserData() throws Exception {
            LoginGoogleRequest request = new LoginGoogleRequest();
            request.setIdToken("google-id-token");

            UserGoogleProfile googleProfile = new UserGoogleProfile("test@example.com", "testuser");
            TokenResponse tokens = new TokenResponse("access-token", "refresh-token");
            UserDto userDto = createUserDto();

            when(googleTokenVerifierUtil.getUserInfoFromIdToken(request.getIdToken())).thenReturn(googleProfile);
            when(authService.loginUserGoogle(googleProfile)).thenReturn(tokens);
            when(jwtUtil.getSubject(tokens.getAccessToken())).thenReturn("1");
            when(userService.getUserById(1L)).thenReturn(userDto);

            mockMvc.perform(post("/auth/login-google")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token.accessToken").value("access-token"))
                    .andExpect(jsonPath("$.token.refreshToken").value("refresh-token"))
                    .andExpect(jsonPath("$.user.id").value(1))
                    .andExpect(jsonPath("$.user.email").value("test@example.com"));

            verify(googleTokenVerifierUtil).getUserInfoFromIdToken(request.getIdToken());
            verify(authService).loginUserGoogle(googleProfile);
            verify(jwtUtil).getSubject(tokens.getAccessToken());
            verify(userService).getUserById(1L);
        }

        @Test
        void whenInvalidGoogleToken_shouldReturn400() throws Exception {
            LoginGoogleRequest request = new LoginGoogleRequest();
            request.setIdToken("invalid-token");

            when(googleTokenVerifierUtil.getUserInfoFromIdToken(request.getIdToken()))
                    .thenThrow(new IllegalArgumentException("Token không hợp lệ"));

            mockMvc.perform(post("/auth/login-google")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(googleTokenVerifierUtil).getUserInfoFromIdToken(request.getIdToken());
        }
    }

    @Nested
    @DisplayName("Refresh Token Tests")
    class RefreshTokenTests {

        @Test
        void whenValidRefreshToken_shouldReturnNewTokens() throws Exception {
            RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
            TokenResponse newTokens = new TokenResponse("new-access-token", "new-refresh-token");
            UserDto userDto = createUserDto();

            when(authService.refreshAccessToken(request.getRefreshToken())).thenReturn(newTokens);
            when(jwtUtil.getSubject(newTokens.getAccessToken())).thenReturn("1");
            when(userService.getUserById(1L)).thenReturn(userDto);

            mockMvc.perform(post("/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token.accessToken").value("new-access-token"))
                    .andExpect(jsonPath("$.token.refreshToken").value("new-refresh-token"))
                    .andExpect(jsonPath("$.user.id").value(1));

            verify(authService).refreshAccessToken(request.getRefreshToken());
            verify(jwtUtil).getSubject(newTokens.getAccessToken());
            verify(userService).getUserById(1L);
        }

        @Test
        void whenInvalidRefreshToken_shouldReturn401() throws Exception {
            RefreshTokenRequest request = new RefreshTokenRequest("invalid-refresh-token");
            when(authService.refreshAccessToken(request.getRefreshToken()))
                    .thenThrow(new JwtException("Token không hợp lệ hoặc đã hết hạn"));

            mockMvc.perform(post("/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());

            verify(authService).refreshAccessToken(request.getRefreshToken());
        }
    }

    @Nested
    @DisplayName("Logout Tests")
    class LogoutTests {

        @Test
        void whenValidTokens_shouldBlacklistAndReturn200() throws Exception {
            RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");

            MockHttpServletRequest httpRequest = new MockHttpServletRequest();
            httpRequest.addHeader("Authorization", "Bearer valid-access-token");
            RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpRequest));

            doNothing().when(tokenBlacklistService).blacklistToken("valid-access-token");
            doNothing().when(tokenBlacklistService).blacklistToken("valid-refresh-token");

            mockMvc.perform(post("/auth/logout")
                            .header("Authorization", "Bearer valid-access-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(tokenBlacklistService).blacklistToken("valid-access-token");
            verify(tokenBlacklistService).blacklistToken("valid-refresh-token");
        }

        @Test
        void whenMissingAuthHeader_shouldReturn400() throws Exception {
            RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");

            mockMvc.perform(post("/auth/logout")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(tokenBlacklistService, never()).blacklistToken(anyString());
        }

        @Test
        void whenInvalidAuthHeader_shouldReturn400() throws Exception {
            RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");

            mockMvc.perform(post("/auth/logout")
                            .header("Authorization", "InvalidHeader")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(tokenBlacklistService, never()).blacklistToken(anyString());
        }
    }

    private UserDto createUserDto() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setEmail("test@example.com");
        dto.setUsername("testuser");
        dto.setAdmin(false);
        dto.setCommentBlocked(false);
        return dto;
    }
}
