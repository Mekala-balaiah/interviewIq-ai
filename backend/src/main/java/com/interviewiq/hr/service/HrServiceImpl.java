package com.interviewiq.hr.service;

import com.interviewiq.application.repository.ApplicationRepository;
import com.interviewiq.common.exception.BusinessException;
import com.interviewiq.common.exception.ResourceNotFoundException;
import com.interviewiq.common.response.PagedResponse;
import com.interviewiq.company.repository.CompanyRepository;
import com.interviewiq.hr.dto.HrDashboardDto;
import com.interviewiq.hr.dto.HrProfileDto;
import com.interviewiq.hr.dto.JobApprovalDto;
import com.interviewiq.hr.dto.JobApprovalRequest;
import com.interviewiq.hr.dto.UpdateHrProfileRequest;
import com.interviewiq.hr.entity.HrProfile;
import com.interviewiq.hr.entity.JobApproval;
import com.interviewiq.hr.enums.JobApprovalStatus;
import com.interviewiq.hr.mapper.HrMapper;
import com.interviewiq.hr.mapper.JobApprovalMapper;
import com.interviewiq.hr.repository.HrProfileRepository;
import com.interviewiq.hr.repository.JobApprovalRepository;
import com.interviewiq.job.dto.JobDto;
import com.interviewiq.job.entity.Job;
import com.interviewiq.job.enums.JobStatus;
import com.interviewiq.job.mapper.JobMapper;
import com.interviewiq.job.repository.JobRepository;
import com.interviewiq.recruiter.repository.RecruiterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HrServiceImpl implements HrService {

    private final HrProfileRepository hrProfileRepository;
    private final JobRepository jobRepository;
    private final JobApprovalRepository jobApprovalRepository;
    private final ApplicationRepository applicationRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final HrMapper hrMapper;
    private final JobApprovalMapper jobApprovalMapper;
    private final JobMapper jobMapper;

    @Override
    @Transactional(readOnly = true)
    public HrProfileDto getProfile(UUID userId) {
        HrProfile profile = hrProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("HrProfile", "userId", userId));
        return hrMapper.toDto(profile);
    }

    @Override
    @Transactional
    public HrProfileDto updateProfile(UUID userId, UpdateHrProfileRequest request) {
        HrProfile profile = hrProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("HrProfile", "userId", userId));

        if (request.getTitle() != null) {
            profile.setTitle(request.getTitle());
        }
        if (request.getDepartment() != null) {
            profile.setDepartment(request.getDepartment());
        }
        if (request.getLinkedinUrl() != null) {
            profile.setLinkedinUrl(request.getLinkedinUrl());
        }

        profile = hrProfileRepository.save(profile);
        return hrMapper.toDto(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public HrDashboardDto getDashboardMetrics(UUID userId) {
        HrProfile profile = hrProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("HrProfile", "userId", userId));

        UUID companyId = profile.getCompany() != null ? profile.getCompany().getId() : null;

        HrDashboardDto dto = new HrDashboardDto();
        
        if (companyId != null) {
            long pendingApprovals = jobRepository.countByCompanyIdAndStatusAndDeletedAtIsNull(companyId, JobStatus.PENDING_APPROVAL);
            long activeJobs = jobRepository.countByCompanyIdAndStatusAndDeletedAtIsNull(companyId, JobStatus.ACTIVE);
            
            long totalApps = applicationRepository.countByJobCompanyId(companyId);
            long teamSize = recruiterProfileRepository.countByCompanyId(companyId);

            dto.setPendingJobApprovals(pendingApprovals);
            dto.setActiveJobs(activeJobs);
            dto.setTotalApplications(totalApps);
            dto.setTeamSize(teamSize);
        }

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<JobDto> getPendingJobs(UUID userId, Pageable pageable) {
        HrProfile profile = hrProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("HrProfile", "userId", userId));

        UUID companyId = profile.getCompany() != null ? profile.getCompany().getId() : null;
        if (companyId == null) {
            return new PagedResponse<>(Page.empty(pageable).map(jobMapper::toDto));
        }

        Page<Job> jobs = jobRepository.findByCompanyIdAndStatusAndDeletedAtIsNull(companyId, JobStatus.PENDING_APPROVAL, pageable);
        return new PagedResponse<>(jobs.map(jobMapper::toDto));
    }

    @Override
    @Transactional
    public JobApprovalDto processJobApproval(UUID jobId, UUID hrUserId, JobApprovalRequest request) {
        HrProfile profile = hrProfileRepository.findByUserId(hrUserId)
                .orElseThrow(() -> new ResourceNotFoundException("HrProfile", "userId", hrUserId));

        Job job = jobRepository.findByIdAndDeletedAtIsNull(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));

        if (profile.getCompany() == null || !profile.getCompany().getId().equals(job.getCompany().getId())) {
            throw new BusinessException("You are not authorized to approve jobs for this company");
        }

        if (job.getStatus() != JobStatus.PENDING_APPROVAL) {
            throw new BusinessException("Job is not pending approval");
        }

        JobApproval approval = new JobApproval();
        approval.setJob(job);
        approval.setHr(profile);
        approval.setComments(request.getComments());

        if (Boolean.TRUE.equals(request.getApproved())) {
            approval.setStatus(JobApprovalStatus.APPROVED);
            job.setStatus(JobStatus.ACTIVE);
            job.setPublishDate(Instant.now());
        } else {
            approval.setStatus(JobApprovalStatus.REJECTED);
            job.setStatus(JobStatus.DRAFT);
        }

        jobRepository.save(job);
        approval = jobApprovalRepository.save(approval);

        return jobApprovalMapper.toDto(approval);
    }
}
