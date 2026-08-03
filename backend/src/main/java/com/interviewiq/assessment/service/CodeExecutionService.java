package com.interviewiq.assessment.service;

import com.interviewiq.assessment.dto.ExecutionResultDto;
import com.interviewiq.assessment.dto.TestCaseDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CodeExecutionService {
    
    /**
     * Executes candidate code against a list of test cases.
     */
    ExecutionResultDto executeCode(String code, String language, List<TestCaseDto> testCases);
    
    /**
     * Checks if the given code is similar to existing submissions.
     * Returns a plagiarism score from 0.00 to 100.00.
     */
    BigDecimal checkPlagiarism(String code, UUID questionId);
}
