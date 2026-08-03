package com.interviewiq.candidate.service;

import com.interviewiq.candidate.entity.CandidateProfile;
import com.interviewiq.candidate.repository.CandidateProfileRepository;
import com.interviewiq.candidate.repository.CandidateSkillRepository;
import com.interviewiq.candidate.repository.ResumeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

    @Mock
    private CandidateProfileRepository profileRepository;

    @Mock
    private CandidateSkillRepository candidateSkillRepository;

    @Mock
    private ResumeRepository resumeRepository;

    @InjectMocks
    private CandidateServiceImpl candidateService;

    @Test
    void testCalculateProfileCompletion_EmptyProfile() {
        UUID candidateId = UUID.randomUUID();
        CandidateProfile profile = CandidateProfile.builder()
                .id(candidateId)
                .build();

        when(profileRepository.findById(candidateId)).thenReturn(Optional.of(profile));
        when(candidateSkillRepository.findByCandidateId(candidateId)).thenReturn(List.of());
        when(resumeRepository.findByCandidateId(candidateId)).thenReturn(List.of());

        candidateService.calculateProfileCompletion(candidateId);

        verify(profileRepository).save(profile);
        assertEquals(0, profile.getProfileCompletionPct());
        assertFalse(profile.getProfileComplete());
    }

    @Test
    void testCalculateProfileCompletion_FullProfile() {
        UUID candidateId = UUID.randomUUID();
        CandidateProfile profile = CandidateProfile.builder()
                .id(candidateId)
                .headline("Software Engineer") // 10
                .bio("I write code") // 10
                .location("New York") // 10
                .linkedinUrl("url") // 10
                .yearsOfExperience(5) // 10
                .currentTitle("Senior SWE") // 10
                .currentCompany("Tech Corp") // 10
                .build();

        when(profileRepository.findById(candidateId)).thenReturn(Optional.of(profile));
        // Mock 3 skills -> 15 points
        when(candidateSkillRepository.findByCandidateId(candidateId)).thenReturn(List.of(
                mock(com.interviewiq.candidate.entity.CandidateSkill.class),
                mock(com.interviewiq.candidate.entity.CandidateSkill.class),
                mock(com.interviewiq.candidate.entity.CandidateSkill.class)
        ));
        // Mock 1 resume -> 15 points
        when(resumeRepository.findByCandidateId(candidateId)).thenReturn(List.of(
                mock(com.interviewiq.candidate.entity.Resume.class)
        ));

        candidateService.calculateProfileCompletion(candidateId);

        verify(profileRepository).save(profile);
        // Total expected = 40 (basic) + 30 (exp) + 15 (skills) + 15 (resume) = 100
        assertEquals(100, profile.getProfileCompletionPct());
        assertTrue(profile.getProfileComplete());
    }
}
