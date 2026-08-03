package com.interviewiq.application.service;

import com.interviewiq.application.dto.ApplicationDto;
import com.interviewiq.application.dto.ApplyForJobRequest;
import com.interviewiq.application.entity.Application;
import com.interviewiq.application.enums.ApplicationStatus;
import com.interviewiq.application.mapper.ApplicationMapper;
import com.interviewiq.application.repository.ApplicationRepository;
import com.interviewiq.candidate.entity.CandidateProfile;
import com.interviewiq.candidate.entity.Resume;
import com.interviewiq.candidate.repository.CandidateProfileRepository;
import com.interviewiq.candidate.repository.ResumeRepository;
import com.interviewiq.common.exception.BusinessException;
import com.interviewiq.common.exception.DuplicateResourceException;
import com.interviewiq.common.exception.ResourceNotFoundException;
import com.interviewiq.job.entity.Job;
import com.interviewiq.job.enums.JobStatus;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final ResumeRepository resumeRepository;
    private final ApplicationMapper applicationMapper;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public ApplicationDto applyForJob(UUID candidateUserId, ApplyForJobRequest request) {
        CandidateProfile profile = candidateProfileRepository.findByUserId(candidateUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));

        // Use EntityManager to reference the Job entity without needing a JobRepository right now
        Job job = entityManager.find(Job.class, request.getJobId());
        if (job == null) {
            throw new ResourceNotFoundException("Job not found");
        }
        
        if (job.getStatus() != JobStatus.PUBLISHED) {
            throw new BusinessException("Cannot apply to a job that is not published");
        }

        if (applicationRepository.findByJobIdAndCandidateId(job.getId(), profile.getId()).isPresent()) {
            throw new DuplicateResourceException("You have already applied for this job");
        }

        Resume resume = null;
        if (request.getResumeId() != null) {
            resume = resumeRepository.findByIdAndCandidateId(request.getResumeId(), profile.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
        } else {
            // Find primary resume if exists
            resume = resumeRepository.findByCandidateId(profile.getId())
                    .stream()
                    .filter(Resume::getIsPrimary)
                    .findFirst()
                    .orElse(null);
                    
            if (resume == null) {
                throw new BusinessException("A resume is required to apply for a job");
            }
        }

        Application application = Application.builder()
                .job(job)
                .candidate(profile)
                .resume(resume)
                .status(ApplicationStatus.APPLIED)
                .coverLetter(request.getCoverLetter())
                .build();

        application = applicationRepository.save(application);
        
        // Increment application count on Job
        job.setApplicationCount(job.getApplicationCount() + 1);
        entityManager.merge(job);
        
        // TODO: Publish Kafka Event for Notification (Recruiter email, AI matching trigger)
        log.info("Application submitted successfully for candidate {} and job {}", profile.getId(), job.getId());

        return applicationMapper.toDto(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationDto> getMyApplications(UUID candidateUserId) {
        CandidateProfile profile = candidateProfileRepository.findByUserId(candidateUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));
                
        return applicationRepository.findByCandidateId(profile.getId())
                .stream()
                .map(applicationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public com.interviewiq.common.response.PagedResponse<ApplicationDto> getApplicationsForJob(UUID jobId, UUID recruiterUserId, org.springframework.data.domain.Pageable pageable) {
        Job job = entityManager.find(Job.class, jobId);
        if (job == null) {
            throw new ResourceNotFoundException("Job not found");
        }
        
        if (!job.getRecruiter().getUser().getId().equals(recruiterUserId)) {
            throw new com.interviewiq.common.exception.UnauthorizedException("You are not authorized to view applications for this job");
        }

        org.springframework.data.domain.Page<Application> page = applicationRepository.findByJobId(jobId, pageable);
        org.springframework.data.domain.Page<ApplicationDto> dtoPage = page.map(applicationMapper::toDto);

        return new com.interviewiq.common.response.PagedResponse<>(
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
    public ApplicationDto updateApplicationStatus(UUID applicationId, UUID recruiterUserId, ApplicationStatus status) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!application.getJob().getRecruiter().getUser().getId().equals(recruiterUserId)) {
            throw new com.interviewiq.common.exception.UnauthorizedException("You are not authorized to update this application");
        }

        application.setStatus(status);
        application = applicationRepository.save(application);
        
        // TODO: Send notification to candidate about status update
        
        return applicationMapper.toDto(application);
    }
}
