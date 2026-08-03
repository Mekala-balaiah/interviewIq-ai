package com.interviewiq.job.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewiq.auth.entity.User;
import com.interviewiq.auth.enums.UserRole;
import com.interviewiq.auth.security.JwtService;
import com.interviewiq.auth.security.UserPrincipal;
import com.interviewiq.job.dto.CreateJobRequest;
import com.interviewiq.job.dto.JobDto;
import com.interviewiq.job.enums.EmploymentType;
import com.interviewiq.job.enums.ExperienceLevel;
import com.interviewiq.job.enums.WorkMode;
import com.interviewiq.job.service.JobService;
import com.interviewiq.job.service.JobSkillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private JobService jobService;

    @MockBean
    private JobSkillService jobSkillService;

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
    void testCreateJob() throws Exception {
        CreateJobRequest request = new CreateJobRequest();
        request.setTitle("Senior Java Developer");
        request.setDescription("We need a Java expert.");
        request.setEmploymentType(EmploymentType.FULL_TIME);
        request.setWorkMode(WorkMode.REMOTE);
        request.setExperienceLevel(ExperienceLevel.SENIOR);

        JobDto responseDto = new JobDto();
        responseDto.setId(UUID.randomUUID());
        responseDto.setTitle("Senior Java Developer");

        when(jobService.createJob(eq(userId), any(CreateJobRequest.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/jobs")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("Senior Java Developer"))
                .andExpect(jsonPath("$.success").value(true));
    }
}
