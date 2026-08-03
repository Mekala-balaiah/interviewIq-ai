package com.interviewiq.assessment.dto;

import com.interviewiq.assessment.enums.Verdict;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class AssessmentSubmissionDto {
    private UUID id;
    private UUID assessmentId;
    private UUID questionId;
    private UUID candidateId;
    
    private String code;
    private String language;
    private Verdict verdict;
    
    private Integer testCasesPassed;
    private Integer totalTestCases;
    private Integer executionTimeMs;
    private Integer memoryUsedKb;
    
    // testResults as JSON string
    private String testResults;
    
    private Instant submittedAt;
}
