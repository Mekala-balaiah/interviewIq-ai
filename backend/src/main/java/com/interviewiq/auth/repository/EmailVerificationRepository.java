package com.interviewiq.auth.repository;

import com.interviewiq.auth.entity.EmailVerification;
import com.interviewiq.auth.enums.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, UUID> {
    
    Optional<EmailVerification> findByUserIdAndOtpCodeAndPurposeAndUsedFalse(UUID userId, String otpCode, OtpPurpose purpose);
}
