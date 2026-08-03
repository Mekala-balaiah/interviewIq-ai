package com.interviewiq.candidate.service;

import com.interviewiq.candidate.dto.CandidateProfileDto;
import com.interviewiq.candidate.dto.UpdateCandidateProfileRequest;

import java.util.UUID;

public interface CandidateService {
    
    CandidateProfileDto getProfileByUserId(UUID userId);
    
    CandidateProfileDto updateProfile(UUID userId, UpdateCandidateProfileRequest request);
    
    void calculateProfileCompletion(UUID candidateId);
}
