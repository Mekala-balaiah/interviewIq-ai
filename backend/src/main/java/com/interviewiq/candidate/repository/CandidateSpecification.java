package com.interviewiq.candidate.repository;

import com.interviewiq.candidate.entity.CandidateProfile;
import com.interviewiq.candidate.entity.CandidateSkill;
import com.interviewiq.job.entity.Skill;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class CandidateSpecification {

    public static Specification<CandidateProfile> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return null;
            }
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("headline")), likePattern),
                    cb.like(cb.lower(root.get("bio")), likePattern),
                    cb.like(cb.lower(root.get("currentTitle")), likePattern)
            );
        };
    }

    public static Specification<CandidateProfile> hasLocation(String location) {
        return (root, query, cb) -> {
            if (location == null || location.trim().isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%");
        };
    }

    public static Specification<CandidateProfile> hasMinExperience(Integer minExperience) {
        return (root, query, cb) -> {
            if (minExperience == null) {
                return null;
            }
            return cb.greaterThanOrEqualTo(root.get("yearsOfExperience"), minExperience);
        };
    }

    public static Specification<CandidateProfile> isOpenToRemote(Boolean openToRemote) {
        return (root, query, cb) -> {
            if (openToRemote == null) {
                return null;
            }
            return cb.equal(root.get("openToRemote"), openToRemote);
        };
    }

    public static Specification<CandidateProfile> hasSkills(List<String> skills) {
        return (root, query, cb) -> {
            if (skills == null || skills.isEmpty()) {
                return null;
            }
            
            // Note: Since CandidateProfile doesn't map candidate_skills directly via @OneToMany yet, 
            // We would need that mapping or do an EXISTS subquery.
            // Let's ensure CandidateProfile has no CandidateSkills mapped?
            // Actually, wait, let's use a subquery if we don't have the mapping.
            jakarta.persistence.criteria.Subquery<CandidateSkill> subquery = query.subquery(CandidateSkill.class);
            jakarta.persistence.criteria.Root<CandidateSkill> skillRoot = subquery.from(CandidateSkill.class);
            Join<CandidateSkill, Skill> skillJoin = skillRoot.join("skill");
            
            subquery.select(skillRoot)
                    .where(
                        cb.equal(skillRoot.get("candidate"), root),
                        cb.lower(skillJoin.get("name")).in(
                                skills.stream().map(String::toLowerCase).toList()
                        )
                    );
                    
            return cb.exists(subquery);
        };
    }
}
