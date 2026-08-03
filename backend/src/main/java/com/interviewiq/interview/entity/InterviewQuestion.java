package com.interviewiq.interview.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "interview_questions")
@Getter
@Setter
public class InterviewQuestion {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    @Column(name = "sequence_number", nullable = false)
    private int sequenceNumber;

    @Column(name = "question_type", nullable = false)
    private String questionType;

    @Column(name = "difficulty", nullable = false)
    private String difficulty = "MEDIUM";

    @Column(name = "question_text", nullable = false)
    private String questionText;

    @Column(name = "expected_answer")
    private String expectedAnswer;

    @Column(name = "topic")
    private String topic;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
