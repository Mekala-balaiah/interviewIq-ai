package com.interviewiq.assessment.service;

import com.interviewiq.assessment.dto.ExecutionResultDto;
import com.interviewiq.assessment.dto.TestCaseDto;
import com.interviewiq.assessment.enums.Verdict;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MockCodeExecutionServiceImpl implements CodeExecutionService {

    @Override
    public ExecutionResultDto executeCode(String code, String language, List<TestCaseDto> testCases) {
        ExecutionResultDto result = new ExecutionResultDto();
        result.setTotalTestCases(testCases != null ? testCases.size() : 0);
        result.setExecutionTimeMs(ThreadLocalRandom.current().nextInt(10, 500));
        result.setMemoryUsedKb(ThreadLocalRandom.current().nextInt(1024, 65536));
        
        if (code == null || code.trim().isEmpty()) {
            result.setVerdict(Verdict.COMPILATION_ERROR);
            result.setTestCasesPassed(0);
            result.setDetailedResults("{\"error\": \"Code cannot be empty\"}");
            return result;
        }

        if (code.toLowerCase().contains("while(true)") || code.toLowerCase().contains("while (true)")) {
            result.setVerdict(Verdict.TIME_LIMIT_EXCEEDED);
            result.setTestCasesPassed(0);
            result.setDetailedResults("{\"error\": \"Time limit exceeded on test case 1\"}");
            return result;
        }

        // Mock 80% chance of ACCEPTED if code is reasonably long
        if (code.length() > 20 && ThreadLocalRandom.current().nextDouble() > 0.2) {
            result.setVerdict(Verdict.ACCEPTED);
            result.setTestCasesPassed(result.getTotalTestCases());
            result.setDetailedResults("{\"status\": \"All test cases passed successfully\"}");
        } else {
            result.setVerdict(Verdict.WRONG_ANSWER);
            result.setTestCasesPassed(Math.max(0, result.getTotalTestCases() - 1));
            result.setDetailedResults("{\"error\": \"Failed on hidden test case 3\"}");
        }

        return result;
    }

    @Override
    public BigDecimal checkPlagiarism(String code, UUID questionId) {
        // Mock plagiarism check: 5% chance of being flagged highly
        if (ThreadLocalRandom.current().nextDouble() > 0.95) {
            return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(75.0, 100.0));
        }
        return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(0.0, 30.0));
    }
}
