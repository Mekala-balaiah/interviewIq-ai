package com.interviewiq.candidate.controller;

import com.interviewiq.candidate.dto.CandidateProfileDto;
import com.interviewiq.candidate.dto.CandidateSearchRequest;
import com.interviewiq.candidate.service.CandidateService;
import com.interviewiq.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/candidates/search")
@RequiredArgsConstructor
@Tag(name = "Candidate Search", description = "Endpoints for recruiters to search candidates")
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasAnyRole('RECRUITER', 'HR_MANAGER')")
public class CandidateSearchController {

    private final CandidateService candidateService;

    @GetMapping
    @Operation(summary = "Search candidate profiles")
    public ApiResponse<Page<CandidateProfileDto>> searchCandidates(
            @ModelAttribute CandidateSearchRequest request,
            Pageable pageable) {
        return ApiResponse.success(candidateService.searchCandidates(request, pageable), "Candidates retrieved successfully");
    }
}
