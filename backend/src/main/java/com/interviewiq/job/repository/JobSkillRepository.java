package com.interviewiq.job.repository;

import com.interviewiq.job.entity.JobSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobSkillRepository extends JpaRepository<JobSkill, UUID> {
    List<JobSkill> findByJobId(UUID jobId);
    Optional<JobSkill> findByJobIdAndSkillId(UUID jobId, UUID skillId);
}
