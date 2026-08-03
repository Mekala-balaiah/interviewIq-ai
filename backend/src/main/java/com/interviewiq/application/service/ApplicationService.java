package com.interviewiq.application.service;

import com.interviewiq.application.dto.ApplicationDto;
import com.interviewiq.application.dto.ApplyForJobRequest;

import java.util.List;
import java.util.UUID;

public interface ApplicationService {
    
    ApplicationDto applyForJob(UUID userId, ApplyForJobRequest request);
    
    List<ApplicationDto> getMyApplications(UUID userId);
}
