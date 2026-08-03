package com.interviewiq.candidate.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class ResumeAnalysisDto {
    private UUID id;
    private UUID resumeId;
    private UUID jobId;
    
    private int atsScore;
    private int keywordMatchScore;
    private int semanticMatchScore;
    private int formatScore;
    private int experienceScore;
    
    private List<String> extractedSkills;
    private List<String> extractedEducation;
    private List<String> extractedExperience;
    private List<String> missingSkills;
    
    private String aiSummary;
    private String improvementSuggestions;
    private String modelVersion;
    
    private Instant createdAt;
}
