package com.interviewiq.candidate.service;

import com.interviewiq.candidate.dto.ResumeAnalysisDto;

public interface AiService {
    
    /**
     * Parse resume text and optionally match against a job description.
     * 
     * @param resumeText The raw text of the resume.
     * @param jobDescription Optional. The job description and requirements.
     * @return A dto containing parsed skills and simulated ATS scores.
     */
    ResumeAnalysisDto analyzeResume(String resumeText, String jobDescription);
}
