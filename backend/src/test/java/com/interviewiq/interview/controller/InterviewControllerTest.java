package com.interviewiq.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewiq.auth.enums.UserRole;
import com.interviewiq.auth.security.JwtAuthenticationFilter;
import com.interviewiq.auth.security.UserPrincipal;
import com.interviewiq.interview.dto.InterviewDto;
import com.interviewiq.interview.dto.InterviewQuestionDto;
import com.interviewiq.interview.dto.ScheduleInterviewRequest;
import com.interviewiq.interview.dto.SubmitResponseRequest;
import com.interviewiq.interview.dto.InterviewResponseDto;
import com.interviewiq.interview.enums.InterviewType;
import com.interviewiq.interview.service.InterviewService;
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
    controllers = InterviewController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class InterviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InterviewService interviewService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserPrincipal candidateUser;
    private UserPrincipal recruiterUser;
    private UUID interviewId;

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
        interviewId = UUID.randomUUID();
    }

    @Test
    void scheduleInterview_ReturnsSuccess() throws Exception {
        ScheduleInterviewRequest request = new ScheduleInterviewRequest();
        request.setApplicationId(UUID.randomUUID());
        request.setType(InterviewType.AI_INTERVIEW);
        request.setDurationMinutes(60);

        InterviewDto dto = new InterviewDto();
        dto.setId(interviewId);
        dto.setType(InterviewType.AI_INTERVIEW);
        dto.setAiConducted(true);

        when(interviewService.scheduleInterview(any(ScheduleInterviewRequest.class))).thenReturn(dto);

        mockMvc.perform(post("/api/v1/interviews/schedule")
                .with(SecurityMockMvcRequestPostProcessors.user(recruiterUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.aiConducted").value(true));
    }

    @Test
    void startInterview_ReturnsSuccess() throws Exception {
        InterviewDto dto = new InterviewDto();
        dto.setId(interviewId);

        when(interviewService.startInterview(any(UUID.class))).thenReturn(dto);

        mockMvc.perform(post("/api/v1/interviews/{interviewId}/start", interviewId)
                .with(SecurityMockMvcRequestPostProcessors.user(candidateUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getQuestions_ReturnsSuccess() throws Exception {
        InterviewQuestionDto qDto = new InterviewQuestionDto();
        qDto.setId(UUID.randomUUID());
        qDto.setQuestionText("Explain Dependency Injection.");

        when(interviewService.getQuestions(any(UUID.class))).thenReturn(List.of(qDto));

        mockMvc.perform(get("/api/v1/interviews/{interviewId}/questions", interviewId)
                .with(SecurityMockMvcRequestPostProcessors.user(candidateUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].questionText").value("Explain Dependency Injection."));
    }

    @Test
    void submitResponse_ReturnsSuccess() throws Exception {
        SubmitResponseRequest request = new SubmitResponseRequest();
        request.setResponseText("Dependency injection is...");

        InterviewResponseDto dto = new InterviewResponseDto();
        dto.setId(UUID.randomUUID());
        dto.setAiScore(8);

        when(interviewService.submitResponse(any(UUID.class), any(UUID.class), any(SubmitResponseRequest.class))).thenReturn(dto);

        mockMvc.perform(post("/api/v1/interviews/{interviewId}/questions/{questionId}/submit", interviewId, UUID.randomUUID())
                .with(SecurityMockMvcRequestPostProcessors.user(candidateUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.aiScore").value(8));
    }
}
