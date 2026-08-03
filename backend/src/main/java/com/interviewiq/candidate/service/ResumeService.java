package com.interviewiq.candidate.service;

import com.interviewiq.candidate.dto.ResumeDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ResumeService {
    
    ResumeDto uploadResume(UUID userId, MultipartFile file, Boolean isPrimary);
    
    List<ResumeDto> getMyResumes(UUID userId);
    
    void deleteResume(UUID userId, UUID resumeId);
}
