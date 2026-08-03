package com.interviewiq.analytics.service;

import com.interviewiq.analytics.dto.ApplicationTrendDto;
import com.interviewiq.analytics.dto.PipelineFunnelDto;
import com.interviewiq.analytics.dto.RecruiterKpiDto;

import java.util.UUID;

public interface AnalyticsService {
    RecruiterKpiDto getRecruiterKpis(UUID recruiterId);
    
    PipelineFunnelDto getPipelineFunnel(UUID recruiterId);
    
    ApplicationTrendDto getApplicationTrends(UUID recruiterId, int days);
}
