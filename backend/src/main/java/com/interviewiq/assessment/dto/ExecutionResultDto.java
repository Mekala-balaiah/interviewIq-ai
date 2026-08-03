package com.interviewiq.assessment.dto;

import com.interviewiq.assessment.enums.Verdict;
import lombok.Data;

@Data
public class ExecutionResultDto {
    private Verdict verdict;
    private int testCasesPassed;
    private int totalTestCases;
    private int executionTimeMs;
    private int memoryUsedKb;
    
    // Storing detailed results as JSON string (could be a complex nested object)
    private String detailedResults;
}
