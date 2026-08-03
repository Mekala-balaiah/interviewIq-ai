package com.interviewiq.candidate.repository;

import com.interviewiq.candidate.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID> {
    Optional<Skill> findByNormalizedName(String normalizedName);
    
    List<Skill> findByNameContainingIgnoreCase(String name);
}
