package com.interviewiq.assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewiq.assessment.dto.*;
import com.interviewiq.assessment.enums.AssessmentStatus;
import com.interviewiq.assessment.enums.Verdict;
import com.interviewiq.assessment.service.AssessmentService;
import com.interviewiq.auth.enums.UserRole;
import com.interviewiq.auth.security.JwtAuthenticationFilter;
import com.interviewiq.auth.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
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
    controllers = AssessmentController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class AssessmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssessmentService assessmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserPrincipal candidateUser;
    private UserPrincipal recruiterUser;
    private UUID assessmentId;

    @BeforeEach
    void setUp() {
        candidateUser = new UserPrincipal(
                UUID.randomUUID(),
                "candidate@example.com",
                "password",
                UserRole.CANDIDATE,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CANDIDATE"))
        );
        recruiterUser = new UserPrincipal(
                UUID.randomUUID(),
                "recruiter@example.com",
                "password",
                UserRole.RECRUITER,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_RECRUITER"))
        );
        assessmentId = UUID.randomUUID();
    }

    @Test
    void assignAssessment_ReturnsSuccess() throws Exception {
        CreateAssessmentRequest request = new CreateAssessmentRequest();
        request.setApplicationId(UUID.randomUUID());
        request.setDifficulty("HARD");

        AssessmentDto dto = new AssessmentDto();
        dto.setId(assessmentId);
        dto.setDifficulty("HARD");
        dto.setStatus(AssessmentStatus.NOT_STARTED);

        when(assessmentService.assignAssessment(any(CreateAssessmentRequest.class))).thenReturn(dto);

        mockMvc.perform(post("/api/v1/assessments")
                .with(SecurityMockMvcRequestPostProcessors.user(recruiterUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("NOT_STARTED"));
    }

    @Test
    void startAssessment_ReturnsSuccess() throws Exception {
        AssessmentDto dto = new AssessmentDto();
        dto.setId(assessmentId);
        dto.setStatus(AssessmentStatus.IN_PROGRESS);

        when(assessmentService.startAssessment(any(UUID.class))).thenReturn(dto);

        mockMvc.perform(post("/api/v1/assessments/{assessmentId}/start", assessmentId)
                .with(SecurityMockMvcRequestPostProcessors.user(candidateUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));
    }

    @Test
    void getQuestions_ReturnsSuccess() throws Exception {
        AssessmentQuestionDto qDto = new AssessmentQuestionDto();
        qDto.setId(UUID.randomUUID());
        qDto.setProblemStatement("Write a function to return the sum of two integers.");

        when(assessmentService.getQuestions(any(UUID.class))).thenReturn(List.of(qDto));

        mockMvc.perform(get("/api/v1/assessments/{assessmentId}/questions", assessmentId)
                .with(SecurityMockMvcRequestPostProcessors.user(candidateUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].problemStatement").value("Write a function to return the sum of two integers."));
    }

    @Test
    void submitCode_ReturnsSuccess() throws Exception {
        SubmitCodeRequest request = new SubmitCodeRequest();
        request.setCode("class Solution {}");
        request.setLanguage("JAVA");

        AssessmentSubmissionDto dto = new AssessmentSubmissionDto();
        dto.setId(UUID.randomUUID());
        dto.setVerdict(Verdict.ACCEPTED);

        when(assessmentService.submitCode(any(UUID.class), any(UUID.class), any(SubmitCodeRequest.class))).thenReturn(dto);

        mockMvc.perform(post("/api/v1/assessments/{assessmentId}/questions/{questionId}/submit", assessmentId, UUID.randomUUID())
                .with(SecurityMockMvcRequestPostProcessors.user(candidateUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.verdict").value("ACCEPTED"));
    }
}
