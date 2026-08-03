package com.interviewiq.interview.controller;

import com.interviewiq.common.response.ApiResponse;
import com.interviewiq.interview.dto.*;
import com.interviewiq.interview.service.InterviewService;
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
@RequestMapping("/api/v1/interviews")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Interview AI Engine", description = "Endpoints for scheduling and conducting AI interviews")
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/schedule")
    @PreAuthorize("hasAnyRole('RECRUITER', 'HR_MANAGER')")
    @Operation(summary = "Schedule a new interview")
    public ApiResponse<InterviewDto> scheduleInterview(
            @Valid @RequestBody ScheduleInterviewRequest request) {
        return ApiResponse.success(interviewService.scheduleInterview(request), "Interview scheduled successfully");
    }

    @PostMapping("/{interviewId}/start")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    @Operation(summary = "Start the interview and generate questions")
    public ApiResponse<InterviewDto> startInterview(@PathVariable UUID interviewId) {
        return ApiResponse.success(interviewService.startInterview(interviewId), "Interview started successfully");
    }

    @GetMapping("/{interviewId}/questions")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    @Operation(summary = "Get the list of generated questions for the interview")
    public ApiResponse<List<InterviewQuestionDto>> getQuestions(@PathVariable UUID interviewId) {
        return ApiResponse.success(interviewService.getQuestions(interviewId), "Questions fetched successfully");
    }

    @PostMapping("/{interviewId}/questions/{questionId}/submit")
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Submit a response for a specific question")
    public ApiResponse<InterviewResponseDto> submitResponse(
            @PathVariable UUID interviewId,
            @PathVariable UUID questionId,
            @Valid @RequestBody SubmitResponseRequest request) {
        return ApiResponse.success(interviewService.submitResponse(interviewId, questionId, request), "Response submitted successfully");
    }

    @PostMapping("/{interviewId}/complete")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER')")
    @Operation(summary = "Complete the interview and generate final summary")
    public ApiResponse<InterviewDto> completeInterview(@PathVariable UUID interviewId) {
        return ApiResponse.success(interviewService.completeInterview(interviewId), "Interview completed successfully");
    }

    @GetMapping("/{interviewId}")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'HR_MANAGER')")
    @Operation(summary = "Get full details of the interview")
    public ApiResponse<InterviewDto> getInterviewDetails(@PathVariable UUID interviewId) {
        return ApiResponse.success(interviewService.getInterviewDetails(interviewId), "Interview details fetched successfully");
    }
}
