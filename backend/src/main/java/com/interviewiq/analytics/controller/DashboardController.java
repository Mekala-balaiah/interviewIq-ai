package com.interviewiq.analytics.controller;

import com.interviewiq.analytics.dto.ApplicationTrendDto;
import com.interviewiq.analytics.dto.PipelineFunnelDto;
import com.interviewiq.analytics.dto.RecruiterKpiDto;
import com.interviewiq.analytics.service.AnalyticsService;
import com.interviewiq.auth.security.UserPrincipal;
import com.interviewiq.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Dashboard Analytics", description = "Endpoints for Recruiter and HR dashboard statistics")
public class DashboardController {

    private final AnalyticsService analyticsService;

    @GetMapping("/kpis")
    @PreAuthorize("hasAnyRole('RECRUITER', 'HR_MANAGER')")
    @Operation(summary = "Get high-level key performance indicators for the current recruiter")
    public ApiResponse<RecruiterKpiDto> getKpis(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(analyticsService.getRecruiterKpis(userPrincipal.getId()), "KPIs fetched successfully");
    }

    @GetMapping("/funnel")
    @PreAuthorize("hasAnyRole('RECRUITER', 'HR_MANAGER')")
    @Operation(summary = "Get pipeline funnel data (counts of applications per status)")
    public ApiResponse<PipelineFunnelDto> getFunnel(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(analyticsService.getPipelineFunnel(userPrincipal.getId()), "Funnel data fetched successfully");
    }

    @GetMapping("/trends")
    @PreAuthorize("hasAnyRole('RECRUITER', 'HR_MANAGER')")
    @Operation(summary = "Get application trends over time (time series)")
    public ApiResponse<ApplicationTrendDto> getTrends(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "30") int days) {
        return ApiResponse.success(analyticsService.getApplicationTrends(userPrincipal.getId(), days), "Trends fetched successfully");
    }
}
