package com.interviewiq.candidate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewiq.auth.enums.UserRole;
import com.interviewiq.auth.security.JwtAuthenticationFilter;
import com.interviewiq.auth.security.UserPrincipal;
import com.interviewiq.candidate.dto.AnalyzeResumeRequest;
import com.interviewiq.candidate.dto.ResumeAnalysisDto;
import com.interviewiq.candidate.mapper.ResumeAnalysisMapper;
import com.interviewiq.candidate.repository.ResumeAnalysisRepository;
import com.interviewiq.candidate.service.ResumeParsingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = ResumeAnalysisController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class ResumeAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResumeParsingService resumeParsingService;

    @MockBean
    private ResumeAnalysisRepository resumeAnalysisRepository;

    @MockBean
    private ResumeAnalysisMapper resumeAnalysisMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private UserPrincipal authUser;
    private UUID resumeId;

    @BeforeEach
    void setUp() {
        authUser = new UserPrincipal(
                UUID.randomUUID(),
                "user@example.com",
                "password",
                UserRole.CANDIDATE,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CANDIDATE"))
        );
        resumeId = UUID.randomUUID();
    }

    @Test
    void analyzeResume_ReturnsSuccess() throws Exception {
        AnalyzeResumeRequest request = new AnalyzeResumeRequest();
        request.setJobId(UUID.randomUUID());

        ResumeAnalysisDto dto = new ResumeAnalysisDto();
        dto.setId(UUID.randomUUID());
        dto.setAtsScore(85);
        dto.setExtractedSkills(List.of("Java", "Spring Boot"));

        when(resumeParsingService.analyzeResume(any(UUID.class), any(UUID.class))).thenReturn(dto);

        mockMvc.perform(post("/api/v1/resumes/{resumeId}/analyze", resumeId)
                .with(SecurityMockMvcRequestPostProcessors.user(authUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.atsScore").value(85));
    }

    @Test
    void getAnalyses_ReturnsSuccess() throws Exception {
        when(resumeAnalysisRepository.findByResumeIdOrderByCreatedAtDesc(any(UUID.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/resumes/{resumeId}/analyses", resumeId)
                .with(SecurityMockMvcRequestPostProcessors.user(authUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
