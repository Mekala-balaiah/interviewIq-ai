package com.interviewiq.interview.repository;

import com.interviewiq.interview.entity.Interview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, UUID> {
    Page<Interview> findByCandidateIdOrderByCreatedAtDesc(UUID candidateId, Pageable pageable);
    Page<Interview> findByJobIdOrderByCreatedAtDesc(UUID jobId, Pageable pageable);
}
