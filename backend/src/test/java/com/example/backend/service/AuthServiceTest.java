package com.example.backend.service;

import com.example.backend.dto.auth.TokenResponse;
import com.example.backend.dto.auth.UserGoogleProfile;
import com.example.backend.entity.OtpVerification;
import com.example.backend.entity.User;
import com.example.backend.enums.OtpType;
import com.example.backend.exception.InvalidOtpException;
import com.example.backend.exception.PasswordResetException;
import com.example.backend.exception.UserRegistrationException;
import com.example.backend.repository.OtpVerificationRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private OtpVerificationRepository otpVerificationRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    private User user;
    private OtpVerification otpVerification;
    private final String testEmail = "test@email.com";
    private final String testUsername = "testUser";
    private final String testPassword = "securePassword";
    private final String encodedPassword = "encodedPassword";
    private final String testOtp = "123456";

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail(testEmail);
        user.setUsername(testUsername);
        user.setPassword(encodedPassword);
        user.setIsAdmin(false);
        user.setIsCommentBlocked(false);

        otpVerification = new OtpVerification();
        otpVerification.setEmail(testEmail);
        otpVerification.setOtp(testOtp);
        otpVerification.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        otpVerification.setUsername(testUsername);
        otpVerification.setPassword(encodedPassword);
        otpVerification.setOtpType(OtpType.REGISTRATION);
        otpVerification.setUsed(false);
        otpVerification.setAttemptCount(0);
    }

    @Nested
    @DisplayName("initiateRegistration Tests")
    class InitiateRegistrationTests {

        @Test
        void whenUsernameExists_ShouldThrowException() {
            when(userRepository.existsByUsername(testUsername)).thenReturn(true);

            UserRegistrationException exception = assertThrows(UserRegistrationException.class,
                    () -> authService.initiateRegistration(testUsername, testPassword, testEmail));

            assertEquals("Username đã tồn tại", exception.getMessage());
            verify(userRepository).existsByUsername(testUsername);
            verify(userRepository, never()).existsByEmail(anyString());
            verify(otpVerificationRepository, never()).save(any());
            verify(emailService, never()).sendOtpEmail(anyString(), anyString(), any());
        }

        @Test
        void whenEmailExists_ShouldThrowException() {
            when(userRepository.existsByUsername(testUsername)).thenReturn(false);
            when(userRepository.existsByEmail(testEmail)).thenReturn(true);

            UserRegistrationException exception = assertThrows(UserRegistrationException.class,
                    () -> authService.initiateRegistration(testUsername, testPassword, testEmail));

            assertEquals("Email đã tồn tại", exception.getMessage());
            verify(userRepository).existsByUsername(testUsername);
            verify(userRepository).existsByEmail(testEmail);
            verify(otpVerificationRepository, never()).save(any());
            verify(emailService, never()).sendOtpEmail(anyString(), anyString(), any());
        }

        @Test
        void whenUserIsNew_ShouldSaveOtpAndSendEmail() {
            when(userRepository.existsByUsername(testUsername)).thenReturn(false);
            when(userRepository.existsByEmail(testEmail)).thenReturn(false);
            when(passwordEncoder.encode(testPassword)).thenReturn(encodedPassword);
            when(otpVerificationRepository.findByEmailAndOtpType(testEmail, OtpType.REGISTRATION))
                    .thenReturn(Optional.empty());

            authService.initiateRegistration(testUsername, testPassword, testEmail);

            ArgumentCaptor<OtpVerification> otpCaptor = ArgumentCaptor.forClass(OtpVerification.class);
            verify(otpVerificationRepository).save(otpCaptor.capture());
            OtpVerification savedOtp = otpCaptor.getValue();

            assertEquals(testEmail, savedOtp.getEmail());
            assertEquals(testUsername, savedOtp.getUsername());
            assertEquals(encodedPassword, savedOtp.getPassword());
            assertEquals(OtpType.REGISTRATION, savedOtp.getOtpType());
            assertNotNull(savedOtp.getOtp());
            assertNotNull(savedOtp.getExpiryDate());

            verify(emailService).sendOtpEmail(eq(testEmail), anyString(), eq(OtpType.REGISTRATION));
        }

        @Test
        void whenPreviousOtpExists_ShouldDeleteAndCreateNew() {
            when(userRepository.existsByUsername(testUsername)).thenReturn(false);
            when(userRepository.existsByEmail(testEmail)).thenReturn(false);
            when(passwordEncoder.encode(testPassword)).thenReturn(encodedPassword);
            when(otpVerificationRepository.findByEmailAndOtpType(testEmail, OtpType.REGISTRATION))
                    .thenReturn(Optional.of(otpVerification));

            authService.initiateRegistration(testUsername, testPassword, testEmail);

            verify(otpVerificationRepository).delete(otpVerification);
            verify(otpVerificationRepository).save(any(OtpVerification.class));
            verify(emailService).sendOtpEmail(eq(testEmail), anyString(), eq(OtpType.REGISTRATION));
        }
    }

    @Nested
    @DisplayName("verifyOtpAndRegisterUser Tests")
    class VerifyOtpAndRegisterUserTests {

        @Test
        void whenOtpNotFound_ShouldThrowException() {
            when(otpVerificationRepository.findByEmailAndExpiryDateAfterAndOtpType(
                    eq(testEmail), any(LocalDateTime.class), eq(OtpType.REGISTRATION)))
                    .thenReturn(Optional.empty());

            InvalidOtpException exception = assertThrows(InvalidOtpException.class,
                    () -> authService.verifyOtpAndRegisterUser(testEmail, testOtp));

            assertEquals("Mã OTP không hợp lệ hoặc đã hết hạn", exception.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        void whenOtpIsUsed_ShouldThrowException() {
            otpVerification.setUsed(true);
            when(otpVerificationRepository.findByEmailAndExpiryDateAfterAndOtpType(
                    eq(testEmail), any(LocalDateTime.class), eq(OtpType.REGISTRATION)))
                    .thenReturn(Optional.of(otpVerification));

            InvalidOtpException exception = assertThrows(InvalidOtpException.class,
                    () -> authService.verifyOtpAndRegisterUser(testEmail, testOtp));

            assertEquals("Mã OTP đã được sử dụng", exception.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        void whenAttemptLimitExceeded_ShouldThrowException() {
            otpVerification.setAttemptCount(3);
            when(otpVerificationRepository.findByEmailAndExpiryDateAfterAndOtpType(
                    eq(testEmail), any(LocalDateTime.class), eq(OtpType.REGISTRATION)))
                    .thenReturn(Optional.of(otpVerification));

            InvalidOtpException exception = assertThrows(InvalidOtpException.class,
                    () -> authService.verifyOtpAndRegisterUser(testEmail, testOtp));

            assertEquals("Bạn đã nhập sai OTP quá số lần cho phép", exception.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        void whenOtpIsIncorrect_ShouldIncrementAttempts() {
            when(otpVerificationRepository.findByEmailAndExpiryDateAfterAndOtpType(
                    eq(testEmail), any(LocalDateTime.class), eq(OtpType.REGISTRATION)))
                    .thenReturn(Optional.of(otpVerification));

            InvalidOtpException exception = assertThrows(InvalidOtpException.class,
                    () -> authService.verifyOtpAndRegisterUser(testEmail, "wrong-otp"));

            assertEquals("Mã OTP không đúng", exception.getMessage());
            assertEquals(1, otpVerification.getAttemptCount());
            verify(otpVerificationRepository).save(otpVerification);
            verify(userRepository, never()).save(any());
        }

        @Test
        void whenOtpIsIncorrectAndLastAttempt_ShouldMarkAsUsed() {
            otpVerification.setAttemptCount(2);
            when(otpVerificationRepository.findByEmailAndExpiryDateAfterAndOtpType(
                    eq(testEmail), any(LocalDateTime.class), eq(OtpType.REGISTRATION)))
                    .thenReturn(Optional.of(otpVerification));

            InvalidOtpException exception = assertThrows(InvalidOtpException.class,
                    () -> authService.verifyOtpAndRegisterUser(testEmail, "wrong-otp"));

            assertEquals("Bạn đã nhập sai OTP quá số lần cho phép", exception.getMessage());
            assertEquals(3, otpVerification.getAttemptCount());
            assertTrue(otpVerification.isUsed());
            verify(otpVerificationRepository).save(otpVerification);
            verify(userRepository, never()).save(any());
        }

        @Test
        void whenOtpIsCorrect_ShouldRegisterUser() {
            when(otpVerificationRepository.findByEmailAndExpiryDateAfterAndOtpType(
                    eq(testEmail), any(LocalDateTime.class), eq(OtpType.REGISTRATION)))
                    .thenReturn(Optional.of(otpVerification));

            authService.verifyOtpAndRegisterUser(testEmail, testOtp);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();

            assertEquals(testUsername, savedUser.getUsername());
            assertEquals(testEmail, savedUser.getEmail());
            assertEquals(encodedPassword, savedUser.getPassword());
            assertFalse(savedUser.getIsAdmin());
            assertFalse(savedUser.getIsCommentBlocked());

            assertTrue(otpVerification.isUsed());
            verify(otpVerificationRepository).save(otpVerification);
        }
    }

    @Nested
    @DisplayName("initiatePasswordReset Tests")
    class InitiatePasswordResetTests {

        @Test
        void whenUserNotFound_ShouldThrowException() {
            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

            PasswordResetException exception = assertThrows(PasswordResetException.class,
                    () -> authService.initiatePasswordReset(testEmail));

            assertEquals("Không tìm thấy người dùng với email này", exception.getMessage());
            verify(otpVerificationRepository, never()).save(any());
            verify(emailService, never()).sendOtpEmail(anyString(), anyString(), any());
        }

        @Test
        void whenUserFound_ShouldSendOtp() {
            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));
            when(otpVerificationRepository.findByEmailAndOtpType(testEmail, OtpType.PASSWORD_RESET))
                    .thenReturn(Optional.empty());

            authService.initiatePasswordReset(testEmail);

            ArgumentCaptor<OtpVerification> otpCaptor = ArgumentCaptor.forClass(OtpVerification.class);
            verify(otpVerificationRepository).save(otpCaptor.capture());
            OtpVerification savedOtp = otpCaptor.getValue();

            assertEquals(testEmail, savedOtp.getEmail());
            assertEquals(OtpType.PASSWORD_RESET, savedOtp.getOtpType());
            assertNotNull(savedOtp.getOtp());
            assertNotNull(savedOtp.getExpiryDate());

            verify(emailService).sendOtpEmail(eq(testEmail), anyString(), eq(OtpType.PASSWORD_RESET));
        }

        @Test
        void whenPreviousOtpExists_ShouldDeleteAndCreateNew() {
            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));
            OtpVerification previousOtp = new OtpVerification();
            previousOtp.setEmail(testEmail);
            previousOtp.setOtpType(OtpType.PASSWORD_RESET);

            when(otpVerificationRepository.findByEmailAndOtpType(testEmail, OtpType.PASSWORD_RESET))
                    .thenReturn(Optional.of(previousOtp));

            authService.initiatePasswordReset(testEmail);

            verify(otpVerificationRepository).delete(previousOtp);
            verify(otpVerificationRepository).save(any(OtpVerification.class));
            verify(emailService).sendOtpEmail(eq(testEmail), anyString(), eq(OtpType.PASSWORD_RESET));
        }
    }

    @Nested
    @DisplayName("verifyOtpAndResetPassword Tests")
    class VerifyOtpAndResetPasswordTests {

        @Test
        void whenOtpNotFound_ShouldThrowException() {
            when(otpVerificationRepository.findByEmailAndExpiryDateAfterAndOtpType(
                    eq(testEmail), any(LocalDateTime.class), eq(OtpType.PASSWORD_RESET)))
                    .thenReturn(Optional.empty());

            InvalidOtpException exception = assertThrows(InvalidOtpException.class,
                    () -> authService.verifyOtpAndResetPassword(testEmail, testOtp, "newPassword"));

            assertEquals("Mã OTP không hợp lệ hoặc đã hết hạn", exception.getMessage());
            verify(userRepository, never()).save(any());
        }

        @Test
        void whenOtpIsValid_ShouldResetPassword() {
            otpVerification.setOtpType(OtpType.PASSWORD_RESET);
            when(otpVerificationRepository.findByEmailAndExpiryDateAfterAndOtpType(
                    eq(testEmail), any(LocalDateTime.class), eq(OtpType.PASSWORD_RESET)))
                    .thenReturn(Optional.of(otpVerification));
            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));
            when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

            authService.verifyOtpAndResetPassword(testEmail, testOtp, "newPassword");

            assertEquals("newEncodedPassword", user.getPassword());
            assertTrue(otpVerification.isUsed());
            verify(userRepository).save(user);
            verify(otpVerificationRepository).save(otpVerification);
        }

        @Test
        void whenUserNotFound_ShouldThrowException() {
            otpVerification.setOtpType(OtpType.PASSWORD_RESET);
            when(otpVerificationRepository.findByEmailAndExpiryDateAfterAndOtpType(
                    eq(testEmail), any(LocalDateTime.class), eq(OtpType.PASSWORD_RESET)))
                    .thenReturn(Optional.of(otpVerification));
            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> authService.verifyOtpAndResetPassword(testEmail, testOtp, "newPassword"));

            assertEquals("Không tìm thấy người dùng với email này", exception.getMessage());
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("loginUser Tests")
    class LoginUserTests {

        @Test
        void whenUserNotFound_ShouldThrowException() {
            when(userRepository.findByUsername(testUsername)).thenReturn(Optional.empty());

            UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                    () -> authService.loginUser(testUsername, testPassword));

            assertEquals("Không tìm thấy user", exception.getMessage());
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(jwtUtil, never()).generateAccessToken(any());
            verify(jwtUtil, never()).generateRefreshToken(any());
        }

        @Test
        void whenPasswordIncorrect_ShouldThrowException() {
            when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(testPassword, encodedPassword)).thenReturn(false);

            BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                    () -> authService.loginUser(testUsername, testPassword));

            assertEquals("Sai mật khẩu hoặc thông tin đăng nhập.", exception.getMessage());
            verify(jwtUtil, never()).generateAccessToken(any());
            verify(jwtUtil, never()).generateRefreshToken(any());
        }

        @Test
        void whenCredentialsCorrect_ShouldReturnTokens() {
            when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(testPassword, encodedPassword)).thenReturn(true);
            when(jwtUtil.generateAccessToken(user)).thenReturn("access-token");
            when(jwtUtil.generateRefreshToken(user)).thenReturn("refresh-token");

            TokenResponse response = authService.loginUser(testUsername, testPassword);

            assertEquals("access-token", response.getAccessToken());
            assertEquals("refresh-token", response.getRefreshToken());
            verify(jwtUtil).generateAccessToken(user);
            verify(jwtUtil).generateRefreshToken(user);
        }
    }

    @Nested
    @DisplayName("loginUserGoogle Tests")
    class LoginUserGoogleTests {

        @Test
        void whenUserNotExists_ShouldCreateAndLogin() {
            UserGoogleProfile googleProfile = new UserGoogleProfile(testEmail, "googleUser");

            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());
            when(jwtUtil.generateAccessToken(any(User.class))).thenReturn("access-token");
            when(jwtUtil.generateRefreshToken(any(User.class))).thenReturn("refresh-token");

            TokenResponse response = authService.loginUserGoogle(googleProfile);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();

            assertEquals(testEmail, savedUser.getEmail());
            assertEquals("googleUser", savedUser.getUsername());
            assertEquals("", savedUser.getPassword());
            assertFalse(savedUser.getIsAdmin());
            assertFalse(savedUser.getIsCommentBlocked());

            assertEquals("access-token", response.getAccessToken());
            assertEquals("refresh-token", response.getRefreshToken());
        }

        @Test
        void whenUserExists_ShouldLoginExistingUser() {
            UserGoogleProfile googleProfile = new UserGoogleProfile(testEmail, "googleUser");

            when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));
            when(jwtUtil.generateAccessToken(user)).thenReturn("access-token");
            when(jwtUtil.generateRefreshToken(user)).thenReturn("refresh-token");

            TokenResponse response = authService.loginUserGoogle(googleProfile);

            assertEquals("access-token", response.getAccessToken());
            assertEquals("refresh-token", response.getRefreshToken());
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("refreshAccessToken Tests")
    class RefreshAccessTokenTests {

        @Test
        void whenTokenInvalid_ShouldThrowException() {
            String refreshToken = "invalid-token";
            when(jwtUtil.isRefreshToken(refreshToken)).thenReturn(false);

            JwtException exception = assertThrows(JwtException.class,
                    () -> authService.refreshAccessToken(refreshToken));

            assertEquals("Refresh token không hợp lệ hoặc đã hết hạn", exception.getMessage());
            verify(userRepository, never()).findById(anyLong());
            verify(jwtUtil, never()).generateAccessToken(any());
        }

        @Test
        void whenTokenBlacklisted_ShouldThrowException() {
            String refreshToken = "blacklisted-token";
            when(jwtUtil.isRefreshToken(refreshToken)).thenReturn(true);
            when(tokenBlacklistService.isTokenBlacklisted(refreshToken)).thenReturn(true);

            JwtException exception = assertThrows(JwtException.class,
                    () -> authService.refreshAccessToken(refreshToken));

            assertEquals("Refresh token không hợp lệ hoặc đã hết hạn", exception.getMessage());
            verify(userRepository, never()).findById(anyLong());
            verify(jwtUtil, never()).generateAccessToken(any());
        }

        @Test
        void whenUserNotFound_ShouldThrowException() {
            String refreshToken = "valid-token";
            when(jwtUtil.isRefreshToken(refreshToken)).thenReturn(true);
            when(tokenBlacklistService.isTokenBlacklisted(refreshToken)).thenReturn(false);
            when(jwtUtil.getSubject(refreshToken)).thenReturn("1");
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                    () -> authService.refreshAccessToken(refreshToken));

            assertEquals("Không tìm thấy user", exception.getMessage());
            verify(jwtUtil, never()).generateAccessToken(any());
        }

        @Test
        void whenValidToken_ShouldGenerateNewAccessToken() {
            String refreshToken = "valid-token";
            when(jwtUtil.isRefreshToken(refreshToken)).thenReturn(true);
            when(tokenBlacklistService.isTokenBlacklisted(refreshToken)).thenReturn(false);
            when(jwtUtil.getSubject(refreshToken)).thenReturn("1");
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(jwtUtil.generateAccessToken(user)).thenReturn("new-access-token");

            TokenResponse response = authService.refreshAccessToken(refreshToken);

            assertEquals("new-access-token", response.getAccessToken());
            assertEquals(refreshToken, response.getRefreshToken());
            verify(jwtUtil).generateAccessToken(user);
        }
    }

    @Nested
    @DisplayName("cleanupExpiredOtp Tests")
    class CleanupExpiredOtpTests {

        @Test
        void shouldDeleteExpiredOtp() {
            authService.cleanupExpiredOtp();

            verify(otpVerificationRepository).deleteByExpiryDateBefore(any(LocalDateTime.class));
        }
    }
}
