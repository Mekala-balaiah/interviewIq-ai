package com.interviewiq.auth.service;

import com.interviewiq.auth.dto.*;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(TokenRefreshRequest request);

    void logout(String refreshToken);

    void verifyEmail(VerifyEmailRequest request);

    void resendVerificationEmail(String email);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}
