package com.interviewiq.application.controller;

import com.interviewiq.application.dto.ApplicationDto;
import com.interviewiq.application.enums.ApplicationStatus;
import com.interviewiq.application.service.ApplicationService;
import com.interviewiq.auth.security.UserPrincipal;
import com.interviewiq.common.response.ApiResponse;
import com.interviewiq.common.response.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pipeline")
@RequiredArgsConstructor
@Tag(name = "Candidate Pipeline", description = "Endpoints for recruiters to manage candidate applications")
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasRole('RECRUITER')")
public class PipelineController {

    private final ApplicationService applicationService;

    @GetMapping("/jobs/{jobId}/applications")
    @Operation(summary = "Get all applications for a specific job")
    public ApiResponse<PagedResponse<ApplicationDto>> getApplicationsForJob(
            @PathVariable UUID jobId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Pageable pageable) {
        return ApiResponse.success(
                applicationService.getApplicationsForJob(jobId, userPrincipal.getId(), pageable), 
                "Applications retrieved successfully"
        );
    }

    @PutMapping("/applications/{applicationId}/status")
    @Operation(summary = "Update the status of a candidate's application")
    public ApiResponse<ApplicationDto> updateApplicationStatus(
            @PathVariable UUID applicationId,
            @RequestParam ApplicationStatus status,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(
                applicationService.updateApplicationStatus(applicationId, userPrincipal.getId(), status),
                "Application status updated successfully"
        );
    }
}
