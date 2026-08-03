package com.interviewiq.job.service;

import com.interviewiq.candidate.entity.Skill;
import com.interviewiq.candidate.repository.SkillRepository;
import com.interviewiq.common.exception.ResourceNotFoundException;
import com.interviewiq.common.exception.UnauthorizedException;
import com.interviewiq.job.dto.AddJobSkillRequest;
import com.interviewiq.job.dto.JobSkillDto;
import com.interviewiq.job.entity.Job;
import com.interviewiq.job.entity.JobSkill;
import com.interviewiq.job.mapper.JobMapper;
import com.interviewiq.job.repository.JobRepository;
import com.interviewiq.job.repository.JobSkillRepository;
import com.interviewiq.recruiter.entity.RecruiterProfile;
import com.interviewiq.recruiter.repository.RecruiterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobSkillServiceImpl implements JobSkillService {

    private final JobSkillRepository jobSkillRepository;
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final JobMapper jobMapper;

    @Override
    @Transactional
    public JobSkillDto addSkillToJob(UUID jobId, UUID recruiterUserId, AddJobSkillRequest request) {
        Job job = getJobAndVerifyOwnership(jobId, recruiterUserId);

        String normalizedName = request.getSkillName().trim().toLowerCase();
        
        Skill skill = skillRepository.findByNormalizedName(normalizedName)
                .orElseGet(() -> {
                    Skill newSkill = new Skill();
                    newSkill.setName(request.getSkillName().trim());
                    newSkill.setNormalizedName(normalizedName);
                    newSkill.setUsageCount(0);
                    return skillRepository.save(newSkill);
                });

        Optional<JobSkill> existing = jobSkillRepository.findByJobIdAndSkillId(jobId, skill.getId());
        if (existing.isPresent()) {
            JobSkill js = existing.get();
            js.setIsRequired(request.getIsRequired());
            js.setProficiencyLevel(request.getProficiencyLevel());
            return jobMapper.toDto(jobSkillRepository.save(js));
        }

        JobSkill newJobSkill = JobSkill.builder()
                .job(job)
                .skill(skill)
                .isRequired(request.getIsRequired())
                .proficiencyLevel(request.getProficiencyLevel())
                .build();
                
        skill.setUsageCount(skill.getUsageCount() + 1);
        skillRepository.save(skill);

        return jobMapper.toDto(jobSkillRepository.save(newJobSkill));
    }

    @Override
    @Transactional
    public void removeSkillFromJob(UUID jobId, UUID jobSkillId, UUID recruiterUserId) {
        getJobAndVerifyOwnership(jobId, recruiterUserId);
        
        JobSkill jobSkill = jobSkillRepository.findById(jobSkillId)
                .orElseThrow(() -> new ResourceNotFoundException("JobSkill", "id", jobSkillId));
                
        if (!jobSkill.getJob().getId().equals(jobId)) {
            throw new IllegalArgumentException("Skill does not belong to this job");
        }

        Skill skill = jobSkill.getSkill();
        skill.setUsageCount(Math.max(0, skill.getUsageCount() - 1));
        skillRepository.save(skill);

        jobSkillRepository.delete(jobSkill);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobSkillDto> getSkillsForJob(UUID jobId) {
        return jobSkillRepository.findByJobId(jobId).stream()
                .map(jobMapper::toDto)
                .collect(Collectors.toList());
    }
    
    private Job getJobAndVerifyOwnership(UUID jobId, UUID recruiterUserId) {
        Job job = jobRepository.findByIdAndDeletedAtIsNull(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));
                
        RecruiterProfile recruiter = recruiterProfileRepository.findByUserId(recruiterUserId)
                .orElseThrow(() -> new ResourceNotFoundException("RecruiterProfile", "userId", recruiterUserId));
                
        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw new UnauthorizedException("You do not have permission to modify this job");
        }
        
        return job;
    }
}
