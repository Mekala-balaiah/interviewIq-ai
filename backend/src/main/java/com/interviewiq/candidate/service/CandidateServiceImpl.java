package com.interviewiq.candidate.service;

import com.interviewiq.auth.entity.User;
import com.interviewiq.auth.repository.UserRepository;
import com.interviewiq.candidate.dto.CandidateProfileDto;
import com.interviewiq.candidate.dto.UpdateCandidateProfileRequest;
import com.interviewiq.candidate.dto.CandidateSearchRequest;
import com.interviewiq.candidate.entity.CandidateProfile;
import com.interviewiq.candidate.mapper.CandidateMapper;
import com.interviewiq.candidate.repository.CandidateProfileRepository;
import com.interviewiq.candidate.repository.CandidateSkillRepository;
import com.interviewiq.candidate.repository.CandidateSpecification;
import com.interviewiq.candidate.repository.ResumeRepository;
import com.interviewiq.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final CandidateSkillRepository candidateSkillRepository;
    private final ResumeRepository resumeRepository;
    private final CandidateMapper candidateMapper;
    private final CandidateSearchService candidateSearchService;

    @Override
    @Transactional
    public CandidateProfileDto getProfileByUserId(UUID userId) {
        CandidateProfile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultProfile(userId));
                
        return candidateMapper.toDto(profile);
    }

    @Override
    @Transactional
    public CandidateProfileDto updateProfile(UUID userId, UpdateCandidateProfileRequest request) {
        CandidateProfile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultProfile(userId));

        if (request.getHeadline() != null) profile.setHeadline(request.getHeadline());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getLocation() != null) profile.setLocation(request.getLocation());
        if (request.getLinkedinUrl() != null) profile.setLinkedinUrl(request.getLinkedinUrl());
        if (request.getGithubUrl() != null) profile.setGithubUrl(request.getGithubUrl());
        if (request.getPortfolioUrl() != null) profile.setPortfolioUrl(request.getPortfolioUrl());
        if (request.getYearsOfExperience() != null) profile.setYearsOfExperience(request.getYearsOfExperience());
        if (request.getCurrentTitle() != null) profile.setCurrentTitle(request.getCurrentTitle());
        if (request.getCurrentCompany() != null) profile.setCurrentCompany(request.getCurrentCompany());
        if (request.getEmploymentStatus() != null) profile.setEmploymentStatus(request.getEmploymentStatus());
        if (request.getNoticePeriod() != null) profile.setNoticePeriod(request.getNoticePeriod());
        if (request.getExpectedSalaryMin() != null) profile.setExpectedSalaryMin(request.getExpectedSalaryMin());
        if (request.getExpectedSalaryMax() != null) profile.setExpectedSalaryMax(request.getExpectedSalaryMax());
        if (request.getSalaryCurrency() != null) profile.setSalaryCurrency(request.getSalaryCurrency());
        if (request.getAvailability() != null) profile.setAvailability(request.getAvailability());
        if (request.getOpenToRemote() != null) profile.setOpenToRemote(request.getOpenToRemote());

        profile = profileRepository.save(profile);
        
        calculateProfileCompletion(profile.getId());
        
        // Sync updated profile to Elasticsearch
        try {
            candidateSearchService.syncCandidate(profile.getId());
        } catch (Exception e) {
            log.warn("Failed to sync candidate {} to Elasticsearch after profile update: {}", profile.getId(), e.getMessage());
        }
        
        return candidateMapper.toDto(profile);
    }

    @Override
    @Transactional
    public void calculateProfileCompletion(UUID candidateId) {
        CandidateProfile profile = profileRepository.findById(candidateId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate Profile not found"));

        int score = 0;
        
        // Basic Info (40%)
        if (profile.getHeadline() != null && !profile.getHeadline().isEmpty()) score += 10;
        if (profile.getBio() != null && !profile.getBio().isEmpty()) score += 10;
        if (profile.getLocation() != null && !profile.getLocation().isEmpty()) score += 10;
        if (profile.getLinkedinUrl() != null && !profile.getLinkedinUrl().isEmpty()) score += 10;
        
        // Experience Details (30%)
        if (profile.getYearsOfExperience() != null && profile.getYearsOfExperience() > 0) score += 10;
        if (profile.getCurrentTitle() != null && !profile.getCurrentTitle().isEmpty()) score += 10;
        if (profile.getCurrentCompany() != null && !profile.getCurrentCompany().isEmpty()) score += 10;
        
        // Skills (15%)
        long skillCount = candidateSkillRepository.findByCandidateId(candidateId).size();
        if (skillCount > 0) score += 5;
        if (skillCount >= 3) score += 10; // Max 15 for skills
        
        // Resumes (15%)
        long resumeCount = resumeRepository.findByCandidateId(candidateId).size();
        if (resumeCount > 0) score += 15;
        
        profile.setProfileCompletionPct(score);
        profile.setProfileComplete(score >= 80);
        
        profileRepository.save(profile);
    }
    
    private CandidateProfile createDefaultProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        CandidateProfile profile = CandidateProfile.builder()
                .user(user)
                .yearsOfExperience(0)
                .openToRemote(true)
                .profileComplete(false)
                .profileCompletionPct(0)
                .build();
                
        return profileRepository.save(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CandidateProfileDto> searchCandidates(CandidateSearchRequest request, Pageable pageable) {
        Specification<CandidateProfile> spec = Specification.where(CandidateSpecification.hasKeyword(request.getKeyword()))
                .and(CandidateSpecification.hasLocation(request.getLocation()))
                .and(CandidateSpecification.hasMinExperience(request.getMinExperience()))
                .and(CandidateSpecification.isOpenToRemote(request.getOpenToRemote()))
                .and(CandidateSpecification.hasSkills(request.getSkills()));

        Page<CandidateProfile> profiles = profileRepository.findAll(spec, pageable);
        return profiles.map(candidateMapper::toDto);
    }
}
