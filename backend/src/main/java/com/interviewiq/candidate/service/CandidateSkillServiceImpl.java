package com.interviewiq.candidate.service;

import com.interviewiq.candidate.dto.AddCandidateSkillRequest;
import com.interviewiq.candidate.dto.CandidateSkillDto;
import com.interviewiq.candidate.entity.CandidateProfile;
import com.interviewiq.candidate.entity.CandidateSkill;
import com.interviewiq.candidate.entity.Skill;
import com.interviewiq.candidate.mapper.SkillMapper;
import com.interviewiq.candidate.repository.CandidateProfileRepository;
import com.interviewiq.candidate.repository.CandidateSkillRepository;
import com.interviewiq.candidate.repository.SkillRepository;
import com.interviewiq.common.exception.BusinessException;
import com.interviewiq.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateSkillServiceImpl implements CandidateSkillService {

    private final CandidateProfileRepository candidateProfileRepository;
    private final CandidateSkillRepository candidateSkillRepository;
    private final SkillRepository skillRepository;
    private final SkillService skillService;
    private final SkillMapper skillMapper;
    private final CandidateService candidateService;
    private final CandidateSearchService candidateSearchService;

    @Override
    @Transactional(readOnly = true)
    public List<CandidateSkillDto> getCandidateSkills(UUID userId) {
        CandidateProfile profile = candidateProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));
                
        return candidateSkillRepository.findByCandidateId(profile.getId())
                .stream()
                .map(skillMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CandidateSkillDto addSkillToProfile(UUID userId, AddCandidateSkillRequest request) {
        CandidateProfile profile = candidateProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));

        // Get or Create the skill in the normalized dictionary
        Skill skill = skillRepository.findById(skillService.findOrCreateSkill(request.getSkillName()).getId())
                .orElseThrow(() -> new BusinessException("Failed to find or create skill"));

        // Check if candidate already has this skill
        if (candidateSkillRepository.findByCandidateIdAndSkillId(profile.getId(), skill.getId()).isPresent()) {
            throw new BusinessException("Skill already exists on candidate profile");
        }

        CandidateSkill candidateSkill = CandidateSkill.builder()
                .candidate(profile)
                .skill(skill)
                .proficiencyLevel(request.getProficiencyLevel())
                .yearsExperience(request.getYearsExperience() != null ? request.getYearsExperience() : 0)
                .isPrimary(request.getIsPrimary() != null ? request.getIsPrimary() : false)
                .build();

        candidateSkill = candidateSkillRepository.save(candidateSkill);
        
        // Increment global skill usage count
        skill.setUsageCount(skill.getUsageCount() + 1);
        skillRepository.save(skill);
        
        candidateService.calculateProfileCompletion(profile.getId());

        // Sync skill changes to Elasticsearch
        try {
            candidateSearchService.syncCandidate(profile.getId());
        } catch (Exception e) {
            // Non-blocking: log the failure but don't break the add-skill flow
            log.warn("Failed to sync candidate {} to ES after skill add: {}", profile.getId(), e.getMessage());
        }

        return skillMapper.toDto(candidateSkill);
    }

    @Override
    @Transactional
    public void removeSkillFromProfile(UUID userId, UUID candidateSkillId) {
        CandidateProfile profile = candidateProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));

        CandidateSkill candidateSkill = candidateSkillRepository.findById(candidateSkillId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate skill not found"));

        if (!candidateSkill.getCandidate().getId().equals(profile.getId())) {
            throw new BusinessException("You don't have permission to remove this skill");
        }
        
        Skill skill = candidateSkill.getSkill();

        candidateSkillRepository.delete(candidateSkill);
        
        // Decrement global skill usage count
        if (skill.getUsageCount() > 0) {
            skill.setUsageCount(skill.getUsageCount() - 1);
            skillRepository.save(skill);
        }
        
        candidateService.calculateProfileCompletion(profile.getId());

        // Sync skill removal to Elasticsearch
        try {
            candidateSearchService.syncCandidate(profile.getId());
        } catch (Exception e) {
            log.warn("Failed to sync candidate {} to ES after skill removal: {}", profile.getId(), e.getMessage());
        }
    }
}
