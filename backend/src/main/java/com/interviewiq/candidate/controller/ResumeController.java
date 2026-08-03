package com.interviewiq.candidate.controller;

import com.interviewiq.auth.security.UserPrincipal;
import com.interviewiq.candidate.dto.ResumeDto;
import com.interviewiq.candidate.service.ResumeService;
import com.interviewiq.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/candidates/me/resumes")
@RequiredArgsConstructor
@Tag(name = "Resume Management", description = "Endpoints for uploading and managing resumes")
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasRole('CANDIDATE')")
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a resume")
    public ApiResponse<ResumeDto> uploadResume(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false, defaultValue = "false") Boolean isPrimary) {
        return ApiResponse.success(resumeService.uploadResume(userPrincipal.getId(), file, isPrimary), "Resume uploaded successfully");
    }

    @GetMapping
    @Operation(summary = "Get my resumes")
    public ApiResponse<List<ResumeDto>> getMyResumes(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(resumeService.getMyResumes(userPrincipal.getId()), "Resumes retrieved");
    }

    @DeleteMapping("/{resumeId}")
    @Operation(summary = "Delete a resume")
    public ApiResponse<Void> deleteResume(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID resumeId) {
        resumeService.deleteResume(userPrincipal.getId(), resumeId);
        return ApiResponse.success(null, "Resume deleted successfully");
    }
}
