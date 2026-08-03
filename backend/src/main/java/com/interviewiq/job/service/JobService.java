package com.interviewiq.job.service;

import com.interviewiq.common.response.PagedResponse;
import com.interviewiq.job.dto.CreateJobRequest;
import com.interviewiq.job.dto.JobDto;
import com.interviewiq.job.dto.UpdateJobRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface JobService {
    JobDto createJob(UUID recruiterId, CreateJobRequest request);
    JobDto updateJob(UUID jobId, UUID recruiterId, UpdateJobRequest request);
    void deleteJob(UUID jobId, UUID recruiterId);
    JobDto getJobById(UUID jobId);
    JobDto getJobBySlug(String slug);
    PagedResponse<JobDto> getMyJobs(UUID recruiterId, Pageable pageable);
    JobDto submitForApproval(UUID jobId, UUID recruiterId);
}
