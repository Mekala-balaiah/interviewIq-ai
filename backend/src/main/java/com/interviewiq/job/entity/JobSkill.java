package com.interviewiq.job.entity;

import com.interviewiq.candidate.entity.Skill;
import com.interviewiq.candidate.enums.SkillProficiency;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "job_skills", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"job_id", "skill_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSkill {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "is_required", nullable = false)
    private Boolean isRequired = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "proficiency_level")
    private SkillProficiency proficiencyLevel = SkillProficiency.INTERMEDIATE;
}
