package com.interviewiq.assessment.repository;

import com.interviewiq.assessment.entity.AssessmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssessmentSubmissionRepository extends JpaRepository<AssessmentSubmission, UUID> {
    List<AssessmentSubmission> findByAssessmentId(UUID assessmentId);
    List<AssessmentSubmission> findByAssessmentIdAndQuestionId(UUID assessmentId, UUID questionId);
}
