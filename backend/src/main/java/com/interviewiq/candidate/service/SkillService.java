package com.interviewiq.candidate.service;

import com.interviewiq.candidate.dto.SkillDto;
import java.util.List;

public interface SkillService {
    
    List<SkillDto> searchSkills(String query);
    
    SkillDto findOrCreateSkill(String skillName);
}
