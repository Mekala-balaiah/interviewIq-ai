package com.interviewiq.analytics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewiq.analytics.dto.ApplicationTrendDto;
import com.interviewiq.analytics.dto.PipelineFunnelDto;
import com.interviewiq.analytics.dto.RecruiterKpiDto;
import com.interviewiq.analytics.dto.TimeSeriesDataPoint;
import com.interviewiq.analytics.service.AnalyticsService;
import com.interviewiq.application.enums.ApplicationStatus;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = DashboardController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserPrincipal recruiterUser;

    @BeforeEach
    void setUp() {
        recruiterUser = new UserPrincipal(
                UUID.randomUUID(),
                "recruiter@example.com",
                "password",
                UserRole.RECRUITER,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_RECRUITER"))
        );
    }

    @Test
    void getKpis_ReturnsSuccess() throws Exception {
        RecruiterKpiDto dto = RecruiterKpiDto.builder()
                .totalActiveJobs(5)
                .totalApplications(150)
                .totalInterviewsScheduled(20)
                .totalOffersAccepted(2)
                .build();

        when(analyticsService.getRecruiterKpis(eq(recruiterUser.getId()))).thenReturn(dto);

        mockMvc.perform(get("/api/v1/dashboard/kpis")
                .with(SecurityMockMvcRequestPostProcessors.user(recruiterUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalActiveJobs").value(5))
                .andExpect(jsonPath("$.data.totalApplications").value(150));
    }

    @Test
    void getFunnel_ReturnsSuccess() throws Exception {
        PipelineFunnelDto dto = PipelineFunnelDto.builder()
                .stageCounts(Map.of(ApplicationStatus.APPLIED, 100L, ApplicationStatus.SCREENING, 50L))
                .build();

        when(analyticsService.getPipelineFunnel(eq(recruiterUser.getId()))).thenReturn(dto);

        mockMvc.perform(get("/api/v1/dashboard/funnel")
                .with(SecurityMockMvcRequestPostProcessors.user(recruiterUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.stageCounts.APPLIED").value(100));
    }

    @Test
    void getTrends_ReturnsSuccess() throws Exception {
        ApplicationTrendDto dto = ApplicationTrendDto.builder()
                .data(List.of(new TimeSeriesDataPoint("2026-08-01", 10), new TimeSeriesDataPoint("2026-08-02", 15)))
                .build();

        when(analyticsService.getApplicationTrends(eq(recruiterUser.getId()), anyInt())).thenReturn(dto);

        mockMvc.perform(get("/api/v1/dashboard/trends?days=30")
                .with(SecurityMockMvcRequestPostProcessors.user(recruiterUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.data[0].date").value("2026-08-01"))
                .andExpect(jsonPath("$.data.data[0].count").value(10));
    }
}
