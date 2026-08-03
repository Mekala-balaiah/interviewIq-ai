package com.interviewiq.recruiter.service;

import com.interviewiq.application.enums.ApplicationStatus;
import com.interviewiq.application.repository.ApplicationRepository;
import com.interviewiq.common.exception.ResourceNotFoundException;
import com.interviewiq.job.enums.JobStatus;
import com.interviewiq.job.repository.JobRepository;
import com.interviewiq.recruiter.dto.RecruiterDashboardDto;
import com.interviewiq.recruiter.dto.RecruiterProfileDto;
import com.interviewiq.recruiter.dto.UpdateRecruiterProfileRequest;
import com.interviewiq.recruiter.entity.RecruiterProfile;
import com.interviewiq.recruiter.mapper.RecruiterMapper;
import com.interviewiq.recruiter.repository.RecruiterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecruiterServiceImpl implements RecruiterService {

    private final RecruiterProfileRepository recruiterProfileRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final RecruiterMapper recruiterMapper;

    @Override
    @Transactional(readOnly = true)
    public RecruiterProfileDto getProfileByUserId(UUID userId) {
        RecruiterProfile profile = recruiterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("RecruiterProfile", "userId", userId));
        return recruiterMapper.toDto(profile);
    }

    @Override
    @Transactional
    public RecruiterProfileDto updateProfile(UUID userId, UpdateRecruiterProfileRequest request) {
        RecruiterProfile profile = recruiterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("RecruiterProfile", "userId", userId));

        recruiterMapper.updateEntityFromRequest(request, profile);
        profile = recruiterProfileRepository.save(profile);
        
        return recruiterMapper.toDto(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public RecruiterDashboardDto getDashboardMetrics(UUID userId) {
        RecruiterProfile profile = recruiterProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("RecruiterProfile", "userId", userId));

        long activeJobs = jobRepository.countByRecruiterIdAndStatusAndDeletedAtIsNull(profile.getId(), JobStatus.PUBLISHED);
        
        long totalApps = applicationRepository.countByJobRecruiterId(profile.getId());
        
        long appsToReview = applicationRepository.countByJobRecruiterIdAndStatusIn(
                profile.getId(), 
                Arrays.asList(ApplicationStatus.APPLIED, ApplicationStatus.SCREENING)
        );

        RecruiterDashboardDto dashboard = new RecruiterDashboardDto();
        dashboard.setActiveJobsCount(activeJobs);
        dashboard.setTotalApplicationsCount(totalApps);
        dashboard.setApplicationsToReviewCount(appsToReview);

        return dashboard;
    }
}
