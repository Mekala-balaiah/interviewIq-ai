package com.interviewiq.candidate.service;

import com.interviewiq.candidate.dto.AddCandidateSkillRequest;
import com.interviewiq.candidate.dto.CandidateSkillDto;

import java.util.List;
import java.util.UUID;

public interface CandidateSkillService {
    
    List<CandidateSkillDto> getCandidateSkills(UUID userId);
    
    CandidateSkillDto addSkillToProfile(UUID userId, AddCandidateSkillRequest request);
    
    void removeSkillFromProfile(UUID userId, UUID candidateSkillId);
}
