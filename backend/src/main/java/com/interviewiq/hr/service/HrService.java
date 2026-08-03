package com.interviewiq.hr.service;

import com.interviewiq.hr.dto.HrDashboardDto;
import com.interviewiq.hr.dto.HrProfileDto;
import com.interviewiq.hr.dto.JobApprovalDto;
import com.interviewiq.hr.dto.JobApprovalRequest;
import com.interviewiq.hr.dto.UpdateHrProfileRequest;
import com.interviewiq.job.dto.JobDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.interviewiq.common.response.PagedResponse;

import java.util.UUID;

public interface HrService {
    HrProfileDto getProfile(UUID userId);
    HrProfileDto updateProfile(UUID userId, UpdateHrProfileRequest request);
    HrDashboardDto getDashboardMetrics(UUID userId);
    PagedResponse<JobDto> getPendingJobs(UUID userId, Pageable pageable);
    JobApprovalDto processJobApproval(UUID jobId, UUID hrUserId, JobApprovalRequest request);
}
