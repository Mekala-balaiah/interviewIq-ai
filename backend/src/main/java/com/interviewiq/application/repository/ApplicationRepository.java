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
    
    long countByJobId(UUID jobId);

    long countByJobRecruiterId(UUID recruiterId);
    
    long countByJobCompanyId(UUID companyId);
    
    long countByJobRecruiterIdAndStatusIn(UUID recruiterId, List<com.interviewiq.application.enums.ApplicationStatus> statuses);
    
    org.springframework.data.domain.Page<Application> findByJobId(UUID jobId, org.springframework.data.domain.Pageable pageable);
}
