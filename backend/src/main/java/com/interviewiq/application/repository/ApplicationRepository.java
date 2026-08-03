package com.interviewiq.application.repository;

import com.interviewiq.application.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    List<Application> findByCandidateId(UUID candidateId);
    
    Optional<Application> findByJobIdAndCandidateId(UUID jobId, UUID candidateId);
}
