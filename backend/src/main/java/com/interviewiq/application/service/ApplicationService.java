package com.interviewiq.application.service;

import com.interviewiq.application.dto.ApplicationDto;
import com.interviewiq.application.dto.ApplyForJobRequest;

import java.util.List;
import java.util.UUID;

public interface ApplicationService {
    
    ApplicationDto applyForJob(UUID candidateUserId, ApplyForJobRequest request);
    List<ApplicationDto> getMyApplications(UUID candidateUserId);
    com.interviewiq.common.response.PagedResponse<ApplicationDto> getApplicationsForJob(UUID jobId, UUID recruiterUserId, org.springframework.data.domain.Pageable pageable);
    ApplicationDto updateApplicationStatus(UUID applicationId, UUID recruiterUserId, com.interviewiq.application.enums.ApplicationStatus status);
}
