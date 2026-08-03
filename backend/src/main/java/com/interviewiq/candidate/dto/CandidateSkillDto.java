package com.interviewiq.candidate.dto;

import com.interviewiq.candidate.enums.SkillProficiency;
import lombok.Data;

import java.util.UUID;

@Data
public class CandidateSkillDto {
    private UUID id;
    private SkillDto skill;
    private SkillProficiency proficiencyLevel;
    private Integer yearsExperience;
    private Boolean isPrimary;
}
