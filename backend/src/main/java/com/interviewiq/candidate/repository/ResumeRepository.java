package com.interviewiq.candidate.repository;

import com.interviewiq.candidate.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, UUID> {
    List<Resume> findByCandidateId(UUID candidateId);
    
    Optional<Resume> findByIdAndCandidateId(UUID id, UUID candidateId);
}
