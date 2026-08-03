package com.interviewiq.hr.controller;

import com.interviewiq.auth.security.UserPrincipal;
import com.interviewiq.common.response.ApiResponse;
import com.interviewiq.hr.dto.HrDashboardDto;
import com.interviewiq.hr.dto.HrProfileDto;
import com.interviewiq.hr.dto.UpdateHrProfileRequest;
import com.interviewiq.hr.service.HrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/hr")
@RequiredArgsConstructor
@Tag(name = "HR Manager", description = "Endpoints for HR Manager workflows and profile")
public class HrProfileController {

    private final HrService hrService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('HR_MANAGER')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Get current HR profile")
    public ApiResponse<HrProfileDto> getProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(hrService.getProfile(userPrincipal.getId()), "Profile fetched successfully");
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('HR_MANAGER')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Update current HR profile")
    public ApiResponse<HrProfileDto> updateProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateHrProfileRequest request) {
        return ApiResponse.success(hrService.updateProfile(userPrincipal.getId(), request), "Profile updated successfully");
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('HR_MANAGER')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Get HR dashboard metrics")
    public ApiResponse<HrDashboardDto> getDashboardMetrics(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(hrService.getDashboardMetrics(userPrincipal.getId()), "Dashboard metrics fetched successfully");
    }
}
