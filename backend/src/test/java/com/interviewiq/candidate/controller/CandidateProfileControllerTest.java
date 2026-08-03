package com.interviewiq.candidate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewiq.auth.entity.User;
import com.interviewiq.auth.enums.UserRole;
import com.interviewiq.auth.security.JwtService;
import com.interviewiq.auth.security.UserPrincipal;
import com.interviewiq.candidate.dto.CandidateProfileDto;
import com.interviewiq.candidate.dto.UpdateCandidateProfileRequest;
import com.interviewiq.candidate.service.CandidateService;
import com.interviewiq.candidate.service.CandidateSkillService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CandidateProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private CandidateService candidateService;

    @MockBean
    private CandidateSkillService candidateSkillService;

    private String token;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setEmail("candidate@test.com");
        user.setRole(UserRole.CANDIDATE);
        
        UserPrincipal principal = UserPrincipal.create(user);
        token = jwtService.generateToken(principal);
    }

    @Test
    void testGetMyProfile() throws Exception {
        CandidateProfileDto dto = new CandidateProfileDto();
        dto.setUserId(userId);
        dto.setHeadline("Java Dev");

        when(candidateService.getProfileByUserId(userId)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/candidates/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.headline").value("Java Dev"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testUpdateMyProfile() throws Exception {
        UpdateCandidateProfileRequest req = new UpdateCandidateProfileRequest();
        req.setHeadline("Updated Dev");
        req.setYearsOfExperience(5);

        CandidateProfileDto dto = new CandidateProfileDto();
        dto.setUserId(userId);
        dto.setHeadline("Updated Dev");
        dto.setYearsOfExperience(5);

        when(candidateService.updateProfile(eq(userId), any(UpdateCandidateProfileRequest.class))).thenReturn(dto);

        mockMvc.perform(put("/api/v1/candidates/me")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.headline").value("Updated Dev"))
                .andExpect(jsonPath("$.data.yearsOfExperience").value(5));
    }
}
