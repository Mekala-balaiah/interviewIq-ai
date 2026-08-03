package com.interviewiq.interview.service;

import com.interviewiq.interview.entity.InterviewQuestion;
import com.interviewiq.interview.entity.InterviewResponse;
import com.interviewiq.job.entity.Job;

import java.util.List;
import java.util.Map;

public interface InterviewAiService {
    
    /**
     * Generates a list of questions for a given job description.
     */
    List<InterviewQuestion> generateQuestions(Job job, int count);
    
    /**
     * Evaluates a candidate's response against an expected answer.
     * Returns a map with keys "score" (Integer 0-10) and "feedback" (String).
     */
    Map<String, Object> evaluateResponse(String questionText, String expectedAnswer, String candidateResponse);
    
    /**
     * Generates an overall summary and score out of 100 based on all responses.
     * Returns a map with keys "overallScore" (Integer 0-100) and "summary" (String).
     */
    Map<String, Object> generateSummary(List<InterviewResponse> responses);
}
