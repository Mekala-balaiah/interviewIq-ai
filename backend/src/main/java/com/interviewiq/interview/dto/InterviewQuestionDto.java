package com.interviewiq.interview.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class InterviewQuestionDto {
    private UUID id;
    private UUID interviewId;
    private int sequenceNumber;
    private String questionType;
    private String difficulty;
    private String questionText;
    private String expectedAnswer; // Note: Usually shouldn't be exposed to the candidate, but good for DTO
    private String topic;
    private Instant createdAt;
}
