package com.interviewiq.job.dto;

import com.interviewiq.candidate.enums.SkillProficiency;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddJobSkillRequest {

    @NotBlank(message = "Skill name is required")
    private String skillName;

    private Boolean isRequired = true;

    private SkillProficiency proficiencyLevel = SkillProficiency.INTERMEDIATE;
}
