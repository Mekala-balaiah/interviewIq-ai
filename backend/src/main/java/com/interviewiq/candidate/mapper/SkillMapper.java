package com.interviewiq.candidate.mapper;

import com.interviewiq.candidate.dto.CandidateSkillDto;
import com.interviewiq.candidate.dto.SkillDto;
import com.interviewiq.candidate.entity.CandidateSkill;
import com.interviewiq.candidate.entity.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {

    SkillDto toDto(Skill skill);

    CandidateSkillDto toDto(CandidateSkill candidateSkill);
}
