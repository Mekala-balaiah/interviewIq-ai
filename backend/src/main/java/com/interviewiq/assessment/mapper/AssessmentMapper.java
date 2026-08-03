package com.interviewiq.assessment.mapper;

import com.interviewiq.assessment.dto.AssessmentDto;
import com.interviewiq.assessment.dto.AssessmentQuestionDto;
import com.interviewiq.assessment.dto.AssessmentSubmissionDto;
import com.interviewiq.assessment.entity.Assessment;
import com.interviewiq.assessment.entity.AssessmentQuestion;
import com.interviewiq.assessment.entity.AssessmentSubmission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AssessmentMapper {

    @Mapping(source = "application.id", target = "applicationId")
    @Mapping(source = "job.id", target = "jobId")
    @Mapping(source = "candidate.id", target = "candidateId")
    AssessmentDto toDto(Assessment entity);

    @Mapping(source = "assessment.id", target = "assessmentId")
    AssessmentQuestionDto toDto(AssessmentQuestion entity);

    @Mapping(source = "assessment.id", target = "assessmentId")
    @Mapping(source = "question.id", target = "questionId")
    @Mapping(source = "candidate.id", target = "candidateId")
    AssessmentSubmissionDto toDto(AssessmentSubmission entity);
}
