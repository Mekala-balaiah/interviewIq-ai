package com.interviewiq.auth.controller;

import com.interviewiq.auth.dto.*;
import com.interviewiq.auth.service.AuthService;
import com.interviewiq.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for login, registration, and token management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request), "User registered successfully. Please verify email.");
    }

    @PostMapping("/login")
    @Operation(summary = "Login and obtain tokens")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request), "Login successful");
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        return ApiResponse.success(authService.refreshToken(request), "Token refreshed");
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout and revoke refresh token")
    public ApiResponse<Void> logout(@Valid @RequestBody TokenRefreshRequest request) {
        authService.logout(request.getRefreshToken());
        return ApiResponse.success(null, "Logged out successfully");
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email with OTP")
    public ApiResponse<Void> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        authService.verifyEmail(request);
        return ApiResponse.success(null, "Email verified successfully");
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Resend email verification OTP")
    public ApiResponse<Void> resendVerification(@RequestParam String email) {
        authService.resendVerificationEmail(email);
        return ApiResponse.success(null, "Verification email sent");
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset link")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ApiResponse.success(null, "If an account exists, a reset link has been sent");
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using token")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.success(null, "Password reset successfully");
    }
}
