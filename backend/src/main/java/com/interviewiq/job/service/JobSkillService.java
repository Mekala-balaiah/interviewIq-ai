package com.interviewiq.job.service;

import com.interviewiq.job.dto.AddJobSkillRequest;
import com.interviewiq.job.dto.JobSkillDto;

import java.util.List;
import java.util.UUID;

public interface JobSkillService {
    JobSkillDto addSkillToJob(UUID jobId, UUID recruiterUserId, AddJobSkillRequest request);
    void removeSkillFromJob(UUID jobId, UUID jobSkillId, UUID recruiterUserId);
    List<JobSkillDto> getSkillsForJob(UUID jobId);
}
