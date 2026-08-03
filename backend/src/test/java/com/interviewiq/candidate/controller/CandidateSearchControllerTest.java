package com.interviewiq.candidate.controller;

import com.interviewiq.auth.enums.UserRole;
import com.interviewiq.auth.security.JwtAuthenticationFilter;
import com.interviewiq.auth.security.UserPrincipal;
import com.interviewiq.candidate.dto.CandidateProfileDto;
import com.interviewiq.candidate.service.CandidateService;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = CandidateSearchController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class CandidateSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CandidateService candidateService;

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
    void searchCandidates_ReturnsSuccess() throws Exception {
        CandidateProfileDto profile = new CandidateProfileDto();
        profile.setId(UUID.randomUUID());
        profile.setHeadline("Java Developer");
        
        Page<CandidateProfileDto> page = new PageImpl<>(List.of(profile));
        
        when(candidateService.searchCandidates(any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/candidates/search?keyword=Java")
                .with(SecurityMockMvcRequestPostProcessors.user(recruiterUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].headline").value("Java Developer"));
    }
}
