package com.interviewiq.hr.repository;

import com.interviewiq.hr.entity.HrProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HrProfileRepository extends JpaRepository<HrProfile, UUID> {
    Optional<HrProfile> findByUserId(UUID userId);
}
