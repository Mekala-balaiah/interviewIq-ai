package com.interviewiq.assessment.controller;

import com.interviewiq.assessment.dto.*;
import com.interviewiq.assessment.service.AssessmentService;
import com.interviewiq.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assessments")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Coding Assessment", description = "Endpoints for managing candidate coding assessments")
public class AssessmentController {

    private final AssessmentService assessmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('RECRUITER', 'HR_MANAGER')")
    @Operation(summary = "Assign an assessment to an application")
    public ApiResponse<AssessmentDto> assignAssessment(
            @Valid @RequestBody CreateAssessmentRequest request) {
        return ApiResponse.success(assessmentService.assignAssessment(request), "Assessment assigned successfully");
    }

    @PostMapping("/{assessmentId}/start")
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Start the coding assessment (starts timer)")
    public ApiResponse<AssessmentDto> startAssessment(@PathVariable UUID assessmentId) {
        return ApiResponse.success(assessmentService.startAssessment(assessmentId), "Assessment started successfully");
    }

    @GetMapping("/{assessmentId}/questions")
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Get the list of coding questions for the assessment")
    public ApiResponse<List<AssessmentQuestionDto>> getQuestions(@PathVariable UUID assessmentId) {
        return ApiResponse.success(assessmentService.getQuestions(assessmentId), "Questions fetched successfully");
    }

    @PostMapping("/{assessmentId}/questions/{questionId}/submit")
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Submit code for a specific question")
    public ApiResponse<AssessmentSubmissionDto> submitCode(
            @PathVariable UUID assessmentId,
            @PathVariable UUID questionId,
            @Valid @RequestBody SubmitCodeRequest request) {
        return ApiResponse.success(assessmentService.submitCode(assessmentId, questionId, request), "Code submitted successfully");
    }

    @PostMapping("/{assessmentId}/complete")
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Complete the assessment and calculate final score")
    public ApiResponse<AssessmentDto> completeAssessment(@PathVariable UUID assessmentId) {
        return ApiResponse.success(assessmentService.completeAssessment(assessmentId), "Assessment completed successfully");
    }

    @GetMapping("/{assessmentId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'HR_MANAGER')")
    @Operation(summary = "Get full details and score of the assessment")
    public ApiResponse<AssessmentDto> getAssessmentDetails(@PathVariable UUID assessmentId) {
        return ApiResponse.success(assessmentService.getAssessmentDetails(assessmentId), "Assessment details fetched successfully");
    }
}
