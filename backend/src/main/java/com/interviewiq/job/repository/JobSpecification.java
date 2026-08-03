package com.interviewiq.job.repository;

import com.interviewiq.job.entity.Job;
import com.interviewiq.job.enums.JobStatus;
import com.interviewiq.job.enums.JobType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class JobSpecification {

    public static Specification<Job> isPublished() {
        return (root, query, cb) -> cb.equal(root.get("status"), JobStatus.PUBLISHED);
    }

    public static Specification<Job> isNotDeleted() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Job> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return null;
            }
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), likePattern),
                    cb.like(cb.lower(root.get("description")), likePattern)
            );
        };
    }

    public static Specification<Job> hasLocation(String location) {
        return (root, query, cb) -> {
            if (location == null || location.trim().isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%");
        };
    }

    public static Specification<Job> hasJobType(JobType jobType) {
        return (root, query, cb) -> {
            if (jobType == null) {
                return null;
            }
            return cb.equal(root.get("jobType"), jobType);
        };
    }

    public static Specification<Job> hasMinSalary(BigDecimal minSalary) {
        return (root, query, cb) -> {
            if (minSalary == null) {
                return null;
            }
            return cb.greaterThanOrEqualTo(root.get("salaryMax"), minSalary);
        };
    }
}
