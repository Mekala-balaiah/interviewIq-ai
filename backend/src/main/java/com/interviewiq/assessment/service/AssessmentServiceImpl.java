package com.interviewiq.assessment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewiq.application.entity.Application;
import com.interviewiq.application.repository.ApplicationRepository;
import com.interviewiq.assessment.dto.*;
import com.interviewiq.assessment.entity.Assessment;
import com.interviewiq.assessment.entity.AssessmentQuestion;
import com.interviewiq.assessment.entity.AssessmentSubmission;
import com.interviewiq.assessment.enums.AssessmentStatus;
import com.interviewiq.assessment.enums.Verdict;
import com.interviewiq.assessment.mapper.AssessmentMapper;
import com.interviewiq.assessment.repository.AssessmentQuestionRepository;
import com.interviewiq.assessment.repository.AssessmentRepository;
import com.interviewiq.assessment.repository.AssessmentSubmissionRepository;
import com.interviewiq.common.exception.BusinessException;
import com.interviewiq.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final AssessmentQuestionRepository questionRepository;
    private final AssessmentSubmissionRepository submissionRepository;
    private final ApplicationRepository applicationRepository;
    private final CodeExecutionService codeExecutionService;
    private final AssessmentMapper assessmentMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public AssessmentDto assignAssessment(CreateAssessmentRequest request) {
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", request.getApplicationId()));

        Assessment assessment = new Assessment();
        assessment.setApplication(application);
        assessment.setJob(application.getJob());
        assessment.setCandidate(application.getCandidate());
        assessment.setTitle(application.getJob().getTitle() + " - Technical Assessment");
        assessment.setDifficulty(request.getDifficulty() != null ? request.getDifficulty() : "MEDIUM");
        assessment.setStatus(AssessmentStatus.NOT_STARTED);
        assessment.setDurationMinutes(60);
        assessment.setTotalMarks(100);
        
        Assessment savedAssessment = assessmentRepository.save(assessment);
        
        // Generate mock questions
        createMockQuestions(savedAssessment);
        
        return assessmentMapper.toDto(savedAssessment);
    }

    @Override
    @Transactional
    public AssessmentDto startAssessment(UUID assessmentId) {
        Assessment assessment = getAssessment(assessmentId);
        
        if (assessment.getStatus() != AssessmentStatus.NOT_STARTED) {
            throw new BusinessException("Only NOT_STARTED assessments can be started.");
        }
        
        assessment.setStatus(AssessmentStatus.IN_PROGRESS);
        assessment.setStartedAt(Instant.now());
        // Set deadline based on duration
        assessment.setDeadlineAt(assessment.getStartedAt().plusSeconds(assessment.getDurationMinutes() * 60L));
        
        return assessmentMapper.toDto(assessmentRepository.save(assessment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssessmentQuestionDto> getQuestions(UUID assessmentId) {
        return questionRepository.findByAssessmentIdOrderBySequenceNumberAsc(assessmentId)
                .stream()
                .map(assessmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AssessmentSubmissionDto submitCode(UUID assessmentId, UUID questionId, SubmitCodeRequest request) {
        Assessment assessment = getAssessment(assessmentId);
        if (assessment.getStatus() != AssessmentStatus.IN_PROGRESS) {
            throw new BusinessException("Cannot submit code. Assessment is not IN_PROGRESS.");
        }
        
        if (Instant.now().isAfter(assessment.getDeadlineAt())) {
            throw new BusinessException("Assessment deadline has passed.");
        }

        AssessmentQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("AssessmentQuestion", "id", questionId));
                
        if (!question.getAssessment().getId().equals(assessmentId)) {
            throw new BusinessException("Question does not belong to this assessment.");
        }

        // Setup test cases
        List<TestCaseDto> testCases = List.of(
            new TestCaseDto().setInput("1 2").setExpectedOutput("3"),
            new TestCaseDto().setInput("5 5").setExpectedOutput("10")
        );

        // Execute code
        ExecutionResultDto result = codeExecutionService.executeCode(request.getCode(), request.getLanguage(), testCases);
        
        AssessmentSubmission submission = new AssessmentSubmission();
        submission.setAssessment(assessment);
        submission.setQuestion(question);
        submission.setCandidate(assessment.getCandidate());
        submission.setCode(request.getCode());
        submission.setLanguage(request.getLanguage());
        submission.setVerdict(result.getVerdict());
        submission.setTestCasesPassed(result.getTestCasesPassed());
        submission.setTotalTestCases(result.getTotalTestCases());
        submission.setExecutionTimeMs(result.getExecutionTimeMs());
        submission.setMemoryUsedKb(result.getMemoryUsedKb());
        submission.setTestResults(result.getDetailedResults());

        return assessmentMapper.toDto(submissionRepository.save(submission));
    }

    @Override
    @Transactional
    public AssessmentDto completeAssessment(UUID assessmentId) {
        Assessment assessment = getAssessment(assessmentId);
        if (assessment.getStatus() != AssessmentStatus.IN_PROGRESS) {
            throw new BusinessException("Cannot complete assessment. It is not IN_PROGRESS.");
        }
        
        assessment.setStatus(AssessmentStatus.EVALUATED);
        assessment.setSubmittedAt(Instant.now());
        
        List<AssessmentSubmission> submissions = submissionRepository.findByAssessmentId(assessmentId);
        List<AssessmentQuestion> questions = questionRepository.findByAssessmentIdOrderBySequenceNumberAsc(assessmentId);
        
        int totalScore = 0;
        BigDecimal maxPlagiarism = BigDecimal.ZERO;
        
        for (AssessmentQuestion q : questions) {
            // Find latest submission for this question
            AssessmentSubmission latestSub = submissions.stream()
                .filter(s -> s.getQuestion().getId().equals(q.getId()))
                .reduce((first, second) -> second)
                .orElse(null);
                
            if (latestSub != null) {
                if (latestSub.getVerdict() == Verdict.ACCEPTED) {
                    totalScore += q.getMarks();
                } else if (latestSub.getTotalTestCases() > 0) {
                    // Partial marks
                    totalScore += (int) ((double) latestSub.getTestCasesPassed() / latestSub.getTotalTestCases() * q.getMarks());
                }
                
                BigDecimal plagScore = codeExecutionService.checkPlagiarism(latestSub.getCode(), q.getId());
                if (plagScore.compareTo(maxPlagiarism) > 0) {
                    maxPlagiarism = plagScore;
                }
            }
        }
        
        assessment.setScoreObtained(totalScore);
        assessment.setPercentage((int) (((double) totalScore / assessment.getTotalMarks()) * 100));
        assessment.setPlagiarismScore(maxPlagiarism);
        if (maxPlagiarism.compareTo(BigDecimal.valueOf(70.0)) > 0) {
            assessment.setPlagiarismFlagged(true);
        }
        
        return assessmentMapper.toDto(assessmentRepository.save(assessment));
    }

    @Override
    @Transactional(readOnly = true)
    public AssessmentDto getAssessmentDetails(UUID assessmentId) {
        return assessmentMapper.toDto(getAssessment(assessmentId));
    }
    
    private Assessment getAssessment(UUID id) {
        return assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment", "id", id));
    }
    
    private void createMockQuestions(Assessment assessment) {
        try {
            AssessmentQuestion q1 = new AssessmentQuestion();
            q1.setAssessment(assessment);
            q1.setSequenceNumber(1);
            q1.setProblemStatement("Write a function to return the sum of two integers.");
            q1.setTopic("Basics");
            q1.setMarks(50);
            q1.setTestCases("[{\"input\":\"1 2\",\"output\":\"3\"},{\"input\":\"5 5\",\"output\":\"10\"}]");
            q1.setStarterCode("{\"JAVA\":\"class Solution {\\n  public int sum(int a, int b) {\\n    \\n  }\\n}\"}");
            questionRepository.save(q1);
            
            AssessmentQuestion q2 = new AssessmentQuestion();
            q2.setAssessment(assessment);
            q2.setSequenceNumber(2);
            q2.setProblemStatement("Given an array of integers, find the maximum subarray sum.");
            q2.setTopic("Dynamic Programming");
            q2.setMarks(50);
            q2.setTestCases("[{\"input\":\"[-2,1,-3,4,-1,2,1,-5,4]\",\"output\":\"6\"}]");
            q2.setStarterCode("{\"JAVA\":\"class Solution {\\n  public int maxSubArray(int[] nums) {\\n    \\n  }\\n}\"}");
            questionRepository.save(q2);
        } catch (Exception e) {
            log.error("Failed to mock questions", e);
        }
    }
}
