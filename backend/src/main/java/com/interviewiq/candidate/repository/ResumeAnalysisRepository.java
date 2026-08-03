package com.interviewiq.candidate.repository;

import com.interviewiq.candidate.entity.ResumeAnalysis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis, UUID> {
    Page<ResumeAnalysis> findByResumeIdOrderByCreatedAtDesc(UUID resumeId, Pageable pageable);
    
    Optional<ResumeAnalysis> findFirstByResumeIdAndJobIdOrderByCreatedAtDesc(UUID resumeId, UUID jobId);
}
