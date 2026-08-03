package com.interviewiq.analytics.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecruiterKpiDto {
    private long totalActiveJobs;
    private long totalApplications;
    private long totalInterviewsScheduled;
    private long totalOffersAccepted;
}
