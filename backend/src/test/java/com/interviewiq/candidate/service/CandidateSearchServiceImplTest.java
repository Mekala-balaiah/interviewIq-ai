package com.interviewiq.candidate.service;

import com.interviewiq.auth.entity.User;
import com.interviewiq.candidate.document.CandidateDocument;
import com.interviewiq.candidate.entity.CandidateProfile;
import com.interviewiq.candidate.entity.CandidateSkill;
import com.interviewiq.candidate.entity.Skill;
import com.interviewiq.candidate.repository.CandidateProfileRepository;
import com.interviewiq.candidate.repository.CandidateSearchRepository;
import com.interviewiq.candidate.repository.CandidateSkillRepository;
import com.interviewiq.common.exception.ResourceNotFoundException;
import com.interviewiq.common.response.PagedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitsImpl;
import org.springframework.data.elasticsearch.core.TotalHitsRelation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateSearchServiceImplTest {

    @Mock
    private CandidateProfileRepository candidateProfileRepository;

    @Mock
    private CandidateSkillRepository candidateSkillRepository;

    @Mock
    private CandidateSearchRepository candidateSearchRepository;

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @InjectMocks
    private CandidateSearchServiceImpl candidateSearchService;

    private UUID candidateId;
    private CandidateProfile profile;

    @BeforeEach
    void setUp() {
        candidateId = UUID.randomUUID();

        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");

        profile = new CandidateProfile();
        profile.setId(candidateId);
        profile.setUser(user);
        profile.setHeadline("Senior Java Engineer");
        profile.setCurrentTitle("Software Engineer");
        profile.setLocation("Remote");
        profile.setYearsOfExperience(5);
        profile.setOpenToRemote(true);
    }

    @Test
    void syncCandidate_WithValidProfile_SavesDocument() {
        Skill skill = new Skill();
        skill.setName("Java");

        CandidateSkill cs = new CandidateSkill();
        cs.setSkill(skill);

        when(candidateProfileRepository.findById(candidateId)).thenReturn(Optional.of(profile));
        when(candidateSkillRepository.findByCandidateId(candidateId)).thenReturn(List.of(cs));

        candidateSearchService.syncCandidate(candidateId);

        ArgumentCaptor<CandidateDocument> docCaptor = ArgumentCaptor.forClass(CandidateDocument.class);
        verify(candidateSearchRepository).save(docCaptor.capture());

        CandidateDocument saved = docCaptor.getValue();
        assertEquals(candidateId, saved.getId());
        assertEquals("John Doe", saved.getFullName());
        assertEquals("Senior Java Engineer", saved.getHeadline());
        assertEquals("Remote", saved.getLocation());
        assertTrue(saved.getSkills().contains("Java"));
    }

    @Test
    void syncCandidate_WithMissingProfile_ThrowsResourceNotFoundException() {
        when(candidateProfileRepository.findById(candidateId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> candidateSearchService.syncCandidate(candidateId));

        verify(candidateSearchRepository, never()).save(any());
    }

    @Test
    void searchCandidates_WithKeyword_ReturnsPagedResults() {
        CandidateDocument doc = CandidateDocument.builder()
                .id(candidateId)
                .headline("Senior Java Engineer")
                .location("Remote")
                .build();

        SearchHit<CandidateDocument> hit = new SearchHit<>(
                "candidates", candidateId.toString(), null, 1.0f, null, null, null, null, null, null, doc
        );

        SearchHits<CandidateDocument> searchHits = new SearchHitsImpl<>(
                1, TotalHitsRelation.EQUAL_TO, 1.0f, "candidates", null, null, List.of(hit), null, null
        );

        when(elasticsearchOperations.search(any(), eq(CandidateDocument.class))).thenReturn(searchHits);

        PagedResponse<CandidateDocument> result = candidateSearchService.searchCandidates(
                "java", null, null, null, null, 0, 10
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Senior Java Engineer", result.getContent().get(0).getHeadline());
    }

    @Test
    void searchCandidates_WithNoResults_ReturnsEmptyPage() {
        SearchHits<CandidateDocument> searchHits = new SearchHitsImpl<>(
                0, TotalHitsRelation.EQUAL_TO, 0.0f, "candidates", null, null, List.of(), null, null
        );

        when(elasticsearchOperations.search(any(), eq(CandidateDocument.class))).thenReturn(searchHits);

        PagedResponse<CandidateDocument> result = candidateSearchService.searchCandidates(
                "nonexistent", null, null, null, null, 0, 10
        );

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }
}
