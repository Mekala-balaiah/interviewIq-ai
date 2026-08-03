package com.interviewiq.candidate.controller;

import com.interviewiq.auth.security.UserPrincipal;
import com.interviewiq.candidate.document.CandidateDocument;
import com.interviewiq.candidate.service.CandidateSearchService;
import com.interviewiq.common.response.ApiResponse;
import com.interviewiq.common.response.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/candidates/search")
@RequiredArgsConstructor
@Tag(name = "Candidate Search", description = "Elasticsearch-powered candidate search endpoints")
@SecurityRequirement(name = "BearerAuth")
public class CandidateSearchController {

    private final CandidateSearchService candidateSearchService;

    @GetMapping
    @Operation(summary = "Search candidates using Elasticsearch",
               description = "Full-text fuzzy search with optional filters for location, experience, skills, and remote preference.")
    @PreAuthorize("hasAnyRole('RECRUITER', 'HR_MANAGER', 'ADMIN')")
    public ApiResponse<PagedResponse<CandidateDocument>> searchCandidates(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) List<String> skills,
            @RequestParam(required = false) Boolean openToRemote,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<CandidateDocument> results = candidateSearchService.searchCandidates(
                keyword, location, minExperience, skills, openToRemote, page, size
        );
        return ApiResponse.success(results, "Candidates retrieved");
    }

    @PostMapping("/sync/{candidateId}")
    @Operation(summary = "Manually sync a candidate profile to Elasticsearch (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> syncCandidate(
            @PathVariable UUID candidateId,
            @AuthenticationPrincipal UserPrincipal principal) {
        candidateSearchService.syncCandidate(candidateId);
        return ResponseEntity.ok().build();
    }
}
