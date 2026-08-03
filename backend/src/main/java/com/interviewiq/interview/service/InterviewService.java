package com.interviewiq.interview.service;

import com.interviewiq.interview.dto.*;

import java.util.List;
import java.util.UUID;

public interface InterviewService {
    InterviewDto scheduleInterview(ScheduleInterviewRequest request);
    
    InterviewDto startInterview(UUID interviewId);
    
    List<InterviewQuestionDto> getQuestions(UUID interviewId);
    
    InterviewResponseDto submitResponse(UUID interviewId, UUID questionId, SubmitResponseRequest request);
    
    InterviewDto completeInterview(UUID interviewId);
    
    InterviewDto getInterviewDetails(UUID interviewId);
}
