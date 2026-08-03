package com.interviewiq.candidate.controller;

import com.interviewiq.auth.security.UserPrincipal;
import com.interviewiq.candidate.dto.AnalyzeResumeRequest;
import com.interviewiq.candidate.dto.ResumeAnalysisDto;
import com.interviewiq.candidate.mapper.ResumeAnalysisMapper;
import com.interviewiq.candidate.repository.ResumeAnalysisRepository;
import com.interviewiq.candidate.service.ResumeParsingService;
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
@RequestMapping("/api/v1/resumes")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Resume AI", description = "Endpoints for parsing and analyzing resumes")
public class ResumeAnalysisController {

    private final ResumeParsingService resumeParsingService;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final ResumeAnalysisMapper resumeAnalysisMapper;

    @PostMapping("/{resumeId}/analyze")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    @Operation(summary = "Trigger AI analysis for a resume")
    public ApiResponse<ResumeAnalysisDto> analyzeResume(
            @PathVariable UUID resumeId,
            @RequestBody(required = false) AnalyzeResumeRequest request) {
        
        UUID jobId = (request != null) ? request.getJobId() : null;
        ResumeAnalysisDto analysisDto = resumeParsingService.analyzeResume(resumeId, jobId);
        
        return ApiResponse.success(analysisDto, "Resume analyzed successfully");
    }

    @GetMapping("/{resumeId}/analyses")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    @Operation(summary = "Get previous analyses for a resume")
    public ApiResponse<PagedResponse<ResumeAnalysisDto>> getAnalyses(
            @PathVariable UUID resumeId,
            Pageable pageable) {
        
        var page = resumeAnalysisRepository.findByResumeIdOrderByCreatedAtDesc(resumeId, pageable);
        return ApiResponse.success(new PagedResponse<>(page.map(resumeAnalysisMapper::toDto)), "Analyses fetched successfully");
    }
}
