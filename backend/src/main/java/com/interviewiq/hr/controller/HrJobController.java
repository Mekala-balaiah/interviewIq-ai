package com.interviewiq.hr.controller;

import com.interviewiq.auth.security.UserPrincipal;
import com.interviewiq.common.response.ApiResponse;
import com.interviewiq.common.response.PagedResponse;
import com.interviewiq.hr.dto.JobApprovalDto;
import com.interviewiq.hr.dto.JobApprovalRequest;
import com.interviewiq.hr.service.HrService;
import com.interviewiq.job.dto.JobDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hr/jobs")
@RequiredArgsConstructor
@Tag(name = "HR Job Approval", description = "Endpoints for HR to manage and approve jobs")
public class HrJobController {

    private final HrService hrService;

    @GetMapping("/pending")
    @PreAuthorize("hasRole('HR_MANAGER')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Get jobs pending approval")
    public ApiResponse<PagedResponse<JobDto>> getPendingJobs(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Pageable pageable) {
        return ApiResponse.success(hrService.getPendingJobs(userPrincipal.getId(), pageable), "Pending jobs fetched successfully");
    }

    @PostMapping("/{jobId}/approve")
    @PreAuthorize("hasRole('HR_MANAGER')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Approve or reject a job posting")
    public ApiResponse<JobApprovalDto> processJobApproval(
            @PathVariable UUID jobId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody JobApprovalRequest request) {
        return ApiResponse.success(hrService.processJobApproval(jobId, userPrincipal.getId(), request), "Job approval processed successfully");
    }
}
