package com.interviewiq.candidate.controller;

import com.interviewiq.auth.entity.User;
import com.interviewiq.auth.enums.UserRole;
import com.interviewiq.auth.security.JwtService;
import com.interviewiq.auth.security.UserPrincipal;
import com.interviewiq.candidate.dto.ResumeDto;
import com.interviewiq.candidate.service.ResumeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ResumeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private ResumeService resumeService;

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
    void testUploadResume() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "Dummy content".getBytes()
        );

        ResumeDto dto = new ResumeDto();
        dto.setId(UUID.randomUUID());
        dto.setFileName("resume.pdf");
        dto.setFileUrl("/api/v1/files/download/resumes/" + userId + "/resume.pdf");

        when(resumeService.uploadResume(eq(userId), any(), any())).thenReturn(dto);

        mockMvc.perform(multipart("/api/v1/candidates/me/resumes")
                .file(file)
                .header("Authorization", "Bearer " + token)
                .param("isPrimary", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fileName").value("resume.pdf"))
                .andExpect(jsonPath("$.success").value(true));
    }
}
