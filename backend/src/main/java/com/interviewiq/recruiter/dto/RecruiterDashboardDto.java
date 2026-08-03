package com.interviewiq.recruiter.dto;

import lombok.Data;

@Data
public class RecruiterDashboardDto {
    private long activeJobsCount;
    private long totalApplicationsCount;
    private long applicationsToReviewCount;
}
