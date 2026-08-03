package com.interviewiq.interview.dto;

import com.interviewiq.interview.enums.InterviewType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ScheduleInterviewRequest {
    @NotNull
    private UUID applicationId;
    
    @NotNull
    private InterviewType type;
    
    private String round;
    private Integer durationMinutes;
}
