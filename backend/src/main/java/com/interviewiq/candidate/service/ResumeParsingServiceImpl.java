package com.interviewiq.candidate.service;

import com.interviewiq.candidate.dto.ResumeAnalysisDto;
import com.interviewiq.candidate.entity.Resume;
import com.interviewiq.candidate.entity.ResumeAnalysis;
import com.interviewiq.candidate.enums.ResumeParseStatus;
import com.interviewiq.candidate.mapper.ResumeAnalysisMapper;
import com.interviewiq.candidate.repository.ResumeAnalysisRepository;
import com.interviewiq.candidate.repository.ResumeRepository;
import com.interviewiq.common.exception.ResourceNotFoundException;
import com.interviewiq.job.entity.Job;
import com.interviewiq.job.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeParsingServiceImpl implements ResumeParsingService {

    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final AiService aiService;
    private final ResumeAnalysisMapper resumeAnalysisMapper;

    @Override
    @Transactional
    public ResumeAnalysisDto analyzeResume(UUID resumeId, UUID jobId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume", "id", resumeId));

        Job job = null;
        String jobDescription = null;
        if (jobId != null) {
            job = jobRepository.findByIdAndDeletedAtIsNull(jobId)
                    .orElseThrow(() -> new ResourceNotFoundException("Job", "id", jobId));
            jobDescription = job.getTitle() + "\n" + job.getDescription() + "\n" + job.getRequirements();
        }

        // Mock reading the resume text from file storage
        String resumeText = "Mock extracted text from resume " + resume.getFileName();

        log.info("Analyzing resume ID: {}, Job ID: {}", resumeId, jobId);

        // Call the AI Service
        ResumeAnalysisDto aiResult = aiService.analyzeResume(resumeText, jobDescription);

        // Convert AI result to Entity and save
        ResumeAnalysis analysis = new ResumeAnalysis();
        analysis.setResume(resume);
        analysis.setJob(job);
        analysis.setAtsScore(aiResult.getAtsScore());
        analysis.setKeywordMatchScore(aiResult.getKeywordMatchScore());
        analysis.setSemanticMatchScore(aiResult.getSemanticMatchScore());
        analysis.setFormatScore(aiResult.getFormatScore());
        analysis.setExperienceScore(aiResult.getExperienceScore());
        analysis.setExtractedSkills(aiResult.getExtractedSkills());
        analysis.setExtractedEducation(aiResult.getExtractedEducation());
        analysis.setExtractedExperience(aiResult.getExtractedExperience());
        analysis.setMissingSkills(aiResult.getMissingSkills());
        analysis.setAiSummary(aiResult.getAiSummary());
        analysis.setImprovementSuggestions(aiResult.getImprovementSuggestions());
        analysis.setModelVersion(aiResult.getModelVersion());

        analysis = resumeAnalysisRepository.save(analysis);

        // Update resume status
        resume.setParseStatus(ResumeParseStatus.COMPLETED);
        resume.setParsedAt(Instant.now());
        resumeRepository.save(resume);

        return resumeAnalysisMapper.toDto(analysis);
    }
}
