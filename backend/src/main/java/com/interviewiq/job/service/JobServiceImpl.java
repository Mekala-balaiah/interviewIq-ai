package com.interviewiq.job.service;

import com.interviewiq.common.exception.ResourceNotFoundException;
import com.interviewiq.common.exception.UnauthorizedException;
import com.interviewiq.common.response.PagedResponse;
import com.interviewiq.company.entity.Company;
import com.interviewiq.job.dto.CreateJobRequest;
import com.interviewiq.job.dto.JobDto;
import com.interviewiq.job.dto.UpdateJobRequest;
import com.interviewiq.job.entity.Job;
import com.interviewiq.job.enums.JobStatus;
import com.interviewiq.job.mapper.JobMapper;
import com.interviewiq.job.repository.JobRepository;
import com.interviewiq.recruiter.entity.RecruiterProfile;
import com.interviewiq.recruiter.repository.RecruiterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final JobMapper jobMapper;

    @Override
    @Transactional
    public JobDto createJob(UUID recruiterUserId, CreateJobRequest request) {
        RecruiterProfile recruiter = recruiterProfileRepository.findByUserId(recruiterUserId)
                .orElseThrow(() -> new ResourceNotFoundException("RecruiterProfile", "userId", recruiterUserId));
        
        Company company = recruiter.getCompany();
        if (company == null) {
            throw new IllegalStateException("Recruiter is not associated with any company");
        }

        Job job = jobMapper.toEntity(request);
        job.setRecruiter(recruiter);
        job.setCompany(company);
        
        // Generate a simple slug
        String slugBase = request.getTitle().toLowerCase().replaceAll("[^a-z0-9]+", "-");
        job.setSlug(slugBase + "-" + UUID.randomUUID().toString().substring(0, 8));
        
        job.setStatus(JobStatus.DRAFT);
        job.setApplicationCount(0);
        job.setViewCount(0);

        job = jobRepository.save(job);
        return jobMapper.toDto(job);
    }

    @Override
    @Transactional
    public JobDto updateJob(UUID jobId, UUID recruiterUserId, UpdateJobRequest request) {
        Job job = getJobAndVerifyOwnership(jobId, recruiterUserId);
        
        jobMapper.updateEntityFromRequest(request, job);
        job = jobRepository.save(job);
        
        return jobMapper.toDto(job);
    }

    @Override
    @Transactional
    public void deleteJob(UUID jobId, UUID recruiterUserId) {
        Job job = getJobAndVerifyOwnership(jobId, recruiterUserId);
        job.setDeletedAt(Instant.now());
        jobRepository.save(job);
    }

    @Override
    @Transactional(readOnly = true)
    public JobDto getJobById(UUID jobId) {
        Job job = jobRepository.findByIdAndDeletedAtIsNull(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));
        return jobMapper.toDto(job);
    }

    @Override
    @Transactional(readOnly = true)
    public JobDto getJobBySlug(String slug) {
        Job job = jobRepository.findBySlugAndDeletedAtIsNull(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "slug", slug));
        return jobMapper.toDto(job);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<JobDto> getMyJobs(UUID recruiterUserId, Pageable pageable) {
        RecruiterProfile recruiter = recruiterProfileRepository.findByUserId(recruiterUserId)
                .orElseThrow(() -> new ResourceNotFoundException("RecruiterProfile", "userId", recruiterUserId));
                
        Page<Job> jobs = jobRepository.findByRecruiterIdAndDeletedAtIsNull(recruiter.getId(), pageable);
        Page<JobDto> dtoPage = jobs.map(jobMapper::toDto);
        
        return new PagedResponse<>(
                dtoPage.getContent(),
                dtoPage.getNumber(),
                dtoPage.getSize(),
                dtoPage.getTotalElements(),
                dtoPage.getTotalPages(),
                dtoPage.isLast()
        );
    }

    @Override
    @Transactional
    public JobDto publishJob(UUID jobId, UUID recruiterUserId) {
        Job job = getJobAndVerifyOwnership(jobId, recruiterUserId);
        
        if (job.getStatus() != JobStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT jobs can be published");
        }
        
        job.setStatus(JobStatus.PUBLISHED);
        job.setPublishDate(Instant.now());
        job = jobRepository.save(job);
        
        return jobMapper.toDto(job);
    }
    
    private Job getJobAndVerifyOwnership(UUID jobId, UUID recruiterUserId) {
        Job job = jobRepository.findByIdAndDeletedAtIsNull(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));
                
        RecruiterProfile recruiter = recruiterProfileRepository.findByUserId(recruiterUserId)
                .orElseThrow(() -> new ResourceNotFoundException("RecruiterProfile", "userId", recruiterUserId));
                
        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw new UnauthorizedException("You do not have permission to modify this job");
        }
        
        return job;
    }
}
