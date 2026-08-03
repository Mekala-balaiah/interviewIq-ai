package com.interviewiq.interview.repository;

import com.interviewiq.interview.entity.InterviewResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InterviewResponseRepository extends JpaRepository<InterviewResponse, UUID> {
    List<InterviewResponse> findByInterviewId(UUID interviewId);
    Optional<InterviewResponse> findByInterviewIdAndQuestionId(UUID interviewId, UUID questionId);
}
