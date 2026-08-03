package com.interviewiq.candidate.service;

import com.interviewiq.candidate.dto.ResumeAnalysisDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class MockAiServiceImpl implements AiService {

    @Override
    @Cacheable(value = "ats-scores", key = "#resumeText.hashCode() + '_' + (#jobDescription != null ? #jobDescription.hashCode() : 0)")
    public ResumeAnalysisDto analyzeResume(String resumeText, String jobDescription) {
        // In a real implementation, we would call OpenAI/Anthropic API here
        // and instruct the LLM to return structured JSON.
        
        ResumeAnalysisDto dto = new ResumeAnalysisDto();
        dto.setExtractedSkills(Arrays.asList("Java", "Spring Boot", "SQL", "Git", "REST APIs"));
        dto.setExtractedEducation(Arrays.asList("B.S. Computer Science - University of Tech"));
        dto.setExtractedExperience(Arrays.asList("Software Engineer at TechCorp (2020-2024)"));
        
        if (jobDescription != null && !jobDescription.isBlank()) {
            dto.setAtsScore(85);
            dto.setKeywordMatchScore(90);
            dto.setSemanticMatchScore(80);
            dto.setFormatScore(95);
            dto.setExperienceScore(85);
            dto.setMissingSkills(Arrays.asList("Microservices", "Docker"));
            dto.setAiSummary("Strong candidate with deep Java/Spring expertise. Lacks containerization experience required for the role.");
            dto.setImprovementSuggestions("Consider highlighting any exposure to Docker or Microservices architecture.");
        } else {
            dto.setAtsScore(0); // Generic parsing doesn't have a true ATS score
            dto.setKeywordMatchScore(0);
            dto.setSemanticMatchScore(0);
            dto.setFormatScore(90); // We can still score formatting
            dto.setExperienceScore(0);
            dto.setMissingSkills(List.of());
            dto.setAiSummary("Solid Java backend engineering resume.");
            dto.setImprovementSuggestions("Add metrics to bullet points to show impact.");
        }
        
        dto.setModelVersion("mock-ai-v1.0");
        return dto;
    }
}
