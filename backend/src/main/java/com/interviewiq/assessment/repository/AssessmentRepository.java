package com.interviewiq.assessment.repository;

import com.interviewiq.assessment.entity.Assessment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, UUID> {
    Page<Assessment> findByCandidateIdOrderByCreatedAtDesc(UUID candidateId, Pageable pageable);
    Page<Assessment> findByJobIdOrderByCreatedAtDesc(UUID jobId, Pageable pageable);
}
