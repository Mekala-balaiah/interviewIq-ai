package com.interviewiq.application.controller;

import com.interviewiq.application.dto.ApplicationDto;
import com.interviewiq.application.dto.ApplyForJobRequest;
import com.interviewiq.application.service.ApplicationService;
import com.interviewiq.auth.security.UserPrincipal;
import com.interviewiq.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
@Tag(name = "Job Applications", description = "Endpoints for managing job applications")
@SecurityRequirement(name = "BearerAuth")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Apply for a job")
    public ApiResponse<ApplicationDto> applyForJob(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ApplyForJobRequest request) {
        return ApiResponse.success(applicationService.applyForJob(userPrincipal.getId(), request), "Application submitted successfully");
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Get my applications")
    public ApiResponse<List<ApplicationDto>> getMyApplications(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(applicationService.getMyApplications(userPrincipal.getId()), "Applications retrieved");
    }
}
