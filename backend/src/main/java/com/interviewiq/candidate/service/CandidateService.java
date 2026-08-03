package com.interviewiq.candidate.service;

import com.interviewiq.candidate.dto.CandidateProfileDto;
import com.interviewiq.candidate.dto.UpdateCandidateProfileRequest;
import com.interviewiq.candidate.dto.CandidateSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CandidateService {
    
    CandidateProfileDto getProfileByUserId(UUID userId);
    
    CandidateProfileDto updateProfile(UUID userId, UpdateCandidateProfileRequest request);
    
    void calculateProfileCompletion(UUID candidateId);
    
    Page<CandidateProfileDto> searchCandidates(CandidateSearchRequest request, Pageable pageable);
}
