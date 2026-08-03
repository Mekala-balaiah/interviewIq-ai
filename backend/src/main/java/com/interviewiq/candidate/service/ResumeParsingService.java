package com.interviewiq.candidate.service;

import com.interviewiq.candidate.dto.ResumeAnalysisDto;

import java.util.UUID;

public interface ResumeParsingService {
    ResumeAnalysisDto analyzeResume(UUID resumeId, UUID jobId);
}
