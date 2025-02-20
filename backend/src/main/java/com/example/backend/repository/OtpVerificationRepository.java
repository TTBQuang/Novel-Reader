package com.example.backend.repository;

import com.example.backend.entity.OtpVerification;
import com.example.backend.enums.OtpType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findByEmailAndExpiryDateAfterAndOtpType(
            String email, LocalDateTime now, OtpType otpType);

    void deleteByExpiryDateBefore(LocalDateTime date);

    Optional<OtpVerification> findByEmailAndOtpType(String email, OtpType otpType);
}
