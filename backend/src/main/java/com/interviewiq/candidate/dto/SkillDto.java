package com.interviewiq.candidate.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class SkillDto {
    private UUID id;
    private String name;
    private String category;
    private String normalizedName;
}
