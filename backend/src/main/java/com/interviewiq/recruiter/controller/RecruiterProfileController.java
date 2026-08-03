package com.interviewiq.recruiter.controller;

import com.interviewiq.auth.security.UserPrincipal;
import com.interviewiq.common.response.ApiResponse;
import com.interviewiq.recruiter.dto.RecruiterDashboardDto;
import com.interviewiq.recruiter.dto.RecruiterProfileDto;
import com.interviewiq.recruiter.dto.UpdateRecruiterProfileRequest;
import com.interviewiq.recruiter.service.RecruiterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recruiters/me")
@RequiredArgsConstructor
@Tag(name = "Recruiter Profile", description = "Endpoints for managing recruiter profiles and dashboards")
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasRole('RECRUITER')")
public class RecruiterProfileController {

    private final RecruiterService recruiterService;

    @GetMapping
    @Operation(summary = "Get my recruiter profile")
    public ApiResponse<RecruiterProfileDto> getMyProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(recruiterService.getProfileByUserId(userPrincipal.getId()), "Profile retrieved");
    }

    @PutMapping
    @Operation(summary = "Update my recruiter profile")
    public ApiResponse<RecruiterProfileDto> updateMyProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateRecruiterProfileRequest request) {
        return ApiResponse.success(recruiterService.updateProfile(userPrincipal.getId(), request), "Profile updated");
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get recruiter dashboard metrics")
    public ApiResponse<RecruiterDashboardDto> getDashboardMetrics(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(recruiterService.getDashboardMetrics(userPrincipal.getId()), "Dashboard metrics retrieved");
    }
}
