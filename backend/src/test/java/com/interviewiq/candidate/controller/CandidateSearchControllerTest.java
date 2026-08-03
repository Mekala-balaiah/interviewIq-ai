package com.interviewiq.candidate.controller;

import com.interviewiq.auth.enums.UserRole;
import com.interviewiq.auth.security.JwtAuthenticationFilter;
import com.interviewiq.auth.security.UserPrincipal;
import com.interviewiq.candidate.document.CandidateDocument;
import com.interviewiq.candidate.service.CandidateSearchService;
import com.interviewiq.common.response.PagedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = CandidateSearchController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class CandidateSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CandidateSearchService candidateSearchService;

    private UserPrincipal recruiterPrincipal;
    private UserPrincipal adminPrincipal;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        recruiterPrincipal = new UserPrincipal(
                userId, "recruiter@example.com", "password", UserRole.RECRUITER,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_RECRUITER"))
        );

        adminPrincipal = new UserPrincipal(
                userId, "admin@example.com", "password", UserRole.ADMIN,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
    }

    @Test
    void searchCandidates_WithKeyword_ReturnsOk() throws Exception {
        CandidateDocument doc = CandidateDocument.builder()
                .id(UUID.randomUUID())
                .headline("Senior Java Engineer")
                .location("Remote")
                .build();

        PagedResponse<CandidateDocument> response = new PagedResponse<>(
                List.of(doc), 0, 20, 1, 1, true
        );

        when(candidateSearchService.searchCandidates(
                anyString(), isNull(), isNull(), isNull(), isNull(), anyInt(), anyInt()
        )).thenReturn(response);

        mockMvc.perform(get("/api/v1/candidates/search")
                        .param("keyword", "java")
                        .with(SecurityMockMvcRequestPostProcessors.user(recruiterPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].headline").value("Senior Java Engineer"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void searchCandidates_WithNoParams_ReturnsOk() throws Exception {
        PagedResponse<CandidateDocument> response = new PagedResponse<>(
                List.of(), 0, 20, 0, 0, true
        );

        when(candidateSearchService.searchCandidates(
                isNull(), isNull(), isNull(), isNull(), isNull(), anyInt(), anyInt()
        )).thenReturn(response);

        mockMvc.perform(get("/api/v1/candidates/search")
                        .with(SecurityMockMvcRequestPostProcessors.user(recruiterPrincipal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test
    void syncCandidate_AsAdmin_ReturnsOk() throws Exception {
        UUID candidateId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/candidates/search/sync/{id}", candidateId)
                        .with(SecurityMockMvcRequestPostProcessors.user(adminPrincipal)))
                .andExpect(status().isOk());

        verify(candidateSearchService).syncCandidate(candidateId);
    }
}
