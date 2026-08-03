package com.interviewiq.interview.mapper;

import com.interviewiq.interview.dto.InterviewDto;
import com.interviewiq.interview.dto.InterviewQuestionDto;
import com.interviewiq.interview.dto.InterviewResponseDto;
import com.interviewiq.interview.entity.Interview;
import com.interviewiq.interview.entity.InterviewQuestion;
import com.interviewiq.interview.entity.InterviewResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InterviewMapper {

    @Mapping(source = "application.id", target = "applicationId")
    @Mapping(source = "candidate.id", target = "candidateId")
    @Mapping(source = "recruiter.id", target = "recruiterId")
    @Mapping(source = "job.id", target = "jobId")
    InterviewDto toDto(Interview entity);

    @Mapping(source = "interview.id", target = "interviewId")
    InterviewQuestionDto toDto(InterviewQuestion entity);

    @Mapping(source = "interview.id", target = "interviewId")
    @Mapping(source = "question.id", target = "questionId")
    InterviewResponseDto toDto(InterviewResponse entity);
}
