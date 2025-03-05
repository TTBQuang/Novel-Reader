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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private OtpVerificationRepository otpVerificationRepository;
    private EmailService emailService;

    private static final int OTP_EXPIRY_MINUTES = 10;

    @Transactional
    public void initiateRegistration(String username, String rawPassword, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new UserRegistrationException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(email)) {
            throw new UserRegistrationException("Email đã tồn tại");
        }

        String otp = generateOtp();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        OtpVerification verification = new OtpVerification();
        verification.setEmail(email);
        verification.setOtp(otp);
        verification.setExpiryDate(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        verification.setUsername(username);
        verification.setPassword(encodedPassword);
        verification.setOtpType(OtpType.REGISTRATION);

        otpVerificationRepository.findByEmailAndOtpType(email, OtpType.REGISTRATION)
                .ifPresent(old -> otpVerificationRepository.delete(old));
        otpVerificationRepository.save(verification);

        emailService.sendOtpEmail(email, otp, OtpType.REGISTRATION);
    }

    @Transactional(noRollbackFor = InvalidOtpException.class)
    public void verifyOtpAndRegisterUser(String email, String otp) {
        // Get the OTP record that has not been used, not expired for REGISTRATION
        OtpVerification otpVerification = otpVerificationRepository
                .findByEmailAndExpiryDateAfterAndOtpType(
                        email, LocalDateTime.now(), OtpType.REGISTRATION)
                .orElseThrow(() -> new InvalidOtpException("Mã OTP không hợp lệ hoặc đã hết hạn"));

        verifyOtpAndUpdateAttempt(otpVerification, otp);

        // If the OTP is correct, proceed to register the user
        User user = new User();
        user.setUsername(otpVerification.getUsername());
        user.setEmail(otpVerification.getEmail());
        user.setPassword(otpVerification.getPassword());
        user.setIsAdmin(false);
        user.setIsCommentBlocked(false);
        userRepository.save(user);

        // Mark the OTP as used
        otpVerification.setUsed(true);
        otpVerificationRepository.save(otpVerification);
    }

    @Transactional
    public void initiatePasswordReset(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new PasswordResetException("Không tìm thấy người dùng với email này"));

        String otp = generateOtp();

        OtpVerification verification = new OtpVerification();
        verification.setEmail(email);
        verification.setOtp(otp);
        verification.setExpiryDate(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        verification.setOtpType(OtpType.PASSWORD_RESET);

        otpVerificationRepository.findByEmailAndOtpType(email, OtpType.PASSWORD_RESET)
                .ifPresent(old -> otpVerificationRepository.delete(old));
        otpVerificationRepository.save(verification);

        emailService.sendOtpEmail(email, otp, OtpType.PASSWORD_RESET);
    }

    @Transactional(noRollbackFor = InvalidOtpException.class)
    public void verifyOtpAndResetPassword(String email, String otp, String newPassword) {
        // Get the OTP record that has not been used, not expired for PASSWORD_RESET
        OtpVerification otpVerification = otpVerificationRepository
                .findByEmailAndExpiryDateAfterAndOtpType(
                        email, LocalDateTime.now(), OtpType.PASSWORD_RESET)
                .orElseThrow(() -> new InvalidOtpException("Mã OTP không hợp lệ hoặc đã hết hạn"));

        verifyOtpAndUpdateAttempt(otpVerification, otp);

        // If the OTP is correct, proceed to reset the password
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng với email này"));
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        // Mark the OTP as used
        otpVerification.setUsed(true);
        otpVerificationRepository.save(otpVerification);
    }

    private void verifyOtpAndUpdateAttempt(OtpVerification otpVerification, String otp) {
        if (otpVerification.getAttemptCount() >= 3) {
            throw new InvalidOtpException("Bạn đã nhập sai OTP quá số lần cho phép");
        }

        if (otpVerification.isUsed()) {
            throw new InvalidOtpException("Mã OTP đã được sử dụng");
        }

        if (!otpVerification.getOtp().equals(otp)) {
            otpVerification.setAttemptCount(otpVerification.getAttemptCount() + 1);
            if (otpVerification.getAttemptCount() >= 3) {
                otpVerification.setUsed(true);
            }
            otpVerificationRepository.save(otpVerification);
            if (otpVerification.getAttemptCount() >= 3) {
                throw new InvalidOtpException("Bạn đã nhập sai OTP quá số lần cho phép");
            } else {
                throw new InvalidOtpException("Mã OTP không đúng");
            }
        }
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    @Scheduled(cron = "0 */30 * * * *")
    @Transactional
    public void cleanupExpiredOtp() {
        otpVerificationRepository.deleteByExpiryDateBefore(LocalDateTime.now());
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

    public String createFirebaseCustomToken(String userId) {
        try {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            return firebaseAuth.createCustomToken(userId);
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Không thể tạo Firebase token", e);
        }
    }
}
