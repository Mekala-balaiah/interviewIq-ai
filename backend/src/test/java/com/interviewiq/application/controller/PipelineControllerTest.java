package com.interviewiq.application.controller;

import com.interviewiq.application.dto.ApplicationDto;
import com.interviewiq.application.enums.ApplicationStatus;
import com.interviewiq.application.service.ApplicationService;
import com.interviewiq.auth.entity.User;
import com.interviewiq.auth.enums.UserRole;
import com.interviewiq.auth.security.JwtService;
import com.interviewiq.auth.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PipelineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private ApplicationService applicationService;

    private String token;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail("recruiter@test.com");
        user.setRole(UserRole.RECRUITER);
        
        UserPrincipal principal = UserPrincipal.create(user);
        token = jwtService.generateToken(principal);
    }

    @Test
    void testUpdateApplicationStatus() throws Exception {
        UUID applicationId = UUID.randomUUID();

        ApplicationDto responseDto = new ApplicationDto();
        responseDto.setId(applicationId);
        responseDto.setStatus(ApplicationStatus.SCREENING);

        when(applicationService.updateApplicationStatus(eq(applicationId), eq(userId), eq(ApplicationStatus.SCREENING)))
                .thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/pipeline/applications/{applicationId}/status", applicationId)
                .header("Authorization", "Bearer " + token)
                .param("status", "SCREENING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SCREENING"))
                .andExpect(jsonPath("$.success").value(true));
    }
}
