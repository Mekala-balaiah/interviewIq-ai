package com.interviewiq.recruiter.service;

import com.interviewiq.recruiter.dto.RecruiterDashboardDto;
import com.interviewiq.recruiter.dto.RecruiterProfileDto;
import com.interviewiq.recruiter.dto.UpdateRecruiterProfileRequest;

import java.util.UUID;

public interface RecruiterService {
    RecruiterProfileDto getProfileByUserId(UUID userId);
    RecruiterProfileDto updateProfile(UUID userId, UpdateRecruiterProfileRequest request);
    RecruiterDashboardDto getDashboardMetrics(UUID userId);
}
