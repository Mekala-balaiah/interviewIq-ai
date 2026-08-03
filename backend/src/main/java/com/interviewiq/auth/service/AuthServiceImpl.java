package com.interviewiq.auth.service;

import com.interviewiq.auth.dto.*;
import com.interviewiq.auth.entity.*;
import com.interviewiq.auth.enums.OtpPurpose;
import com.interviewiq.auth.enums.UserStatus;
import com.interviewiq.auth.mapper.UserMapper;
import com.interviewiq.auth.repository.*;
import com.interviewiq.auth.security.JwtTokenProvider;
import com.interviewiq.common.exception.BusinessException;
import com.interviewiq.common.exception.DuplicateResourceException;
import com.interviewiq.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already in use");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .phone(request.getPhone())
                .status(UserStatus.PENDING_VERIFICATION)
                .emailVerified(false)
                .build();

        user = userRepository.save(user);

        // Generate OTP
        String otp = generateNumericOtp(6);
        EmailVerification verification = EmailVerification.builder()
                .user(user)
                .otpCode(otp)
                .purpose(OtpPurpose.EMAIL_VERIFICATION)
                .expiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .build();
        emailVerificationRepository.save(verification);

        // TODO: Publish Kafka Event to Notification Service to send email
        log.info("MOCK EMAIL SENT to {}: Your OTP is {}", user.getEmail(), otp);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        return generateAuthResponse(user, authentication);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new BusinessException("Account is not active. Status: " + user.getStatus());
        }

        user.recordLogin();
        userRepository.save(user);

        return generateAuthResponse(user, authentication);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new BusinessException("Refresh token is not in database!"));

        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException("Refresh token was expired or revoked. Please make a new signin request");
        }

        User user = refreshToken.getUser();
        
        // Revoke the old token (Rotate)
        refreshToken.setRevoked(true);
        refreshToken.setRevokedAt(Instant.now());
        refreshTokenRepository.save(refreshToken);

        // Generate new tokens
        String newAccessToken = tokenProvider.generateAccessToken(
                new UsernamePasswordAuthenticationToken(user.getEmail(), null) // Simplified for refresh
        );
        
        RefreshToken newRefreshTokenObj = RefreshToken.builder()
                .user(user)
                .token(tokenProvider.generateRefreshToken(user.getId()))
                .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();
        refreshTokenRepository.save(newRefreshTokenObj);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshTokenObj.getToken())
                .tokenType("Bearer")
                .user(userMapper.toDto(user))
                .build();
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
            token.setRevoked(true);
            token.setRevokedAt(Instant.now());
            refreshTokenRepository.save(token);
        });
    }

    @Override
    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        EmailVerification verification = emailVerificationRepository
                .findByUserIdAndOtpCodeAndPurposeAndUsedFalse(user.getId(), request.getOtpCode(), OtpPurpose.EMAIL_VERIFICATION)
                .orElseThrow(() -> new BusinessException("Invalid OTP code"));

        if (verification.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException("OTP code has expired");
        }

        verification.setUsed(true);
        verification.setUsedAt(Instant.now());
        emailVerificationRepository.save(verification);

        user.markEmailVerified();
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isEmailVerified()) {
            throw new BusinessException("Email is already verified");
        }

        String otp = generateNumericOtp(6);
        EmailVerification verification = EmailVerification.builder()
                .user(user)
                .otpCode(otp)
                .purpose(OtpPurpose.EMAIL_VERIFICATION)
                .expiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .build();
        emailVerificationRepository.save(verification);

        // TODO: Publish Kafka Event
        log.info("MOCK RESEND EMAIL to {}: Your new OTP is {}", user.getEmail(), otp);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) return; // Don't leak user existence
        
        User user = userOptional.get();

        String tokenStr = UUID.randomUUID().toString();
        PasswordResetToken token = PasswordResetToken.builder()
                .user(user)
                .token(tokenStr)
                .expiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .build();
        passwordResetTokenRepository.save(token);

        // TODO: Publish Kafka Event
        log.info("MOCK FORGOT PASSWORD EMAIL to {}: Reset link token is {}", user.getEmail(), tokenStr);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken token = passwordResetTokenRepository.findByTokenAndUsedFalse(request.getToken())
                .orElseThrow(() -> new BusinessException("Invalid or used reset token"));

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException("Reset token has expired");
        }

        User user = token.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        token.setUsed(true);
        token.setUsedAt(Instant.now());
        passwordResetTokenRepository.save(token);
        
        // Revoke all existing sessions to force re-login
        refreshTokenRepository.revokeAllUserTokens(user.getId(), Instant.now());
    }

    private AuthResponse generateAuthResponse(User user, Authentication authentication) {
        String accessToken = tokenProvider.generateAccessToken(authentication);
        
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(tokenProvider.generateRefreshToken(user.getId()))
                .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();
        
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .user(userMapper.toDto(user))
                .build();
    }

    private String generateNumericOtp(int length) {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append((int) (Math.random() * 10));
        }
        return otp.toString();
    }
}
