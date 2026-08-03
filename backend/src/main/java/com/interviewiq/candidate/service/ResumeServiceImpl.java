package com.interviewiq.candidate.service;

import com.interviewiq.candidate.dto.ResumeDto;
import com.interviewiq.candidate.entity.CandidateProfile;
import com.interviewiq.candidate.entity.Resume;
import com.interviewiq.candidate.enums.ResumeParseStatus;
import com.interviewiq.candidate.mapper.ResumeMapper;
import com.interviewiq.candidate.repository.CandidateProfileRepository;
import com.interviewiq.candidate.repository.ResumeRepository;
import com.interviewiq.common.exception.BusinessException;
import com.interviewiq.common.exception.ResourceNotFoundException;
import com.interviewiq.common.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final FileStorageService fileStorageService;
    private final ResumeMapper resumeMapper;
    private final CandidateService candidateService;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_TYPES = List.of("application/pdf", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

    @Override
    @Transactional
    public ResumeDto uploadResume(UUID userId, MultipartFile file, Boolean isPrimary) {
        if (file.isEmpty()) {
            throw new BusinessException("Cannot upload empty file");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("File size exceeds 10MB limit");
        }
        
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BusinessException("Only PDF and DOCX files are allowed");
        }

        CandidateProfile profile = candidateProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));

        // Upload to storage
        String fileUrl = fileStorageService.uploadFile(file, "resumes/" + profile.getId());

        // If this is marked as primary, unmark others
        if (isPrimary != null && isPrimary) {
            List<Resume> existingResumes = resumeRepository.findByCandidateId(profile.getId());
            existingResumes.forEach(r -> {
                if (r.getIsPrimary()) {
                    r.setIsPrimary(false);
                    resumeRepository.save(r);
                }
            });
        }

        Resume resume = Resume.builder()
                .candidate(profile)
                .fileName(file.getOriginalFilename())
                .fileUrl(fileUrl)
                .fileType(file.getContentType())
                .fileSizeBytes(file.getSize())
                .isPrimary(isPrimary != null ? isPrimary : false)
                .parseStatus(ResumeParseStatus.PENDING)
                .build();

        resume = resumeRepository.save(resume);
        
        // TODO: Publish Kafka Event for AI parsing (Sprint 9: Resume AI Engine)
        log.info("Published resume parsing event for resume ID: {}", resume.getId());
        
        candidateService.calculateProfileCompletion(profile.getId());

        return resumeMapper.toDto(resume);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResumeDto> getMyResumes(UUID userId) {
        CandidateProfile profile = candidateProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));
                
        return resumeRepository.findByCandidateId(profile.getId())
                .stream()
                .map(resumeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteResume(UUID userId, UUID resumeId) {
        CandidateProfile profile = candidateProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found"));

        Resume resume = resumeRepository.findByIdAndCandidateId(resumeId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found or doesn't belong to you"));

        // Delete from storage
        fileStorageService.deleteFile(resume.getFileUrl());
        
        // Delete from DB
        resumeRepository.delete(resume);
        
        candidateService.calculateProfileCompletion(profile.getId());
    }
}
