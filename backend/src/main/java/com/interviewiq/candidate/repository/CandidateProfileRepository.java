package com.interviewiq.candidate.repository;

import com.interviewiq.candidate.entity.CandidateProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CandidateProfileRepository extends JpaRepository<CandidateProfile, UUID>, JpaSpecificationExecutor<CandidateProfile> {
    Optional<CandidateProfile> findByUserId(UUID userId);
}
