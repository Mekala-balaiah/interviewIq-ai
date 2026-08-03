package com.interviewiq.job.dto;

import com.interviewiq.candidate.dto.SkillDto;
import com.interviewiq.candidate.enums.SkillProficiency;
import lombok.Data;

import java.util.UUID;

@Data
public class JobSkillDto {
    private UUID id;
    private UUID jobId;
    private SkillDto skill;
    private Boolean isRequired;
    private SkillProficiency proficiencyLevel;
}
