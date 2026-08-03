package com.interviewiq.hr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewiq.auth.enums.UserRole;
import com.interviewiq.auth.security.JwtAuthenticationFilter;
import com.interviewiq.auth.security.UserPrincipal;
import com.interviewiq.common.response.PagedResponse;
import com.interviewiq.hr.dto.JobApprovalDto;
import com.interviewiq.hr.dto.JobApprovalRequest;
import com.interviewiq.hr.enums.JobApprovalStatus;
import com.interviewiq.hr.service.HrService;
import com.interviewiq.job.dto.JobDto;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = HrJobController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class HrJobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HrService hrService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserPrincipal hrUser;
    private UUID jobId;

    @BeforeEach
    void setUp() {
        hrUser = new UserPrincipal(
                UUID.randomUUID(),
                "hr@example.com",
                "password",
                UserRole.HR_MANAGER,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_HR_MANAGER"))
        );
        jobId = UUID.randomUUID();
    }

    @Test
    void getPendingJobs_AsHr_ReturnsSuccess() throws Exception {
        JobDto jobDto = new JobDto();
        jobDto.setId(jobId);
        jobDto.setTitle("Software Engineer");
        
        Page<JobDto> page = new PageImpl<>(List.of(jobDto));
        PagedResponse<JobDto> response = new PagedResponse<>(page);

        when(hrService.getPendingJobs(any(UUID.class), any(Pageable.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/hr/jobs/pending")
                .with(SecurityMockMvcRequestPostProcessors.user(hrUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("Software Engineer"));
    }

    @Test
    void processJobApproval_Approve_ReturnsSuccess() throws Exception {
        JobApprovalRequest request = new JobApprovalRequest();
        request.setApproved(true);
        request.setComments("Looks good");

        JobApprovalDto dto = new JobApprovalDto();
        dto.setId(UUID.randomUUID());
        dto.setJobId(jobId);
        dto.setStatus(JobApprovalStatus.APPROVED);

        when(hrService.processJobApproval(eq(jobId), any(UUID.class), any(JobApprovalRequest.class)))
                .thenReturn(dto);

        mockMvc.perform(post("/api/v1/hr/jobs/{jobId}/approve", jobId)
                .with(SecurityMockMvcRequestPostProcessors.user(hrUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("APPROVED"));
    }
}
