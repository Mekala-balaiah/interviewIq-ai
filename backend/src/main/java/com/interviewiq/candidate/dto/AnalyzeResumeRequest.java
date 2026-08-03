package com.interviewiq.candidate.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AnalyzeResumeRequest {
    
    // Optional. If provided, scores ATS match against this job.
    // If not provided, it just extracts skills and does generic scoring.
    private UUID jobId;
}
