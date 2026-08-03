package com.interviewiq.candidate.service;

import com.interviewiq.candidate.dto.SkillDto;
import com.interviewiq.candidate.entity.Skill;
import com.interviewiq.candidate.mapper.SkillMapper;
import com.interviewiq.candidate.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SkillDto> searchSkills(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return skillRepository.findByNameContainingIgnoreCase(query)
                .stream()
                .map(skillMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SkillDto findOrCreateSkill(String skillName) {
        String normalizedName = skillName.trim().toLowerCase().replaceAll("[^a-z0-9+#]", "");
        
        Skill skill = skillRepository.findByNormalizedName(normalizedName)
                .orElseGet(() -> {
                    Skill newSkill = Skill.builder()
                            .name(skillName.trim())
                            .normalizedName(normalizedName)
                            .usageCount(0)
                            .build();
                    return skillRepository.save(newSkill);
                });
                
        return skillMapper.toDto(skill);
    }
}
