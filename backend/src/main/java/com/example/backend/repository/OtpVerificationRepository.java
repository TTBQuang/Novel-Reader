package com.example.backend.repository;

import com.example.backend.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
    Optional<OtpVerification> findByEmailAndOtpAndUsedFalseAndExpiryDateAfter(
            String email, String otp, LocalDateTime now);
    void deleteByExpiryDateBefore(LocalDateTime date);
    Optional<OtpVerification> findByEmail(String email);
}
