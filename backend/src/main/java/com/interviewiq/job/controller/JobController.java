package com.interviewiq.job.controller;

import com.interviewiq.auth.security.UserPrincipal;
import com.interviewiq.common.response.ApiResponse;
import com.interviewiq.common.response.PagedResponse;
import com.interviewiq.job.dto.AddJobSkillRequest;
import com.interviewiq.job.dto.CreateJobRequest;
import com.interviewiq.job.dto.JobDto;
import com.interviewiq.job.dto.JobSkillDto;
import com.interviewiq.job.dto.UpdateJobRequest;
import com.interviewiq.job.service.JobService;
import com.interviewiq.job.service.JobSkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs", description = "Endpoints for managing job postings")
public class JobController {

    private final JobService jobService;
    private final JobSkillService jobSkillService;

    @PostMapping
    @PreAuthorize("hasRole('RECRUITER')")
    @SecurityRequirement(name = "BearerAuth")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new job posting (Draft)")
    public ApiResponse<JobDto> createJob(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CreateJobRequest request) {
        return ApiResponse.success(jobService.createJob(userPrincipal.getId(), request), "Job created successfully");
    }

    @GetMapping
    @PreAuthorize("hasRole('RECRUITER')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Get my created jobs")
    public ApiResponse<PagedResponse<JobDto>> getMyJobs(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Pageable pageable) {
        return ApiResponse.success(jobService.getMyJobs(userPrincipal.getId(), pageable), "Jobs retrieved");
    }

    @GetMapping("/{jobId}")
    @Operation(summary = "Get job details by ID")
    public ApiResponse<JobDto> getJobById(@PathVariable UUID jobId) {
        return ApiResponse.success(jobService.getJobById(jobId), "Job retrieved");
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get job details by slug")
    public ApiResponse<JobDto> getJobBySlug(@PathVariable String slug) {
        return ApiResponse.success(jobService.getJobBySlug(slug), "Job retrieved");
    }

    @PutMapping("/{jobId}")
    @PreAuthorize("hasRole('RECRUITER')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Update a job posting")
    public ApiResponse<JobDto> updateJob(
            @PathVariable UUID jobId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateJobRequest request) {
        return ApiResponse.success(jobService.updateJob(jobId, userPrincipal.getId(), request), "Job updated successfully");
    }

    @DeleteMapping("/{jobId}")
    @PreAuthorize("hasRole('RECRUITER')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Delete (soft-delete) a job posting")
    public ApiResponse<Void> deleteJob(
            @PathVariable UUID jobId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        jobService.deleteJob(jobId, userPrincipal.getId());
        return ApiResponse.success(null, "Job deleted successfully");
    }

    @PutMapping("/{jobId}/submit-for-approval")
    @PreAuthorize("hasRole('RECRUITER')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Submit a draft job posting for HR approval")
    public ApiResponse<JobDto> submitForApproval(
            @PathVariable UUID jobId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(jobService.submitForApproval(jobId, userPrincipal.getId()), "Job submitted for approval successfully");
    }

    // --- Job Skills ---

    @PostMapping("/{jobId}/skills")
    @PreAuthorize("hasRole('RECRUITER')")
    @SecurityRequirement(name = "BearerAuth")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a skill requirement to a job")
    public ApiResponse<JobSkillDto> addSkillToJob(
            @PathVariable UUID jobId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AddJobSkillRequest request) {
        return ApiResponse.success(jobSkillService.addSkillToJob(jobId, userPrincipal.getId(), request), "Skill added to job");
    }

    @DeleteMapping("/{jobId}/skills/{jobSkillId}")
    @PreAuthorize("hasRole('RECRUITER')")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "Remove a skill requirement from a job")
    public ApiResponse<Void> removeSkillFromJob(
            @PathVariable UUID jobId,
            @PathVariable UUID jobSkillId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        jobSkillService.removeSkillFromJob(jobId, jobSkillId, userPrincipal.getId());
        return ApiResponse.success(null, "Skill removed from job");
    }

    @GetMapping("/{jobId}/skills")
    @Operation(summary = "Get skills required for a job")
    public ApiResponse<List<JobSkillDto>> getSkillsForJob(@PathVariable UUID jobId) {
        return ApiResponse.success(jobSkillService.getSkillsForJob(jobId), "Job skills retrieved");
    }
}
