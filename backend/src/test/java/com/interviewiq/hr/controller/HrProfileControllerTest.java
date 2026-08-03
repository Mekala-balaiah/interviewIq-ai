package com.interviewiq.hr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewiq.auth.enums.UserRole;
import com.interviewiq.auth.security.JwtAuthenticationFilter;
import com.interviewiq.auth.security.UserPrincipal;
import com.interviewiq.hr.dto.HrDashboardDto;
import com.interviewiq.hr.dto.HrProfileDto;
import com.interviewiq.hr.service.HrService;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = HrProfileController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class HrProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HrService hrService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserPrincipal hrUser;
    private UserPrincipal candidateUser;

    @BeforeEach
    void setUp() {
        hrUser = new UserPrincipal(
                UUID.randomUUID(),
                "hr@example.com",
                "password",
                UserRole.HR_MANAGER,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_HR_MANAGER"))
        );

        candidateUser = new UserPrincipal(
                UUID.randomUUID(),
                "cand@example.com",
                "password",
                UserRole.CANDIDATE,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CANDIDATE"))
        );
    }

    @Test
    void getProfile_AsHr_ReturnsSuccess() throws Exception {
        HrProfileDto dto = new HrProfileDto();
        dto.setUserId(hrUser.getId());
        dto.setTitle("Senior HR Manager");

        when(hrService.getProfile(any(UUID.class))).thenReturn(dto);

        mockMvc.perform(get("/api/v1/hr/me")
                .with(SecurityMockMvcRequestPostProcessors.user(hrUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Senior HR Manager"));
    }

    @Test
    void getDashboard_AsHr_ReturnsMetrics() throws Exception {
        HrDashboardDto dto = new HrDashboardDto();
        dto.setActiveJobs(5);
        dto.setPendingJobApprovals(2);

        when(hrService.getDashboardMetrics(any(UUID.class))).thenReturn(dto);

        mockMvc.perform(get("/api/v1/hr/dashboard")
                .with(SecurityMockMvcRequestPostProcessors.user(hrUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.activeJobs").value(5))
                .andExpect(jsonPath("$.data.pendingJobApprovals").value(2));
    }
}
