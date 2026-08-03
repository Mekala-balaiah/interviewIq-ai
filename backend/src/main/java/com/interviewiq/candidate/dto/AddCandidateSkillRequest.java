package com.interviewiq.candidate.dto;

import com.interviewiq.candidate.enums.SkillProficiency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddCandidateSkillRequest {

    @NotBlank(message = "Skill name is required")
    private String skillName;
    
    @NotNull(message = "Proficiency level is required")
    private SkillProficiency proficiencyLevel;
    
    private Integer yearsExperience;
    
    private Boolean isPrimary;
}
