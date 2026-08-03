package com.interviewiq.hr.repository;

import com.interviewiq.hr.entity.JobApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobApprovalRepository extends JpaRepository<JobApproval, UUID> {
    List<JobApproval> findByJobIdOrderByCreatedAtDesc(UUID jobId);
}
