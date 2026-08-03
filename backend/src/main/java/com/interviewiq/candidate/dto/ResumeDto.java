package com.interviewiq.candidate.dto;

import com.interviewiq.candidate.enums.ResumeParseStatus;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class ResumeDto {
    private UUID id;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSizeBytes;
    private Boolean isPrimary;
    private ResumeParseStatus parseStatus;
    private Instant parsedAt;
    private Instant createdAt;
}
