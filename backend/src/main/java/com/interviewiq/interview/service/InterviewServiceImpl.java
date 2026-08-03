package com.interviewiq.interview.service;

import com.interviewiq.application.entity.Application;
import com.interviewiq.application.repository.ApplicationRepository;
import com.interviewiq.common.exception.BusinessException;
import com.interviewiq.common.exception.ResourceNotFoundException;
import com.interviewiq.interview.dto.*;
import com.interviewiq.interview.entity.Interview;
import com.interviewiq.interview.entity.InterviewQuestion;
import com.interviewiq.interview.entity.InterviewResponse;
import com.interviewiq.interview.enums.InterviewStatus;
import com.interviewiq.interview.enums.InterviewType;
import com.interviewiq.interview.mapper.InterviewMapper;
import com.interviewiq.interview.repository.InterviewQuestionRepository;
import com.interviewiq.interview.repository.InterviewRepository;
import com.interviewiq.interview.repository.InterviewResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository questionRepository;
    private final InterviewResponseRepository responseRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewAiService interviewAiService;
    private final InterviewMapper interviewMapper;

    @Override
    @Transactional
    public InterviewDto scheduleInterview(ScheduleInterviewRequest request) {
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", request.getApplicationId()));

        Interview interview = new Interview();
        interview.setApplication(application);
        interview.setCandidate(application.getCandidate());
        interview.setJob(application.getJob());
        interview.setRecruiter(application.getJob().getRecruiter());
        interview.setType(request.getType());
        interview.setStatus(InterviewStatus.SCHEDULED);
        interview.setRound(request.getRound());
        interview.setDurationMinutes(request.getDurationMinutes());
        
        if (request.getType() == InterviewType.AI_INTERVIEW) {
            interview.setAiConducted(true);
        }

        return interviewMapper.toDto(interviewRepository.save(interview));
    }

    @Override
    @Transactional
    public InterviewDto startInterview(UUID interviewId) {
        Interview interview = getInterview(interviewId);
        
        if (interview.getStatus() != InterviewStatus.SCHEDULED) {
            throw new BusinessException("Only SCHEDULED interviews can be started.");
        }
        
        interview.setStatus(InterviewStatus.IN_PROGRESS);
        interview.setStartedAt(Instant.now());
        
        if (interview.isAiConducted()) {
            // Generate 3 questions for the mock
            List<InterviewQuestion> questions = interviewAiService.generateQuestions(interview.getJob(), 3);
            for (InterviewQuestion q : questions) {
                q.setInterview(interview);
                questionRepository.save(q);
            }
        }
        
        return interviewMapper.toDto(interviewRepository.save(interview));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewQuestionDto> getQuestions(UUID interviewId) {
        return questionRepository.findByInterviewIdOrderBySequenceNumberAsc(interviewId)
                .stream()
                .map(interviewMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InterviewResponseDto submitResponse(UUID interviewId, UUID questionId, SubmitResponseRequest request) {
        Interview interview = getInterview(interviewId);
        if (interview.getStatus() != InterviewStatus.IN_PROGRESS) {
            throw new BusinessException("Cannot submit response. Interview is not IN_PROGRESS.");
        }

        InterviewQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("InterviewQuestion", "id", questionId));
        
        if (!question.getInterview().getId().equals(interviewId)) {
            throw new BusinessException("Question does not belong to this interview.");
        }
        
        // Ensure we don't submit twice for same question
        responseRepository.findByInterviewIdAndQuestionId(interviewId, questionId).ifPresent(r -> {
            throw new BusinessException("Response already submitted for this question.");
        });

        InterviewResponse response = new InterviewResponse();
        response.setInterview(interview);
        response.setQuestion(question);
        response.setResponseText(request.getResponseText());
        response.setResponseDurationSeconds(request.getResponseDurationSeconds());
        
        if (interview.isAiConducted()) {
            Map<String, Object> eval = interviewAiService.evaluateResponse(
                    question.getQuestionText(), 
                    question.getExpectedAnswer(), 
                    request.getResponseText()
            );
            response.setAiScore((Integer) eval.get("score"));
            response.setAiFeedback((String) eval.get("feedback"));
        }
        
        return interviewMapper.toDto(responseRepository.save(response));
    }

    @Override
    @Transactional
    public InterviewDto completeInterview(UUID interviewId) {
        Interview interview = getInterview(interviewId);
        if (interview.getStatus() != InterviewStatus.IN_PROGRESS) {
            throw new BusinessException("Cannot complete interview. It is not IN_PROGRESS.");
        }
        
        interview.setStatus(InterviewStatus.COMPLETED);
        interview.setCompletedAt(Instant.now());
        
        if (interview.isAiConducted()) {
            List<InterviewResponse> responses = responseRepository.findByInterviewId(interviewId);
            Map<String, Object> summary = interviewAiService.generateSummary(responses);
            interview.setOverallScore((Integer) summary.get("overallScore"));
            interview.setAiSummary((String) summary.get("summary"));
        }
        
        return interviewMapper.toDto(interviewRepository.save(interview));
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewDto getInterviewDetails(UUID interviewId) {
        return interviewMapper.toDto(getInterview(interviewId));
    }
    
    private Interview getInterview(UUID id) {
        return interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview", "id", id));
    }
}
