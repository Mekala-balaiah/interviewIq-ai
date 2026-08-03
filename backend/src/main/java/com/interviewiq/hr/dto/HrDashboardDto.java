package com.interviewiq.hr.dto;

import lombok.Data;

@Data
public class HrDashboardDto {
    private long pendingJobApprovals;
    private long activeJobs;
    private long totalApplications;
    private long teamSize;
}
