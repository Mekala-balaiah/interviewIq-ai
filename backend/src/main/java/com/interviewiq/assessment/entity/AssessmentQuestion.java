package com.interviewiq.assessment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "assessment_questions")
@Getter
@Setter
public class AssessmentQuestion {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @Column(name = "sequence_number", nullable = false)
    private int sequenceNumber;

    @Column(name = "question_type", nullable = false)
    private String questionType = "CODING";

    @Column(name = "difficulty", nullable = false)
    private String difficulty = "MEDIUM";

    @Column(name = "problem_statement", nullable = false)
    private String problemStatement;

    @Column(name = "constraints")
    private String constraints;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "test_cases", columnDefinition = "jsonb")
    private String testCases; // JSON array of test cases

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "starter_code", columnDefinition = "jsonb")
    private String starterCode; // JSON map of language to starter code

    @Column(name = "marks", nullable = false)
    private int marks = 10;

    @Column(name = "topic")
    private String topic;

    @Column(name = "time_limit_ms")
    private Integer timeLimitMs = 2000;

    @Column(name = "memory_limit_kb")
    private Integer memoryLimitKb = 256000;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
