package com.interviewiq.assessment.service;

import com.interviewiq.assessment.dto.*;

import java.util.List;
import java.util.UUID;

public interface AssessmentService {
    AssessmentDto assignAssessment(CreateAssessmentRequest request);
    
    AssessmentDto startAssessment(UUID assessmentId);
    
    List<AssessmentQuestionDto> getQuestions(UUID assessmentId);
    
    AssessmentSubmissionDto submitCode(UUID assessmentId, UUID questionId, SubmitCodeRequest request);
    
    AssessmentDto completeAssessment(UUID assessmentId);
    
    AssessmentDto getAssessmentDetails(UUID assessmentId);
}
