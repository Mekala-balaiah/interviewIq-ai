package com.interviewiq.job.repository;

import com.interviewiq.job.entity.Job;
import com.interviewiq.job.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {
    Page<Job> findByRecruiterIdAndDeletedAtIsNull(UUID recruiterId, Pageable pageable);
    Optional<Job> findByIdAndDeletedAtIsNull(UUID id);
    Optional<Job> findBySlugAndDeletedAtIsNull(String slug);
    long countByRecruiterIdAndStatusAndDeletedAtIsNull(UUID recruiterId, JobStatus status);
    long countByCompanyIdAndStatusAndDeletedAtIsNull(UUID companyId, JobStatus status);
    Page<Job> findByCompanyIdAndStatusAndDeletedAtIsNull(UUID companyId, JobStatus status, Pageable pageable);
}
