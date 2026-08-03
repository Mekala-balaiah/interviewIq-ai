package com.interviewiq.interview.service;

import com.interviewiq.interview.entity.InterviewQuestion;
import com.interviewiq.interview.entity.InterviewResponse;
import com.interviewiq.job.entity.Job;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MockInterviewAiServiceImpl implements InterviewAiService {

    @Override
    @Cacheable(value = "ai-responses", key = "#job.id.toString() + '_' + #count")
    public List<InterviewQuestion> generateQuestions(Job job, int count) {
        List<InterviewQuestion> questions = new ArrayList<>();
        
        for (int i = 1; i <= count; i++) {
            InterviewQuestion q = new InterviewQuestion();
            q.setSequenceNumber(i);
            q.setQuestionType("TECHNICAL");
            q.setDifficulty("MEDIUM");
            
            if (i == 1) {
                q.setTopic("Core Concepts");
                q.setQuestionText("Explain the core differences between a monolith and a microservices architecture.");
                q.setExpectedAnswer("A monolith is a unified unit where all components are tightly coupled. Microservices are independent, loosely coupled services communicating via APIs.");
            } else if (i == 2) {
                q.setTopic("Databases");
                q.setQuestionText("When would you choose a NoSQL database over a relational database?");
                q.setExpectedAnswer("When dealing with unstructured data, requiring high horizontal scalability, or when schema flexibility is paramount.");
            } else {
                q.setTopic("General " + job.getTitle());
                q.setQuestionText("Can you describe a challenging problem you solved related to " + job.getTitle() + "?");
                q.setExpectedAnswer("Candidate should provide a structured STAR method response detailing a complex technical challenge.");
            }
            questions.add(q);
        }
        
        return questions;
    }

    @Override
    @Cacheable(value = "ai-responses", key = "#questionText.hashCode() + '_' + #candidateResponse.hashCode()")
    public Map<String, Object> evaluateResponse(String questionText, String expectedAnswer, String candidateResponse) {
        Map<String, Object> result = new HashMap<>();
        
        // Mock evaluation logic
        if (candidateResponse == null || candidateResponse.trim().length() < 10) {
            result.put("score", 2);
            result.put("feedback", "Response is too short or missing key details. Expected: " + expectedAnswer);
        } else if (candidateResponse.toLowerCase().contains("microservice") || candidateResponse.toLowerCase().contains("nosql")) {
            result.put("score", 9);
            result.put("feedback", "Excellent answer. You clearly understand the core concepts and trade-offs.");
        } else {
            result.put("score", 6);
            result.put("feedback", "Good attempt, but could be more detailed. Consider mentioning specific architectural trade-offs.");
        }
        
        return result;
    }

    @Override
    public Map<String, Object> generateSummary(List<InterviewResponse> responses) {
        Map<String, Object> result = new HashMap<>();
        
        if (responses.isEmpty()) {
            result.put("overallScore", 0);
            result.put("summary", "No responses provided.");
            return result;
        }
        
        int totalScore = 0;
        for (InterviewResponse r : responses) {
            totalScore += (r.getAiScore() != null ? r.getAiScore() : 0);
        }
        
        // Average score out of 10, converted to a percentage out of 100
        int averagePercentage = (int) (((double) totalScore / (responses.size() * 10)) * 100);
        
        result.put("overallScore", averagePercentage);
        
        if (averagePercentage >= 80) {
            result.put("summary", "Strong performance. The candidate demonstrated solid technical knowledge and articulated their thoughts clearly.");
        } else if (averagePercentage >= 60) {
            result.put("summary", "Average performance. The candidate knows the basics but struggled with some deeper technical nuances.");
        } else {
            result.put("summary", "Poor performance. The candidate lacked fundamental knowledge required for this role.");
        }
        
        return result;
    }
}
