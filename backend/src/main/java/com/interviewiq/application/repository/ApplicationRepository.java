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

    @org.springframework.data.jpa.repository.Query("SELECT a.status, COUNT(a) FROM Application a WHERE a.job.recruiter.id = :recruiterId GROUP BY a.status")
    List<Object[]> countApplicationsByStatusForRecruiter(@org.springframework.data.repository.query.Param("recruiterId") UUID recruiterId);

    @org.springframework.data.jpa.repository.Query(value = "SELECT TO_CHAR(a.created_at, 'YYYY-MM-DD') as dateStr, COUNT(a.id) " +
            "FROM applications a JOIN jobs j ON a.job_id = j.id " +
            "WHERE j.recruiter_id = :recruiterId AND a.created_at >= CURRENT_DATE - INTERVAL '30 days' " +
            "GROUP BY TO_CHAR(a.created_at, 'YYYY-MM-DD') ORDER BY dateStr ASC", nativeQuery = true)
    List<Object[]> countApplicationsByDateForRecruiterLast30Days(@org.springframework.data.repository.query.Param("recruiterId") UUID recruiterId);
}
