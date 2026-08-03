package com.interviewiq.candidate.controller;

import com.interviewiq.auth.security.UserPrincipal;
import com.interviewiq.candidate.dto.AddCandidateSkillRequest;
import com.interviewiq.candidate.dto.CandidateProfileDto;
import com.interviewiq.candidate.dto.CandidateSkillDto;
import com.interviewiq.candidate.dto.UpdateCandidateProfileRequest;
import com.interviewiq.candidate.service.CandidateService;
import com.interviewiq.candidate.service.CandidateSkillService;
import com.interviewiq.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/candidates/me")
@RequiredArgsConstructor
@Tag(name = "Candidate Profile", description = "Endpoints for managing the candidate profile")
@SecurityRequirement(name = "BearerAuth")
@PreAuthorize("hasRole('CANDIDATE')")
public class CandidateProfileController {

    private final CandidateService candidateService;
    private final CandidateSkillService candidateSkillService;

    @GetMapping
    @Operation(summary = "Get my candidate profile")
    public ApiResponse<CandidateProfileDto> getMyProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(candidateService.getProfileByUserId(userPrincipal.getId()), "Profile retrieved");
    }

    @PutMapping
    @Operation(summary = "Update my candidate profile")
    public ApiResponse<CandidateProfileDto> updateMyProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateCandidateProfileRequest request) {
        return ApiResponse.success(candidateService.updateProfile(userPrincipal.getId(), request), "Profile updated");
    }

    @GetMapping("/skills")
    @Operation(summary = "Get my skills")
    public ApiResponse<List<CandidateSkillDto>> getMySkills(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(candidateSkillService.getCandidateSkills(userPrincipal.getId()), "Skills retrieved");
    }

    @PostMapping("/skills")
    @Operation(summary = "Add a skill to my profile")
    public ApiResponse<CandidateSkillDto> addSkill(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AddCandidateSkillRequest request) {
        return ApiResponse.success(candidateSkillService.addSkillToProfile(userPrincipal.getId(), request), "Skill added");
    }

    @DeleteMapping("/skills/{candidateSkillId}")
    @Operation(summary = "Remove a skill from my profile")
    public ApiResponse<Void> removeSkill(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable UUID candidateSkillId) {
        candidateSkillService.removeSkillFromProfile(userPrincipal.getId(), candidateSkillId);
        return ApiResponse.success(null, "Skill removed");
    }
}
